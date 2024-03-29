package org.sigoiugeorge.energy.service.api;

import com.sun.istack.NotNull;

import java.util.List;

public interface CrudOperationsService<T> {
    /**
     * save the new object in database
     */
    @NotNull
    T create(@NotNull T entity);

    /**
     * remove the object with specified id from database
     */
    void remove(long id);

    /**
     * remove the object from database
     */
    void remove(@NotNull T entity);

    /**
     * retrieve the object with specified id from database
     */
    @NotNull
    T get(long id);

    /**
     * get all the instances of the objects
     * @return List of objects
     */
    List<T> getAll();

    /**
     * update the values of the object.
     * The object needs the id to be specified in order to update the
     * other values.
     */
    @NotNull
    T update(@NotNull T entity);
}
