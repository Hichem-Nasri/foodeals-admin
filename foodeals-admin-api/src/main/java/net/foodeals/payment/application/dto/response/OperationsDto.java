package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.order.domain.enums.OrderDeliveryType;

import java.util.UUID;

public record OperationsDto(OrderDeliveryType type, ProductInfo product, UUID id, Price amount, Integer quantity, Price cashAmount, Price cashCommission, Price cardAmount, Price commissionCard) {
}
