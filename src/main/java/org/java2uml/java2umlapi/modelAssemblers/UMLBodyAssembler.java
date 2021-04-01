package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.lightWeight.UMLBody;
import org.java2uml.java2umlapi.restControllers.ProjectInfoController;
import org.java2uml.java2umlapi.restControllers.UMLController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Maps UMLBody entity to EntityModel for representational purposes.<br>
 * Adds few useful links to entityModel generated from this class.
 * </p>
 *
 * @author kawaiifox
 */
@SuppressWarnings("NullableProblems")
@Component
public class UMLBodyAssembler implements RepresentationModelAssembler<UMLBody, EntityModel<UMLBody>> {

    @Override
    public EntityModel<UMLBody> toModel(UMLBody entity) {
        ProjectInfo projectInfo = entity.getProjectInfo();
        return EntityModel.of(
                entity,
                linkTo(methodOn(UMLController.class).getPUMLCode(projectInfo.getId())).withSelfRel(),
                linkTo(methodOn(UMLController.class).getSvg(projectInfo.getId())).withRel("umlSvg"),
                linkTo(methodOn(ProjectInfoController.class).one(projectInfo.getId())).withRel("projectInfo")
        );
    }
}
