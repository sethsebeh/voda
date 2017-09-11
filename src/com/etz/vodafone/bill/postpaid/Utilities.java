/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.vodafone.bill.postpaid;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import sun.misc.BASE64Encoder;

/**
 *
 * @author seth.sebeh
 */
public class Utilities {
    
    public static String generateSOAPMessageAsString(Object obj) throws Exception{        
        String usernameStr = "TEST";
        String passwordStr = "59Lr/?2baJ#z6^%(";
        String ACCOUNT_ID = "test3";
        
        //From the spec: Password_Digest = Base64 ( SHA-1 ( nonce + created + password ) )
        //Make the nonce
        SecureRandom rand = SecureRandom.getInstance("SHA1PRNG");
        rand.setSeed(System.currentTimeMillis());
        byte[] nonceBytes = new byte[16];
        rand.nextBytes(nonceBytes);
        
        //Make the created date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String createdDate = df.format(Calendar.getInstance().getTime());
        
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, 5);
        String expiredDate = df.format(now.getTime().getTime());
        
        byte[] createdDateBytes = createdDate.getBytes("UTF-8");
        
        //Make the password
        byte[] passwordBytes = passwordStr.getBytes("UTF-8");
        
        //SHA-1 hash the bunch of it.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(nonceBytes);
        baos.write(createdDateBytes);
        baos.write(passwordBytes);
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digestedPassword = md.digest(baos.toByteArray());
        
        //Encode the password and nonce for sending                   
        String passwordB64 = (new BASE64Encoder()).encode(digestedPassword);
        String nonceB64 = (new BASE64Encoder()).encode(nonceBytes);
        
      
         MessageFactory mf = MessageFactory.newInstance();
         SOAPMessage message = mf.createMessage();
         
         SOAPPart sp = message.getSOAPPart();
         SOAPEnvelope envelope = sp.getEnvelope();
         envelope.addNamespaceDeclaration("bill", "http://vodafone/bill/bill-payment-service");
         SOAPBody body = envelope.getBody();
         SOAPHeader header = envelope.getHeader();
         
        SOAPElement security = header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
          security.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
          security.addAttribute(new QName("SOAP-ENV:mustUnderstand"), "1");
        SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
        usernameToken.addAttribute(new QName("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        usernameToken.setAttribute("wsu:Id", "UsernameToken-EC9F7473E024359C6A14589178984712");

        SOAPElement username = usernameToken.addChildElement("Username", "wsse");       
        username.addTextNode(usernameStr);
        
        SOAPElement password = usernameToken.addChildElement("Password", "wsse");
        password.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest");
        password.addTextNode(passwordB64);
        
        SOAPElement nonce = usernameToken.addChildElement("Nonce", "wsse");
        nonce.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
        nonce.addTextNode(nonceB64);
        
        SOAPElement created = usernameToken.addChildElement("Created", "wsu");
        created.addTextNode(createdDate);
        
        SOAPElement timeStamp = security.addChildElement("Timestamp", "wsu");
        timeStamp.setAttribute("wsu:Id", "TS-EC9F7473E024359C6A14589178984531");
        
//        timeStamp.addChildElement(created);       
        
        SOAPElement timeStampCreated = timeStamp.addChildElement("Created", "wsu");
        timeStampCreated.addTextNode(createdDate);  
        
        SOAPElement expires = timeStamp.addChildElement("Expires", "wsu");
        expires.addTextNode(expiredDate);                   
        
        
        
        JAXBContext jc = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(obj, body);

        message.saveChanges();
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
       
        String soapMessageToPost = new String(outputStream.toByteArray());
       
        return soapMessageToPost;
    }
    
    public static String sendRequest(String soapMessageToPost) throws Exception{
        String ENDPOINT = "https://80.87.70.205:8001/ws";
        //post xml over http
        URL url = new URL(ENDPOINT);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
 
       //add reqest header
        con.setRequestMethod("POST");
	con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Accept", "text/xml; charset=utf-8");
        con.setRequestProperty("Content-type", "text/xml; charset=utf-8");
        con.setDoOutput(true);
        con.setDoInput(true);
        
	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(soapMessageToPost);
        wr.flush();
	wr.close();
        
        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
	}
	in.close();
        
        return response.toString();
    }
    
    
}
