package org.java2uml.java2umlapi.fileStorage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.java2uml.java2umlapi.lightWeight.Source;

import javax.persistence.*;
import java.util.List;
import java.util.Vector;

import static javax.persistence.CascadeType.*;

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
    private Long id;
    private String projectName;
    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = {DETACH, MERGE, REFRESH, REMOVE})
    private Source source;
    private Long size;
    private String fileType;
    @ElementCollection
    private List<String> messages;
    volatile private boolean isBadRequest;
    volatile private boolean isParsed;

    protected ProjectInfo() {
    }

    public ProjectInfo(String projectName, Long size, String fileType) {
        this.projectName = projectName;
        this.size = size;
        this.fileType = fileType;
        this.messages = new Vector<>();
        this.isBadRequest = false;
        this.isParsed = false;
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
    synchronized public Source getSource() {
        return source;
    }

    /**
     * Setter for source
     *
     * @param source source
     */
    synchronized public void setSource(Source source) {
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

    /**
     * Getter for messages.
     *
     * @return messages
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Setter for messages.
     *
     * @param messages List of messages.
     */
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    /**
     * Add message to this {@link ProjectInfo} instance.
     *
     * @param message message to be added.
     */
    public void addMessage(String message) {
        messages.add(message);
    }

    public boolean isBadRequest() {
        return isBadRequest;
    }

    public void setBadRequest(boolean badRequest) {
        isBadRequest = badRequest;
    }

    public boolean isParsed() {
        return isParsed;
    }

    public void setParsed(boolean parsed) {
        isParsed = parsed;
    }
}
