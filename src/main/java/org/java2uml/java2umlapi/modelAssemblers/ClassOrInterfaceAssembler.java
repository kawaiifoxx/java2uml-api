package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.restControllers.LWControllers.*;
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
 * Maps ClassOrInterface entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class ClassOrInterfaceAssembler implements
        RepresentationModelAssembler<ClassOrInterface, EntityModel<ClassOrInterface>> {
    @Override
    public EntityModel<ClassOrInterface> toModel(ClassOrInterface entity) {
        var parent = entity.getParent();
        return EntityModel.of(
                entity,
                linkTo(methodOn(ConstructorController.class).allByParent(entity.getId())).withRel("constructors"),
                linkTo(methodOn(FieldController.class).allByParent(entity.getId())).withRel("fields"),
                linkTo(methodOn(MethodController.class).allByParent(entity.getId())).withRel("methods"),
                linkTo(methodOn(BodyController.class).bodyByParentId(entity.getId())).withRel("body"),
                linkTo(methodOn(ClassOrInterfaceController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(SourceController.class).one(parent.getId())).withRel("parent"),
                linkTo(methodOn(ClassOrInterfaceController.class).allBySource(parent.getId())).withRel("classes"),
                linkTo(methodOn(ClassRelationController.class).allBySource(parent.getId())).withRel("relations")
        );
    }

    @Override
    public CollectionModel<EntityModel<ClassOrInterface>> toCollectionModel(
            Iterable<? extends ClassOrInterface> entities
    ) {
        var collectionModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
        collectionModel.addAllIf(
                entities.iterator().hasNext(),
                () -> {
                    var parentId = entities.iterator().next().getParent().getId();
                    return List.of(
                            linkTo(methodOn(ClassOrInterfaceController.class).allBySource(parentId)).withSelfRel()
                    );
                }
        );

        return collectionModel;
    }
}
