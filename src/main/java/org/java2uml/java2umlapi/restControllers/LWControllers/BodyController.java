package org.java2uml.java2umlapi.restControllers.LWControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.repository.BodyRepository;
import org.java2uml.java2umlapi.lightWeight.repository.LightWeightRepository;
import org.java2uml.java2umlapi.modelAssemblers.BodyAssembler;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.restControllers.response.ErrorResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.*;

/**
 * <p>
 * Rest Controller for accessing body entities.
 * </p>
 *
 * @author kawaiifox
 */
@RestController
@RequestMapping("/api/body")
@Tag(name = "Code Snippet", description = "Represents a body of a function/class/interface.")
public class BodyController {
    private final BodyRepository bodyRepository;
    private final BodyAssembler assembler;
    private final LightWeightRepository lightWeightRepository;

    public BodyController(
            BodyRepository bodyRepository,
            BodyAssembler assembler,
            LightWeightRepository lightWeightRepository
    ) {
        this.bodyRepository = bodyRepository;
        this.assembler = assembler;
        this.lightWeightRepository = lightWeightRepository;
    }

    /**
     * Retrieves body with given id.
     *
     * @param bodyId id of the body to be retrieved.
     * @return entity model of body with useful links.
     * @throws LightWeightNotFoundException if body is not found.
     */
    @Operation(summary = "Get Code Snippet", description = "get code snippet by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{bodyId}")
    public EntityModel<Body> one(
            @Parameter(description = BODY_ID_DESC) @PathVariable("bodyId") Long bodyId) {
        return assembler.toModel(
                bodyRepository.findById(bodyId)
                        .orElseThrow(() -> new LightWeightNotFoundException("Body not found with id: " + bodyId))
        );
    }

    /**
     * Retrieves body with provided parent id.
     *
     * @param parentId id of parent of body.
     * @return entity model of body with useful links.
     * @throws LightWeightNotFoundException if either parent or body is not found.
     */
    @Operation(summary = "Get Code Snippet", description = "get code snippet by parent's id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-parent/{parentId}")
    public EntityModel<Body> bodyByParentId(
            @Parameter(description = PARENT_ID_DESC) @PathVariable("parentId") Long parentId) {
        var parent = lightWeightRepository.findById(parentId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch parent with id: " + parentId));

        return assembler.toModel(
                bodyRepository.findByParent(parent)
                        .orElseThrow(
                                () -> new LightWeightNotFoundException("Body does not exist for parent id:" + parentId)
                        )
        );
    }
}
