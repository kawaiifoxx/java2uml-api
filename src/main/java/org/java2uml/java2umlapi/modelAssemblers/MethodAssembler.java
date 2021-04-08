package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.restControllers.LWControllers.BodyController;
import org.java2uml.java2umlapi.restControllers.LWControllers.ClassOrInterfaceController;
import org.java2uml.java2umlapi.restControllers.LWControllers.EnumLWController;
import org.java2uml.java2umlapi.restControllers.LWControllers.MethodController;
import org.java2uml.java2umlapi.restControllers.callGraphControllers.CallGraphController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps Method entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class MethodAssembler implements RepresentationModelAssembler<Method, EntityModel<Method>> {
    @Override
    public EntityModel<Method> toModel(Method entity) {
        var entityModel = EntityModel.of(
                entity,
                linkTo(methodOn(BodyController.class).bodyByParentId(entity.getId())).withRel("body"),
                linkTo(methodOn(MethodController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(CallGraphController.class).getCallGraph(entity.getId(), entity.getPackageName()))
                        .withRel("callGraph"),
                linkTo(methodOn(MethodController.class).allByParent(entity.getParent().getId())).withRel("methods")
        );
        addParentLink(entity, entityModel);
        return entityModel;
    }

    /**
     * Adds parent link to the entity model.
     * @param entity method from which parent is extracted.
     * @param entityModel model to which entity is added.
     */
    private void addParentLink(Method entity, EntityModel<Method> entityModel) {
        var parent = entity.getParent();
        if (parent.asEnumLW().isPresent()) {
            entityModel.add(linkTo(methodOn(EnumLWController.class).one(parent.getId())).withRel("parent"));
        }
        if (parent.asClassOrInterface().isPresent()) {
            entityModel.add(linkTo(methodOn(ClassOrInterfaceController.class).one(parent.getId())).withRel("parent"));
        }
    }

    @Override
    public CollectionModel<EntityModel<Method>> toCollectionModel(Iterable<? extends Method> entities) {
        var collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.addAllIf(
                entities.iterator().hasNext(),
                () -> {
                    var parentId = entities.iterator().next().getParent().getId();
                    return List.of(
                            linkTo(methodOn(MethodController.class).allByParent(parentId)).withSelfRel()
                    );
                }
        );
        return collectionModel;
    }
}
