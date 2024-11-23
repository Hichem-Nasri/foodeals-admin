package net.foodeals.payment.application.dto.response;

import net.foodeals.common.valueOjects.Price;
import net.foodeals.order.domain.enums.OrderDeliveryType;
import net.foodeals.user.application.dtos.responses.SimpleUserDto;
import net.foodeals.user.application.dtos.responses.UserInfoDto;

import java.util.UUID;

public record DeliveryOperationDto(OrderDeliveryType type, ProductInfo product, UUID id, Price amount, Integer quantity, Price cashAmount, Price cardAmount, Price commission, SimpleUserDto deliveryBoy) {
}
