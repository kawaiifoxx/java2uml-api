package org.java2uml.java2umlapi.lightWeight;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
 * <p>
 * An Entity Class representing specified exceptions on a method or a constructor.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class SpecifiedException implements LightWeight {
    private String name;
    @Id
    @GeneratedValue
    private Long id;

    private Long ownerId;

    protected SpecifiedException() {
    }

    public SpecifiedException(String name) {
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
