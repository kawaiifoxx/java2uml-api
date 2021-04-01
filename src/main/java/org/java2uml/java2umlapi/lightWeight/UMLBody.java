package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;

/**
 * <p>
 * An entity class for representing generated uml for a project.
 * </p>
 *
 * @author kawaiifox
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UMLBody {
    private String content;
    @JsonIgnore
    private ProjectInfo projectInfo;

    public UMLBody(String content, ProjectInfo projectInfo) {
        this.content = content;
        this.projectInfo = projectInfo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }
}
