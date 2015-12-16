package nl.cwi.examples.schemas;

/**
 * This schema is managed by a data manager capable of initialization
 * allowing the objects (points) to be created with starting values.
 */
public interface Point {
    Integer x(Integer ...x);
    Integer y(Integer ...y);
}