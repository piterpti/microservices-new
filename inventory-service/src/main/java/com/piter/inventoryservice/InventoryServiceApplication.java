package com.piter.inventoryservice;

import com.piter.inventoryservice.model.Inventory;
import com.piter.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            inventoryRepository.save(new Inventory("iphone_13", 100));
            inventoryRepository.save(new Inventory("iphone_13_red", 0));
            inventoryRepository.save(new Inventory("iphone_13_green", 10));
        };
    }

}
