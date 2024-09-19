package com.example.underwritingaidepoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication //(exclude = {HibernateJpaAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.example.underwritingaidepoc.repository")
public class UnderwritingAidePocApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnderwritingAidePocApplication.class, args);
    }

}
