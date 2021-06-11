package org.java2uml.java2umlapi.lightWeight.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * This service stores mapping for projectInfo id's to all method signatures present in that project.
 * </p>
 *
 * @author kawaiifox
 */
@Service
public class MethodSignatureToMethodIdMapService {
    private final Map<Long, Map<String, Long>> projectInfoIdMap;

    public MethodSignatureToMethodIdMapService() {
        this.projectInfoIdMap = new HashMap<>();
    }

    /**
     * Adds given map with given projectInfo id.
     *
     * @param projectInfoId id associated with the projectInfo.
     * @param map      map of method signatures to ids.
     */
    public void save(Long projectInfoId, Map<String, Long> map) {
        projectInfoIdMap.put(projectInfoId, map);
    }

    /**
     * @param projectInfoId id for which method signature to method id map is required.
     * @return a map with mapping from method signature to method id or null if map does not exist.
     */
    public Optional<Map<String, Long>> findById(Long projectInfoId) {
        return projectInfoIdMap.containsKey(projectInfoId) ? Optional.of(projectInfoIdMap.get(projectInfoId)) : Optional.empty();
    }

    /**
     * Deletes the map with given projectInfo id.
     *
     * @param projectInfo id associated with the project info.
     */
    public void delete(Long projectInfo) {
        projectInfoIdMap.remove(projectInfo);
    }
}
