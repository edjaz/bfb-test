package com.bforbank.testing.service;

import com.bforbank.testing.TfTestApp;
import com.bforbank.testing.domain.Label;
import com.bforbank.testing.domain.Project;
import com.bforbank.testing.domain.Ticket;
import com.bforbank.testing.domain.User;
import com.bforbank.testing.repository.LabelRepository;
import com.bforbank.testing.repository.ProjectRepository;
import com.bforbank.testing.repository.TicketRepository;
import com.bforbank.testing.repository.UserRepository;
import com.bforbank.testing.service.dto.TicketDTO;
import com.bforbank.testing.service.mapper.TicketMapper;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = TfTestApp.class)
@Transactional
public class TicketServiceTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private LabelRepository labelRepository;


    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketMapper ticketMapper;


    @Mock
    DateTimeProvider dateTimeProvider;

    private User user;

    private Set<Ticket> tickets;


    private Ticket last;


    @Before
    public void init() {
        user = createUser("johndoe");
        User user1 = createUser("u1");
        User user2 = createUser("u2");

        Project project = new Project();
        project.setName("p1");
        projectRepository.saveAndFlush(project);

        Label lbl1 = new Label();
        lbl1.setLabel("incident");
        labelRepository.saveAndFlush(lbl1);


        tickets = new HashSet();

        tickets.add(createTicket(project, lbl1, "title", "desc", LocalDate.now().plusDays(1), user));
        tickets.add(createTicket(project, lbl1, "title1", "desc1", LocalDate.now().plusDays(2), user));
        tickets.add(createTicket(project, lbl1, "title2", "desc2", LocalDate.now().plusDays(3), user));

        tickets.add(createTicket(project, lbl1, "title4", "desc", LocalDate.now().plusDays(1), user1));
        tickets.add(createTicket(project, lbl1, "title5", "desc", LocalDate.now().plusDays(1), user2));


        last = createTicket(project, lbl1, "title3", "desc", LocalDate.now().plusDays(4), user);

        tickets.add(last);


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


    @Test
    @Transactional
    @WithMockUser("johndoe")
    public void should_return_3_last_due_date_of_current_user() {
        int limit = 3;
        Set<TicketDTO> tickets = ticketService.findNextDueDate(limit);

        assertThat(tickets).size().isEqualTo(limit);
        assertThat(tickets).doesNotContain(ticketMapper.toDto(last));
    }


}
