

/**
 * MailSenderServiceTest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
    package com.jspl.www.mailplatform;

import com.jspl.www.mailplatform.MailSenderServiceStub.MailPartVO;
import com.jspl.www.mailplatform.MailSenderServiceStub.SendCustomEmail;
import com.jspl.www.mailplatform.MailSenderServiceStub.SendEmail;

    /*
     *  MailSenderServiceTest Junit test case
    */

    public  class MailSenderServiceTest extends junit.framework.TestCase{

     
          /**
          * Auto generated test method
          */
          public  void testsendCustomEmail(String evenId,String users[], String[] otherDetails,
        		  String[] CC, String[] BCC) throws java.lang.Exception{

          com.jspl.www.mailplatform.MailSenderServiceStub stub =
          new com.jspl.www.mailplatform.MailSenderServiceStub();//the default implementation should point to the right endpoint
          com.jspl.www.mailplatform.MailSenderServiceStub.SendCustomEmailE sendCustomEmail4=
                  (com.jspl.www.mailplatform.MailSenderServiceStub.SendCustomEmailE)getTestObject(com.jspl.www.mailplatform.MailSenderServiceStub.SendCustomEmailE.class);
                      // TODO : Fill in the sendCustomEmail4 here
                  

                  //There is no output to be tested!
          MailPartVO part = new MailPartVO();
          SendCustomEmail email=new SendCustomEmail();
      	email.setEventId(evenId);
      	email.setUser(users);
      	part.setMailCc(CC);
      	part.setMailBcc(BCC);
      	email.setOtherDetails(otherDetails);
      	email.setMailPart(part);
      	sendCustomEmail4.setSendCustomEmail(email);
                  stub.sendCustomEmail(
                  sendCustomEmail4);

              
          }
      
          /**
          * Auto generated test method
          */
          public  void testsendEmail() throws java.lang.Exception{

          com.jspl.www.mailplatform.MailSenderServiceStub stub =
          new com.jspl.www.mailplatform.MailSenderServiceStub();//the default implementation should point to the right endpoint
          com.jspl.www.mailplatform.MailSenderServiceStub.SendEmailE sendEmail5=
                  (com.jspl.www.mailplatform.MailSenderServiceStub.SendEmailE)getTestObject(com.jspl.www.mailplatform.MailSenderServiceStub.SendEmailE.class);
                      // TODO : Fill in the sendEmail5 here
                  

                  //There is no output to be tested!
          SendEmail email=new SendEmail();
        	email.setEventId("44");
//        	email.
        	String[] user = {"pran3bull@gmail.com"};
        	email.setUser(user);
        	
        	sendEmail5.setSendEmail(email);
                  stub.sendEmail(
                  sendEmail5);

              
          }
      
        //Create an ADBBean and provide it as the test object
        public org.apache.axis2.databinding.ADBBean getTestObject(java.lang.Class type) throws java.lang.Exception{
           return (org.apache.axis2.databinding.ADBBean) type.newInstance();
        }

        
        
public static void main(String[] args) {
	MailSenderServiceTest a = new MailSenderServiceTest();
	try {
		String[] user = {"pran3bull@gmail.com"};
		String[] user2 = {"fdfgdfg"};
        String[] param = {"pran3bull@gmail.com","pranav.arya@visioninfocon.com"};
      	//String[] otherDetails = {"pranav","himanshu"};
		a.testsendCustomEmail("44",user,user2,param,null);
		//a.testsendEmail();
		System.out.println("hello");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
    }
    