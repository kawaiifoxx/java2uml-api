package org.java2uml.java2umlapi.callGraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * <p>
 * An entity class for representing a relation between two nodes of a call graph generated from method calls.
 * </p>
 *
 * @author kawaiifox
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallGraphRelation {
    private String from;
    @JsonIgnore
    private Long fromId;
    private String to;
    @JsonIgnore
    private Long toId;

    public CallGraphRelation(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * @return name of the method from which relation is established.
     */
    public String getFrom() {
        return from;
    }

    /**
     * @return id of the method from which relation is established.<br>
     * please note that this can return null if the method from which
     * this relation is established is not part of project.
     */
    public Long getFromId() {
        return fromId;
    }

    /**
     * @return name of the method to which relation is established.
     */
    public String getTo() {
        return to;
    }

    /**
     * @return id of the method to which relation is established.<br>
     * please note that this can return null if the method from which
     * this relation is established is not part of project.
     */
    public Long getToId() {
        return toId;
    }

    /**
     * setter for from method name.
     * @param from name of the method, from which relation is established.
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * setter for from id.
     * @param fromId id of method, from which relation is established.
     */
    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    /**
     * setter for to method name.
     * @param to name of the method, to which relation is established.
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * setter for to id.
     * @param toId id of method, to which relation is established.
     */
    public void setToId(Long toId) {
        this.toId = toId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallGraphRelation)) return false;

        CallGraphRelation that = (CallGraphRelation) o;

        if (!getFrom().equals(that.getFrom())) return false;
        return getTo().equals(that.getTo());
    }

    @Override
    public int hashCode() {
        int result = getFrom().hashCode();
        result = 31 * result + getTo().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CallGraphRelation{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
