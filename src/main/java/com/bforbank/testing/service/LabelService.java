package com.bforbank.testing.service;

import com.bforbank.testing.service.dto.LabelDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Label.
 */
public interface LabelService {

    /**
     * Save a label.
     *
     * @param labelDTO the entity to save
     * @return the persisted entity
     */
    LabelDTO save(LabelDTO labelDTO);

    /**
     * Get all the labels.
     *
     * @return the list of entities
     */
    List<LabelDTO> findAll();


    /**
     * Get the "id" label.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<LabelDTO> findOne(Long id);

    /**
     * Delete the "id" label.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
