package org.java2uml.java2umlapi.restControllers.LWControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.java2uml.java2umlapi.lightWeight.ClassOrInterface;
import org.java2uml.java2umlapi.lightWeight.repository.ClassOrInterfaceRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.modelAssemblers.ClassOrInterfaceAssembler;
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
 * Rest Controller for ClassOrInterface entities.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Class Or Interface", description = "class or interface represents class or interface in your source code.")
@RestController
@RequestMapping("/api/class-or-interface")
public class ClassOrInterfaceController {
    private final ClassOrInterfaceRepository classOrInterfaceRepository;
    private final ClassOrInterfaceAssembler assembler;
    private final SourceRepository sourceRepository;

    public ClassOrInterfaceController(
            ClassOrInterfaceRepository classOrInterfaceRepository,
            ClassOrInterfaceAssembler assembler,
            SourceRepository sourceRepository
    ) {
        this.classOrInterfaceRepository = classOrInterfaceRepository;
        this.assembler = assembler;
        this.sourceRepository = sourceRepository;
    }


    /**
     * Retrieves ClassOrInterface entity with provided classId
     *
     * @param classId id associated with ClassOrInterface entity you want retrieve.
     * @return Entity model of ClassOrInterface with useful links.
     * @throws LightWeightNotFoundException if no ClassOrInterface with provided classId is found
     */
    @Operation(summary = "Get Class Or Interface", description = "get class or interface by class id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{classId}")
    public EntityModel<ClassOrInterface> one(@Parameter(description = CLASS_ID_DESC) @PathVariable("classId") Long classId) {
        var classOrInterface = classOrInterfaceRepository.findById(classId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch class with id " + classId));
        return assembler.toModel(classOrInterface);
    }

    /**
     * Retrieves ClassOrInterface entities associated with provided source id.
     *
     * @param sourceId id of source of which you want to retrieve all the classes.
     * @return Collection model of ClassOrInterface entities with useful links.
     * @throws LightWeightNotFoundException if source with provided id is not found
     */
    @Operation(summary = "Get All Class(es) Or Interface(s)", description = "get class(es) or interface(s) by source id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = OK_200_RESPONSE),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_404,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/by-source/{sourceId}")
    public CollectionModel<EntityModel<ClassOrInterface>> allBySource(
            @Parameter(description = SOURCE_ID_DESC) @PathVariable("sourceId") Long sourceId) {
        var source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch source with id " + sourceId));

        var classOrInterfaceList = source.getClassOrInterfaceList();

        return assembler.toCollectionModel(classOrInterfaceList)
                .add(linkTo(methodOn(SourceController.class).one(sourceId)).withRel("parent"));
    }
}
