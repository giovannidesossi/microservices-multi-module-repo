package it.desossi.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import it.desossi.orderservice.dto.InventoryResponse;
import it.desossi.orderservice.dto.OrderLineItemsDto;
import it.desossi.orderservice.dto.OrderRequest;
import it.desossi.orderservice.model.Order;
import it.desossi.orderservice.model.OrderLineItems;
import it.desossi.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final WebClient.Builder webClientBuilder;

	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto)
				.toList();
		order.setOrderLineItemsList(orderLineItems);
		
		//Check Inventory service...
		List<String> skuCodesList = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();
		InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
				.uri("http://inventory-service/api/inventory", 
						uriBuilder -> uriBuilder.queryParam("skuCode", skuCodesList).build())
				.retrieve()
				.bodyToMono(InventoryResponse[].class)
				.block();//synchronous communication
		
		boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

		if (allProductsInStock) {
			orderRepository.save(order);
		} else {
			throw new IllegalArgumentException("One or more products are not in stock, please try again later");
		}
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}

}
