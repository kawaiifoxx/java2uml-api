package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.callGraph.CallGraphRelation;
import org.java2uml.java2umlapi.restControllers.LWControllers.MethodController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps CallGraphRelation entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class CallGraphRelationAssembler
        implements RepresentationModelAssembler<CallGraphRelation, EntityModel<CallGraphRelation>> {

    @Override
    public EntityModel<CallGraphRelation> toModel(CallGraphRelation entity) {
        return EntityModel.of(entity)
                .addIf(
                        entity.getFromId() != null,
                        () -> linkTo(methodOn(MethodController.class).one(entity.getFromId())).withRel("from")
                ).addIf(
                        entity.getToId() != null,
                        () -> linkTo(methodOn(MethodController.class).one(entity.getToId())).withRel("to")
                );
    }
}
