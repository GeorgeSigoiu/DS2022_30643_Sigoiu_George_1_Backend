package org.sigoiugeorge.energy.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.service.api.EnergyConsumptionService;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.sigoiugeorge.energy.utils.EnergyConsumptionResponse;
import org.sigoiugeorge.energy.websocket.TextMessageDTO;
import org.sigoiugeorge.energy.websocket.WebSocketTextController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MessageQueueReceiver {
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.connection.uri}")
    private String uri;

    private final EnergyConsumptionService energyConsumptionService;
    private final MeteringDeviceService deviceService;
    private final WebSocketTextController ws;

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
            Boolean valueExceeded = deviceService.deviceExceededMaxConsumption(readObject.getDeviceId());
            if (valueExceeded) {
                JSONObject json = new JSONObject();
                MeteringDevice device = deviceService.get(readObject.getDeviceId());
                String address = device.getAddress();
                Integer maxHourlyEnergyConsumption = device.getMaxHourlyEnergyConsumption();
                json.put("address", address);
                json.put("max_consumption", maxHourlyEnergyConsumption);
                TextMessageDTO textMessageDTO = new TextMessageDTO(json.toString());
                System.out.println("Message to send next: " + textMessageDTO);
                ws.sendMessage(textMessageDTO);
            }
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }


}
