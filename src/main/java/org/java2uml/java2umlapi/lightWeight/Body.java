package org.java2uml.java2umlapi.lightWeight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * <p>
 * An Entity Class representing body of class, method, constructor, etc.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
@Proxy(lazy = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Body extends LightWeight{
    @Type(type = "text")
    private String content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private LightWeight parent;

    public Body(String text) {
        this.content = text;
    }

    public Body(String content, LightWeight parent) {
        this.content = content;
        this.parent = parent;
    }

    protected Body() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public LightWeight getParent() {
        return parent;
    }

    public void setParent(LightWeight parent) {
        this.parent = parent;
    }
}
