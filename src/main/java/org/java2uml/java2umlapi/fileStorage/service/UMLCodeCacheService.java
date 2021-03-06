package org.java2uml.java2umlapi.fileStorage.service;

import org.java2uml.java2umlapi.util.cache.AddOnceCacheService;
import org.springframework.stereotype.Service;

/**
 * Service for uml code cache.
 *
 * @author kawaiifox
 */
@Service
public class UMLCodeCacheService extends AddOnceCacheService<Long, String> {
}
