package co.wethinkcode.healthsafe.mq;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import co.wethinkcode.healthsafe.EquipmentFailure;

import javax.jms.*;

public class EquipmentFailureConsumer {
    private final ActiveMQConnectionFactory factory;
    private final Connection connection;
    private final Session session;
    private final Queue queue;
    private final MessageConsumer consumer;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public EquipmentFailureConsumer() throws Exception {
        factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        connection =factory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        queue = session.createQueue(MqConfig.QUEUE);
        consumer = session.createConsumer(queue);
    }

    public void listen() throws Exception {
        consumer.setMessageListener(message ->{
            if (message instanceof TextMessage){
                String json;
                try {
                    json = ((TextMessage) message).getText();

                    EquipmentFailure equipmentFailure = mapper.readValue(json, EquipmentFailure.class);
                    System.out.println("Received Equiment failure event for ward: " + equipmentFailure.getWardId());
                    message.acknowledge();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        EquipmentFailureConsumer consumer = new EquipmentFailureConsumer();

        consumer.listen();

        System.out.println("Consumer is listening...");
    }

}
