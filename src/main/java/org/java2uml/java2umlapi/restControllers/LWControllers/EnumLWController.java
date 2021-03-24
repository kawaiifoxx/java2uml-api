package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.lightWeight.EnumLW;
import org.java2uml.java2umlapi.lightWeight.repository.EnumLWRepository;
import org.java2uml.java2umlapi.lightWeight.repository.SourceRepository;
import org.java2uml.java2umlapi.modelAssemblers.EnumLWAssembler;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>
 * Rest Controller for the EnumLW entities.
 * </p>
 *
 * @author kawaiifox
 */
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
     * @param enumId id associated with enumLW that is going to be retrieved.
     * @return Entity Model of enumLW with useful links.
     * @throws LightWeightNotFoundException if enumLW is not found.
     */
    @GetMapping("/{enumId}")
    public EntityModel<EnumLW> one(@PathVariable("enumId") Long enumId) {
        var enumLW = enumLWRepository.findById(enumId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch enum with id " + enumId));

        return assembler.toModel(enumLW);
    }

    /**
     * Retrieves all the enumLW associated with the specified source.
     * @param sourceId id of source for which all enumLWs is going to be retrieved.
     * @return CollectionModel of enumLW with useful links.
     * @throws LightWeightNotFoundException if parent is not found.
     */
    @GetMapping("/by-source/{sourceId}")
    public CollectionModel<EntityModel<EnumLW>> allBySource(@PathVariable("sourceId") Long sourceId) {
        var enumLWList = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch source with id " + sourceId))
                .getEnumLWList();

        return assembler.toCollectionModel(enumLWList)
                .add(linkTo(methodOn(SourceController.class).one(sourceId)).withRel("parent"));
    }
}
