package org.java2uml.java2umlapi.lightWeight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * <p>
 * An Entity Class representing typeParam of method or a class or a interface.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class TypeParam implements LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;

    private Long ownerId;

    @Id
    @GeneratedValue
    private Long id;

    protected TypeParam() {
    }

    public TypeParam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
