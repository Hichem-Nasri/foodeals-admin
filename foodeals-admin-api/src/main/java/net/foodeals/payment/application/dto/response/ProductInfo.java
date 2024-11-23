package net.foodeals.payment.application.dto.response;

import net.foodeals.order.domain.enums.OrderType;
import net.foodeals.product.domain.enums.ProductType;

public record ProductInfo(String name, String avatarPath){
}
