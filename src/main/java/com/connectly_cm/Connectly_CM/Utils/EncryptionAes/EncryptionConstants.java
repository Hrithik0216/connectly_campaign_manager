package com.connectly_cm.Connectly_CM.Utils.EncryptionAes;

import org.springframework.beans.factory.annotation.Value;

public class EncryptionConstants {
    public static final String SHA_CRYPT = "SHA-256";
    public static final String AES_ALGORITHM = "AES";
    public static final String AES_ALGORITHM_GCM = "AES/GCM/NoPadding";

    public static final Integer IV_LENGTH_ENCRYPT = 12;
    public static final Integer TAG_LENGTH_ENCRYPT = 16;

//    @Value("${local.paraphrase}")
    public static final String LOCAL_PASSPHRASE="hG7*knQ2!Lp9#rT4&vN6@wY8";
}
