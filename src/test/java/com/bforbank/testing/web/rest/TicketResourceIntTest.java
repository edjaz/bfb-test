package com.bforbank.testing.web.rest;

import com.bforbank.testing.TfTestApp;

import com.bforbank.testing.domain.Label;
import com.bforbank.testing.domain.Project;
import com.bforbank.testing.domain.Ticket;
import com.bforbank.testing.domain.User;
import com.bforbank.testing.repository.LabelRepository;
import com.bforbank.testing.repository.ProjectRepository;
import com.bforbank.testing.repository.TicketRepository;
import com.bforbank.testing.repository.UserRepository;
import com.bforbank.testing.service.TicketService;
import com.bforbank.testing.service.dto.TicketDTO;
import com.bforbank.testing.service.mapper.TicketMapper;
import com.bforbank.testing.web.rest.errors.ExceptionTranslator;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import static com.bforbank.testing.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TicketResource REST controller.
 *
 * @see TicketResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TfTestApp.class)
public class TicketResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_DONE = false;
    private static final Boolean UPDATED_DONE = true;

    @Autowired
    private TicketRepository ticketRepository;

    @Mock
    private TicketRepository ticketRepositoryMock;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Mock
    private TicketService ticketServiceMock;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TicketResource ticketResource = new TicketResource(ticketService);
        this.restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ticket createEntity(EntityManager em) {
        Ticket ticket = new Ticket()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .dueDate(DEFAULT_DUE_DATE)
            .done(DEFAULT_DONE);
        return ticket;
    }

    @Before
    public void initTest() {
        ticket = createEntity(em);
    }

    @Test
    @Transactional
    public void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);
        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isCreated());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate + 1);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTicket.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTicket.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testTicket.isDone()).isEqualTo(DEFAULT_DONE);
    }

    @Test
    @Transactional
    public void createTicketWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket with an existing ID
        ticket.setId(1L);
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = ticketRepository.findAll().size();
        // set the field null
        ticket.setTitle(null);

        // Create the Ticket, which fails.
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        restTicketMockMvc.perform(post("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTickets() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the ticketList
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].done").value(hasItem(DEFAULT_DONE.booleanValue())));
    }

    @SuppressWarnings({"unchecked"})
    public void getAllTicketsWithEagerRelationshipsIsEnabled() throws Exception {
        TicketResource ticketResource = new TicketResource(ticketServiceMock);
        when(ticketServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restTicketMockMvc.perform(get("/api/tickets?eagerload=true"))
        .andExpect(status().isOk());

        verify(ticketServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllTicketsWithEagerRelationshipsIsNotEnabled() throws Exception {
        TicketResource ticketResource = new TicketResource(ticketServiceMock);
            when(ticketServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restTicketMockMvc.perform(get("/api/tickets?eagerload=true"))
        .andExpect(status().isOk());

            verify(ticketServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.done").value(DEFAULT_DONE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket
        Ticket updatedTicket = ticketRepository.findById(ticket.getId()).get();
        // Disconnect from session so that the updates on updatedTicket are not directly saved in db
        em.detach(updatedTicket);
        updatedTicket
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .dueDate(UPDATED_DUE_DATE)
            .done(UPDATED_DONE);
        TicketDTO ticketDTO = ticketMapper.toDto(updatedTicket);

        restTicketMockMvc.perform(put("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = ticketList.get(ticketList.size() - 1);
        assertThat(testTicket.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTicket.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTicket.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTicket.isDone()).isEqualTo(UPDATED_DONE);
    }

    @Test
    @Transactional
    public void updateNonExistingTicket() throws Exception {
        int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Create the Ticket
        TicketDTO ticketDTO = ticketMapper.toDto(ticket);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketMockMvc.perform(put("/api/tickets")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ticketDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Ticket in the database
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        int databaseSizeBeforeDelete = ticketRepository.findAll().size();

        // Get the ticket
        restTicketMockMvc.perform(delete("/api/tickets/{id}", ticket.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Ticket> ticketList = ticketRepository.findAll();
        assertThat(ticketList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ticket.class);
        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        Ticket ticket2 = new Ticket();
        ticket2.setId(ticket1.getId());
        assertThat(ticket1).isEqualTo(ticket2);
        ticket2.setId(2L);
        assertThat(ticket1).isNotEqualTo(ticket2);
        ticket1.setId(null);
        assertThat(ticket1).isNotEqualTo(ticket2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketDTO.class);
        TicketDTO ticketDTO1 = new TicketDTO();
        ticketDTO1.setId(1L);
        TicketDTO ticketDTO2 = new TicketDTO();
        assertThat(ticketDTO1).isNotEqualTo(ticketDTO2);
        ticketDTO2.setId(ticketDTO1.getId());
        assertThat(ticketDTO1).isEqualTo(ticketDTO2);
        ticketDTO2.setId(2L);
        assertThat(ticketDTO1).isNotEqualTo(ticketDTO2);
        ticketDTO1.setId(null);
        assertThat(ticketDTO1).isNotEqualTo(ticketDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(ticketMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(ticketMapper.fromId(null)).isNull();
    }


    @Test
    @Transactional
    @WithMockUser("johndoe")
    public void getLastDueDateTicket() throws Exception {


        // Initialize the database
        initForDueDate();

        restTicketMockMvc.perform(get("/api/tickets/mine?last=3"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.length()").value(3)
            );
    }




    public void initForDueDate() {
        User user = createUser("johndoe");
        User user1 = createUser("u1");
        User user2 = createUser("u2");

        Project project = new Project();
        project.setName("p1");
        projectRepository.saveAndFlush(project);

        Label lbl1 = new Label();
        lbl1.setLabel("incident");
        labelRepository.saveAndFlush(lbl1);


        HashSet tickets = new HashSet();

        tickets.add(createTicket(project, lbl1, "title", "desc", LocalDate.now().plusDays(1), user));
        tickets.add(createTicket(project, lbl1, "title1", "desc1", LocalDate.now().plusDays(2), user));
        tickets.add(createTicket(project, lbl1, "title2", "desc2", LocalDate.now().plusDays(3), user));

        tickets.add(createTicket(project, lbl1, "title4", "desc", LocalDate.now().plusDays(1), user1));
        tickets.add(createTicket(project, lbl1, "title5", "desc", LocalDate.now().plusDays(1), user2));

        tickets.add(createTicket(project, lbl1, "title3", "desc", LocalDate.now().plusDays(4), user));

    }



    private User createUser(String login) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(login+"@localhost");
        user.setFirstName("john");
        user.setLastName("doe");
        user.setImageUrl("http://placehold.it/50x50");
        user.setLangKey("en");
        return userRepository.saveAndFlush(user);
    }

    private Ticket createTicket(Project project, Label label, String title, String desc, LocalDate dueDate, User assignedTo) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setAssignedTo(assignedTo);
        ticket.setDescription(desc);
        ticket.setDone(false);
        ticket.setProject(project);
        ticket.setLabels(Sets.newHashSet(label));
        ticket.setDueDate(dueDate);
        return ticketRepository.saveAndFlush(ticket);
    }


}
