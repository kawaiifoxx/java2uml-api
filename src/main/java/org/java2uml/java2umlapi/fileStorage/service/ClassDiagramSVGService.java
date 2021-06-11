package org.java2uml.java2umlapi.fileStorage.service;

import org.springframework.stereotype.Service;

/**
 * Cache for Generated svg files.
 *
 * @author shrey
 */
@Service
public class ClassDiagramSVGService extends CacheService<Long, String> {
}
