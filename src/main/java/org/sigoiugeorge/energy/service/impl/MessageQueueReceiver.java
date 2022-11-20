package org.sigoiugeorge.energy.service.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.Getter;
import org.sigoiugeorge.energy.service.api.EnergyConsumptionService;
import org.sigoiugeorge.energy.utils.EnergyConsumptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class MessageQueueReceiver {
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.connection.uri}")
    private String uri;

    private EnergyConsumptionService energyConsumptionService;

    @Autowired
    private void setEnergyConsumptionService(EnergyConsumptionService service) {
        energyConsumptionService = service;
    }


    public void start() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println(" [*] Waiting for messages.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            ObjectMapper mapper = new ObjectMapper();
            EnergyConsumptionResponse readObject = mapper
                    .registerModule(new JavaTimeModule())
                    .readerFor(EnergyConsumptionResponse.class)
                    .readValue(message);
            energyConsumptionService.addEnergyConsumption(readObject);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }



}
