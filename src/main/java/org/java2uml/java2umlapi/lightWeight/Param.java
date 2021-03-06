package org.java2uml.java2umlapi.lightWeight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * <p>
 * A lightweight data class containing all necessary information for a particular parameter in source files.
 * </p>
 *
 * @author kawaiifox
 */

@Entity
public class Param implements LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String typeName;
    @Column(columnDefinition = "varchar(500)")
    private String name;
    private Long ownerId;
    @Id
    @GeneratedValue
    private Long id;

    protected Param() {
    }

    public Param(String typeName, String name) {
        this.typeName = typeName;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
