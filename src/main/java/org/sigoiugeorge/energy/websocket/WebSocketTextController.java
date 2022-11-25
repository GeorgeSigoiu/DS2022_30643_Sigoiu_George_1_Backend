package org.sigoiugeorge.energy.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class WebSocketTextController {

    private final SimpMessagingTemplate template;

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody TextMessageDTO textMessageDTO) {
        System.out.println("CEVA 1");
        template.convertAndSend("/topic/message", textMessageDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload TextMessageDTO textMessageDTO) {
        System.out.println("received message");
        // receive message from client
    }


    @SendTo("/topic/message")
    public void broadcastMessage(@Payload TextMessageDTO textMessageDTO) {
        System.out.println("broadcast message");
        template.convertAndSend("/topic/message", textMessageDTO);
    }

    @MessageMapping("/private-message/{username}")
    public TextMessageDTO privateMessage(@Payload TextMessageDTO textMessageDTO, @PathVariable String username) {
        template.convertAndSendToUser(username, "/private", textMessageDTO);
        System.out.println("private message to: " + username);
        System.out.println("message: " + textMessageDTO);
        return textMessageDTO;
    }
}