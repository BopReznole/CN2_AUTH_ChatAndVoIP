package com.cn2.communication;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;	//for generating key with password

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;	//for generating key with password
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;	//for generating key with password
import java.util.Base64;

public class AESci {
    private SecretKey key;
    private final int KEY_SIZE = 128;
    private final int T_LEN = 128;
    private byte[] IV;
    private String salt = "potato";
    private String encodedIV = "";
    private String ivstr = "";
    public int counter = 0;
    
    public AESci() throws Exception {
    	init();
    	initFromPassword("sdfg");	//sets key
    	IVgen();
    	IVset();
//    	prt();
//    	ct();
    }
    
    public void ct() {
    	counter++;
    }
    public void prt() {
    	System.err.println("Counter: " + counter);
    }
    
    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    private void initFromStrings(String secretKey, String IV) {
        key = new SecretKeySpec(decode(secretKey), "AES");
        this.IV = decode(IV);
    }
    
    public void initFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
    

    public void IVgen() {
    SecureRandom random = new SecureRandom();
    byte[] iv2 = new byte[12];
    random.nextBytes(iv2);
    ivstr = encode(iv2);
//    System.err.println(ivstr);	//debug
//    this.IV = decode(ivstr);
    }
  
  public void IVset() {
  	if (!ivstr.isEmpty()) {
  		this.IV = decode(ivstr);
  	}
  }
  public void setIV(String IVnew) {
	  	if (!IVnew.isEmpty()) {
	  		this.IV = decode(IVnew);
  	}
  }
  
    public String getIV() {
        return encode(IV);
    }
    
    public String encrypt(String message) throws Exception {
//    	prt();
//    	ct();
    	byte[] messageInBytes = message.getBytes();
//        byte[] messageInBytes = message.getBytes(StandardCharsets.UTF_8);
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
//        IVgen();
//        IV = encryptionCipher.getIV();
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception {
//    	prt();
//    	ct();
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
//        return new String(decryptedBytes, StandardCharsets.UTF_8);
        return new String(decryptedBytes);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    
    public String IVForMessage() {
    	return encode(IV);
    }
    
    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private void exportKeys() {
    	System.err.println(encode(key.getEncoded()));
    	System.err.println(encode(IV));
//        System.err.println(key.getEncoded());
//        System.err.println(IV);
    }
    public void exportIV() {
    	System.err.println(encode(IV));
    }
    public void exportIV2() {
    	System.err.println(IV);
    }
    public void exportKey() {
    	System.err.println(encode(key.getEncoded()));
    }
    
    public String encryptMessage(String plainMessage) throws Exception {
    	IVgen(); //generates new IV to add in the message before sending it (saved in ivstr)
//    	exportIV();
    	String encryptedMessage = encrypt(ivstr + plainMessage);
//    	setIV(ivstr); //sets the new IV which was previously generated //ONLY WORKS FOR SEPARATE DEVICES
    	System.err.println(encryptedMessage);
    	return (encryptedMessage);
    }
    
    public String decryptMessage(String encryptedMessage) throws Exception {
    	if(!encryptedMessage.substring(0, 12).equals("[Voice-Call]"))	{ //checks if it's related to voice call
//    		exportIV();
    		System.err.println(encryptedMessage);
	 		encryptedMessage = decrypt(encryptedMessage);
	 		String IVnew = encryptedMessage.substring(0, 16);	//first 16 chars is the IV
//	 		setIV(IVnew);  //Sets the new IV //ONLY WORKS FOR SEPARATE DEVICES
	 		encryptedMessage = encryptedMessage.substring(16, encryptedMessage.length());
	 		System.err.println(encryptedMessage);
	 		}
    	return encryptedMessage;
    }
    
//    public static void main(String[] args) {
//        try {
//            AESci aesci = new AESci();
//            aesci.init();
//            aesci.initFromPassword("sdfg");	//sets key
//            aesci.IVgen();
//            String encryptedMessage = aesci.encrypt("TheXCoders");
//            String decryptedMessage = aesci.decrypt(encryptedMessage);
//
//            System.err.println("Encrypted Message : " + encryptedMessage + "Length: " + aesci.IVForMessage().length());
//            System.err.println("Decrypted Message : " + decryptedMessage);
//            aesci.exportKeys();
//            
////            String teststr = aesci.encrypt("qwertyuio\ndsfdsfs");
////            System.err.println(aesci.decrypt(teststr));
//        } catch (Exception ignored) {
//        }
//    }
}
