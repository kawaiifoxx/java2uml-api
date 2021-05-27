package org.java2uml.java2umlapi.restControllers.LWControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.lightWeight.Field;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.lightWeight.repository.FieldRepository;
import org.java2uml.java2umlapi.lightWeight.repository.LightWeightRepository;
import org.java2uml.java2umlapi.modelAssemblers.FieldAssembler;
import org.java2uml.java2umlapi.restControllers.error.ErrorResponse;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
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
 * Rest controller for Field entities.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Field", description = "represents field in a source code.")
@RestController
@RequestMapping("/api/field")
public class FieldController {

    private final FieldRepository fieldRepository;
    private final FieldAssembler assembler;
    private final LightWeightRepository lightWeightRepository;

    public FieldController(
            FieldRepository fieldRepository,
            FieldAssembler assembler,
            LightWeightRepository lightWeightRepository
    ) {
        this.fieldRepository = fieldRepository;
        this.assembler = assembler;
        this.lightWeightRepository = lightWeightRepository;
    }

    /**
     * Retrieves a field with provided id.
     *
     * @param fieldId id of the field associated with the id.
     * @return Entity model of the field with useful links.
     * @throws LightWeightNotFoundException if field is not found.
     */
    @Operation(summary = "Get Field", description = "get field by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{fieldId}")
    public EntityModel<Field> one(@Parameter(description = FIELD_ID_DESC) @PathVariable("fieldId") Long fieldId) {
        return assembler.toModel(
                fieldRepository.findById(fieldId)
                        .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch field by id: " + fieldId))
        );
    }

    /**
     * Retrieves all the fields associated with parent which has provided parentId.
     *
     * @param parentId id of parent
     * @return Collection Model of fields with useful links.
     * @throws LightWeightNotFoundException if paren is not found.
     */
    @Operation(summary = "Get Fields", description = "get fields by parent id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-parent/{parentId}")
    public CollectionModel<EntityModel<Field>> allByParent(@Parameter(description = PARENT_ID_DESC)
                                                           @PathVariable("parentId") Long parentId) {
        var parent = lightWeightRepository.findById(parentId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch parent with id: " + parentId));
        return toCollectionModel(parent);
    }

    /**
     * @param parent of which CollectionModel of fields is to be retrieved
     * @return Collection Model of fields with parent link added.
     */
    private CollectionModel<EntityModel<Field>> toCollectionModel(LightWeight parent) {
        return assembler.toCollectionModel(fieldRepository.findAllByParent(parent))
                .addIf(
                        parent.asClassOrInterface().isPresent(),
                        () -> linkTo(methodOn(ClassOrInterfaceController.class).one(parent.getId())).withRel("parent"))
                .addIf(
                        parent.asEnumLW().isPresent(),
                        () -> linkTo(methodOn(EnumLWController.class).one(parent.getId())).withRel("parent")
                );
    }

}
