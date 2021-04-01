package org.java2uml.java2umlapi.lightWeight.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * This service stores mapping for source id's to all method signatures in that source with there ids
 * </p>
 *
 * @author kawaiifox
 */
@Service
public class MethodSignatureToMethodIdMapService {
    private final Map<Long, Map<String, Long>> sourceIdMap;

    public MethodSignatureToMethodIdMapService() {
        this.sourceIdMap = new HashMap<>();
    }

    /**
     * Adds given map with given source id.
     *
     * @param sourceId id associated with the source.
     * @param map      map of method signatures to ids.
     */
    public void save(Long sourceId, Map<String, Long> map) {
        sourceIdMap.put(sourceId, map);
    }

    /**
     * @param sourceId id for which method signature to method id map is required.
     * @return a map with mapping from method signature to method id or null if map does not exist.
     */
    public Optional<Map<String, Long>> findById(Long sourceId) {
        return sourceIdMap.containsKey(sourceId) ? Optional.of(sourceIdMap.get(sourceId)) : Optional.empty();
    }

    /**
     * Deletes the map with given source id.
     *
     * @param sourceId id associated with the project info.
     */
    public void delete(Long sourceId) {
        sourceIdMap.remove(sourceId);
    }
}
