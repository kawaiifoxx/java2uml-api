package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.Field;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.restControllers.LWControllers.ClassOrInterfaceController;
import org.java2uml.java2umlapi.restControllers.LWControllers.EnumLWController;
import org.java2uml.java2umlapi.restControllers.LWControllers.FieldController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps Field entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class FieldAssembler implements RepresentationModelAssembler<Field, EntityModel<Field>> {
    @Override
    public EntityModel<Field> toModel(Field entity) {
        var parent = entity.getParent();
        var entityModel = EntityModel.of(
                entity,
                linkTo(methodOn(FieldController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(FieldController.class).allByParent(parent.getId())).withRel("fields")
        );
        addParentLink(parent, entityModel);
        return entityModel;
    }

    /**
     * Adds links to parent if parent is a valid parent.
     *
     * @param parent      parent of the entity.
     * @param entityModel entity model to which links will be added.
     */
    private void addParentLink(LightWeight parent, EntityModel<Field> entityModel) {
        entityModel
                .addIf(
                        parent.asClassOrInterface().isPresent(),
                        () -> linkTo(methodOn(ClassOrInterfaceController.class).one(parent.getId())).withRel("parent"))
                .addIf(
                        parent.asEnumLW().isPresent(),
                        () -> linkTo(methodOn(EnumLWController.class).one(parent.getId())).withRel("parent")
                );
    }

    @Override
    public CollectionModel<EntityModel<Field>> toCollectionModel(Iterable<? extends Field> entities) {
        var collectionModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        collectionModel.addAllIf(
                entities.iterator().hasNext(),
                () -> {
                    var parentId = entities.iterator().next().getParent().getId();
                    return List.of(
                            linkTo(methodOn(FieldController.class).allByParent(parentId)).withSelfRel()
                    );
                }
        );
        return collectionModel;
    }
}
