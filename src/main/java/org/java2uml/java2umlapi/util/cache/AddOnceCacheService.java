package org.java2uml.java2umlapi.util.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An abstract class, which defines all the methods for a simple cache.
 * <p>
 * This class can be implemented by any AddOnceCacheService to get few general methods for free.
 * <p>
 * Classes implementing this class will have a special behaviour,
 * if you delete a key and that key has not been added to this class yet,
 * after doing delete, with that key if you try to do a save,  then that
 * key value pair will not be saved.
 *
 * @author kawaiifox
 */
public abstract class AddOnceCacheService<K, V> {
    private final Map<K, V> cache = new ConcurrentHashMap<>();
    /**
     * Contains id of values that need to be deleted.
     * <p>
     * This is added because a delete request may be issued before the
     * value is created in which case the value might be added later and it will
     * remain there hogging memory.
     */
    private final Set<K> toBeDeletedSet = ConcurrentHashMap.newKeySet();


    /**
     * Returns true if this repo contains a mapping for the specified key.
     * More formally, returns true if and only if this repo contains a mapping
     * for a key k such that Objects.equals(key, k). (There can be at most one such mapping.)
     *
     * @param id id whose presence in the repo is to be tested
     * @return true if id is present else false.
     */
    public boolean contains(K id) {
        return cache.containsKey(id);
    }

    /**
     * Returns a value if this repo contains a mapping for the specified key.
     * if no such mapping is present returns null.
     *
     * @param id key for which value is needed
     * @return Object if key has mapping else null.
     */
    public V get(K id) {
        return cache.get(id);
    }

    /**
     * Saves given mapping in the repository and returns the saved value.
     *
     * @param id    key for saving value.
     * @param value value to be saved.
     * @return saved value
     */
    public V save(K id, V value) {
        if (toBeDeletedSet.contains(id)) {
            toBeDeletedSet.remove(id);
            return value;
        }

        cache.put(id, value);
        return value;
    }

    /**
     * Removes mapping form the repository.
     *
     * @param id id to be removed from repository.
     */
    public void delete(K id) {
        if (cache.remove(id) == null) {
            toBeDeletedSet.add(id);
        }
    }
}
