package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.java2uml.java2umlapi.lightWeight.repository.ClassRelationRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.modelAssemblers.ClassRelationAssembler;
import org.java2uml.java2umlapi.restControllers.exceptions.ClassRelationNotFoundException;
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
 * Rest Controller for Class Relation entities.
 * </p>
 *
 * @author kawaiifox
 */
@RestController
@RequestMapping("/api/relation")
public class ClassRelationController {

    private final ClassRelationRepository classRelationRepository;
    private final ClassRelationAssembler assembler;
    private final SourceRepository sourceRepository;

    public ClassRelationController(
            ClassRelationRepository classRelationRepository,
            ClassRelationAssembler assembler,
            SourceRepository sourceRepository
    ) {
        this.classRelationRepository = classRelationRepository;
        this.assembler = assembler;
        this.sourceRepository = sourceRepository;
    }

    /**
     * Retrieves class relation entity with the provided id.
     * @param classRelationId id associated with the class relation entity
     * @return Entity model of class relation entity with useful links.
     * @throws ClassRelationNotFoundException if the queried class relation entity cannot be found.
     */
    @GetMapping("/{classRelationId}")
    public EntityModel<ClassRelation> one(@PathVariable("classRelationId") Long classRelationId) {
        var classRelation = classRelationRepository.findById(classRelationId)
                .orElseThrow(() ->
                        new ClassRelationNotFoundException("Unable to fetch relation with id " + classRelationId));

        return assembler.toModel(classRelation);
    }

    /**
     * Retrieves all the class relation entities associated with a source.
     * @param sourceId id of the source.
     * @return Collection model of ClassRelation entities with useful links.
     */
    @GetMapping("/by-source/{sourceId}")
    public CollectionModel<EntityModel<ClassRelation>> allBySource(@PathVariable("sourceId") Long sourceId) {
        var source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch source with id " + sourceId));
        return assembler.toCollectionModel(source.getClassRelationList())
                .add(linkTo(methodOn(SourceController.class).one(sourceId)).withRel("parent"));
    }
}
