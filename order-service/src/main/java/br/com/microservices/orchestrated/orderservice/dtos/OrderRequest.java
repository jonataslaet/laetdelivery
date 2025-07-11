package br.com.microservices.orchestrated.orderservice.dtos;

import br.com.microservices.orchestrated.orderservice.documents.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private List<OrderItem> orderItems;

}
