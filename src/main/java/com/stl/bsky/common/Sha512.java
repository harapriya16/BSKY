package com.stl.bsky.common;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

@Component
public class Sha512 {

    private static final String key = "6A80FD8D38D579D1090F6CDB62CA34CA";
    private static final String initVector = "79b67e539e7fcadf";

    public Sha512() {
        super();
//		System.out.println("Hash Service initialized..");
    }
    public String SHA512(String password) {
        String generatedKey=null;
        try{
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.update(password.getBytes("UTF-8"));
            byte[] output = algorithm.digest();
            generatedKey = bytesToHex(output).toLowerCase();
        }catch(Exception e){
            e.printStackTrace();
        }
        return generatedKey;
    }

    public String bytesToHex(byte[] b) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer buf = new StringBuffer();
        for (int j=0; j<b.length; j++) {
            buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
            buf.append(hexDigit[b[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            return  Base64.getEncoder().encodeToString(cipher.doFinal(value.getBytes("UTF-8")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static String encryptClass(CaptchaModel value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            return  Base64.getEncoder().encodeToString(cipher.doFinal(value.toString().getBytes("UTF-8")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public static void main(String args[]) {
       // System.out.println(Sha512.encrypt("jdbc:postgresql://localhost:5432/sm_anmol_93")); 
       // System.out.println(Sha512.decrypt("C89Eu1NcnW8k2Pit0+IoUQ==")); //C89Eu1NcnW8k2Pit0+IoUQ==
        
        Date date = new Date();
        System.out.println(date.getTime());
        System.out.println(new Date(date.getTime()+1800000));
//       // CaptchaModel cm= new CaptchaModel();
//        Gson gson = new Gson();
//    	String jsonString = gson.toJson(Sha512.decrypt(Sha512.encryptClass(new CaptchaModel())));
//    	CaptchaModel cm = gson.fromJson(jsonString , CaptchaModel.class);
//    	System.out.println(cm.toString());
        System.out.println(Sha512.decrypt(Sha512.encryptClass(new CaptchaModel())));
        System.out.println(Utils.getCaptchaKeyFromResponse(Sha512.decrypt(Sha512.encryptClass(new CaptchaModel()))));
        System.out.println(Utils.getCaptchaValidityFromResponse(Sha512.decrypt(Sha512.encryptClass(new CaptchaModel()))));
    }

}
