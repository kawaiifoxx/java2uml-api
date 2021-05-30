package org.java2uml.java2umlapi.fileStorage.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for Generated svg files.
 *
 * @author shrey
 */
@Component
public class ClassDiagramSVGService {
    private final Map<Long, String> svgCache;
    /**
     * Contains id of SVGs that need to be deleted.
     * <p>
     * This is added because a delete request may be issued before the
     * svg is created in which case the svg might be added later and it will
     * remain there hogging memory.
     */
    private final Set<Long> toBeDeletedSet;

    public ClassDiagramSVGService() {
        svgCache = new ConcurrentHashMap<>();
        toBeDeletedSet = ConcurrentHashMap.newKeySet();
    }

    /**
     * Returns true if this repo contains a mapping for the specified key.
     * More formally, returns true if and only if this repo contains a mapping
     * for a key k such that Objects.equals(key, k). (There can be at most one such mapping.)
     *
     * @param projectInfoId id whose presence in the repo is to be tested
     * @return true if projectInfoId is present else false.
     */
    public boolean contains(Long projectInfoId) {
        return svgCache.containsKey(projectInfoId);
    }

    /**
     * Returns a svg string if this repo contains a mapping for the specified key.
     * if no such mapping is present returns null.
     *
     * @param projectInfoId key for which svg string is needed
     * @return svg string if key has mapping else null.
     */
    public String get(Long projectInfoId) {
        return svgCache.get(projectInfoId);
    }

    /**
     * Saves given mapping in the repository and returns the saved svgString.
     *
     * @param projectInfoId key for saving svgString.
     * @param svgString     svgString to be saved.
     * @return saved svgString
     */
    public String save(Long projectInfoId, String svgString) {
        if (toBeDeletedSet.contains(projectInfoId)) {
            toBeDeletedSet.remove(projectInfoId);
            return svgString;
        }

        svgCache.put(projectInfoId, svgString);
        return svgString;
    }

    /**
     * Removes mapping form the repository.
     *
     * @param projectInfoId id to be removed from repository.
     */
    public void delete(Long projectInfoId) {
        if (svgCache.remove(projectInfoId) == null) {
            toBeDeletedSet.add(projectInfoId);
        }
    }
}
