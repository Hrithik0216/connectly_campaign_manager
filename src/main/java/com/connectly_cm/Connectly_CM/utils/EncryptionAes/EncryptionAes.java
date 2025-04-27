package com.connectly_cm.Connectly_CM.utils.EncryptionAes;


import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.SecureRandom;

import static com.connectly_cm.Connectly_CM.utils.EncryptionAes.EncryptionConstants.*;

public class EncryptionAes {
    static SecureRandom secureRandom = new SecureRandom();
    private static final byte[] FIXED_SALT = "FixedSaltForDev123".getBytes();

    public static String localEncrypt(String plainText) throws Exception {
        // Generate a random IV
        byte[] iv = new byte[IV_LENGTH_ENCRYPT];

        secureRandom.nextBytes(iv);

        // Generate the AES key from the local passphrase
        SecretKeySpec aesKey = generateAesKeyFromPassphrase();

        // Initialize cipher in AES-GCM mode
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

        // Encrypt the plaintext
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted text and encode them as Base64
        byte[] combinedIvAndCipherText = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combinedIvAndCipherText, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combinedIvAndCipherText, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
    }

    public static String localDecrypt(String cipherText) throws Exception {
        if (cipherText.trim().isEmpty() || cipherText == null) {
            throw new IllegalArgumentException("The cipher text is empty");
        }
        if (cipherText.length() % 4 != 0) {
            throw new IllegalArgumentException("Invalid base64 lenght");
        }
        byte[] decodedCipherText = Base64.getDecoder().decode(cipherText);

        // Generate the AES key from the local passphrase
        SecretKeySpec aesKey = generateAesKeyFromPassphrase();

        // Extract IV and encrypted text
        byte[] iv = new byte[IV_LENGTH_ENCRYPT];
        System.arraycopy(decodedCipherText, 0, iv, 0, iv.length);
        if (decodedCipherText.length <= IV_LENGTH_ENCRYPT) {
            throw new IllegalArgumentException("Invalid ciphertext - too short");
        }
        byte[] encryptedText = new byte[decodedCipherText.length - IV_LENGTH_ENCRYPT];
        System.arraycopy(decodedCipherText, IV_LENGTH_ENCRYPT, encryptedText, 0, encryptedText.length);

        // Initialize cipher in AES-GCM mode
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

        // Decrypt the ciphertext
        byte[] decryptedBytes = cipher.doFinal(encryptedText);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    private static SecretKeySpec generateAesKeyFromPassphrase() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(
                LOCAL_PASSPHRASE.toCharArray(),
                FIXED_SALT,  // Use fixed salt
                100000,
                256
        );
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }
}
