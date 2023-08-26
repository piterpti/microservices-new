package com.piter.inventoryservice.repository;

import com.piter.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, String> {

    List<Inventory> findBySkuCodeIn(List<String> skuCode);

}
