package net.foodeals.organizationEntity.application.dtos.requests;

import net.foodeals.processors.annotations.Processable;

public record DeliveryPartnerContract(String solution, Float amount, Float commission) {
}