package com.piter.orderservice.service;

import com.piter.orderservice.dto.InventoryResponse;
import com.piter.orderservice.dto.OrderLineItemsDto;
import com.piter.orderservice.dto.OrderRequest;
import com.piter.orderservice.model.Order;
import com.piter.orderservice.model.OrderLineItems;
import com.piter.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public void createOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLines = orderRequest.getOrderLineItemsDtoList()
                .stream().map(this::map)
                .toList();

        order.setOrderLineItemsList(orderLines);

        List<String> skuCodes = order.getOrderLineItemsList().stream().
                map(OrderLineItems::getSkuCode)
                .toList();


        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsInStock = Arrays.stream(inventoryResponses)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Product is not available");
        }
    }

    private OrderLineItems map(OrderLineItemsDto line) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(line.getQuantity());
        orderLineItems.setPrice(line.getPrice());
        orderLineItems.setSkuCode(line.getSkuCode());
        return orderLineItems;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
