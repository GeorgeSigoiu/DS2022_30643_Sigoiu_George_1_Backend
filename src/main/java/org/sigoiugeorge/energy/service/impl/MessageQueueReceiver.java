package org.sigoiugeorge.energy.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.*;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.sigoiugeorge.energy.model.EnergyConsumption;
import org.sigoiugeorge.energy.model.MeteringDevice;
import org.sigoiugeorge.energy.service.api.EnergyConsumptionService;
import org.sigoiugeorge.energy.service.api.MeteringDeviceService;
import org.sigoiugeorge.energy.utils.EnergyConsumptionResponse;
import org.sigoiugeorge.energy.websocket.ConsumptionMessage;
import org.sigoiugeorge.energy.websocket.WebSocketController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MessageQueueReceiver {
    @Value("${rabbitmq.queue.name}")
    private String queueName;
    @Value("${rabbitmq.connection.uri}")
    private String uri;

    private final EnergyConsumptionService energyConsumptionService;
    private final MeteringDeviceService deviceService;
    private final WebSocketController ws;

    public void start() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, false, false, false, null);
        System.out.println(" [*] Waiting for messages.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> callBack(delivery);
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    private void callBack(@NotNull Delivery delivery) throws JsonProcessingException {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received '" + message + "'");
        ObjectMapper mapper = new ObjectMapper();
        EnergyConsumptionResponse readObject = mapper
                .registerModule(new JavaTimeModule())
                .readerFor(EnergyConsumptionResponse.class)
                .readValue(message);
        energyConsumptionService.addEnergyConsumption(readObject);
        sendMessage(readObject);
    }

    private void sendMessage(@NotNull EnergyConsumptionResponse readObject) {
        Boolean valueExceeded = deviceService.deviceExceededMaxHourlyConsumption(readObject.getDeviceId(), readObject.getTimestamp());
        String messageExceededConsumption = new JSONObject().toString();
        if (valueExceeded) {
            JSONObject json = new JSONObject();
            MeteringDevice device = deviceService.get(readObject.getDeviceId());
            String address = device.getAddress();
            Integer maxHourlyEnergyConsumption = device.getMaxHourlyEnergyConsumption();
            json.put("address", address);
            json.put("max_hourly_consumption", maxHourlyEnergyConsumption);
            json.put("date", readObject.getTimestamp().toLocalDate().toString());
            json.put("time", readObject.getTimestamp().getHour() + 1);
            messageExceededConsumption = json.toString();
        }
        JSONObject json = new JSONObject();
        json.put("device_id", readObject.getDeviceId());
        int value = readObject.getCurrentEnergyValue().intValue();
        MeteringDevice meteringDevice = deviceService.get(readObject.getDeviceId());
        LocalDate localDate = readObject.getTimestamp().minusHours(1).toLocalDate();
        int hour = readObject.getTimestamp().getHour();
        try {
            Integer integer = meteringDevice.getEnergyConsumption()
                    .stream()
                    .filter(e -> e.getTimestamp().toLocalDate().equals(localDate))
                    .filter(e -> e.getTimestamp().plusHours(1).getHour() == hour)
                    .map(EnergyConsumption::getEnergyConsumption)
                    .max(Integer::compareTo).get();
            value -= integer;
        } catch (Exception e) {
            //just don't decrement the value
        }
        json.put("value", value);
        json.put("date", readObject.getTimestamp().toLocalDate().toString());
        json.put("hour", readObject.getTimestamp().getHour() + 1);
        ConsumptionMessage consumptionMessage = new ConsumptionMessage(messageExceededConsumption, json.toString());
//        System.out.println("Message to send next: " + consumptionMessage);
        String username = meteringDevice.getUser().getCredentials().getUsername();
        ws.privateMessage(consumptionMessage, username);
    }


}
