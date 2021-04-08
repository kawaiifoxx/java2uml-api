package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.java2uml.java2umlapi.restControllers.LWControllers.ClassOrInterfaceController;
import org.java2uml.java2umlapi.restControllers.LWControllers.ClassRelationController;
import org.java2uml.java2umlapi.restControllers.LWControllers.SourceController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps ClassRelation entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class ClassRelationAssembler implements RepresentationModelAssembler<ClassRelation, EntityModel<ClassRelation>> {
    @Override
    public EntityModel<ClassRelation> toModel(ClassRelation entity) {
        var entityModel = EntityModel.of(entity);
        addFromAndTo(entity, entityModel);
        entityModel.add(
                linkTo(methodOn(ClassRelationController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(SourceController.class).one(entity.getParent().getId())).withRel("parent"),
                linkTo(methodOn(ClassRelationController.class).allBySource(entity.getParent().getId()))
                        .withRel("relations")
        );

        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<ClassRelation>> toCollectionModel(Iterable<? extends ClassRelation> entities) {
        var collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.addAllIf(
                entities.iterator().hasNext(),
                () -> {
                    var parentId = entities.iterator().next().getParent().getId();
                    return List.of(
                            linkTo(methodOn(ClassRelationController.class).allBySource(parentId)).withSelfRel()
                    );
                }
        );
        return collectionModel;
    }

    /**
     * Adds links of from class and to class to the entity model.
     *
     * @param entity      ClassRelation from which from and to will be extracted.
     * @param entityModel entity model to which from and to  links will be added.
     */
    private void addFromAndTo(ClassRelation entity, EntityModel<ClassRelation> entityModel) {
        if (entity.getFrom().asClassOrInterface().isPresent()) {
            entityModel.add(
                    linkTo(methodOn(ClassOrInterfaceController.class).one(entity.getFrom().getId())).withRel("from")
            );
        }
        if (entity.getTo().asClassOrInterface().isPresent()) {
            entityModel.add(
                    linkTo(methodOn(ClassOrInterfaceController.class).one(entity.getTo().getId())).withRel("to")
            );
        }
    }
}
