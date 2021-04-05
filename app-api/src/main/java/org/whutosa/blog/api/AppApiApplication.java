package org.whutosa.blog.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author bobo
 */
@SpringBootApplication(scanBasePackages = "org.whutosa.blog")
@EnableJpaRepositories(basePackages = "org.whutosa.blog.data.repository.concrete")
@EntityScan(basePackages="org.whutosa.blog.data.entity")
public class AppApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApiApplication.class, args);
    }

}
