package encryptedmessages;

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

public class AES {
    private SecretKey key;
    private final int KEY_SIZE = 128;
    private final int T_LEN = 128;
    private byte[] IV;
    private String salt = "potato";
    
    
    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    private void initFromStrings(String secretKey, String IV) {
        key = new SecretKeySpec(decode(secretKey), "AES");
        this.IV = decode(IV);
    }
    
//    private void initFromPassword(String secretKey) {
//        key = new SecretKeySpec(decode(secretKey), "AES");
//        IVgen();
//    }
    
    public void initFromPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWithSHA1AndDESede");
//        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 1000, KEY_SIZE);
//        key = factory.generateSecret(spec);
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
//        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
//        key = SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
//        IVgen();
    }
    
    public void IVgen() {
    	SecureRandom random = new SecureRandom();
    	IV = new byte[12];	//128/8
        random.nextBytes(IV);
    }
    
    public String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes(StandardCharsets.UTF_8);
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
//        IVgen();
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception {
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private void exportKeys() {
    	System.err.println(encode(key.getEncoded()));
//    	System.err.println(encode(IV));
//        System.err.println(key.getEncoded());
        System.err.println(IV);
    }
    private void exportIV() {
    	System.err.println(IV);
    }
    
    private void exportKey() {
    	System.err.println(encode(key.getEncoded()));
    }
    
    public static void main(String[] args) {
        try {
            AES aes = new AES();
            aes.init();
            aes.exportKeys();	//IV is null, key is unkown
            aes.IVgen();	//sets IV
//            aes.exportKeys();
            aes.initFromPassword("sdfg");	//sets key
//            aes.exportKeys();
            String encryptedMessage = aes.encrypt("TheXCoders");
            String decryptedMessage = aes.decrypt(encryptedMessage);

            System.err.println("Encrypted Message : " + encryptedMessage);
            System.err.println("Decrypted Message : " + decryptedMessage);
            aes.exportKeys();
        } catch (Exception ignored) {
        }
    }
}
