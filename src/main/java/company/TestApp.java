package company;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@SpringBootApplication
public class TestApp extends SpringBootServletInitializer {

    // private Subscriber subscriber;

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
        log.info("successfully started test application");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(TestApp.class);
    }

    //If you want to mess around with pubsub resources, add this back in
//    @PostConstruct
//    public void initSubscriber() {
//        log.info("Setting up pull subscriber");
//        String projectId = SystemProperty.applicationId.get();
//
//        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, "test1-pull");
//
//        subscriber = Subscriber.newBuilder(subscriptionName, (message, consumer) -> {
//            try {
//                String mess = message.getData().toString("UTF-8");
//                log.info("Info: We got a message {}", mess);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            } finally {
//                consumer.ack();
//            }
//        }).build();
//
//        subscriber.startAsync();
//    }

//    @PreDestroy
//    public void stopSubscriber() {
//        log.info("Shutting down subscriber");
//        subscriber.stopAsync().awaitTerminated();
//    }

    @RestController
    public static class TestController {

        @GetMapping("/")
        public String ping() {
            return "Hello!";
        }
    }
}
