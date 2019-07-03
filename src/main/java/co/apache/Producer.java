package co.apache;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
	
	public void call(String userId, String email) {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"tcp://10.36.76.185:61616");
		Connection connection; 
		try {
			connection = connectionFactory.createConnection();
			connection.start();

			// JMS messages are sent and received using a Session. We will
			// create here a non-transactional session object. If you want
			// to use transactions you should set the first parameter to 'true'
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Topic topic = session.createTopic("UserCreationRave");

			MessageProducer producer = session.createProducer(topic);

			// We will send a small text message saying 'Hello'

			TextMessage message = session.createTextMessage();

			message.setText("ConnectionRequest");
			message.setStringProperty("userId", userId);
			message.setStringProperty("emailId", email);

			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			// Here we are sending the message!
			producer.send(message);
			System.out.println("Sent message:::::" + message.getText());

			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	 * public static String getIP() throws Exception {
	 * 
	 * DocumentBuilderFactory builderfactory =
	 * DocumentBuilderFactory.newInstance();
	 * builderfactory.setNamespaceAware(true);
	 * 
	 * DocumentBuilder builder = builderfactory.newDocumentBuilder(); Document
	 * xmlDocument = builder.parse("../ACTIVE_MQ/src/ACTIVE_MQ.xml"); // new
	 * File(Consumer.class.getResource("./Client.xml").getFile())); XPathFactory
	 * factory = javax.xml.xpath.XPathFactory.newInstance(); XPath xPath =
	 * factory.newXPath(); XPathExpression xPathExpression2 =
	 * xPath.compile("//ActiveMq//CLIENT//Queue"); String
	 * abc=xPathExpression2.evaluate
	 * (xmlDocument,XPathConstants.STRING).toString(); return abc;
	 * 
	 * }
	 */
	
	public static void main(String[] args) {
		
		
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				"tcp://10.36.76.185:61616");
		Connection connection;
		try {
			connection = connectionFactory.createConnection();

			connection.start();

			// JMS messages are sent and received using a Session. We will
			// create here a non-transactional session object. If you want
			// to use transactions you should set the first parameter to 'true'
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Topic topic = session.createTopic("UserCreationRave");

			MessageProducer producer = session.createProducer(topic);

			// We will send a small text message saying 'Hello'

			TextMessage message = session.createTextMessage();

			message.setText("UserCreationRave");
			message.setStringProperty("userId", "pran3bull@gmail.com");
			message.setStringProperty("emailId", "pran3bull@gmail.com");
			message.setStringProperty("name", "Pranav");
			message.setStringProperty("password", "12345");
			message.setStringProperty("mobileNumber", "09897654325");
			message.setStringProperty("extension", "4444");
			
			message.setStringProperty("requestTo", "pran3bull@gmail.com");

			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			// Here we are sending the message!
			producer.send(message);
			System.out.println("Sent message:::::" + message.getText());

			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
