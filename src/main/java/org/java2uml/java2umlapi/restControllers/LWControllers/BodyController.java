package org.java2uml.java2umlapi.restControllers.LWControllers;

import org.java2uml.java2umlapi.lightWeight.Body;
import org.java2uml.java2umlapi.lightWeight.repository.BodyRepository;
import org.java2uml.java2umlapi.lightWeight.repository.LightWeightRepository;
import org.java2uml.java2umlapi.modelAssemblers.BodyAssembler;
import org.java2uml.java2umlapi.restControllers.exceptions.LightWeightNotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Rest Controller for accessing body entities.
 * </p>
 *
 * @author kawaiifox
 */
@RestController
@RequestMapping("/api/body")
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
     * @param bodyId id of the body to be retrieved.
     * @return entity model of body with useful links.
     * @throws LightWeightNotFoundException if body is not found.
     */
    @GetMapping("/{bodyId}")
    public EntityModel<Body> one(@PathVariable("bodyId") Long bodyId) {
        return assembler.toModel(
                bodyRepository.findById(bodyId)
                        .orElseThrow(() -> new LightWeightNotFoundException("Body not found with id: " + bodyId))
        );
    }

    /**
     * Retrieves body with provided parent id.
     * @param parentId id of parent of body.
     * @return entity model of body with useful links.
     * @throws LightWeightNotFoundException if either parent or body is not found.
     */
    @GetMapping("/by-parent/{parentId}")
    public EntityModel<Body> bodyByParentId(@PathVariable("parentId") Long parentId) {
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
