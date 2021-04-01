package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.restControllers.LWControllers.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps Body entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class BodyAssembler implements RepresentationModelAssembler<Body, EntityModel<Body>> {
    @Override
    public EntityModel<Body> toModel(Body entity) {
        var parent = entity.getParent();
        var entityModel = EntityModel.of(
                entity,
                linkTo(methodOn(BodyController.class).one(entity.getId())).withSelfRel()
        );
        addParentLink(parent, entityModel);
        return entityModel;
    }

    /**
     * adds links of parent of this entity.
     *
     * @param parent      of which link needs to be added.
     * @param entityModel to which link needs to be added.
     */
    private void addParentLink(LightWeight parent, EntityModel<Body> entityModel) {
        if (parent.asClassOrInterface().isPresent()) {
            entityModel.add(linkTo(methodOn(ClassOrInterfaceController.class).one(parent.getId())).withRel("parent"));
        } else if (parent.asEnumLW().isPresent()) {
            entityModel.add(linkTo(methodOn(EnumLWController.class).one(parent.getId())).withRel("parent"));
        } else if (parent.asConstructor().isPresent()) {
            entityModel.add(linkTo(methodOn(ConstructorController.class).one(parent.getId())).withRel("parent"));
        } else if (parent.asMethod().isPresent()) {
            entityModel.add(linkTo(methodOn(MethodController.class).one(parent.getId())).withRel("parent"));
        }
    }
}
