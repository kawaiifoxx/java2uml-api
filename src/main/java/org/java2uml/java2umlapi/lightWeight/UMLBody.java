package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    private Long projectInfoId;

    public UMLBody(String content, Long projectInfoId) {
        this.content = content;
        this.projectInfoId = projectInfoId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public Long getProjectInfoId() {
        return projectInfoId;
    }

    public void setProjectInfoId(Long projectInfoId) {
        this.projectInfoId = projectInfoId;
    }
}
