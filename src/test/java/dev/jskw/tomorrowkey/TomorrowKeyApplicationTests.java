package dev.jskw.tomorrowkey;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class TomorrowKeyApplicationTests {

    @Test
    void contextLoads() {
    }

    @Configuration
    static class TestConfig {
        @Bean
        public String exampleBean() {
            return "example";
        }
    }

}
