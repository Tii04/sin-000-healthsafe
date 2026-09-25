package co.wethinkcode.healthsafe.mq;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.EquipmentFailure;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.OffsetDateTime;

import javax.jms.*;

public class EquipmentFailurePublisher {
    private final ActiveMQConnectionFactory factory;
    private final Connection connection;
    private final Session session;
    private final Queue queue;
    private final MessageProducer producer;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());;

    public EquipmentFailurePublisher() throws Exception{
        factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);
        
        connection = factory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        queue = session.createQueue(MqConfig.QUEUE);
        producer = session.createProducer(queue);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void publish(EquipmentFailure equipmentFailure) throws Exception{
        String json = mapper.writeValueAsString(equipmentFailure);
        TextMessage message = session.createTextMessage(json);
        producer.send(message);
    }
}
