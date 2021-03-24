package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.EnumLW;
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
 * Maps EnumLW entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class EnumLWAssembler implements RepresentationModelAssembler<EnumLW, EntityModel<EnumLW>> {
    @Override
    public EntityModel<EnumLW> toModel(EnumLW entity) {
        return EntityModel.of(
                entity,
                linkTo(methodOn(EnumConstantController.class).allByParent(entity.getId())).withRel("enumConstants"),
                linkTo(methodOn(FieldController.class).allByParent(entity.getId())).withRel("fields"),
                linkTo(methodOn(ConstructorController.class).allByParent(entity.getId())).withRel("constructors"),
                linkTo(methodOn(BodyController.class).bodyByParentId(entity.getId())).withRel("body"),
                linkTo(methodOn(EnumLWController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(SourceController.class).one(entity.getParent().getId())).withRel("parent"),
                linkTo(methodOn(EnumLWController.class).allBySource(entity.getParent().getId())).withRel("enums")
        );
    }

    @Override
    public CollectionModel<EntityModel<EnumLW>> toCollectionModel(Iterable<? extends EnumLW> entities) {
        var collectionModel = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        collectionModel.addAllIf(
                entities.iterator().hasNext(),
                () -> {
                    var parentId = entities.iterator().next().getParent().getId();
                    return List.of(
                            linkTo(methodOn(EnumLWController.class).allBySource(parentId)).withSelfRel()
                    );
                }
        );
        return collectionModel;
    }
}
