package org.java2uml.java2umlapi.restControllers.callGraphControllers;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import org.java2uml.java2umlapi.callGraph.CallGraphRelation;
import org.java2uml.java2umlapi.callGraph.MethodCallGraph;
import org.java2uml.java2umlapi.callGraph.MethodCallGraphImpl;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.lightWeight.service.MethodSignatureToMethodIdMapService;
import org.java2uml.java2umlapi.modelAssemblers.CallGraphRelationAssembler;
import org.java2uml.java2umlapi.parsedComponent.ParsedMethodComponent;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.LWControllers.MethodController;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.MethodNameToMethodIdNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ParsedComponentNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.SourceComponentNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * RestController for generating call graphs for methods.
 * </p>
 *
 * @author kawaiifox
 */
@RestController
@RequestMapping("/api/call-graph")
public class CallGraphController {
    private final MethodSignatureToMethodIdMapService methodIdMapService;
    private final CallGraphRelationAssembler assembler;
    private final MethodRepository methodRepository;
    private final SourceComponentService sourceComponentService;

    public CallGraphController(
            MethodSignatureToMethodIdMapService methodIdMapService,
            CallGraphRelationAssembler assembler,
            MethodRepository methodRepository,
            SourceComponentService sourceComponentService
    ) {
        this.methodIdMapService = methodIdMapService;
        this.assembler = assembler;
        this.methodRepository = methodRepository;
        this.sourceComponentService = sourceComponentService;
    }

    /**
     * Defines /api/call-graph/{methodId} get mapping.<br>
     * generates a call graph for given method and then wraps it with collection model
     * which has some useful navigational links.
     *
     * @param methodId    id of method for which callGraphRelations is needed.
     * @param packageName name of the package to which you want to limit the call graph relations to.
     * @return CollectionModel of List of CallGraphRelations.
     */
    @GetMapping("/{methodId}")
    public CollectionModel<EntityModel<CallGraphRelation>> getCallGraph(
            @PathVariable("methodId") Long methodId,
            @RequestParam("package") String packageName
    ) {
        var method = getMethod(methodId);
        if (packageName == null || packageName.replaceAll(" ", "").isEmpty()) {
            packageName = method.getPackageName();
        }
        return assembler.toCollectionModel(getCallGraphRelations(method, packageName))
                .add(
                        linkTo(methodOn(CallGraphController.class).getCallGraph(methodId, packageName)).withSelfRel(),
                        linkTo(methodOn(MethodController.class).one(methodId)).withRel("method")
                );
    }

    /**
     * Generates CallGraphRelations from methodCallGraph.
     *
     * @param method      method for which the call graph relations are needed.
     * @param packageName name of the package to limit the search.
     * @return CallGraphRelations for provided method.
     */
    private List<CallGraphRelation> getCallGraphRelations(Method method, String packageName) {
        var source = getSource(method);
        var sourceComponent = getSourceComponent(method.getId(), source);
        var resolvedMethodDecl = getResolvedMethodDeclaration(method, sourceComponent);
        MethodCallGraph methodCallGraph = new MethodCallGraphImpl(resolvedMethodDecl, packageName);
        return methodCallGraph.getCallGraphRelations(getMethodNameToMethodIdMap(method.getId(), source));
    }

    /**
     * Fetches methodNameToMethodIdMap from methodIdMapService.
     *
     * @param methodId id of method for which methodNameToMethodIdMap is needed.
     * @param source   source
     * @return methodNameToMethodIdMap
     * @throws MethodNameToMethodIdNotFoundException if methodNameToMethodIdMap has not been found.
     */
    private Map<String, Long> getMethodNameToMethodIdMap(Long methodId, Source source) {
        return methodIdMapService.findById(source.getId())
                .orElseThrow(
                        () -> new MethodNameToMethodIdNotFoundException(
                                "Unable to fetch map for method with id:" + methodId
                        )
                );
    }

    /**
     * ResolvedMethodDeclaration for provided method and source component.
     *
     * @param method          for which resolvedMethodDeclaration is needed.
     * @param sourceComponent from which resolvedMethodDeclaration will be fetched.
     * @return ResolvedMethodDeclaration
     * @throws ParsedComponentNotFoundException if ParsedMethodComponent for given method is  not available.
     */
    private ResolvedMethodDeclaration getResolvedMethodDeclaration(Method method, SourceComponent sourceComponent) {
        return sourceComponent.
                find(method.getSignature(), ParsedMethodComponent.class)
                .orElseThrow(() -> new ParsedComponentNotFoundException(
                        "Unable to find paredMethodComponent with signature " + method.getSignature()))
                .getResolvedMethodDeclaration();
    }

    /**
     * Fetches sourceComponent for provided method id and source.
     *
     * @param methodId id associated with method
     * @param source   source
     * @return SourceComponent
     * @throws SourceComponentNotFoundException if sourceComponent was not found for given method id and source.
     */
    private SourceComponent getSourceComponent(Long methodId, Source source) {
        var projectInfo = source.getProjectInfo();
        return sourceComponentService.get(projectInfo.getSourceComponentId())
                .orElseThrow(() -> new SourceComponentNotFoundException(
                        "Unable to fetch source component for method id: " + methodId));
    }

    /**
     * Retrieves source for provided method.
     *
     * @param method method for which source needs to be fetched.
     * @return source
     * @throws LightWeightNotFoundException if unable to fetch source for provided method.
     */
    private Source getSource(Method method) {
        return method.getParent().getParent().asSource().orElseThrow(() -> new LightWeightNotFoundException(
                "Unable to fetch source for method with id: " + method.getId())
        );
    }

    /**
     * @param methodId id of method to be retrieved.
     * @return method for provided method id.
     * @throws LightWeightNotFoundException if method is not present for provided method id.
     */
    private Method getMethod(Long methodId) {
        return methodRepository.findById(methodId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch method with id: " + methodId));
    }
}
