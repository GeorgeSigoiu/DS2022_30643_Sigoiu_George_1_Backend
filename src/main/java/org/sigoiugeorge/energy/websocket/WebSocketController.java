package org.sigoiugeorge.energy.websocket;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.model.Message;
import org.sigoiugeorge.energy.model.Ticket;
import org.sigoiugeorge.energy.service.api.MessageService;
import org.sigoiugeorge.energy.service.api.TicketService;
import org.sigoiugeorge.energy.utils.TicketShort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate template;
    private Set<String> usersConnected = new HashSet<>();
    private final TicketService topicService;
    private final MessageService messageService;


    /**
     * Send a private message with device consumption
     */
    @MessageMapping("/private-message/{username}")
    public ConsumptionMessage privateMessage(@Payload ConsumptionMessage consumptionMessage, @PathVariable String username) {
        template.convertAndSendToUser(username, "/private", consumptionMessage);
//        System.out.println("private message to: " + username);
//        System.out.println("message: " + consumptionMessage);
        return consumptionMessage;
    }

    @PostMapping("/ws/connected")
    public ResponseEntity<Void> newUserConnected(@RequestBody String username) {
        usersConnected.add(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ws/disconnected")
    public ResponseEntity<Void> userDisconnected(@RequestBody String username) {
        usersConnected.remove(username);
        return ResponseEntity.ok().build();
    }

    /**
     * Send private message from a client to admin
     */
    @PostMapping("/ws/send-message/to-admin")
    @Transactional
    public ResponseEntity<Void> sendMessageToAdmin(@RequestBody @NotNull Map<String, String> body) {
        String username = body.get("username");
        String message = body.get("message");
        if (message == null || username == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Ticket> topic = topicService.findOpenTopicForClient(username);
        Ticket leTicket;
        if (topic.isEmpty()) {
            //create topic
            Ticket ticket1 = new Ticket();
            ticket1.setClientUsername(username);
            ticket1.setStatus("open");
            leTicket = topicService.create(ticket1);
            String mes = "new topic added";
            template.convertAndSend("/topic/messages", mes);
        } else {
            leTicket = topic.get();
        }
        Message message1 = new Message();
        message1.setText(message);
        message1.setUserType("client");
        message1.setTimestamp(LocalDateTime.now());
        message1.setTicket(leTicket);
        message1.setUsername(username);
        message1.setRead(0);
        Message message2 = messageService.create(message1);
        String adminUsername = leTicket.getAdminUsername();
        if (adminUsername != null) {
            template.convertAndSendToUser(adminUsername, "/chat", message2);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Send private message from admin to client
     */
    @PostMapping("/ws/send-message/to-client")
    public ResponseEntity<Void> sendMessageToClient(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String admin = body.get("admin");
        String message = body.get("message");
        if (message == null || username == null) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Ticket> openTopic = topicService.findOpenTopicForClient(username);
        if (openTopic.isEmpty()) {
            throw new RuntimeException("Topic does not exist!");
        }
        Ticket ticket = openTopic.get();
        if (admin.equals("solved")) {
            ticket.setStatus("solved");
            ticket = topicService.update(ticket);
        }
        String adminUsername = ticket.getAdminUsername();
        Message message1 = new Message();
        message1.setTicket(ticket);
        message1.setText(message);
        message1.setUserType(admin);
        message1.setUsername(adminUsername);
        message1.setTimestampNow();
        message1.setRead(0);
        Message message2 = messageService.create(message1);
        template.convertAndSendToUser(username, "/chat", message2);
        return ResponseEntity.ok().build();
    }

    /**
     * Return all the messages from the topic for user specified.
     * The last message can have the userType = solved -> that means the admin considered
     * the ticket solved -> this is the item after which the client can close the ticket, also
     * in that last message will be a conclusion message.
     */
    @GetMapping("/ws/get-messages/client/{username}")
    public ResponseEntity<List<Message>> getClientMessages(@PathVariable String username) {
        Optional<Ticket> openTopicForClient = topicService.findOpenOrSolvedTopicForClient(username);
        if (openTopicForClient.isPresent()) {
            Set<Message> messages = openTopicForClient.get().getMessages();
            List<Message> list = new ArrayList<>(messages);
            list.sort(Comparator.comparing(Message::getTimestamp));
            return ResponseEntity.ok().body(list);
        }
        return ResponseEntity.ok().body(new ArrayList<>());
    }

    @PostMapping("/ws/close-ticket")
    public ResponseEntity<Void> closeTicket(@RequestBody String username) {
        Optional<Ticket> openTopicForClient = topicService.findSolvedTopicForClient(username.replaceAll("\"", ""));
        if (openTopicForClient.isEmpty()) {
            throw new RuntimeException("The ticket should be solved. For username: " + username);
        }
        Ticket ticket = openTopicForClient.get();
        ticket.setStatus("closed");
        topicService.update(ticket);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/ws/get-tickets/admin/{username}")
    public ResponseEntity<List<TicketShort>> getTopicsForAdmin(@PathVariable String username) {
        List<Ticket> all = topicService.getAll().stream().filter(t -> t.getStatus().equals("open")).toList();
        List<Ticket> topicsAssignedToAdmin = all.stream().filter(t -> t.getAdminUsername() != null && t.getAdminUsername().equals(username)).toList();
        List<TicketShort> shorts = new ArrayList<>();
        topicsAssignedToAdmin.forEach(topic -> {
            TicketShort ticketShort = new TicketShort(
                    topic.getId(),
                    topic.getClientUsername(),
                    topic.getAdminUsername(),
                    getUnreadMessagesNumberFromAdmin(topic));
            shorts.add(ticketShort);
        });
        return ResponseEntity.ok().body(shorts);
    }

    /**
     * number of message not read by admin (sent by client)
     */
    @Contract(pure = true)
    private @NotNull Integer getUnreadMessagesNumberFromAdmin(Ticket ticket) {
        return ticket.getMessages().stream()
                .filter(m -> m.getUserType().equals("client"))
                .filter(m -> m.getRead() == 0)
                .toList()
                .size();
    }

    @PostMapping("/ws/assign-ticket-to-admin")
    public ResponseEntity<TicketShort> assignTicket(@RequestBody String username) {
        Ticket unassignedTicket = topicService.getUnassignedTopic();
        unassignedTicket.setAdminUsername(username.replaceAll("\"", ""));
        Ticket update = topicService.update(unassignedTicket);
        TicketShort ts = new TicketShort(
                update.getId(),
                update.getClientUsername(),
                update.getAdminUsername(),
                getUnreadMessagesNumberFromAdmin(update)
        );
        String mes = "topic retrieved";
        template.convertAndSend("/topic/messages", mes);
        return ResponseEntity.ok().body(ts);
    }

    /**
     * Number of messages not read by client (sent by admin)
     */
    private @NotNull Integer getUnreadMessagesNumberFromClient(@NotNull Ticket ticket) {
        return ticket.getMessages().stream()
                .filter(m -> m.getUserType().equals("admin"))
                .filter(m -> m.getRead() == 0)
                .toList()
                .size();
    }

    @GetMapping("/ws/get-ticket/{id}")
    public ResponseEntity<Ticket> getTopic(@PathVariable Long id) {
        Ticket ticket = topicService.get(id);
        Set<Message> sorted = ticket.getMessages().stream().sorted(Comparator.comparing(Message::getTimestamp)).collect(Collectors.toCollection(LinkedHashSet::new));
        ticket.setMessages(sorted);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/ws/get-tickets-number/unassigned")
    public ResponseEntity<Integer> getTicketsNumber() {
        int size = topicService.getAll().stream().filter(t -> t.getAdminUsername() == null).toList().size();
        return ResponseEntity.ok().body(size);
    }

    @PutMapping("/ws/read-messages")
    public ResponseEntity<Void> setMessagesAsRead(@RequestBody @NotNull Map<String, String> body) {
        String userType = body.get("userType");
        String clientUsername = body.get("client_username");
        Optional<Ticket> openTopicForClient = topicService.findOpenOrSolvedTopicForClient(clientUsername);
        Ticket ticket = openTopicForClient.get();
        String adminUsername = ticket.getAdminUsername();
        Set<Message> messages = ticket.getMessages();
        //set that admin read the messages
        if (userType.equals("admin")) {
            //that means messages from client will be read
            messages.stream().filter(m -> m.getUserType().equals("client")).forEach(m -> {
                m.setRead(1);
                messageService.update(m);
            });
            template.convertAndSendToUser(clientUsername, "/chat", "messages read");
        }
        //set that client read the messages
        else if (userType.equals("client")) {
            //that means messages from admin will be read
            messages.stream().filter(m -> m.getUserType().equals("admin")).forEach(m -> {
                m.setRead(1);
                messageService.update(m);
            });
            template.convertAndSendToUser(adminUsername, "/chat", "messages read");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/ws/typing")
    public ResponseEntity<Void> typing(@RequestBody Map<String, String> body) {
        String clientUsername = body.get("client_username");
        String userType = body.get("userType");
        String action = body.get("action");
        Ticket ticket = topicService.findOpenTopicForClient(clientUsername).get();
        String adminUsername = ticket.getAdminUsername();
        String username = "";
        //the client is typing
        if (userType.equals("client")) {
            username = adminUsername;
        }
        //the admin is typing
        else if (userType.equals("admin")) {
            username = clientUsername;
        }
        String message = "";
        if (action.equals("yes")) {
            message = "is typing";
        } else if (action.equals("no")) {
            message = "not typing";
        }
        template.convertAndSendToUser(username, "/chat", message);
        return ResponseEntity.ok().build();
    }
}