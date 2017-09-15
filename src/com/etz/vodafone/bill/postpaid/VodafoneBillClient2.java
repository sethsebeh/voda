package com.etz.vodafone.bill.postpaid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;
import vodafone.bill.bill_payment_service.*;

public class VodafoneBillClient2 {
    
   //for testing environment only to bypass certificate validation errors. to be reomverd from production
    static {
        disableSslVerification();
    }  
    
    private static final String ENDPOINT = "https://80.87.70.205:8001/ws";
    
    public static void main(String[] args) throws JAXBException, IOException, Exception {
       
    try{
        
        //getCustomerInfo       
        GetCustomerInfoResponse getCustomerInfoResponse = getCustomerInfo("202240051");
        Customer customer = getCustomerInfoResponse.getCustomer();
        
        System.out.println("-------------------------" );
        System.out.println("getCustomerInfo()" );
        System.out.println("-------------------------" );
        //resultCode 0 is success
        if(customer.getResultCode().equals("0")){    
            System.out.println("AccountName>> " + customer.getAccountName());
            System.out.println("AccountNo>> " + customer.getAccountNo());           
            System.out.println("PaymentMode>> " + customer.getPaymentMode());
            System.out.println("CustomerName>> " + customer.getCustomerName());
            System.out.println("Billing Account>> " + customer.getBillingAccount());
            //other method calls of customer ommitted            
        }
        else{
            System.out.println("ResultCode>> " + customer.getResultCode());
            System.out.println("ResultDescription>> " + customer.getResultDescr());
        }
        System.out.println();
             
        
        //getBalance
        GetBalanceResponse getBalanceResponse = getBalance("302671694");
        Balance balance = getBalanceResponse.getBalance();
        System.out.println("-------------------------" );
        System.out.println("getBalance()" );
        System.out.println("-------------------------" );
        if(balance.getResultCode().equals("0")){               
            System.out.println("Account Key>> " + balance.getAccountKey());
            System.out.println("Total Amount>> " + balance.getTotalAmount());
             System.out.println("Remaining Amount>> " + balance.getRemainingAmount());
            System.out.println("Usage Amount>> " + balance.getUsageAmount());
             System.out.println("Result Code>> " + balance.getResultCode());
            System.out.println("Result Description>> " + balance.getResultDescr());
            //other method calls of balance ommitted
        }
        else{
            System.out.println("ResultCode>> " + balance.getResultCode());
            System.out.println("ResultDescription>> " + balance.getResultDescr());
        }
        System.out.println();
        
        
        //getOutstanding
        GetOutStandingResponse getOutStandingResponse = getOutStanding("302671694");
        Outstanding outStanding = getOutStandingResponse.getOutstanding();
        System.out.println("getOutstanding()" );
        System.out.println("-------------------------" ); 
        if(outStanding.getResultCode().equals("0")){   
            System.out.println("TotalOutstandingAmount>> " + outStanding.getTotalOutStandingAmount());         
            System.out.println("ResultCode>> " + outStanding.getResultCode());
            System.out.println("ResultDescription>> " + outStanding.getResultDescr());
            //bill cycle
            List<Outstanding.BillCycle> billCycle = outStanding.getBillCycle();
            for (Outstanding.BillCycle b : billCycle) {
                System.out.println();
                System.out.println("Bill Cycle" );
                System.out.println("-------------------------" );
                System.out.println("billCycleID >>" + b.getBillCycleID());
                System.out.println("billCycleBeginTime>> " + b.getBillCycleBeginTime());
                System.out.println("billCycleEndTime>> " + b.getBillCycleEndTime());
                System.out.println("outStandingAmount>> " + b.getOutStandingAmount());   
            }
        }
        else{
            System.out.println("ResultCode>> " + outStanding.getResultCode());
            System.out.println("ResultDescription>> " + outStanding.getResultDescr());
        }
        System.out.println();
                   
        //getUnBilled
        GetUnBilledResponse getUnBilledResponse = getUnBilled("302671694");
        UnBilled unBilled = getUnBilledResponse.getUnBilled();
        System.out.println("getUnbilled()" );
        System.out.println("-------------------------" );
        if(unBilled.getResultCode().equals("0")){   
            System.out.println("UnBilledBalance>> " + unBilled.getUnBilledBalance());        
            System.out.println("ResultCode>> " + unBilled.getResultCode());
            System.out.println("ResultDescription>> " + unBilled.getResultDescr());
        }
        else{
            System.out.println("ResultCode>> " + unBilled.getResultCode());
            System.out.println("ResultDescription>> " + unBilled.getResultDescr());
        }
        System.out.println();
        
        
        //doPayment        
        DoPaymentResponse doPaymentResponse = doPayment("0302721521", "z9vDJqKEQF)4G", "1");
        Payment payment = doPaymentResponse.getPayment();
        System.out.println("doPayment()" );
        System.out.println("-------------------------" );
        if(payment.getResultCode().equals("0")){   
            System.out.println("ClientReference>> " + payment.getClientReference());
            System.out.println("OldBalance>> " + payment.getOldBalance());        
            System.out.println("NewBalance>> " + payment.getNewBalance());                          
            System.out.println("ResultCode>> " + payment.getResultCode());
            System.out.println("ResultDescription>> " + payment.getResultDescr());
        }
        else{
            System.out.println("ResultCode>> " + payment.getResultCode());
            System.out.println("ResultDescription>> " + payment.getResultDescr());
        }
        System.out.println();         
        
        //doDeposit        
        //DO NOT USE THIS METHOD
        //PLEASE USE doPayment to effect all payments
//        DoDepositResponse doDepositResponse = doDeposit("test10", "z9vDJqKEQF)4G", "1", "ROAMINGDEPOSIT");
//        Deposit deposit = doDepositResponse.getDeposit();
//        System.out.println("doDeposit()" );
//        System.out.println("-------------------------" );
//        if(deposit.getResultCode().equals("0")){ 
//            System.out.println("OldBalance>> " + deposit.getOldBalance());        
//            System.out.println("NewBalance> " + deposit.getNewBalance());        
//        }
//        else{
//            System.out.println("ResultCode>> " + deposit.getResultCode());
//            System.out.println("ResultDescription>> " + deposit.getResultDescr());
//        }
//        System.out.println();
        
     }catch (SOAPFaultException e) {
            System.out.println("Fault Code: " + e.getFault().getFaultCode());
            System.out.println("Fault String: " + e.getFault().getFaultString());
            //e.printStackTrace(System.out);
     }catch(Exception e){
            System.out.println("Exception Message: " + e.getMessage());           
            //e.printStackTrace(System.out);
     } 
   }

    private static GetCustomerInfoResponse getCustomerInfo(String AccountId) throws Exception {
        
        GetCustomerInfoRequest getCustomerInfoRequest = new GetCustomerInfoRequest();
        getCustomerInfoRequest.setAccountId(AccountId);        
        String soapMessageToPost = Utilities.generateSOAPMessageAsString(getCustomerInfoRequest);      
        String response = Utilities.sendRequest(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP REQUEST");
         System.out.println("-----------------------------");
        System.out.println(soapMessageToPost);
          System.out.println();
         System.out.println("SOAP RESPONSE FROM VODAFONE");
         System.out.println("-----------------------------");
         System.out.println(response);
         System.out.println();
         
        //convert response message to GetCustomerInfoResponse
        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(response.getBytes()));
        Unmarshaller unmarshaller = JAXBContext.newInstance(GetCustomerInfoResponse.class).createUnmarshaller();
        GetCustomerInfoResponse getCustomerInfoResponse = (GetCustomerInfoResponse)unmarshaller.unmarshal(sm.getSOAPBody().extractContentAsDocument());
        return getCustomerInfoResponse;
    }
    
    private static GetBalanceResponse getBalance(String AccountId) throws Exception{
       
        GetBalanceRequest getBalanceRequest = new GetBalanceRequest();
        getBalanceRequest.setAccountId(AccountId);
        String soapMessageToPost = Utilities.generateSOAPMessageAsString(getBalanceRequest);      
        String response = Utilities.sendRequest(soapMessageToPost);
        System.out.println();
         System.out.println("SOAP REQUEST");
         System.out.println("-----------------------------");
        System.out.println(soapMessageToPost);
          System.out.println();
         System.out.println("SOAP RESPONSE FROM VODAFONE");
         System.out.println("-----------------------------");
         System.out.println(response);
         System.out.println();
        //convert response message to GetCustomerInfoResponse
        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(response.getBytes()));
        Unmarshaller unmarshaller = JAXBContext.newInstance(GetBalanceResponse.class).createUnmarshaller();
        GetBalanceResponse getBalanceResponse = (GetBalanceResponse)unmarshaller.unmarshal(sm.getSOAPBody().extractContentAsDocument());
        return getBalanceResponse;
    }
    
    private static GetOutStandingResponse getOutStanding(String AccountId) throws Exception {
        
        GetOutStandingRequest getOutStandingRequest = new GetOutStandingRequest();
        getOutStandingRequest.setAccountId(AccountId);
        String soapMessageToPost = Utilities.generateSOAPMessageAsString(getOutStandingRequest);      
        String response = Utilities.sendRequest(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP REQUEST");
         System.out.println("-----------------------------");
         System.out.println(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP RESPONSE FROM VODAFONE");
         System.out.println("-----------------------------");
         System.out.println(response);
         System.out.println();
        //convert response message to GetCustomerInfoResponse
        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(response.getBytes()));
        Unmarshaller unmarshaller = JAXBContext.newInstance(GetOutStandingResponse.class).createUnmarshaller();
        GetOutStandingResponse getOutStandingResponse = (GetOutStandingResponse)unmarshaller.unmarshal(sm.getSOAPBody().extractContentAsDocument());
        return getOutStandingResponse;
    }
    
    private static GetUnBilledResponse getUnBilled(String AccountId) throws Exception {
        
        GetUnBilledRequest getUnBilledRequest = new GetUnBilledRequest();
        getUnBilledRequest.setAccountId(AccountId);
        String soapMessageToPost = Utilities.generateSOAPMessageAsString(getUnBilledRequest);      
        String response = Utilities.sendRequest(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP REQUEST");
         System.out.println("-----------------------------");
         System.out.println(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP RESPONSE FROM VODAFONE");
         System.out.println("-----------------------------");
         System.out.println(response);
         System.out.println();
        //convert response message to GetCustomerInfoResponse
        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(response.getBytes()));
        Unmarshaller unmarshaller = JAXBContext.newInstance(GetUnBilledResponse.class).createUnmarshaller();
        GetUnBilledResponse getUnBilledResponse = (GetUnBilledResponse)unmarshaller.unmarshal(sm.getSOAPBody().extractContentAsDocument());
        return getUnBilledResponse;
    }

    private static DoPaymentResponse doPayment(String accountId, String operatorCode, String amount) throws Exception{
        
        //--STEP 1 . call getCustomerInfo() to get accountNo and paymentMode        
        GetCustomerInfoResponse getCustomerInfoResponse = getCustomerInfo(accountId);
        Customer customer = getCustomerInfoResponse.getCustomer();
        
         if(customer.getResultCode().equals("0")){    
            System.out.println("AccountName>> " + customer.getAccountName());
            System.out.println("AccountNo>> " + customer.getAccountNo());           
            System.out.println("PaymentMode>> " + customer.getPaymentMode());
            System.out.println("CustomerName>> " + customer.getCustomerName());
            System.out.println("Billing Account>> " + customer.getBillingAccount());
            //other method calls of customer ommitted            
        }
        else{
            System.out.println("ResultCode>> " + customer.getResultCode());
            System.out.println("ResultDescription>> " + customer.getResultDescr());
        }
         
        //--STEP 2. use customer accountNo and PaymentMode to create a doPaymentRequest Object
        DoPaymentRequest doPaymentRequest = new DoPaymentRequest();              
        doPaymentRequest.setAccountNo(customer.getAccountNo());
        doPaymentRequest.setOperatorCode(operatorCode);
        doPaymentRequest.setAmount(amount);
        doPaymentRequest.setReference(generateRandomNumber());
        doPaymentRequest.setPaymentMode(customer.getPaymentMode());
        
        String soapMessageToPost = Utilities.generateSOAPMessageAsString(doPaymentRequest);      
        String response = Utilities.sendRequest(soapMessageToPost);
        
         System.out.println();
         System.out.println("SOAP REQUEST");
         System.out.println("-----------------------------");
         System.out.println(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP RESPONSE FROM VODAFONE");
         System.out.println("-----------------------------");
         System.out.println(response);
         System.out.println();
        
        //convert response message to DoPaymentResponse
        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(response.getBytes()));
        Unmarshaller unmarshaller = JAXBContext.newInstance(DoPaymentResponse.class).createUnmarshaller();
        DoPaymentResponse doPaymentResponse = (DoPaymentResponse)unmarshaller.unmarshal(sm.getSOAPBody().extractContentAsDocument());
        return doPaymentResponse;       
    }  
    
    //doDeposit        
    //DO NOT USE THIS METHOD
    //PLEASE USE doPayment to effect all payments
    private static DoDepositResponse doDeposit(String accountId, String operatorCode, String amount, String depositType) throws Exception {
        
        //--STEP 1 . call getCustomerInfo() to get accountNo and paymentMode        
        GetCustomerInfoResponse getCustomerInfoResponse = getCustomerInfo("test10");
        Customer customer = getCustomerInfoResponse.getCustomer();
        
          if(customer.getResultCode().equals("0")){    
            System.out.println("AccountName>> " + customer.getAccountName());
            System.out.println("AccountNo>> " + customer.getAccountNo());           
            System.out.println("PaymentMode>> " + customer.getPaymentMode());
            System.out.println("CustomerName>> " + customer.getCustomerName());
            System.out.println("Billing Account>> " + customer.getBillingAccount());
            //other method calls of customer ommitted            
        }
        else{
            System.out.println("ResultCode>> " + customer.getResultCode());
            System.out.println("ResultDescription>> " + customer.getResultDescr());
        }
        
        //--STEP 2. use accountNo and PaymentMode to create a doPaymentRequest Object
        DoDepositRequest doDepositRequest = new DoDepositRequest();
        doDepositRequest.setAccountNo(customer.getAccountNo());
        doDepositRequest.setOperatorCode(operatorCode);
        doDepositRequest.setAmount(amount);
        doDepositRequest.setReference(generateRandomNumber());
        doDepositRequest.setDepositType(depositType);  
        
        String soapMessageToPost = Utilities.generateSOAPMessageAsString(doDepositRequest);      
        String response = Utilities.sendRequest(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP REQUEST");
         System.out.println("-----------------------------");
         System.out.println(soapMessageToPost);
         System.out.println();
         System.out.println("SOAP RESPONSE FROM VODAFONE");
         System.out.println("-----------------------------");
         System.out.println(response);
         System.out.println();
         
        //convert response message to GetCustomerInfoResponse      
        SOAPMessage sm = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(response.getBytes()));
        Unmarshaller unmarshaller = JAXBContext.newInstance(DoDepositResponse.class).createUnmarshaller();
        DoDepositResponse doDepositResponse = (DoDepositResponse)unmarshaller.unmarshal(sm.getSOAPBody().extractContentAsDocument());
        return doDepositResponse;    
    }
    
    private static String generateRandomNumber(){
       Random r = new Random(System.currentTimeMillis());
       Long randomNumber = Math.abs(r.nextLong());
       return Long.toString(randomNumber);
    }   
    
    private static void disableSslVerification() {
    try
    {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (KeyManagementException e) {
        e.printStackTrace();
    }
  } 
  
}