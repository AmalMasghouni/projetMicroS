package com.programming.techie.orderservice.dto;

import com.programming.techie.orderservice.model.OrderLineItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    @NotEmpty(message = "Order must have at least one item")
    private List<@Valid OrderLineItems> orderLineItemsList;}
