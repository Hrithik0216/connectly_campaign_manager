package com.connectly_cm.Connectly_CM;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConnectlyCmApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectlyCmApplication.class, args);
		String log4jConfPath = "D:/Connectly Campaign manager/Connectly_CM/src/main/resources/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
	}

}
