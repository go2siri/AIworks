package com.insurance.quote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableConfigurationProperties
public class InsuranceQuoteApplication {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceQuoteApplication.class);
    
    private final Environment environment;

    public InsuranceQuoteApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        logger.info("Starting Insurance Quote Backend Application...");
        SpringApplication.run(InsuranceQuoteApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            String protocol = "http";
            String serverPort = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            
            logger.info("\n----------------------------------------------------------\n\t" +
                    "Application '{}' is running! Access URLs:\n\t" +
                    "Local: \t\t{}://localhost:{}{}\n\t" +
                    "External: \t{}://{}:{}{}\n\t" +
                    "Profile(s): \t{}\n\t" +
                    "H2 Console: \t{}://localhost:{}{}/h2-console\n\t" +
                    "Swagger UI: \t{}://localhost:{}{}/swagger-ui/index.html\n" +
                    "----------------------------------------------------------",
                    environment.getProperty("spring.application.name", "Insurance Quote Backend"),
                    protocol, serverPort, contextPath,
                    protocol, hostAddress, serverPort, contextPath,
                    environment.getActiveProfiles().length == 0 ? 
                        environment.getDefaultProfiles() : environment.getActiveProfiles(),
                    protocol, serverPort, contextPath,
                    protocol, serverPort, contextPath
            );
        } catch (UnknownHostException e) {
            logger.warn("The host name could not be determined, using `localhost` as fallback");
        }
    }
}