/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.vodafone.bill.postpaid;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import sun.misc.BASE64Encoder;

/**
 *
 * @author seth.sebeh
 */
public class WSSESoapSecurityCredentials {
 
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException {
        
        String usernameStr = "TEST";
        String passwordStr = "59Lr/?2baJ#z6^%(";
        
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
        
        System.out.println("created: " + createdDate);
        System.out.println("nonce: " + nonceB64);
        System.out.println("passwordbytes: " + passwordB64);
    }
    
}
