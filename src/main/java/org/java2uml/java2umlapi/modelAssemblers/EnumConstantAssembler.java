package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.EnumConstant;
import org.java2uml.java2umlapi.restControllers.LWControllers.EnumConstantController;
import org.java2uml.java2umlapi.restControllers.LWControllers.EnumLWController;
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
 * Maps EnumConstant entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class EnumConstantAssembler implements RepresentationModelAssembler<EnumConstant, EntityModel<EnumConstant>> {
    @Override
    public EntityModel<EnumConstant> toModel(EnumConstant entity) {
        return EntityModel.of(
                entity,
                linkTo(methodOn(EnumConstantController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(EnumConstantController.class).
                        allByParent(entity.getParent().getId())).withRel("enumConstants"),
                linkTo(methodOn(EnumLWController.class).one(entity.getParent().getId())).withRel("parent")
        );
    }

    @Override
    public CollectionModel<EntityModel<EnumConstant>> toCollectionModel(Iterable<? extends EnumConstant> entities) {
        var collectionModel = StreamSupport
                .stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        collectionModel.addAllIf(
                entities.iterator().hasNext(),
                () -> {
                    var parentId = entities.iterator().next().getParent().getId();
                    return List.of(
                            linkTo(methodOn(EnumConstantController.class).allByParent(parentId)).withSelfRel()
                    );
                }
        );
        return collectionModel;
    }
}
