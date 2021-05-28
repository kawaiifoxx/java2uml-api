package org.java2uml.java2umlapi.fileStorage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.java2uml.java2umlapi.lightWeight.Source;

import javax.persistence.*;
import java.util.List;

/**
 * <p>
 * An entity class for representing a project. This class also contains essential information about the project.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class ProjectInfo {
    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
    private String projectName;
    @JsonIgnore
    private String unzippedFileName;
    @JsonIgnore
    private Integer sourceComponentId;
    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Source source;
    private Long size;
    private String fileType;
    @ElementCollection
    private List<String> suggestions;

    protected ProjectInfo() {
    }

    public ProjectInfo(
            String unzippedFileName,
            String projectName,
            Long size, String fileType,
            Integer sourceComponentId
    ) {
        this.unzippedFileName = unzippedFileName;
        this.projectName = projectName;
        this.size = size;
        this.fileType = fileType;
        this.sourceComponentId = sourceComponentId;
    }

    /**
     * Setter for project info id.
     * @param id id of the project info.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return id associated with the project.
     */
    public Long getId() {
        return id;
    }

    /**
     * @return name of the project.
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Setter for name of the project.
     * @param projectName name of the project.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return source component id.
     */
    public Integer getSourceComponentId() {
        return sourceComponentId;
    }

    /**
     * setter for source component id.
     * @param sourceComponentId source component id.
     */
    public void setSourceComponentId(Integer sourceComponentId) {
        this.sourceComponentId = sourceComponentId;
    }

    /**
     * @return source if present else null is returned.
     */
    public Source getSource() {
        return source;
    }

    /**
     * Setter for source
     * @param source source
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * @return file size.
     */
    public Long getSize() {
        return size;
    }

    /**
     * Setter for file size.
     * @param size file size.
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return type of file.
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * setter for file type
     * @param fileType type of file.
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return unzipped file name which has unique uuid.
     */
    public String getUnzippedFileName() {
        return unzippedFileName;
    }

    /**
     * @param unzippedFileName setter for unzipped file name.
     */
    public void setUnzippedFileName(String unzippedFileName) {
        this.unzippedFileName = unzippedFileName;
    }

    /**
     * @return suggestions if any or null otherwise.
     */
    public List<String> getSuggestions() {
        return suggestions;
    }

    /**
     * @param suggestions setter for suggestions
     */
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
