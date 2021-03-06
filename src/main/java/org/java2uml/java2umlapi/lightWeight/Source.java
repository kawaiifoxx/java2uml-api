package org.java2uml.java2umlapi.lightWeight;

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
public class Source implements LightWeight {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassOrInterface> classOrInterfaceList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnumLW> enumLWList;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassRelation> classRelationList;

    protected Source() {
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<ClassOrInterface> getClassOrInterfaceList() {
        return classOrInterfaceList;
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
}
