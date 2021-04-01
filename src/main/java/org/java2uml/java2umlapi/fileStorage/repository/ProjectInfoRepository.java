package org.java2uml.java2umlapi.fileStorage.repository;

import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;
import org.springframework.data.repository.CrudRepository;

/**
 * <p>
 * Spring Data crud repository for crud operations on project info entity.
 * </p>
 *
 * @author kawaiifox
 */
public interface ProjectInfoRepository extends CrudRepository<ProjectInfo, Long> {
    ProjectInfo findByProjectName(String projectName);
}
