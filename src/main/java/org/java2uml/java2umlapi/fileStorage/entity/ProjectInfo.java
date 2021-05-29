package org.java2uml.java2umlapi.fileStorage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.java2uml.java2umlapi.lightWeight.Source;

import javax.persistence.*;

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
    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    private Source source;
    private Long size;
    private String fileType;

    protected ProjectInfo() {
    }

    public ProjectInfo(String projectName, Long size, String fileType) {
        this.projectName = projectName;
        this.size = size;
        this.fileType = fileType;
    }

    /**
     * Setter for project info id.
     *
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
     *
     * @param projectName name of the project.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return source if present else null is returned.
     */
    public Source getSource() {
        return source;
    }

    /**
     * Setter for source
     *
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
     *
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
     *
     * @param fileType type of file.
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
