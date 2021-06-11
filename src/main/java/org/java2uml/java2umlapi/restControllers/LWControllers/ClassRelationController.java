package org.java2uml.java2umlapi.restControllers.LWControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.lightWeight.ClassRelation;
import org.java2uml.java2umlapi.lightWeight.repository.ClassRelationRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.modelAssemblers.ClassRelationAssembler;
import org.java2uml.java2umlapi.restControllers.exceptions.ClassRelationNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.restControllers.response.ErrorResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Rest Controller for Class Relation entities.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Class Relation", description = "ClassRelation defines Relations b/w different classes.")
@RestController
@RequestMapping("/api/relation")
public class ClassRelationController {

    private final ClassRelationRepository classRelationRepository;
    private final ClassRelationAssembler assembler;
    private final SourceRepository sourceRepository;

    public ClassRelationController(
            ClassRelationRepository classRelationRepository,
            ClassRelationAssembler assembler,
            SourceRepository sourceRepository
    ) {
        this.classRelationRepository = classRelationRepository;
        this.assembler = assembler;
        this.sourceRepository = sourceRepository;
    }

    /**
     * Retrieves class relation entity with the provided id.
     *
     * @param classRelationId id associated with the class relation entity
     * @return Entity model of class relation entity with useful links.
     * @throws ClassRelationNotFoundException if the queried class relation entity cannot be found.
     */
    @Operation(summary = "Get Class Relation", description = "get class relation by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{classRelationId}")
    public EntityModel<ClassRelation> one(
            @Parameter(description = CLASS_REL_ID_DESC) @PathVariable("classRelationId") Long classRelationId) {
        var classRelation = classRelationRepository.findById(classRelationId)
                .orElseThrow(() ->
                        new ClassRelationNotFoundException("Unable to fetch relation with id " + classRelationId));

        return assembler.toModel(classRelation);
    }

    /**
     * Retrieves all the class relation entities associated with a source.
     *
     * @param sourceId id of the source.
     * @return Collection model of ClassRelation entities with useful links.
     */
    @Operation(summary = "Get Class Relations", description = "get class relation by source id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-source/{sourceId}")
    public CollectionModel<EntityModel<ClassRelation>> allBySource(
            @Parameter(description = SOURCE_ID_DESC) @PathVariable("sourceId") Long sourceId) {
        var source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch source with id " + sourceId));
        return assembler.toCollectionModel(source.getClassRelationList())
                .add(linkTo(methodOn(SourceController.class).one(sourceId)).withRel("parent"));
    }
}
