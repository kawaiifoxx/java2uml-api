package org.java2uml.java2umlapi.modelAssemblers;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.restControllers.EventSubscriptionController;
import org.java2uml.java2umlapi.restControllers.LWControllers.SourceController;
import org.java2uml.java2umlapi.restControllers.ProjectInfoController;
import org.java2uml.java2umlapi.restControllers.UMLController;
import org.java2uml.java2umlapi.restControllers.dependencyMatrix.DependencyMatrixController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
                linkTo(methodOn(ProjectInfoController.class).one(entity.getId())).withSelfRel(),
                linkTo(methodOn(ProjectInfoController.class).delete(entity.getId())).withRel("delete"),
                linkTo(methodOn(UMLController.class).getPUMLCode(entity.getId())).withRel("umlText"),
                linkTo(methodOn(UMLController.class).getSvg(entity.getId())).withRel("umlSvg"),
                linkTo(methodOn(DependencyMatrixController.class).get(entity.getId())).withRel("dependencyMatrix"),
                linkTo(methodOn(SourceController.class).findByProjectId(entity.getId())).withRel("projectModel"),
                linkTo(methodOn(EventSubscriptionController.class).subscribeToParseEvent(entity.getId())).withRel("Subscribe to parse event"),
                linkTo(methodOn(EventSubscriptionController.class).subscribeToSourceGeneration(entity.getId())).withRel("Subscribe to source generation"),
                linkTo(methodOn(EventSubscriptionController.class).subscribeToUMLSVGGenerationEvent(entity.getId())).withRel("Subscribe to uml svg generation event"),
                linkTo(methodOn(EventSubscriptionController.class).subscribeToUMLCodeGenerationEvent(entity.getId())).withRel("Subscribe to uml code generation event"),
                linkTo(methodOn(EventSubscriptionController.class).subscribeToDependencyMatrixGeneration(entity.getId())).withRel("Subscribe to dependency matrix generation")
        );
    }
}
