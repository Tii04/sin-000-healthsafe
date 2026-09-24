package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.StaffingSchedule;

import javax.jms.Connection;
import javax.jms.*;
;

public class StaffingEventPublisher{
    private final ActiveMQConnectionFactory factory;
    private final Connection connection;
    private final Session session;
    private final Topic topic;
    private final MessageProducer producer;
    private final ObjectMapper mapper = new ObjectMapper();

    public StaffingEventPublisher() throws Exception{
    factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

    connection = factory.createConnection();
    connection.start();

    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    topic = session.createTopic(MqConfig.TOPIC);
    producer = session.createProducer(topic);
    }

    public void publish(StaffingSchedule schedule) throws Exception{
        String json = mapper.writeValueAsString(schedule);
        TextMessage message = session.createTextMessage(json);
        producer.send(message);

    }

    public static void main(String[] args) throws Exception{
        StaffingEventPublisher publisher = new StaffingEventPublisher();

        StaffingSchedule schedule = new StaffingSchedule(
            "W-01",
            "Cardiology",
            8,
            2
        );

        publisher.publish(schedule);
        System.out.println("Staffing event published.");
    }
}