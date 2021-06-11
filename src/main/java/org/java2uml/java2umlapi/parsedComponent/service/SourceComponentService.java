package org.java2uml.java2umlapi.parsedComponent.service;

import org.java2uml.java2umlapi.exceptions.EmptySourceDirectoryException;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parser.Parser;
import org.java2uml.java2umlapi.restControllers.exceptions.BadRequest;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * A service class for managing source component instances. provides basic crud operations.
 * </p>
 *
 * @author kawaiifox
 */
@Service
public class SourceComponentService {
    private final Map<Long, SourceComponent> sourceComponents;
    private final Set<Long> toBeDeleted;

    public SourceComponentService() {
        this.sourceComponents = new ConcurrentHashMap<>();
        this.toBeDeleted = ConcurrentHashMap.newKeySet();
    }

    /**
     * Returns a Optional of SourceComponent, Optional is empty
     * if source component has not been found with given index.
     *
     * @param index of source component to be fetched.
     * @return Optional of SourceComponent
     */
    public Optional<SourceComponent> get(Long index) {
        if (!sourceComponents.containsKey(index)) {
            return Optional.empty();
        }

        return Optional.of(sourceComponents.get(index));
    }

    /**
     * Saves a SourceComponent with provided id.
     *
     * @param sourceComponent to be saved
     * @param projectInfoId   id of corresponding {@link org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo}
     */
    public void save(Long projectInfoId, SourceComponent sourceComponent) {
        if (toBeDeleted.contains(projectInfoId)) {
            toBeDeleted.remove(projectInfoId);
            return;
        }

        sourceComponents.put(projectInfoId, sourceComponent);
    }

    /**
     * tries to generate a source component by parsing files on the provided path.
     *
     * @param path          on which source files located for parsing
     * @param projectInfoId id of corresponding {@link org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo}
     * @throws BadRequest if source directory does not contain any .java files.
     */
    public void save(Long projectInfoId, Path path) {
        if (toBeDeleted.contains(projectInfoId)) {
            toBeDeleted.remove(projectInfoId);
            return;
        }

        try {
            sourceComponents.put(projectInfoId, Parser.parse(path));
        } catch (EmptySourceDirectoryException exception) {
            throw new BadRequest(exception.getMessage(), exception);
        }
    }

    /**
     * Deletes the source component with provided id.
     *
     * @param projectInfoId id of the project info for which you want to delete the source component.
     */
    public void delete(Long projectInfoId) {
        if (sourceComponents.remove(projectInfoId) == null)
            toBeDeleted.add(projectInfoId);
    }
}
