package org.java2uml.java2umlapi.lightWeight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;

/**
 * <p>
 * An Entity Class representing a field in source files.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class Field implements LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String typeName;
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @Column(columnDefinition = "varchar(10)")
    private String visibility;
    private Long ownerId;

    @Id
    @GeneratedValue
    private Long id;

    protected Field() {
    }

    public Field(String typeName, String name, String visibility) {
        this.typeName = typeName;
        this.name = name;
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
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

    /**
     * if this light weight is Field then a Field is returned.
     *
     * @return Field
     */
    @Override
    public Optional<Field> asField() {
        return Optional.of(this);
    }
}
