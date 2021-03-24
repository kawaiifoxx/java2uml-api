package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
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
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private EnumLW enumLW;
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

    public void setEnumLW(EnumLW enumLW) {
        this.enumLW = enumLW;
    }

    public String getName() {
        return name;
    }

    public EnumLW getEnumLW() {
        return enumLW;
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
