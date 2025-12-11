package com.sfm.backend.config;

import com.sfm.backend.model.Item;
import com.sfm.backend.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ItemRepository repository) {
        return args -> {
            // Only initialize if database is empty
            if (repository.count() == 0) {
                repository.save(new Item(null, "Example 1", "Első példa elem"));
                repository.save(new Item(null, "Example 2", "Második példa elem"));
                System.out.println("Database initialized with sample data");
            }
        };
    }
}

