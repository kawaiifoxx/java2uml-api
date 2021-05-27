package org.java2uml.java2umlapi.restControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.UMLBody;
import org.java2uml.java2umlapi.modelAssemblers.UMLBodyAssembler;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.error.ErrorResponse;
import org.java2uml.java2umlapi.restControllers.exceptions.CannotGenerateSVGException;
import org.java2uml.java2umlapi.restControllers.exceptions.ParsedComponentNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.java2uml.java2umlapi.visitors.umlExtractor.UMLExtractor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.java2uml.java2umlapi.restControllers.SwaggerDescription.*;

/**
 * <p>
 * This controller handles all the requests having path "/api/uml".<br>
 * This controller performs generation of uml code and uml svg.
 * </p>
 *
 * @author kawaiifox
 */
@Tag(name = "Generate Class Diagrams", description = "generate plant uml code and plant uml class diagrams," +
        " Note: pls upload the project files first then use any get requests.")
@RestController
@RequestMapping("/api/uml")
public class UMLController {
    private final UMLBodyAssembler umlBodyAssembler;
    private final ProjectInfoRepository projectInfoRepository;
    private final SourceComponentService sourceComponentService;

    public UMLController(
            UMLBodyAssembler umlBodyAssembler,
            ProjectInfoRepository projectInfoRepository,
            SourceComponentService sourceComponentService
    ) {
        this.umlBodyAssembler = umlBodyAssembler;
        this.projectInfoRepository = projectInfoRepository;
        this.sourceComponentService = sourceComponentService;
    }


    /**
     * This method defines get mapping for "/api/uml/plant-uml-code/{projectInfoId}"<br>
     * uml code is generated and then {@link EntityModel} containing uml code and useful links is returned.
     *
     * @param projectInfoId id of {@link ProjectInfo}
     * @return {@link EntityModel} of {@link UMLBody}
     * @throws ProjectInfoNotFoundException     if {@link ProjectInfo} is not found.
     * @throws ParsedComponentNotFoundException if {@link SourceComponent} is not found.
     */
    @Operation(summary = "Generate Plant UML.",
            description = "generate plant uml code from uploaded java source code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generation Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = SOURCE_CODE_NOT_FOUND_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/plant-uml-code/{projectInfoId}")
    public EntityModel<UMLBody> getPUMLCode(
            @Parameter(description = PROJECT_ID_DESC) @PathVariable Long projectInfoId
    ) {
        var projectInfo = projectInfoRepository.findById(projectInfoId)
                .orElseThrow(() -> new ProjectInfoNotFoundException("file with id " + projectInfoId +
                        "does not exist, please try uploading the given file again."));

        var sourceComponent = sourceComponentService.get(projectInfo.getSourceComponentId()).
                orElseThrow(
                        () -> new ParsedComponentNotFoundException("Please, upload your file again.")
                );

        return umlBodyAssembler.toModel(new UMLBody(sourceComponent.accept(new UMLExtractor()), projectInfo));
    }

    /**
     * This method defines get mapping for "/api/uml/svg/{projectInfoId}"<br>
     * Svg is generated from plant uml code and {@link ResponseEntity} containing this svg is returned
     * content type "image/svg+xml"
     *
     * @param projectInfoId id of the {@link ProjectInfo}
     * @return UML in form of SVG
     * @throws ParsedComponentNotFoundException if {@link SourceComponent} is not found.
     * @throws CannotGenerateSVGException       if Svg cannot be generated.
     */
    @Operation(summary = "Generate Class Diagram",
            description = "generate plant uml class diagram svg from uploaded java source code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Generation Successful"),
            @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = SOURCE_CODE_NOT_FOUND_DESC,
                    content = @Content(mediaType = ERR_RESPONSE_MEDIA_TYPE,
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/svg/{projectInfoId}", produces = {"image/svg+xml"})
    public ResponseEntity<String> getSvg(
            @Parameter(description = PROJECT_ID_DESC) @PathVariable Long projectInfoId
    ) {
        var projectInfo = projectInfoRepository.findById(projectInfoId)
                .orElseThrow(() -> new ProjectInfoNotFoundException("The information about file you were looking " +
                        "for is not present. please consider, uploading the given file again."));

        var sourceComponent = sourceComponentService.get(projectInfo.getSourceComponentId()).
                orElseThrow(
                        () -> new ParsedComponentNotFoundException("Please, upload your file again.")
                );

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/svg+xml"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\"" + getFileName(projectInfo) + "\""
                ).body(generateSVG(sourceComponent.accept(new UMLExtractor())));
    }

    /**
     * Generates svg from given plant uml code.
     *
     * @param uml String of plant uml code.
     * @return Generated svg
     * @throws CannotGenerateSVGException if svg cannot be generated.
     */
    private String generateSVG(String uml) {
        final ByteArrayOutputStream os;
        try {
            var reader = new SourceStringReader(uml);
            os = new ByteArrayOutputStream();
            //noinspection deprecation
            reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
            os.close();
        } catch (NullPointerException | IOException exception) {
            throw new CannotGenerateSVGException(
                    "Server encountered some error while generating svg, please try again later.",
                    exception
            );
        }

        return os.toString();
    }

    /**
     * Checks if .zip is present in project name if it is present then this method replaces it with .svg.
     * otherwise appends .svg to the project name.
     *
     * @param projectInfo {@link ProjectInfo} for which svg is being generated.
     * @return file name for generated svg.
     */
    private String getFileName(ProjectInfo projectInfo) {
        var fileName = projectInfo.getProjectName();
        if (fileName.contains(".zip")) {
            fileName = fileName.replace(".zip", ".svg");
        } else {
            fileName = fileName + ".svg";
        }
        return fileName;
    }

}
