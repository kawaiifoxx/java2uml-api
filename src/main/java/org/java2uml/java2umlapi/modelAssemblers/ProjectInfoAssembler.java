package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.restControllers.LWControllers.SourceController;
import org.java2uml.java2umlapi.restControllers.ProjectInfoController;
import org.java2uml.java2umlapi.restControllers.UMLController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * <p>
 * Maps ProjectInfo entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class ProjectInfoAssembler implements RepresentationModelAssembler<ProjectInfo, EntityModel<ProjectInfo>> {

    @Override
    public EntityModel<ProjectInfo> toModel(ProjectInfo entity) {
        return EntityModel.of(
                entity,
                linkTo(methodOn(ProjectInfoController.class).one(entity.getId())).withSelfRel()
                        .andAffordance(afford(methodOn(ProjectInfoController.class).delete(entity.getId())))
                        .withRel("delete"),
                linkTo(methodOn(UMLController.class).getPUMLCode(entity.getId())).withRel("uml:text"),
                linkTo(methodOn(UMLController.class).getSvg(entity.getId())).withRel("uml:svg"),
                linkTo(methodOn(SourceController.class).findByProjectId(entity.getId())).withRel("projectModel")
        );
    }
}
