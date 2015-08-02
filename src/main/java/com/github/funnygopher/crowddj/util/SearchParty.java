package com.github.funnygopher.crowddj.util;

/**
 * The <code>SearchParty</code> class is a utility class for detecting whether or not a list contains an object. Instead
 * of searching a list to check if a desired object is present, and then looping back through the list to retrieve the
 * object, a <code>SearchParty</code> object can be used to return whether or not the object was found, along with the
 * potential object. This keeps from having to loop through the list twice to retrieve an object.
 * @param <T> The object type of the object being searched for
 */
public class SearchParty<T> {

    /**
     * A boolean representing whether or not the target was found.
     */
    private boolean found;

    /**
     * A generic object that holds the found object.
     */
    private T target;

    /**
     * Constructor for a <code>SearchParty</code> object that found it's target.
     * @param target The found object
     */
    public SearchParty(T target) {
        this.found = true;
        this.target = target;
    }

    /**
     * Constructor for a <code>SearchParty</code> object that couldn't find it's target.
     */
    public SearchParty() {
        this.found = false;
        this.target = null;
    }

    public boolean found() {
        return found;
    }

    public T rescue() {
        return target;
    }
}
