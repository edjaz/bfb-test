package com.bforbank.testing.service.mapper;

import com.bforbank.testing.domain.*;
import com.bforbank.testing.service.dto.TicketDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Ticket and its DTO TicketDTO.
 */
@Mapper(componentModel = "spring", uses = {ProjectMapper.class, UserMapper.class, LabelMapper.class})
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "project.name", target = "projectName")
    @Mapping(source = "assignedTo.id", target = "assignedToId")
    @Mapping(source = "assignedTo.login", target = "assignedToLogin")
    TicketDTO toDto(Ticket ticket);

    @Mapping(source = "projectId", target = "project")
    @Mapping(source = "assignedToId", target = "assignedTo")
    Ticket toEntity(TicketDTO ticketDTO);

    default Ticket fromId(Long id) {
        if (id == null) {
            return null;
        }
        Ticket ticket = new Ticket();
        ticket.setId(id);
        return ticket;
    }
}
