package com.connectly_cm.Connectly_CM;

import com.connectly_cm.Connectly_CM.EncryptionUtils.EncryptionAes.EncryptionAes;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
public class ConnectlyCmApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConnectlyCmApplication.class, args);
        String log4jConfPath = "/home/hrithik/Desktop/Connectly/connectly_campaign_manager/src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
    }

}
