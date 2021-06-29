package org.java2uml.java2umlapi.fileStorage.service;

import org.java2uml.java2umlapi.util.cache.CacheService;
import org.springframework.stereotype.Service;

/**
 * Cache for Generated svg files.
 *
 * @author shrey
 */
@Service
public class ClassDiagramSVGService extends CacheService<Long, String> {
}
