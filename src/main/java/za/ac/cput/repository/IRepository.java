/* IRepository.java
 * Generic Repository Interface – DDD Building Block
 * TechStore E-Commerce – ADP372S
 * Author: Pertunia (221692568)
 * Date: 2026
 */
package za.ac.cput.repository;

import java.util.List;
import java.util.Optional;

/**
 * IRepository
 * Defines standard CRUD operations for all repositories.
 *
 * @param <T>  the domain entity type
 * @param <ID> the type of the entity identifier
 */
public interface IRepository<T, ID> {

    /** Saves a new entity or updates an existing one. */
    T save(T entity);

    /** Finds an entity by its ID. Returns empty if not found. */
    Optional<T> findById(ID id);

    /** Returns all entities. */
    List<T> findAll();

    /** Updates an existing entity. */
    T update(T entity);

    /** Deletes an entity by its ID. */
    void deleteById(ID id);
}
