package com.example.moviezip;

import com.example.moviezip.domain.chat.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class MoviezipApplication  implements CommandLineRunner {
    @Autowired
    private MongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(MoviezipApplication.class, args);
	}
    @Override
    public void run(String... args) throws Exception {
        // MongoDB 테스트용 데이터 삽입
        ChatRoom testRoom = new ChatRoom(1L,2L);
        mongoTemplate.save(testRoom);
        System.out.println("Test room saved: " + testRoom.getId());
    }

}

