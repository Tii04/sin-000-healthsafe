package co.wethinkcode.healthsafe.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.wethinkcode.healthsafe.StaffingSchedule;

import javax.jms.*;

public class StaffingEventSubscriber{
    private final ActiveMQConnectionFactory factory;
    private final Connection connection;
    private final Session session;
    private final Topic topic;
    private final MessageConsumer consumer;
    private final ObjectMapper mapper = new ObjectMapper();

    public StaffingEventSubscriber() throws Exception{
        factory = new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

    connection = factory.createConnection();
    connection.start();

    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    topic = session.createTopic(MqConfig.TOPIC);
    consumer = session.createConsumer(topic);
    }

    public void listen() throws Exception {
        consumer.setMessageListener(message -> {
            if (message instanceof TextMessage){
                String json;
				try {
					json = ((TextMessage) message).getText();
				
					StaffingSchedule schedule = mapper.readValue(json, StaffingSchedule.class);
                    System.out.println("Received staffing event for ward: " + schedule.getWardId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

    public static void main(String[] args) throws Exception {
        StaffingEventSubscriber subscriber = new StaffingEventSubscriber();

        subscriber.listen();

        System.out.println("Subscriber is listening...");
    }
    
}