package com.bforbank.testing.service.mapper;

import com.bforbank.testing.domain.*;
import com.bforbank.testing.service.dto.LabelDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Label and its DTO LabelDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LabelMapper extends EntityMapper<LabelDTO, Label> {


    @Mapping(target = "tickets", ignore = true)
    Label toEntity(LabelDTO labelDTO);

    default Label fromId(Long id) {
        if (id == null) {
            return null;
        }
        Label label = new Label();
        label.setId(id);
        return label;
    }
}
