package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.Constructor;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.restControllers.LWControllers.BodyController;
import org.java2uml.java2umlapi.restControllers.LWControllers.ClassOrInterfaceController;
import org.java2uml.java2umlapi.restControllers.LWControllers.ConstructorController;
import org.java2uml.java2umlapi.restControllers.LWControllers.EnumLWController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps Constructor entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class ConstructorAssembler implements RepresentationModelAssembler<Constructor, EntityModel<Constructor>> {
    @Override
    public EntityModel<Constructor> toModel(Constructor entity) {
        var parent = entity.getParent();
        var entityModel = EntityModel.of(
                entity,
                linkTo(methodOn(BodyController.class).bodyByParentId(entity.getId())).withRel("body"),
                linkTo(methodOn(ConstructorController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(ConstructorController.class).allByParent(parent.getId())).withRel("constructors")
        );
        attachParentLink(parent, entityModel);
        return entityModel;
    }

    private void attachParentLink(LightWeight parent, EntityModel<Constructor> entityModel) {
        if (parent.asEnumLW().isPresent()) {
            entityModel.add(linkTo(methodOn(EnumLWController.class).one(parent.getId())).withRel("parent"));
        }
        if (parent.asClassOrInterface().isPresent()) {
            entityModel.add(linkTo(methodOn(ClassOrInterfaceController.class).one(parent.getId())).withRel("parent"));
        }
    }

    @Override
    public CollectionModel<EntityModel<Constructor>> toCollectionModel(Iterable<? extends Constructor> entities) {
        var collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.addAllIf(
                entities.iterator().hasNext(),
                () -> {
                    var parentId = entities.iterator().next().getParent().getId();
                    return List.of(
                            linkTo(methodOn(ConstructorController.class).allByParent(parentId)).withSelfRel()
                    );
                }
        );
        return collectionModel;
    }
}
