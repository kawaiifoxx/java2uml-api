package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.java2uml.java2umlapi.fileStorage.entity.ProjectInfo;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * An Entity Class representing the parsed project.
 * </p>
 *
 * @author kawaiifox
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source extends LightWeight {
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassOrInterface> classOrInterfaceList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnumLW> enumLWList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassRelation> classRelationList;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private ProjectInfo projectInfo;

    public Source() {
    }

    public Source(List<ClassOrInterface> classOrInterfaceList, List<EnumLW> enumLWList, List<ClassRelation> classRelationList) {
        this.classOrInterfaceList = classOrInterfaceList;
        this.enumLWList = enumLWList;
        this.classRelationList = classRelationList;
    }

    @Override
    public Optional<Source> asSource() {
        return Optional.of(this);
    }

    public List<ClassOrInterface> getClassOrInterfaceList() {
        return classOrInterfaceList;
    }

    /**
     * This method is added in source to ignore parent property when serializing to json.
     * @return parent of this lightWeight.
     */
    @Override
    @JsonIgnore
    public LightWeight getParent() {
        return super.getParent();
    }

    public void setClassOrInterfaceList(List<ClassOrInterface> classOrInterfaceList) {
        this.classOrInterfaceList = classOrInterfaceList;
    }

    public List<EnumLW> getEnumLWList() {
        return enumLWList;
    }

    public void setEnumLWList(List<EnumLW> enumLWList) {
        this.enumLWList = enumLWList;
    }

    public List<ClassRelation> getClassRelationList() {
        return classRelationList;
    }

    public void setClassRelationList(List<ClassRelation> classRelationList) {
        this.classRelationList = classRelationList;
    }

    public ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }
}
