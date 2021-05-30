package org.java2uml.java2umlapi.restControllers.LWControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.repository.EnumLWRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.modelAssemblers.EnumLWAssembler;
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
 * Rest Controller for the EnumLW entities.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Enum", description = "represents enum in a source code.")
@RestController
@RequestMapping("/api/enum")
public class EnumLWController {
    private final EnumLWRepository enumLWRepository;
    private final EnumLWAssembler assembler;
    private final SourceRepository sourceRepository;

    public EnumLWController(
            EnumLWRepository enumLWRepository,
            EnumLWAssembler assembler,
            SourceRepository sourceRepository
    ) {
        this.enumLWRepository = enumLWRepository;
        this.assembler = assembler;
        this.sourceRepository = sourceRepository;
    }

    /**
     * Retrieves a enumLW with provided enumId.
     *
     * @param enumId id associated with enumLW that is going to be retrieved.
     * @return Entity Model of enumLW with useful links.
     * @throws LightWeightNotFoundException if enumLW is not found.
     */
    @Operation(summary = "Get Enum", description = "get enum by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{enumId}")
    public EntityModel<EnumLW> one(@Parameter(description = ENUM_ID_DESC) @PathVariable("enumId") Long enumId) {
        var enumLW = enumLWRepository.findById(enumId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch enum with id " + enumId));

        return assembler.toModel(enumLW);
    }

    /**
     * Retrieves all the enumLW associated with the specified source.
     *
     * @param sourceId id of source for which all enumLWs is going to be retrieved.
     * @return CollectionModel of enumLW with useful links.
     * @throws LightWeightNotFoundException if parent is not found.
     */
    @Operation(summary = "Get Enums", description = "get enums by source id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-source/{sourceId}")
    public CollectionModel<EntityModel<EnumLW>> allBySource(@Parameter(name = SOURCE_ID_DESC)
                                                            @PathVariable("sourceId") Long sourceId) {
        var enumLWList = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch source with id " + sourceId))
                .getEnumLWList();

        return assembler.toCollectionModel(enumLWList)
                .add(linkTo(methodOn(SourceController.class).one(sourceId)).withRel("parent"));
    }
}
