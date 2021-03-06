package org.java2uml.java2umlapi.lightWeight;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * <p>
 * An Entity Class representing body of class, method, constructor, etc.
 * </p>
 *
 * @author kawaiifox
 */
@Entity
public class Body {
    @Type(type = "text")
    private String content;

    private Long ownerID;

    @Id
    @GeneratedValue
    private Long id;

    public Body(String text) {
        this.content = text;
    }

    protected Body() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Long ownerID) {
        this.ownerID = ownerID;
    }
}
