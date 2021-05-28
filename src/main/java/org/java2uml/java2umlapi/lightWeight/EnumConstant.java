package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.util.Optional;

/**
 * <p>
 * An Entity Class representing Enum Constant from source files.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnumConstant extends LightWeight {
    @Column(columnDefinition = "varchar(1000)")
    private String name;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight parent;

    protected EnumConstant() {
    }

    public EnumConstant(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * if this light weight is EnumConstant then a EnumConstant is returned.
     *
     * @return EnumConstant
     */
    @Override
    public Optional<EnumConstant> asEnumConstant() {
        return Optional.of(this);
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }
}
