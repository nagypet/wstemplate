package hu.perit.template.gateway;

import hu.perit.spvitamin.spring.environment.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = {"hu.perit.spvitamin", "hu.perit.template.gateway"})
public class Application
{

    public static void main(String[] args)
    {
        //SpringApplication.run(Application.class, args);
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new EnvironmentPostProcessor());
        application.run(args);
    }

}
