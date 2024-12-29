package encryptedmessages;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {
    private SecretKey key;
    private final int KEY_SIZE = 128;
    private final int T_LEN = 128;
    private byte[] IV;

    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    private void initFromStrings(String secretKey, String IV) {
        key = new SecretKeySpec(decode(secretKey), "AES");
        this.IV = decode(IV);
    }
    
    private void initFromPassword(String secretKey) {
        key = new SecretKeySpec(decode(secretKey), "AES");
        IVgen();
    }

    public void IVgen() {
    	SecureRandom random = new SecureRandom();
    	IV = new byte[12];
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
        System.err.println(key.getEncoded());
        System.err.println(IV);
    }
    private void exportIV() {
    	System.err.println(IV);
    }
    
    public static void main(String[] args) {
        try {
            AES aes = new AES();
            aes.init();
//            aes.exportIV();
            aes.IVgen();
//            aes.exportIV();
            String encryptedMessage = aes.encrypt("TheXCoders");
            String decryptedMessage = aes.decrypt(encryptedMessage);

            System.err.println("Encrypted Message : " + encryptedMessage);
            System.err.println("Decrypted Message : " + decryptedMessage);
            aes.exportKeys();
        } catch (Exception ignored) {
        }
    }
}
