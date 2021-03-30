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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * RestController for generating {@link CallGraphRelation}s for {@link Method}.
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
     * generates {@link CallGraphRelation}s for given {@link Method} and then wraps it with {@link CollectionModel}
     * which has some useful navigational links.
     *
     * @param methodId    id of {@link Method} for which list of {@link CallGraphRelation} is needed.
     * @param packageName name of the package to which you want to limit the call graph relations to.
     * @return {@link CollectionModel} of List of {@link CallGraphRelation}.
     */
    @GetMapping("/{methodId}")
    public CollectionModel<EntityModel<CallGraphRelation>> getCallGraph(
            @PathVariable("methodId") Long methodId,
            @RequestParam(value = "package", required = false) String packageName
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
     * Generates {@link CallGraphRelation} list  from {@link MethodCallGraph}.
     *
     * @param method      {@link Method} for which the {@link CallGraphRelation}s  are needed.
     * @param packageName name of the package to limit the search.
     * @return {@link CallGraphRelation}s for provided {@link Method}.
     */
    private List<CallGraphRelation> getCallGraphRelations(Method method, String packageName) {
        var source = getSource(method);
        var sourceComponent = getSourceComponent(method.getId(), source);
        var resolvedMethodDecl = getResolvedMethodDeclaration(method, sourceComponent);
        MethodCallGraph methodCallGraph = new MethodCallGraphImpl(resolvedMethodDecl, packageName);
        return methodCallGraph.getCallGraphRelations(getMethodNameToMethodIdMap(method.getId(), source));
    }

    /**
     * Fetches methodNameToMethodIdMap from {@link MethodSignatureToMethodIdMapService}.
     *
     * @param methodId id of {@link Method} for which methodNameToMethodIdMap is needed.
     * @param source   {@link Source}
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
     * {@link ResolvedMethodDeclaration} for provided method and source component.
     *
     * @param method          for which {@link ResolvedMethodDeclaration} is needed.
     * @param sourceComponent from which {@link ResolvedMethodDeclaration} will be fetched.
     * @return {@link ResolvedMethodDeclaration}
     * @throws ParsedComponentNotFoundException if {@link ParsedMethodComponent} for given method is  not available.
     */
    private ResolvedMethodDeclaration getResolvedMethodDeclaration(Method method, SourceComponent sourceComponent) {
        return sourceComponent.
                find(method.getSignature(), ParsedMethodComponent.class)
                .orElseThrow(() -> new ParsedComponentNotFoundException(
                        "Unable to find paredMethodComponent with signature " + method.getSignature()))
                .getResolvedMethodDeclaration();
    }

    /**
     * Fetches {@link SourceComponent} for provided {@link Method} id and {@link Source}.
     *
     * @param methodId id associated with {@link Method}
     * @param source   {@link Source}
     * @return {@link SourceComponent}
     * @throws ParsedComponentNotFoundException if {@link SourceComponent} was not found
     * for given {@link Method} id and {@link Source}.
     */
    private SourceComponent getSourceComponent(Long methodId, Source source) {
        var projectInfo = source.getProjectInfo();
        return sourceComponentService.get(projectInfo.getSourceComponentId())
                .orElseThrow(() -> new ParsedComponentNotFoundException(
                        "Unable to fetch source component for method id: " + methodId));
    }

    /**
     * Retrieves {@link Source} for provided {@link Method}.
     *
     * @param method {@link Method} for which {@link Source} needs to be fetched.
     * @return source
     * @throws LightWeightNotFoundException if unable to fetch {@link Source} for provided {@link Method}.
     */
    private Source getSource(Method method) {
        return method.getParent().getParent().asSource().orElseThrow(() -> new LightWeightNotFoundException(
                "Unable to fetch source for method with id: " + method.getId())
        );
    }

    /**
     * @param methodId id of {@link Method} to be retrieved.
     * @return {@link Method}
     * @throws LightWeightNotFoundException if {@link Method} is not present for provided methodId.
     */
    private Method getMethod(Long methodId) {
        return methodRepository.findById(methodId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch method with id: " + methodId));
    }
}
