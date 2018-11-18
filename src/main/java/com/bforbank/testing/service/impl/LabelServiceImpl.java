package com.bforbank.testing.service.impl;

import com.bforbank.testing.service.LabelService;
import com.bforbank.testing.domain.Label;
import com.bforbank.testing.repository.LabelRepository;
import com.bforbank.testing.service.dto.LabelDTO;
import com.bforbank.testing.service.mapper.LabelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Label.
 */
@Service
@Transactional
public class LabelServiceImpl implements LabelService {

    private final Logger log = LoggerFactory.getLogger(LabelServiceImpl.class);

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    public LabelServiceImpl(LabelRepository labelRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    /**
     * Save a label.
     *
     * @param labelDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public LabelDTO save(LabelDTO labelDTO) {
        log.debug("Request to save Label : {}", labelDTO);

        Label label = labelMapper.toEntity(labelDTO);
        label = labelRepository.save(label);
        return labelMapper.toDto(label);
    }

    /**
     * Get all the labels.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<LabelDTO> findAll() {
        log.debug("Request to get all Labels");
        return labelRepository.findAll().stream()
            .map(labelMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one label by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LabelDTO> findOne(Long id) {
        log.debug("Request to get Label : {}", id);
        return labelRepository.findById(id)
            .map(labelMapper::toDto);
    }

    /**
     * Delete the label by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Label : {}", id);
        labelRepository.deleteById(id);
    }
}
