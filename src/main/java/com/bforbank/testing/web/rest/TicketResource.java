package com.bforbank.testing.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.bforbank.testing.domain.Ticket;
import com.bforbank.testing.repository.TicketRepository;
import com.bforbank.testing.web.rest.errors.BadRequestAlertException;
import com.bforbank.testing.web.rest.util.HeaderUtil;
import com.bforbank.testing.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Ticket.
 */
@RestController
@RequestMapping("/api")
public class TicketResource {

    private final Logger log = LoggerFactory.getLogger(TicketResource.class);

    private static final String ENTITY_NAME = "ticket";

    private final TicketRepository ticketRepository;

    public TicketResource(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * POST  /tickets : Create a new ticket.
     *
     * @param ticket the ticket to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ticket, or with status 400 (Bad Request) if the ticket has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tickets")
    @Timed
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) throws URISyntaxException {
        log.debug("REST request to save Ticket : {}", ticket);
        if (ticket.getId() != null) {
            throw new BadRequestAlertException("A new ticket cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ticket result = ticketRepository.save(ticket);
        return ResponseEntity.created(new URI("/api/tickets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tickets : Updates an existing ticket.
     *
     * @param ticket the ticket to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ticket,
     * or with status 400 (Bad Request) if the ticket is not valid,
     * or with status 500 (Internal Server Error) if the ticket couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tickets")
    @Timed
    public ResponseEntity<Ticket> updateTicket(@Valid @RequestBody Ticket ticket) throws URISyntaxException {
        log.debug("REST request to update Ticket : {}", ticket);
        if (ticket.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Ticket result = ticketRepository.save(ticket);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ticket.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tickets : get all the tickets.
     *
     * @param pageable the pagination information
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many)
     * @return the ResponseEntity with status 200 (OK) and the list of tickets in body
     */
    @GetMapping("/tickets")
    @Timed
    public ResponseEntity<List<Ticket>> getAllTickets(Pageable pageable, @RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get a page of Tickets");
        Page<Ticket> page;
        if (eagerload) {
            page = ticketRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = ticketRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/api/tickets?eagerload=%b", eagerload));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /tickets/:id : get the "id" ticket.
     *
     * @param id the id of the ticket to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ticket, or with status 404 (Not Found)
     */
    @GetMapping("/tickets/{id}")
    @Timed
    public ResponseEntity<Ticket> getTicket(@PathVariable Long id) {
        log.debug("REST request to get Ticket : {}", id);
        Optional<Ticket> ticket = ticketRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(ticket);
    }

    /**
     * DELETE  /tickets/:id : delete the "id" ticket.
     *
     * @param id the id of the ticket to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tickets/{id}")
    @Timed
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        log.debug("REST request to delete Ticket : {}", id);

        ticketRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
