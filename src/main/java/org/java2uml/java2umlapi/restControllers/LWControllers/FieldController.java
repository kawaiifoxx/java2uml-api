package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.lightWeight.Field;
import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.lightWeight.repository.FieldRepository;
import org.java2uml.java2umlapi.lightWeight.repository.LightWeightRepository;
import org.java2uml.java2umlapi.modelAssemblers.FieldAssembler;
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
 * Rest controller for Field entities.
 * </p>
 *
 * @author kawaiifox
 */
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
     * @param fieldId id of the field associated with the id.
     * @return Entity model of the field with useful links.
     * @throws LightWeightNotFoundException if field is not found.
     */
    @GetMapping("/{fieldId}")
    public EntityModel<Field> one(@PathVariable("fieldId") Long fieldId) {
        return assembler.toModel(
                fieldRepository.findById(fieldId)
                        .orElseThrow(() -> new LightWeightNotFoundException("Unable to fetch field by id: " + fieldId))
        );
    }

    /**
     * Retrieves all the fields associated with parent which has provided parentId.
     * @param parentId id of parent
     * @return Collection Model of fields with useful links.
     * @throws LightWeightNotFoundException if paren is not found.
     */
    @GetMapping("/by-parent/{parentId}")
    public CollectionModel<EntityModel<Field>> allByParent(@PathVariable("parentId") Long parentId) {
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
