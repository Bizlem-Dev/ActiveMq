package co.apache;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.json.JSONException;
//import org.json.simple.JSONObject;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jspl.www.mailplatform.MailSenderServiceTest;

@SuppressWarnings("serial")
public class ConsumerClient extends HttpServlet {

	ResourceBundle bundle = ResourceBundle.getBundle("server");

	@Override
	public void init() throws ServletException {
		try {
			InputStream in = this.getClass().getClassLoader()
					.getResourceAsStream("Client.xml");
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(in);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("queue");
			for (int i = 0; i < list.getLength(); i++) {
				Node childNode = list.item(i);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element queueElement = (Element) childNode;
					System.out.println("calling call consumer--"+queueElement.getAttribute("topic"));
					callConsumer(queueElement.getAttribute("topic"),
							queueElement.getAttribute("clientId"),
							queueElement.getChildNodes());
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    String strToppicId = "";
	public void callConsumer(String topicId, String clientId,final NodeList actionNodes) {
		try {
			System.out.println("inside call consumer--"+topicId);
			TopicConnectionFactory connectionFactory = new ActiveMQConnectionFactory(bundle.getString("activeMQ.URL"));
			Connection connection = connectionFactory.createConnection();
			connection.setClientID(clientId);
			connection.start();

			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);

			Topic topic = session.createTopic(topicId);
			strToppicId = topicId;
			System.out.println("second strToppicId======="+strToppicId);
			TopicSubscriber durableSubscriber = session
					.createDurableSubscriber(topic, "Test_Durable_Subscriber");
			MessageListener listner = new MessageListener() {
				
				public void onMessage(Message message) {
					try {
						System.out.println("inside listerner---");
						if (message instanceof TextMessage) {
							System.out.println("This is called for Text message or when producer is from java project");
							TextMessage textMessage = (TextMessage) message;
							System.out.println("message:"
									+ textMessage.getText());
							for (int j = 0; j < actionNodes.getLength(); j++) {
								Node actionNode = actionNodes.item(j);
								if (actionNode.getNodeType() == Node.ELEMENT_NODE) {
									Element actionElement = (Element) actionNode;
									if (actionElement.hasAttribute("value")) {
										if (actionElement.getAttribute("value")
												.equals("mailplatform")) {
											Map<String, Object> mpMap = new HashMap<String, Object>();
											NodeList childNodes = actionElement
													.getElementsByTagName("key");
											mpMap = parseParameter(childNodes,
													textMessage);
											mailPlatform(mpMap);
										} else if (actionElement.getAttribute(
												"value").equals("restService")) {
											Map<String, Object> restMap = new HashMap<String, Object>();
											NodeList childNodes = actionElement
													.getElementsByTagName("key");
											restMap = parseParameter(
													childNodes, textMessage);
											restService(
													restMap,
													actionElement
															.getAttribute("method"),
													actionElement
															.getAttribute("url"));
										} else if (actionElement.getAttribute(
												"value").equals("soapService")) {
											Map<String, Object> soapMap = new HashMap<String, Object>();
											NodeList childNodes = actionElement
													.getElementsByTagName("key");
											soapMap = parseParameter(
													childNodes, textMessage);
											soapService(
													soapMap,
													actionElement
															.getAttribute("url"),
													actionElement
															.getAttribute("method"),
													actionElement
															.getAttribute("namespace"));
										} else if (actionElement.getAttribute(
												"value").equals("phpService")) {
											Map<String, Object> phpServiceMap = new HashMap<String, Object>();
											NodeList childNodes = actionElement
													.getElementsByTagName("key");
											phpServiceMap = parseParameter(
													childNodes, textMessage);
											phpService(
													phpServiceMap,
													actionElement
															.getAttribute("url"),
													actionElement
															.getAttribute("method"));
										}
									}
								}
							}

						} else {
							System.out.println("This is called for Non Text message or when producer is from php project");
							String result = "false";
							for (int j = 0; j < actionNodes.getLength(); j++) {
								Node actionNode = actionNodes.item(j);
								
//								String orderInfo ="";
								if (actionNode.getNodeType() == Node.ELEMENT_NODE) {
									System.out.println("inside node element");
									Element actionElement = (Element) actionNode;
									if (actionElement.hasAttribute("value")) {
										if (actionElement.getAttribute("value")
												.equals("restService")) {
											System.out.println("inside rest service");
											ActiveMQBytesMessage msg = (ActiveMQBytesMessage) message;
											ByteSequence b = msg.getContent();
											String orderInfo = new String(
													b.data);
											NodeList childNodes = actionElement.getElementsByTagName("key");
											Node childNode = childNodes.item(0);
											Element keyElement = (Element) childNode;
											String keyType = keyElement.getAttribute("value");

											System.out.print("third strToppicId Extra ========"+keyType);
											strToppicId=keyType;
											System.out.print("third strToppicId ========"+strToppicId);
											if(strToppicId.equals("commitOrder")){
											ArrayList<ProductBean> al = getProducts(orderInfo);
											System.out.println("getProdcut calls");
											if (al != null) {
												System.out.println("al is not ull");
												for (int i = 0; i < al.size(); i++) {
													System.out.print("calling restservice"+i);
													org.json.JSONObject postObj = null;
													
													 result =restService(
															OrderInfo(al.get(i)),
															actionElement
																	.getAttribute("method"),
															actionElement
																	.getAttribute("url"));
												//calling rest service for user creation on succesful purchase of products.
													 postObj = new org.json.JSONObject();
													 postObj.put( "username",al.get(i).getCustomerId());
													 postObj.put( "roleid",1);
													 postObj.put( "isactive",0);
													 
													String name = uploadToServer(bundle.getString("restUserService.URL"),postObj);
													 
												}

											}
											}else if(strToppicId.equals("VtigerService")){
												
 												ArrayList<CartServiceBean> al = getCartService(orderInfo);
												System.out.println("service calls");
												if (al != null) {
													System.out.println("al is not ull");
													for (int i = 0; i < al.size(); i++) {
														System.out.print("calling restservice"+i);
														
														 result =restService(
																createService(al.get(i)),
																actionElement
																		.getAttribute("method"),
																actionElement
																		.getAttribute("url"));
													}

												}	
												
												
											}else if(strToppicId.equals("CheckOutService")){
												ArrayList<CheckOutCartServiceBeanSO> al = getCheckOutCartService(orderInfo);
												System.out.println("CheckOutService calls");
												if (al != null) {
													System.out.println("al is not ull");
													for (int i = 0; i < al.size(); i++) {
														System.out.print("calling restservice"+i);
													   result =restService(
																 createCheckOutService(al.get(i)),
																actionElement
																		.getAttribute("method"),
																actionElement
																		.getAttribute("url"));
													}

												}	
												
												
											}
											else if(strToppicId.equals("CreateService")){
												ArrayList<CreateServiceBean> al = getCreateService(orderInfo);
												System.out.println("CheckOutService calls");
												if (al != null) {
													System.out.println("al is not ull");
													for (int i = 0; i < al.size(); i++) {
														System.out.print("calling restservice"+i);
													   result =restService(
															   createCreateService(al.get(i)),
																actionElement
																		.getAttribute("method"),
																actionElement
																		.getAttribute("url"));
													}

												}	
												
												
											}
											else if(strToppicId.equals("UpdateService")){
												ArrayList<CreateServiceBean> al = getUpdateService(orderInfo);
												System.out.println("CheckOutService calls");
												if (al != null) {
													System.out.println("al is not ull");
													for (int i = 0; i < al.size(); i++) {
														System.out.print("calling restservice"+i);
													   result =restService(
															   createCreateService(al.get(i)),
																actionElement
																		.getAttribute("method"),
																actionElement
																		.getAttribute("url"));
													}

												}	
												
												
											}
											else if(strToppicId.equals("DeleteService")){
												ArrayList<DeleteServiceBean> al = getDeleteService(orderInfo);
												System.out.println("CheckOutService calls");
												if (al != null) {
													System.out.println("al is not ull");
													for (int i = 0; i < al.size(); i++) {
														System.out.print("calling restservice"+i);
													   result =restService(
															   createDeleteService(al.get(i)),
																actionElement
																		.getAttribute("method"),
																actionElement
																		.getAttribute("url"));
													}

												}	
												
												
											}
											

										}else if (actionElement.getAttribute("value")
												.equals("mailplatform") && result.equals("true")) {
											System.out.println("inside rest service");
											ActiveMQBytesMessage msg1 = (ActiveMQBytesMessage) message;
											ByteSequence b1 = msg1.getContent();
											String orderInfo1 = new String(
													b1.data);
											Map<String, Object> mpMap = new HashMap<String, Object>();
											NodeList childNodes = actionElement
													.getElementsByTagName("key");
											mpMap = parseParameter1(childNodes,orderInfo1);
											mailPlatform(mpMap);
										}
									}
								}
							}
						}
					} catch (Exception e) {
						System.out.println("Caught:" + e);
						e.printStackTrace();
					}
				}

				/*private void uploadToServer(String string, org.json.simple.JSONObject postObj) {
					// TODO Auto-generated method stub
					
				}*/
			};
			durableSubscriber.setMessageListener(listner);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Map<String, Object> parseParameter(NodeList childNodes,
			TextMessage textMessage) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			for (int k = 0; k < childNodes.getLength(); k++) {
				Node childNode = childNodes.item(k);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element keyElement = (Element) childNode;
					String keyType = keyElement.getAttribute("type");
					if (keyType.equals("parameter")) {

						if (!keyElement.getAttribute("map").equals("")) {
							map.put(keyElement.getAttribute("name"),
									textMessage.getStringProperty(keyElement
											.getAttribute("map")));
							System.out.println("map " +map);
						} else {
							map.put(keyElement.getAttribute("name"),
									keyElement.getAttribute("value"));
							System.out.println("map " +map);
						}
						System.out.println("map " +map);
					} else if (keyType.equals("list")) {
						if (keyElement.hasChildNodes()) {
							Map<String, Object> childMap = new HashMap<String, Object>();
							NodeList keyChildNode = keyElement
									.getElementsByTagName(keyElement
											.getAttribute("name"));
							childMap = parseParameter(keyChildNode, textMessage);
							map.put(keyElement.getAttribute("name"), childMap);
						} else {
							map.put(keyElement.getAttribute("name"),
									keyElement.getAttribute("value"));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	
	private Map<String, Object> parseParameter1(NodeList childNodes,String orderInfo) {
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println("parseParameter1---"+orderInfo);
		
		try {
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) new JSONParser().parse(orderInfo);
			for (int k = 0; k < childNodes.getLength(); k++) {
				Node childNode = childNodes.item(k);
				if (childNode.getNodeType() == Node.ELEMENT_NODE) {
					Element keyElement = (Element) childNode;
					String keyType = keyElement.getAttribute("type");
					if (keyType.equals("parameter")) {

						if (!keyElement.getAttribute("map").equals("")) {
							map.put(keyElement.getAttribute("name"),
									json.get("customerId").toString()
//									textMessage.getStringProperty(keyElement
//											.getAttribute("map"))
									);
						} else {
							map.put(keyElement.getAttribute("name"),
									keyElement.getAttribute("value"));
						}
					} 
				}
			}
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	
	
	private void mailPlatform(Map<String, Object> mpMap) {
		MailSenderServiceTest mail = new MailSenderServiceTest();
		String[] users = mpMap.get("userId").toString().split(",");
		String eventId = mpMap.get("eventId").toString().trim();
		String[] otherField = new String[(mpMap.size() - 1)];
		String[] BCC = mpMap.get("BCC").toString().trim().split(",");
		String[] CC = mpMap.get("CC").toString().trim().split(",");
		for (Object key : mpMap.keySet()) {
			if (!key.toString().trim().equals("userId")
					&& !key.toString().trim().equals("BCC")
					&& !key.toString().trim().equals("CC")
					&& !key.toString().trim().equals("eventId")) {
				int index = Integer.parseInt(key.toString()
						.replace("other", ""));
				otherField[index] = mpMap.get(key).toString().trim();
			}

		}

		System.out.println(users[0] + "<<<<<>>>>>>" + mpMap);
		try {
			mail.testsendCustomEmail(eventId, users, otherField, CC, BCC);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String uploadToServer(String urlstr, JSONObject json) throws IOException, JSONException {
		StringBuilder response = null;
		URL url = null;
		HttpURLConnection con = null;
		DataOutputStream wr = null;
		BufferedReader in = null;
		// .replaceAll("[╔,û#á]", "")
		// BufferedWriter writer = null;
		try {
			// out.println("Json passed is "+json);
			url = new URL(urlstr);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");

			con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

			con.setDoOutput(true);
			wr = new DataOutputStream(con.getOutputStream());
			// writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
			// writer.write(json.toString());
			wr.writeBytes(json.toString());
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine = null;
			response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			// System.out.println(response.toString());

		} catch (Exception e) {
			response = null;

		} finally {

			if (null != con) {
				con.disconnect();
				con = null;
			}
			if (null != wr) {
				wr.flush();
				wr.close();
				wr = null;
			}
			/*
			 * if(null != writer){ writer.flush(); writer.close(); writer = null; }
			 */
			if (null != in) {
				in.close();
				in = null;
			}
		}
		return (response == null ? "" : response.toString());

	}

	private String restService(Map<String, Object> restMap, String methodType,
			String url) {
		ArrayList<String> paramKey = new ArrayList<String>();
		ArrayList<String> paramValue = new ArrayList<String>();
		String res = "";
		for (Object key : restMap.keySet()) {
			paramKey.add(key.toString().trim());
			paramValue.add(restMap.get(key).toString().trim());

		}
		System.out.println("Rest--->>" + paramKey + "-------" + paramValue
				+ "^^^^" + url + "~~~~" + methodType);
		if (methodType.equalsIgnoreCase("GET")) {
			callGetService(url, paramKey.toArray(new String[paramKey.size()]),
					paramValue.toArray(new String[paramKey.size()]));
		} else if (methodType.equalsIgnoreCase("POST")) {
			res = callPostService(url, paramKey.toArray(new String[paramKey.size()]),
					paramValue.toArray(new String[paramKey.size()]));
		}
		return res;

	}

	private void soapService(Map<String, Object> soapMap, String url,
			String operation, String nameSpace) {
		List<String> paramKey = new ArrayList<String>();
		List<String> paramValue = new ArrayList<String>();
		for (Object key : soapMap.keySet()) {
			paramKey.add(key.toString().trim());
			paramValue.add(soapMap.get(key).toString().trim());

		}
		System.out.println("SOap--->>" + paramKey + "-------" + paramValue
				+ "^^^^" + url + "~~~~" + operation);
		ConsumeSoapWebService c = new ConsumeSoapWebService();
		c.soapFactory(url, paramKey, paramValue, operation, nameSpace);
	}

	@SuppressWarnings("unchecked")
	private void phpService(Map<String, Object> phpServiceMap, String url,
			String methodType) {
		ArrayList<String> paramKey = new ArrayList<String>();
		ArrayList<String> paramValue = new ArrayList<String>();
		JSONObject json = new JSONObject();
		for (Object key : phpServiceMap.keySet()) {
			paramKey.add(key.toString().trim());
			if (key.toString().trim().equals("messagebody")) {
				json = new JSONObject();
				Map<String, Object> childMap = (Map<String, Object>) phpServiceMap
						.get(key);
				try {
					for (Object childKey : childMap.keySet()) {
						json.accumulate(childKey.toString().trim(), childMap
								.get(childKey.toString()).toString().trim());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				paramValue.add(json.toString());
			} else {
				paramValue.add(phpServiceMap.get(key).toString().trim());
			}

		}
		System.out.println("PHPRest--->>" + paramKey + "-------" + paramValue
				+ "^^^^" + url + "~~~~" + methodType);
		if (methodType.equalsIgnoreCase("GET")) {
			callGetService(url, paramKey.toArray(new String[paramKey.size()]),
					paramValue.toArray(new String[paramKey.size()]));
		} else if (methodType.equalsIgnoreCase("POST")) {
			callPostService(url, paramKey.toArray(new String[paramKey.size()]),
					paramValue.toArray(new String[paramKey.size()]));
		}
	}

	public String callPostService(String urlStr, String[] paramName,
			String[] paramValue) {

		StringBuilder response = new StringBuilder();
		URL url;
		try {
			System.out.println("caalign callPostService");
			url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			OutputStream out = conn.getOutputStream();
			Writer writer = new OutputStreamWriter(out, "UTF-8");
			for (int i = 0; i < paramName.length; i++) {
				writer.write(paramName[i]);
				writer.write("=");
				writer.write(URLEncoder.encode(paramValue[i], "UTF-8"));
				writer.write("&");
			}
			writer.close();
			out.close();
			if (conn.getResponseCode() != 200) {
				System.out.println("indeside caalign responce"+conn.getResponseCode());
			}
			System.out.println("caalign responce");
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();
			System.out.println("closinng conneciotn"+conn.getResponseCode());
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();

		}
		System.out.println("priting responce");
		System.out.println(response.toString()+"~~~~~~~~~~~~~~~~~~~~~~~");
		return response.toString();
	}

	public String callGetService(String urlStr, String[] paramName,
			String[] paramValue) {
		URL url;
		StringBuilder requestString = new StringBuilder(urlStr);
		if (paramName != null && paramName.length > 0) {
			requestString.append("?");
			for (int i = 0; i < paramName.length; i++) {
				requestString.append(paramName[i]);
				requestString.append("=");
				requestString.append(paramValue[i]);
				requestString.append("&");
			}
		}
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(requestString.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return sb.toString();
	}

	
	public ArrayList<CartServiceBean> getCartService(String orderInfo) {
		ArrayList<CartServiceBean> al = new ArrayList<CartServiceBean>();
		try {
			System.out.println(orderInfo);
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) new JSONParser().parse(orderInfo);
			CartServiceBean p = null;
			if (json != null) {
				JSONArray a = (JSONArray) json.get("service");
				for (int i = 0; i < a.size(); i++) {
					 p = new CartServiceBean();

					org.json.simple.JSONObject product = (org.json.simple.JSONObject) new JSONParser().parse(a
							.get(i).toString());
					p.setStrServicename(product.get("servicename").toString());
					p.setPrice(product.get("unit_price").toString());
					p.setFromDate(product.get("sales_start_date").toString());
					p.setToDate(product.get("sales_end_date").toString());
					p.setDescription(product.get("description").toString());
					}
					al.add(p);
					
				}

			}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}
	
	public ArrayList<CreateServiceBean> getCreateService(String orderInfo) {
		ArrayList<CreateServiceBean> al = new ArrayList<CreateServiceBean>();
		try {
			System.out.println(orderInfo);
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) new JSONParser().parse(orderInfo);
			CreateServiceBean p = null;
			if (json != null) {
				JSONArray a = (JSONArray) json.get("service");
				for (int i = 0; i < a.size(); i++) {
					 p = new CreateServiceBean();

					org.json.simple.JSONObject product = (org.json.simple.JSONObject) new JSONParser().parse(a
							.get(i).toString());
					p.setServicename(product.get("servicename").toString());
					p.setPrice(product.get("quantity").toString());
					p.setQuantity(product.get("price").toString());
					p.setDescription(product.get("description").toString());
					
					if(!(product.get("servicename").toString()==null)){
						System.out.println("results inside 1");
						Map<String, Object> smap = new HashMap<String, Object>();
						try {
							smap.put("servicename",product.get("servicename").toString());
							}catch (Exception e) {
							
						     }
					String results="";
					results =restService(smap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/getService.php");
					if(!(results.equals("norecord"))){
						//p.setService_id(results);
						System.out.println("results inside 2");
						System.out.println("results "+results);
					//	return;	
					  }else{
						
					  }
					}
					System.out.println("results inside 3");
					
					}
					al.add(p);
					
				}

			}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}
	public ArrayList<CreateServiceBean> getUpdateService(String orderInfo) {
		ArrayList<CreateServiceBean> al = new ArrayList<CreateServiceBean>();
		try {
			System.out.println(orderInfo);
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) new JSONParser().parse(orderInfo);
			CreateServiceBean p = null;
			if (json != null) {
				JSONArray a = (JSONArray) json.get("service");
				for (int i = 0; i < a.size(); i++) {
					 p = new CreateServiceBean();

					org.json.simple.JSONObject product = (org.json.simple.JSONObject) new JSONParser().parse(a
							.get(i).toString());
					p.setServicename(product.get("servicename").toString());
					p.setPrice(product.get("price").toString());
					p.setQuantity(product.get("quantity").toString());
					p.setDescription(product.get("description").toString());
					
					if(!(product.get("servicename").toString()==null)){
						System.out.println("results inside 1");
						Map<String, Object> smap = new HashMap<String, Object>();
						try {
							smap.put("servicename",product.get("servicename").toString());
							}catch (Exception e) {
							
						     }
					String results="";
					//bundle.getString("phpServer.URL")+
					results =restService(smap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/getService.php");
					if(results.equals("norecord")){
						System.out.println("No records found to update");
					 }else{
						  p.setService_id(results);
					  }
					}
					
					}
					al.add(p);
					
				}

			}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}
	
	public ArrayList<DeleteServiceBean> getDeleteService(String orderInfo) {
		ArrayList<DeleteServiceBean> al = new ArrayList<DeleteServiceBean>();
	//	JSONObject servicejson =null;
    //    JSONArray  servicearray= new JSONArray();
    //    JSONObject servicedetails = new JSONObject();
        String delete_servicename="";
    //    String contactname="";
    //    String lastname="";
    //    String email="";
    //    String organization="";
    //    String description="";
        
        String contact_id="";
        String organization_id="";
        String service_id="";
       // String orderInfo1 = "{\"service\":[{\"servicelist\":[[],{\"servicename\":\"Sharma Industrial Ltd\",\"quantity\":\"1\",\"unit_price\":\"200.0000\",\"sales_start_date\":\"2016-10-12\",\"sales_end_date\":\"2017-10-12\"},{\"servicename\":\"iMac\",\"quantity\":\"1\",\"unit_price\":\"100.0000\",\"sales_start_date\":\"2016-10-12\",\"sales_end_date\":\"2017-10-12\"}],\"contactname\":\"AkhileshKumar\",\"lastname\":\"Yadav\",\"email\":\"akhileshyadav.biz@gmail.com\",\"organization\":\"bizlem\",\"description\":\"hi today test\"}]}";
		try {
			System.out.println("orderInfo inside getCheckOutCartService"+orderInfo);
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) new JSONParser().parse(orderInfo);
			DeleteServiceBean p = null;
			
			if (json != null) {
				JSONArray a = (JSONArray) json.get("service");
				for (int j = 0; j < a.size(); j++) {
					 p = new DeleteServiceBean();

					org.json.simple.JSONObject product = (org.json.simple.JSONObject) new JSONParser().parse(a
							.get(j).toString());
					
		           // JSONArray servicelist = (JSONArray) product.get("deleteservicelist");
		            delete_servicename =product.get("deleteservicelist").toString();
		    
			      
		            if(!(delete_servicename==null)){
						Map<String, Object> smap = new HashMap<String, Object>();
						try {
							smap.put("servicename",delete_servicename);
							}catch (Exception e) {
							
						     }
					String results="";
					results =restService(smap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/getService.php");
					if(!(results.equals("norecord"))){
						service_id=results;
						System.out.println("service_id "+service_id);
					}else{
						
					 }
					}
			  /*
			  
			  for (int i = 1; i < servicelist.size(); i++) {
                  if (i == 0) {
                 // for i==0 it is returning empty array from producer
                  }
                 else {
                   org.json.simple.JSONObject service = (org.json.simple.JSONObject) new JSONParser().parse(servicelist.get(i).toString());
                 
                   if(!(service.get("servicename").toString()==null)){
						Map<String, Object> smap = new HashMap<String, Object>();
						try {
							smap.put("servicename",service.get("servicename").toString());
							}catch (Exception e) {
							
						     }
					String results="";
					results =restService(smap,"POST","http://35.186.173.106/vtigercrm/webservice_for_opencart/getService.php");
					if(!(results.equals("norecord"))){
						service_id=results;
						System.out.println("service_id "+service_id);
					}else{
						
					 }
					}
                   servicejson = new JSONObject();
                   servicejson.put("servicename", service_id);
                   servicejson.put("quantity", service.get("quantity"));
                   servicejson.put("unit_price", service.get("unit_price"));
                   servicejson.put("sales_start_date", service.get("sales_start_date"));
                   servicejson.put("sales_end_date", service.get("sales_end_date"));
                  }
               servicearray.add(servicejson);
             }
			  */
            //   servicedetails.put("servicename", servicearray);
           //   p.setStrServicename(servicedetails.toString());
		            p.setDeleteservice(service_id);
					
					
				}
					al.add(p);
					
				}

			}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}
	
	public ArrayList<CheckOutCartServiceBeanSO> getCheckOutCartService(String orderInfo) {
		ArrayList<CheckOutCartServiceBeanSO> al = new ArrayList<CheckOutCartServiceBeanSO>();
		JSONObject servicejson =null;
        JSONArray  servicearray= new JSONArray();
        JSONObject servicedetails = new JSONObject();
        String contactname="";
        String lastname="";
        String email="";
        String organization="";
        String description="";
        
        String contact_id="";
        String organization_id="";
        String service_id="";
       // String orderInfo1 = "{\"service\":[{\"servicelist\":[[],{\"servicename\":\"Sharma Industrial Ltd\",\"quantity\":\"1\",\"unit_price\":\"200.0000\",\"sales_start_date\":\"2016-10-12\",\"sales_end_date\":\"2017-10-12\"},{\"servicename\":\"iMac\",\"quantity\":\"1\",\"unit_price\":\"100.0000\",\"sales_start_date\":\"2016-10-12\",\"sales_end_date\":\"2017-10-12\"}],\"contactname\":\"AkhileshKumar\",\"lastname\":\"Yadav\",\"email\":\"akhileshyadav.biz@gmail.com\",\"organization\":\"bizlem\",\"description\":\"hi today test\"}]}";
		try {
			System.out.println("orderInfo inside getCheckOutCartService"+orderInfo);
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) new JSONParser().parse(orderInfo);
			CheckOutCartServiceBeanSO p = null;
			
			if (json != null) {
			//	JSONArray a = (JSONArray) json.get("service");
				JSONArray a = (JSONArray) json.get("service");
		for (int j = 0; j < a.size(); j++) {
					 p = new CheckOutCartServiceBeanSO();

					org.json.simple.JSONObject product = (org.json.simple.JSONObject) new JSONParser().parse(a
							.get(j).toString());
					contactname=product.get("contactname").toString();
		            lastname=product.get("lastname").toString();
		            email=product.get("email").toString();
		            organization=product.get("organization").toString();
		            description=product.get("description").toString();
		            JSONArray servicelist = (JSONArray) product.get("servicelist");
		            
			  if(!(contactname==null)){
						Map<String, Object> cmap = new HashMap<String, Object>();
						  try {
							    cmap.put("lastname",product.get("lastname").toString());
							     cmap.put("email",product.get("email").toString());
							  }catch (Exception e) {}
						String resultc="";
						resultc =restService(cmap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/getContact.php");
					   System.out.println("resultc "+resultc);
				    	if(!(resultc.equals("norecord"))){
						     contact_id=resultc;
						     System.out.println("contact_id "+contact_id);
					    }else{
						     Map<String, Object> cccmap = new HashMap<String, Object>();
				    	      try {
				    	        	 cccmap.put("firstname", product.get("contactname").toString());
				    	        	 cccmap.put("lastname", product.get("lastname").toString());
				    	        	 cccmap.put("mobile", "9819384655");
				    	        	 cccmap.put("email", product.get("email").toString());
								  }catch (Exception e) {}
							  contact_id=restService(cccmap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/createContact.php");
					        }
					}
			  if(!(organization==null)){
					Map<String, Object> omap = new HashMap<String, Object>();
					try {
						omap.put("companyname", product.get("organization").toString());
						}catch (Exception e) {}
					String resulto="";
				    resulto =restService(omap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/getOrganization.php");
				     if(!resulto.equals("norecord")){
				    	    organization_id=resulto;
							System.out.println("organization_id "+organization_id);
						} else{
				    	      Map<String, Object> ocmap = new HashMap<String, Object>();
				    	      try {
				    	        	 ocmap.put("companyname", product.get("organization").toString());
								  }catch (Exception e) {}
					         organization_id=restService(ocmap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/createOrganization.php");	
							
						}
				    }
			  
			  
			  for (int i = 1; i < servicelist.size(); i++) {
                  if (i == 0) {
                 // for i==0 it is returning empty array from producer
                  }
                 else {
                   org.json.simple.JSONObject service = (org.json.simple.JSONObject) new JSONParser().parse(servicelist.get(i).toString());
                 
                   if(!(service.get("servicename").toString()==null)){
						Map<String, Object> smap = new HashMap<String, Object>();
						try {
							smap.put("servicename",service.get("servicename").toString());
							}catch (Exception e) {
							
						     }
					String results="";
					results =restService(smap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/getService.php");
					if(!(results.equals("norecord"))){
						service_id=results;
						System.out.println("service_id "+service_id);
					}else{
						// if service is not available in vtiger then use this return so that service is not created
						// or use pop to show the user service is not available
						//return ;	
						/*
						Map<String, Object> cscmap = new HashMap<String, Object>();
				    	 try {
				    		 cscmap.put("servicename", service.get("servicename").toString());
										}catch (Exception e) {
								
							     }
						service_id=restService(cscmap,"POST","http://35.186.173.106/vtigercrm/webservice_for_opencart/createService.php");
						*/
						
					}
					}
                   servicejson = new JSONObject();
                   servicejson.put("servicename", service_id);
                   servicejson.put("quantity", service.get("quantity"));
                   servicejson.put("unit_price", service.get("unit_price"));
                   servicejson.put("sales_start_date", service.get("sales_start_date"));
                   servicejson.put("sales_end_date", service.get("sales_end_date"));
                  }
               servicearray.add(servicejson);
           }
               servicedetails.put("servicename", servicearray);
            //   System.out.println("servicedetails "+servicedetails);
             //  String s=servicedetails.toString();
              // System.out.println(" s   "+s);
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
			  
					
					
					
					
					
					
					Map<String, Object> somap = new HashMap<String, Object>();
					try {

						somap.put("servicename", servicedetails.toString());
						somap.put("lastname", lastname);
						somap.put("contactname",contact_id);
						somap.put("contactname1", contactname);
						somap.put("email", email);
						somap.put("organization", organization_id);
						somap.put("description", description);
			            
						
						} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String salesorder_id =restService(somap,"POST",bundle.getString("phpServer.URL")+"/vtigercrm/webservice_for_opencart/createSalesOrder.php");
					
					
					p.setStrServicename(servicedetails.toString());
					p.setLastname(lastname);
					p.setContactname(contact_id);
					p.setContactname1(contactname);
					p.setEmail(email);
					p.setOrganization(organization_id);
					p.setDescription(description);
					p.setSalesorder(salesorder_id);
					
				}
					al.add(p);
					
				}

			}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}
	
	
	
	public ArrayList<ProductBean> getProducts(String orderInfo) {
		ArrayList<ProductBean> al = new ArrayList<ProductBean>();
		try {
			System.out.println(orderInfo);
			org.json.simple.JSONObject json = (org.json.simple.JSONObject) new JSONParser().parse(orderInfo);
			if (json != null) {
				JSONArray a = (JSONArray) json.get("products");
				for (int i = 0; i < a.size(); i++) {
					ProductBean p = new ProductBean();

					org.json.simple.JSONObject product = (org.json.simple.JSONObject) new JSONParser().parse(a
							.get(i).toString());
					p.setProductId(product.get("prodcustcode").toString());
					JSONArray attributes = (JSONArray) product.get("attribute");
					for (int j = 0; j < attributes.size(); j++) {
						p.setCustomerFName(json.get("customerFName")
								+ " " + json.get("customerLName"));
						p.setCustomerId(json.get("customerId").toString());
						org.json.simple.JSONObject attvalues = (org.json.simple.JSONObject) new JSONParser()
								.parse(attributes.get(j).toString());
						String name = (String) attvalues.get("name");
						String value = (String) attvalues.get("value");
						String type = (String) attvalues.get("type");
						System.out.println(name+"-----"+value+"-----"+type);
						if ((name.indexOf("accounts")!=-1 || name.indexOf("Accounts")!=-1) && type.equalsIgnoreCase("select")) {
							p.setAccountCount(Long.valueOf(value));
						}
						if ((name.indexOf("quantity")!=-1 || name.indexOf("Quantity")!=-1)&&  type.equalsIgnoreCase("select")) {
							p.setQuantity(Long.valueOf(value));
						}
						
						if ((name.indexOf("months")!=-1 || name.indexOf("Months")!=-1) && type.equalsIgnoreCase("select")) {
							p.setServiceMothCoun(Long.valueOf(value));
						}
						
						if ((name.indexOf("records")!=-1 || name.indexOf("Records")!=-1) && type.equalsIgnoreCase("select")) {
							p.setServiceRecordCoun(Long.valueOf(value));
						}
						
						if ((name.indexOf("response")!=-1 || name.indexOf("response")!=-1) && type.equalsIgnoreCase("select")) {
							p.setServiceResponseCoun(Long.valueOf(value));
							//No of response
						}
						
					}
					al.add(p);
					
				}

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}

	public Map<String, Object> OrderInfo(ProductBean p) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			map.put("userId", p.getCustomerId());
			map.put("serviceId", p.getProductId());
			map.put("quantity", p.getQuantity());
			map.put("months", p.getServiceMothCoun());
			map.put("records", p.getServiceRecordCoun());
			map.put("response", p.getServiceResponseCoun());
			map.put("accounts", p.getAccountCount());			
			map.put("name", p.getCustomerFName());
			map.put("sdate", p.getFromDate());
			map.put("edate", p.getToDate());
			map.put("type", "commitOrder");
			} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}

	public Map<String, Object> createService(CartServiceBean p) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			map.put("servicename", p.getStrServicename());
			map.put("unit_price", p.getPrice());
			map.put("sales_start_date", p.getFromDate());
			map.put("sales_end_date", p.getToDate());
			map.put("description", p.getDescription());			
			
			} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}
	public Map<String, Object> createDeleteService(DeleteServiceBean p) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			map.put("servicename", p.getDeleteservice());
			} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}
	
	
	public Map<String, Object> createCheckOutService(CheckOutCartServiceBeanSO p) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			map.put("servicename", p.getStrServicename());
			map.put("lastname", p.getLastname());
			map.put("contactname", p.getContactname());
			map.put("contactname1", p.getContactname1());
			map.put("email", p.getEmail());
			map.put("organization", p.getOrganization());
			map.put("description", p.getDescription());
			map.put("salesorder", p.getSalesorder());		
			
			} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}
	public Map<String, Object> createCreateService(CreateServiceBean p) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			map.put("servicename", p.getServicename());
			map.put("quantity", p.getQuantity());
			map.put("price", p.getPrice());
			map.put("description", p.getDescription());
			map.put("service_id", p.getService_id());
			
			
			} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return map;
	}
	
	

	
	public static void main(String[] args) {
		ConsumerClient a = new ConsumerClient();
		String[] paramKey = { "serviceId", "name", "userId", "quantity", "type" };
		String[] paramValue = { "Chat", "Deepak.kumar",
				"deepak.kumar@visioninfocon.com", "2.00", "activator" };
		a.callPostService(
				"http://10.36.76.123:8085/ServiceLogging/consumption",
				paramKey, paramValue);

		List<String> list = new ArrayList<String>();
		List<String> value = new ArrayList<String>();
		list.add("event-id");
		list.add("user");
		value.add("156");
		value.add("pranav");
		ConsumeSoapWebService c = new ConsumeSoapWebService();
		c.soapFactory(
				"http://10.36.76.89:8080/mailplatform/services/MailSender",
				list, value, "sendEmail", "http://www.jspl.com/mailplatform/");

	}

}
