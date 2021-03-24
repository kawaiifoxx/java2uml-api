package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.lightWeight.EnumConstant;
import org.java2uml.java2umlapi.lightWeight.repository.EnumConstantRepository;
import org.java2uml.java2umlapi.lightWeight.repository.EnumLWRepository;
import org.java2uml.java2umlapi.modelAssemblers.EnumConstantAssembler;
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
 * Rest controller for EnumConstant entities.
 * </p>
 *
 * @author kawaiifox
 */
@RestController
@RequestMapping("/api/enum-constant")
public class EnumConstantController {

    private final EnumConstantRepository enumConstantRepository;
    private final EnumConstantAssembler assembler;
    private final EnumLWRepository enumLWRepository;

    public EnumConstantController(
            EnumConstantRepository enumConstantRepository,
            EnumConstantAssembler assembler,
            EnumLWRepository enumLWRepository
    ) {
        this.enumConstantRepository = enumConstantRepository;
        this.assembler = assembler;
        this.enumLWRepository = enumLWRepository;
    }

    /**
     * Retrieves EnumConstant with provided id.
     * @param enumConstantId id of enum constant to be retrieved.
     * @return Entity model of enum constant with useful links.
     * @throws LightWeightNotFoundException if enum constant is not found.
     */
    @GetMapping("/{enumConstantId}")
    public EntityModel<EnumConstant> one(@PathVariable("enumConstantId") Long enumConstantId) {
        return assembler.toModel(
                enumConstantRepository.findById(enumConstantId)
                        .orElseThrow(() -> new LightWeightNotFoundException(
                                "Unable to fetch enum constant by id: " + enumConstantId
                        ))
        );
    }

    /**
     * Retrieves all the EnumConstants associated with the given parent,
     * whose parent id has been provided.
     * @param parentId id of the parent of enum constants.
     * @return Collection model of EnumConstant with useful links.
     */
    @GetMapping("/by-parent/{parentId}")
    public CollectionModel<EntityModel<EnumConstant>> allByParent(@PathVariable("parentId") Long parentId) {
        var parent = enumLWRepository.findById(parentId)
                .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch parent by id: " + parentId));
        return assembler.toCollectionModel(enumConstantRepository.findEnumConstantByEnumLW(parent))
                .add(linkTo(methodOn(EnumLWController.class).one(parentId)).withRel("parent"));
    }
}
