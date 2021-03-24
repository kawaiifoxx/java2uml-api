package org.java2uml.java2umlapi.parsedComponent.service;

import org.java2uml.java2umlapi.exceptions.EmptySourceDirectoryException;
import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.java2uml.java2umlapi.parser.Parser;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * A service class for managing source component instances. provides basic crud operations.
 * </p>
 *
 * @author kawaiifox
 */
@Service
public class SourceComponentService {
    private final List<SourceComponent> sourceComponents;

    public SourceComponentService() {
        this.sourceComponents = new ArrayList<>();
    }

    /**
     * Returns a Optional of SourceComponent, Optional is empty
     * if source component has not been found with given index.
     * @param index of source component to be fetched.
     * @return Optional of SourceComponent
     */
    public Optional<SourceComponent> get(int index) {
        if (sourceComponents.size() <= index) {
            return Optional.empty();
        }

        return Optional.of(sourceComponents.get(index));
    }

    /**
     * Saves an optional of SourceComponent and provides id for the saved source component.
     * @param sourceComponent to be saved
     * @return id of the saved source component
     */
    public int save(SourceComponent sourceComponent) {
        int size = sourceComponents.size();
        sourceComponents.add(sourceComponent);
        return size;
    }

    /**
     * tries to generate a source component by parsing files on the provided path.
     * @param path on which source files located for parsing
     * @return id of the saved source component.
     * @throws EmptySourceDirectoryException if source directory does not contain any .java files.
     */
    public int save(Path path) {
        int size = sourceComponents.size();
        sourceComponents.add(Parser.parse(path));
        return size;
    }

    /**
     * Deletes the source component with provided id.
     * @param sourceComponentId id of the source Component.
     */
    public void delete(Integer sourceComponentId) {
        if (sourceComponents.size() > sourceComponentId) {
            sourceComponents.remove(
                    sourceComponents.get(sourceComponentId)
            );
        }
    }
}
