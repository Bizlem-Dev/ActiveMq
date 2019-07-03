package co.apache;

import java.util.ArrayList;
import java.util.List;

import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

public class ConsumeSoapWebService {

	public void soapFactory(String URL, List<String> list, List<String> value,
			String operation, String nameSpace) {
		try {

			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
					.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory
					.createConnection();

			SOAPMessage soapResponse = soapConnection.call(
					createSOAPRequest(list, value, operation, nameSpace), URL);

			printSOAPResponse(soapResponse);

			soapConnection.close();
		} catch (Exception e) {
			System.err
					.println("Error occurred while sending SOAP Request to Server");
			e.printStackTrace();
		}
	}

	private static SOAPMessage createSOAPRequest(List<String> list,
			List<String> value, String operation, String nameSpace)
			throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String serverURI = nameSpace;

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("", serverURI);

		SOAPBody soapBody = envelope.getBody();

		SOAPElement soapBodyElem = soapBody.addChildElement(operation, "");
		for (int i = 0; i < list.size(); i++) {
			SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(
					list.get(i), "");
			soapBodyElem1.addTextNode(value.get(i));
		}

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", serverURI + operation);

		soapMessage.saveChanges();

		System.out.print("Request SOAP Message = ");
		soapMessage.writeTo(System.out);
		System.out.println();

		return soapMessage;
	}

	/**
	 * Method used to print the SOAP Response
	 */
	private static void printSOAPResponse(SOAPMessage soapResponse)
			throws Exception {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		Source sourceContent = soapResponse.getSOAPPart().getContent();
		System.out.print("\nResponse SOAP Message = ");
		StreamResult result = new StreamResult(System.out);
		transformer.transform(sourceContent, result);
	}

	public static void main(String args[]) {
		ConsumeSoapWebService c = new ConsumeSoapWebService();
		List<String> list = new ArrayList<String>();
		List<String> value = new ArrayList<String>();
		list.add("event-id");
		list.add("user");
		value.add("156");
		value.add("pranav");
		c.soapFactory(
				"http://10.36.76.18:8080/mailplatform/services/MailSender",
				list, value, "sendEmail", "http://www.jspl.com/mailplatform/");
	}

}
