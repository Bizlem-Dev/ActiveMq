package co.apache;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
@SuppressWarnings("serial")
public class Consumer extends HttpServlet {

	    // Name of the topic from which we will receive messages from = " testt"
	TopicConnectionFactory  connectionFactory = new ActiveMQConnectionFactory("tcp://10.36.76.185:61616");
	@Override
	 public void init() throws ServletException
	    {
		
        
        try {
        	
			callConsumer("456690", "ProfileEdit");
			callConsumer("45692", "ConnectionRequest");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	    }
	 
	 public void callConsumer(String clientId, String topicId){
		 try{
			 
			Connection connection = connectionFactory.createConnection();
				connection.setClientID(clientId);
				connection.start();
	        Session session = connection.createSession(false,
	                Session.AUTO_ACKNOWLEDGE);

	        Topic topic = session.createTopic(topicId);
	        TopicSubscriber durableSubscriber = session.createDurableSubscriber(topic, "Test_Durable_Subscriber");
/*	        MessageConsumer consumer = session.createConsumer(topic);
	        Message msg = consumer.receive();
	        msg.acknowledge();*/
	        MessageListener listner = new MessageListener() {
	            public void onMessage(Message message) {
	                try {
	                    if (message instanceof TextMessage) {
	                        TextMessage textMessage = (TextMessage) message;
	                        System.out.println("Received message::::"
	 	                                + textMessage.getStringProperty("userId") );
	                    }
	                } catch (JMSException e) {
	                    System.out.println("Caught:" + e);
	                    e.printStackTrace();
	                }
	            }
	        };

	        durableSubscriber.setMessageListener(listner);

     }catch(Exception e){
     	e.printStackTrace();
     }
	 }
	 
	 public void oldConsumerCall(){
		 try {
				TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
						"tcp://10.36.76.185:61616");
				Connection connection = connectionFactory.createConnection();
				connection.setClientID("212771061");
				connection.start();

				Session session = connection.createSession(false,
						Session.AUTO_ACKNOWLEDGE);

				Topic topic = session.createTopic("ProfileEdit");
				TopicSubscriber durableSubscriber = session
						.createDurableSubscriber(topic, "Test_Durable_Subscriber");
				MessageConsumer consumer = session.createConsumer(topic);
				Message msg = consumer.receive();
				msg.acknowledge();
				MessageListener listner = new MessageListener() {
					public void onMessage(Message message) {
						try {

							if (message instanceof TextMessage) {
								TextMessage textMessage = (TextMessage) message;
								System.out.println("message:"
										+ textMessage.getText());
								InputStream in = this.getClass().getClassLoader()
										.getResourceAsStream("Client.xml");
								DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
										.newInstance();
								DocumentBuilder docBuilder = docBuilderFactory
										.newDocumentBuilder();
								Document doc = docBuilder.parse(in);
								doc.getDocumentElement().normalize();

								System.out.println("Root element :"
										+ doc.getDocumentElement().getNodeName());
								Element eElement = (Element) doc
										.getDocumentElement()
										.getElementsByTagName(textMessage.getText())
										.item(0);

								if (eElement !=null && eElement.hasChildNodes()) {
									NodeList actionNodes = eElement
											.getElementsByTagName("action");
									for (int j = 0; j < actionNodes.getLength(); j++) {
										Node actionNode = actionNodes.item(j);
										if (actionNode.getNodeType() == Node.ELEMENT_NODE) {
											Element actionElement = (Element) actionNode;
											if (actionElement.hasAttribute("value")) {
												if (actionElement.getAttribute(
														"value").equals(
														"mailplatform")) {
													Map<String, String> mpMap = new HashMap<String, String>();
													NodeList childNodes = actionElement
															.getElementsByTagName("key");
													for (int k = 0; k < childNodes
															.getLength(); k++) {
														Node childNode = childNodes
																.item(k);
														if (childNode.getNodeType() == Node.ELEMENT_NODE) {
															Element keyElement = (Element) childNode;
															String keyType = keyElement
																	.getAttribute("type");
															if (keyType
																	.equals("parameter")) {

																if (!keyElement
																		.getAttribute(
																				"map")
																		.equals("")) {
																	mpMap.put(
																			keyElement
																					.getAttribute("name"),
																			textMessage
																					.getStringProperty(keyElement
																							.getAttribute("map")));

																} else {
																	mpMap.put(
																			keyElement
																					.getAttribute("name"),
																			keyElement
																					.getAttribute("value"));
																}
															}
														}
													}
													/*mailPlatform(
															mpMap,
															actionElement
																	.getAttribute("eventId"));*/
												} else if (actionElement
														.getAttribute("value")
														.equals("restService")) {
													Map<String, String> restMap = new HashMap<String, String>();
													NodeList childNodes = actionElement
															.getElementsByTagName("key");
													System.out.println(childNodes
															.getLength());
													for (int k = 0; k < childNodes
															.getLength(); k++) {
														Node childNode = childNodes
																.item(k);
														if (childNode.getNodeType() == Node.ELEMENT_NODE) {
															Element keyElement = (Element) childNode;
															String keyType = keyElement
																	.getAttribute("type");
															if (keyType
																	.equals("parameter")) {
																if (!keyElement
																		.getAttribute(
																				"map")
																		.equals("")) {
																	restMap.put(
																			keyElement
																					.getAttribute("name"),
																			textMessage
																					.getStringProperty(keyElement
																							.getAttribute("map")));

																} else {
																	restMap.put(
																			keyElement
																					.getAttribute("name"),
																			keyElement
																					.getAttribute("value"));
																}

															}
														}
													}
													/*restService(
															restMap,
															actionElement
																	.getAttribute("method"),
															actionElement
																	.getAttribute("url"));*/
												} else if (actionElement
														.getAttribute("value")
														.equals("soapService")) {
													Map<String, String> soapMap = new HashMap<String, String>();
													NodeList childNodes = actionElement
															.getElementsByTagName("key");
													for (int k = 0; k < childNodes
															.getLength(); k++) {
														Node childNode = childNodes
																.item(k);
														if (childNode.getNodeType() == Node.ELEMENT_NODE) {
															Element keyElement = (Element) childNode;
															String keyType = keyElement
																	.getAttribute("type");
															if (keyType
																	.equals("parameter")) {
																if (!keyElement
																		.getAttribute(
																				"map")
																		.equals("")) {
																	soapMap.put(
																			keyElement
																					.getAttribute("name"),
																			textMessage
																					.getStringProperty(keyElement
																							.getAttribute("map")));

																} else {
																	soapMap.put(
																			keyElement
																					.getAttribute("name"),
																			keyElement
																					.getAttribute("value"));
																}

															}
														}
													}
													/*soapService(
															soapMap,
															actionElement
																	.getAttribute("url"),
															actionElement
																	.getAttribute("method"),
															actionElement
																	.getAttribute("namespace"));*/
												}
											}
										}
									}

								}
							}
							// callConsumer(eElement.getAttribute("Queue"),
							// eElement.getAttribute("clientId"));

							// }

							// }
							// }
						} catch (Exception e) {
							System.out.println("Caught:" + e);
							e.printStackTrace();
						}
					}
				};

				durableSubscriber.setMessageListener(listner);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 
	 }
	 public void pop(){
		 
		 try{/*
		 InputStream in = this.getClass().getClassLoader().getResourceAsStream("Client.xml");
	        //praparing DOM
	                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
	                        .newInstance();
	                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        // by sending input stream as input to DOM
	                Document doc = docBuilder.parse(in);
	                doc.getDocumentElement().normalize();
	                
	            	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	            	NodeList nList = doc.getElementsByTagName("CLIENT");
	            	
	            	for(int i=0;i<nList.getLength();i++){
	            		Node nNode = nList.item(i);
	            		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	            			 
	            			Element eElement = (Element) nNode;
	            			System.out.println(eElement.getAttribute("Queue"));
	            			System.out.println(eElement.getAttribute("clientId"));
	            			if(eElement.hasChildNodes()){
	            				NodeList keyNodes = eElement.getChildNodes();
	            				for(int j=0;j<keyNodes.getLength();j++){
	            					Node childNode = keyNodes.item(j);
	            					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
	            						Element keyElement = (Element) childNode;
	        	            			System.out.println(keyElement.getAttribute("type"));
	        	            			String keyType = keyElement.getAttribute("type");
	        	            			if(keyType.equals("parameter")){
	        	            				System.out.println(keyElement.getAttribute("name"));
	        	            				System.out.println(keyElement.getAttribute("map"));
	        	            			}
	            					}
	            				}
	            			}
	            		}
	            		
	            	}
		    */
			 
		 
		 
			 
	       		 InputStream in = this.getClass().getClassLoader().getResourceAsStream("Client.xml");
	       	        //praparing DOM
	       	                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
	       	                        .newInstance();
	       	                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	       	        // by sending input stream as input to DOM
	       	                Document doc = docBuilder.parse(in);
	       	                
	       	                
	       	            	System.out.println("Root element :" + doc.getElementsByTagName("queue").getLength());
	       	            	//NodeList nList = doc.getElementsByTagName("queue").item(0).getChildNodes();
	       	            	//Element  p =  (Element) doc.getDocumentElement().getElementsByTagName("ConnectionRequest").item(0);
	       	            	//System.out.println(p.getAttribute("id"));
	       	            	/*for(int i=0;i<nList.getLength();i++){
	       	            		Node nNode = nList.item(i);
	       	            		if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	       	            			 
	       	            			Element eElement = (Element) nNode;
	       	            			//callConsumer(eElement.getAttribute("Queue"), eElement.getAttribute("clientId"));
	       	            			System.out.println(eElement.getAttribute("Queue"));
	       	            			System.out.println(eElement.getAttribute("clientId"));
	       	            		}

	       	            	}*/
			 
	       	            	
			 }catch(Exception e){
		    		e.printStackTrace();
		    	}
	 }
	    public static void main(String[] args) throws JMSException {
	        // Getting JMS connection from the server

	    	try{TopicConnectionFactory  connectionFactory = new ActiveMQConnectionFactory("tcp://10.36.76.128:61616");
	        Connection connection = connectionFactory.createConnection();
	        connection.setClientID("1234034457");
	        connection.start();

	        Session session = connection.createSession(false,
	                Session.AUTO_ACKNOWLEDGE);
	        
	      //  Topic topic = session.createTopic("mailsending1235");
	        Topic topic = session.createTopic("mailsending");
	        TopicSubscriber durableSubscriber = session.createDurableSubscriber(topic, "Test_Durable_Subscriber");

	        MessageListener listner = new MessageListener() {
	            public void onMessage(Message message) {
	            	System.out.println("111");
	                try {
	                	System.out.println("222");
	                    if (message instanceof BytesMessage) {
	                    	System.out.println("Byte");
	                    	BytesMessage textMessage = (BytesMessage) message;
	                    	
	                        byte[] byteArr = new byte[(int)textMessage.getBodyLength()];

	                        for (int i = 0; i < (int) textMessage.getBodyLength(); i++) {
	                            byteArr[i] = textMessage.readByte();
	                        }
	                        String msg = new String(byteArr);  
	                        System.out.println(msg);
	                    }else if (message instanceof TextMessage) {
	                    	TextMessage textMessage = (TextMessage) message;
	                    	 System.out.println("Received message:::::::::::::::"
		                                + textMessage.getText());
	                    	 System.out.println(textMessage.getStringProperty("to"));
	                    	 System.out.println(textMessage.getStringProperty("from"));
	                    	 System.out.println(textMessage.getStringProperty("transformation"));
	                    }
	                } catch (Exception e) {
	                    System.out.println("Caught:" + e);
	                    e.printStackTrace();
	                }
	            }
	        };

	        durableSubscriber.setMessageListener(listner);
	        Consumer a = new Consumer();
	       // a.callConsumer("345345", "554");
	    	/*try{
	    		Consumer a = new Consumer();
	    		a.pop();*/
	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}

	    }
	}    


