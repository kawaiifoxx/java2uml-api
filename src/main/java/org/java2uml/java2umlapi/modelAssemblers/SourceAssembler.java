package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.restControllers.LWControllers.ClassOrInterfaceController;
import org.java2uml.java2umlapi.restControllers.LWControllers.ClassRelationController;
import org.java2uml.java2umlapi.restControllers.LWControllers.EnumLWController;
import org.java2uml.java2umlapi.restControllers.LWControllers.SourceController;
import org.java2uml.java2umlapi.restControllers.ProjectInfoController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps Source entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class SourceAssembler implements RepresentationModelAssembler<Source, EntityModel<Source>> {
    @Override
    public EntityModel<Source> toModel(Source entity) {
        var projectInfoId = entity.getProjectInfo().getId();
        return EntityModel.of(
                entity,
                linkTo(methodOn(SourceController.class).one(projectInfoId)).withSelfRel(),
                linkTo(methodOn(ProjectInfoController.class).one(projectInfoId)).withRel("projectInfo"),
                linkTo(methodOn(ClassOrInterfaceController.class).allBySource(entity.getId())).withRel("classes"),
                linkTo(methodOn(EnumLWController.class).allBySource(entity.getId())).withRel("enums"),
                linkTo(methodOn(ClassRelationController.class).allBySource(entity.getId())).withRel("relations")
        );
    }
}
