package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.lightWeight.repository.LightWeightRepository;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.modelAssemblers.MethodAssembler;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Rest Controller for Method entities.
 * </p>
 *
 * @author kawaiifox
 */
@RestController
@RequestMapping("/api/method")
public class MethodController {
    private final MethodRepository methodRepository;
    private final MethodAssembler assembler;
    private final LightWeightRepository lightWeightRepository;

    public MethodController(
            MethodRepository methodRepository,
            MethodAssembler assembler,
            LightWeightRepository lightWeightRepository
    ) {
        this.methodRepository = methodRepository;
        this.assembler = assembler;
        this.lightWeightRepository = lightWeightRepository;
    }

    /**
     * Retrieves Method provided method id.
     *
     * @param methodId id associated with method that is going to be retrieved.
     * @return Entity model of method with useful links.
     * @throws LightWeightNotFoundException if method is not found.
     */
    @GetMapping("/{methodId}")
    public EntityModel<Method> one(@PathVariable("methodId") Long methodId) {
        return assembler.toModel(
                methodRepository.findById(methodId)
                        .orElseThrow(
                                () -> new LightWeightNotFoundException("Unable to fetch method with id: " + methodId)
                        )
        );
    }

    /**
     * Retrieves collection of all the methods associated with given parent.
     *
     * @param parentId id of the parent.
     * @return Collection Model of the methods with useful links.
     * @throws LightWeightNotFoundException if parent is not found.
     */
    @GetMapping("/by-parent/{parentId}")
    public CollectionModel<EntityModel<Method>> allByParent(@PathVariable("parentId") Long parentId) {
        var parent = lightWeightRepository.findById(parentId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch parent with id: " + parentId));
        return toCollectionModel(parent);
    }

    /**
     * Converts list of entities to collection model and adds extra links to it.
     *
     * @param parent parent of the entities.
     * @return collection model with added links.
     */
    private CollectionModel<EntityModel<Method>> toCollectionModel(LightWeight parent) {
        return assembler.toCollectionModel(methodRepository.findAllByParent(parent))
                .addIf(
                        parent.asEnumLW().isPresent(),
                        () -> linkTo(methodOn(EnumLWController.class).one(parent.getId())).withRel("parent"))
                .addIf(parent.asClassOrInterface().isPresent(),
                        () -> linkTo(methodOn(ClassOrInterfaceController.class).one(parent.getId())).withRel("parent"));
    }
}
