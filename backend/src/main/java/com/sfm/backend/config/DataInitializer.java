package com.sfm.backend.config;

import com.sfm.backend.model.Room;
import com.sfm.backend.repository.BookingRepository;
import com.sfm.backend.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RoomRepository roomRepository, BookingRepository bookingRepository) {
        return args -> {

            // Allow one-time forced reseed by setting either the system property
            // -Dapp.data.seed.force=true or the environment variable APP_DATA_SEED_FORCE=true
            String prop = System.getProperty("app.data.seed.force");
            String env = System.getenv("APP_DATA_SEED_FORCE");
            boolean force = "true".equalsIgnoreCase(prop) || "true".equalsIgnoreCase(env);

            if (force) {
                if (roomRepository.count() > 0) {
                    bookingRepository.deleteAll();
                    roomRepository.deleteAll();
                    System.out.println("Existing bookings and rooms deleted before seeding curated list (force=true)");
                }
            }

            if (force || roomRepository.count() == 0) {
                List<Room> rooms = List.of(
                        new Room(null, "gepterem", "Gépterem", 24, "Informatikai épület – 1. emelet", List.of("Gépek: 24 db", "Projektor")),
                        new Room(null, "targyA", "Tárgyaló A", 6, "Főépület – földszint", List.of("Hangszigetelt", "Online meeting")),
                        new Room(null, "konf", "Konferenciaterem", 40, "Konferencia központ – 2. emelet", List.of("Profi hangosítás", "Nagy vetítő")),
                        new Room(null, "csend", "Csendes szoba", 6, "B épület – 3. emelet", List.of("Laptopbarát", "Hangszigetelt")),
                        new Room(null, "small", "Kis tárgyaló", 8, "A épület – 2. emelet", List.of("Projektor")),
                        new Room(null, "targyB", "Tárgyaló B", 10, "Főépület – 1. emelet", List.of("Whiteboard", "Konferencia telefon")),
                        new Room(null, "workshop", "Workshop terem", 30, "Műhely – földszint", List.of("Padok", "Projektor")),
                        new Room(null, "meeting", "Meeting szoba", 12, "B épület – 1. emelet", List.of("Telefon konferencia")),
                        new Room(null, "vidkonf", "Videókonferencia", 8, "A épület – földszint", List.of("Videókonferencia eszköz")),
                        new Room(null, "hangout", "Szabadnap terem", 20, "Központi épület – 3. emelet", List.of("Összejövetel", "Bárpult"))
                );

                roomRepository.saveAll(rooms);
                System.out.println("Seeded 10 curated rooms");
            } else {
                System.out.println("Rooms already exist and force flag not set — skipping seeding");
            }
        };
    }
}
