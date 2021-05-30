package org.java2uml.java2umlapi.restControllers.LWControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.lightWeight.Method;
import org.java2uml.java2umlapi.lightWeight.repository.LightWeightRepository;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.modelAssemblers.MethodAssembler;
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
 * Rest Controller for Method entities.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Method", description = "represents method in a source code.")
@RestController
@RequestMapping("/api/method")
public class MethodController {
    private final MethodRepository methodRepository;
    private final MethodAssembler assembler;
    private final LightWeightRepository lightWeightRepository;

    public MethodController(
            MethodRepository methodRepository,
            MethodAssembler assembler,
            LightWeightRepository lightWeightRepository
    ) {
        this.methodRepository = methodRepository;
        this.assembler = assembler;
        this.lightWeightRepository = lightWeightRepository;
    }

    /**
     * Retrieves Method provided method id.
     *
     * @param methodId id associated with method that is going to be retrieved.
     * @return Entity model of method with useful links.
     * @throws LightWeightNotFoundException if method is not found.
     */
    @Operation(summary = "Get Method", description = "get method by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{methodId}")
    public EntityModel<Method> one(@Parameter(description = METHOD_ID_DESC) @PathVariable("methodId") Long methodId) {
        return assembler.toModel(
                methodRepository.findById(methodId)
                        .orElseThrow(
                                () -> new LightWeightNotFoundException("Unable to fetch method with id: " + methodId)
                        )
        );
    }

    /**
     * Retrieves collection of all the methods associated with given parent.
     *
     * @param parentId id of the parent.
     * @return Collection Model of the methods with useful links.
     * @throws LightWeightNotFoundException if parent is not found.
     */
    @Operation(summary = "Get Methods", description = "get methods by parent id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-parent/{parentId}")
    public CollectionModel<EntityModel<Method>> allByParent(@Parameter(description = PARENT_ID_DESC)
                                                            @PathVariable("parentId") Long parentId) {
        var parent = lightWeightRepository.findById(parentId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch parent with id: " + parentId));
        return toCollectionModel(parent);
    }

    /**
     * Converts list of entities to collection model and adds extra links to it.
     *
     * @param parent parent of the entities.
     * @return collection model with added links.
     */
    private CollectionModel<EntityModel<Method>> toCollectionModel(LightWeight parent) {
        return assembler.toCollectionModel(methodRepository.findAllByParent(parent))
                .addIf(
                        parent.asEnumLW().isPresent(),
                        () -> linkTo(methodOn(EnumLWController.class).one(parent.getId())).withRel("parent"))
                .addIf(parent.asClassOrInterface().isPresent(),
                        () -> linkTo(methodOn(ClassOrInterfaceController.class).one(parent.getId())).withRel("parent"));
    }
}
