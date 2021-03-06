package org.java2uml.java2umlapi.lightWeight;

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
public class EnumConstant implements LightWeight {
    @Column(columnDefinition = "varchar(500)")
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private EnumLW enumLW;

    @Id
    @GeneratedValue
    private Long id;

    protected EnumConstant() {
    }

    public EnumConstant(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnumLW(EnumLW enumLW) {
        this.enumLW = enumLW;
    }

    public Long getId() {
        return id;
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
}
