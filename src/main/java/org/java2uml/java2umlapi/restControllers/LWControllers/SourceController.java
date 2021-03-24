package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.java2uml.java2umlapi.fileStorage.repository.ProjectInfoRepository;
import org.java2uml.java2umlapi.lightWeight.Source;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.lightWeight.service.MethodSignatureToMethodIdMapService;
import org.java2uml.java2umlapi.modelAssemblers.SourceAssembler;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parsedComponent.service.SourceComponentService;
import org.java2uml.java2umlapi.restControllers.exceptions.CannotGenerateSourceException;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.ProjectInfoNotFoundException;
import org.java2uml.java2umlapi.restControllers.exceptions.SourceComponentNotFoundException;
import org.java2uml.java2umlapi.visitors.lightWeightExtractor.specialized.LightWeightExtractorWithMethodSignatureCache;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Rest Controller for source entities.
 * </p>
 *
 * @author kawaiifox.
 */
@RestController
@RequestMapping("/api/source")
public class SourceController {
    private final SourceRepository sourceRepository;
    private final SourceAssembler assembler;
    private final ProjectInfoRepository projectInfoRepository;
    private final MethodRepository methodRepository;
    private final SourceComponentService sourceComponentService;
    private final MethodSignatureToMethodIdMapService methodIdMapService;

    public SourceController(
            SourceRepository sourceRepository,
            SourceAssembler assembler,
            ProjectInfoRepository projectInfoRepository,
            MethodRepository methodRepository,
            SourceComponentService sourceComponentService,
            MethodSignatureToMethodIdMapService methodIdMapService
    ) {
        this.sourceRepository = sourceRepository;
        this.assembler = assembler;
        this.projectInfoRepository = projectInfoRepository;
        this.methodRepository = methodRepository;
        this.sourceComponentService = sourceComponentService;
        this.methodIdMapService = methodIdMapService;
    }

    /**
     * Retrieves source with provided id.
     * @param sourceId id of source to be retrieved.
     * @return EntityModel of source with useful links.
     * @throws LightWeightNotFoundException if source is not found.
     */
    @GetMapping("/{sourceId}")
    public EntityModel<Source> one(@PathVariable("sourceId") Long sourceId) {
        return assembler.toModel(
                sourceRepository.findById(sourceId)
                        .orElseThrow(
                                () -> new LightWeightNotFoundException(
                                        "Unable to fetch source with id " + sourceId
                                )
                        )
        );
    }

    /**
     * Retrieves source by project info id if present, else this method tries to generate source.
     * @param projectInfoId id of project info
     * @return EntityModel of source with useful links.
     * @throws ProjectInfoNotFoundException if project info is not found.
     * @throws CannotGenerateSourceException if source cannot be generated.
     * @throws SourceComponentNotFoundException if source component is not found.
     */
    @GetMapping("/by-project-info/{projectInfoId}")
    public EntityModel<Source> findByProjectId(@PathVariable("projectInfoId") Long projectInfoId) {
        var projectInfo = projectInfoRepository.findById(projectInfoId)
                .orElseThrow(() -> new ProjectInfoNotFoundException("The information about file you were looking " +
                        "for is not present. please consider, uploading the given file again."));
        return assembler.toModel(getSource(projectInfo));
    }

    /**
     * if source has not been generated then this method generates source and returns it.
     * This method also updates projectInfo with the generated source, if originally source was not present.
     *
     * @param projectInfo for which source is needed.
     * @return source
     */
    private Source getSource(ProjectInfo projectInfo) {
        //Extract source if not present.
        if (projectInfo.getSource() == null) {
            extractSource(projectInfo);
        }
        return projectInfo.getSource();
    }

    /**
     * Extracts source from source component and if source is successfully extracted.<br>
     * Sets source in projectInfo.<br>
     * Saves projectInfo.<br>
     * Saves methodIdMap in methodIdMapService.
     *
     * @param projectInfo in which source will be set.
     */
    private void extractSource(ProjectInfo projectInfo) {
        SourceComponent sourceComponent = getSourceComponent(projectInfo);
        var extractor = new LightWeightExtractorWithMethodSignatureCache(methodRepository);
        projectInfo.setSource(
                sourceRepository.save(
                        sourceComponent.accept(extractor)
                                .asSource()
                                .orElseThrow(
                                        () -> new CannotGenerateSourceException(
                                                "Unable to fetch source," + " Please try again later."
                                        )
                                )
                )
        );
        var source = projectInfo.getSource();
        source.setProjectInfo(projectInfo);
        projectInfoRepository.save(projectInfo);
        methodIdMapService.save(source.getId(), extractor.getSignatureToIdMap());
    }

    /**
     * @param projectInfo for which source component needs to be fetched.
     * @return source component
     * @throws SourceComponentNotFoundException if source component is not found.
     */
    private SourceComponent getSourceComponent(ProjectInfo projectInfo) throws SourceComponentNotFoundException {
        return sourceComponentService.get(projectInfo.getSourceComponentId())
                .orElseThrow(() -> new SourceComponentNotFoundException("Unable to fetch source component," +
                        " uploading files again should fix this."));
    }
}
