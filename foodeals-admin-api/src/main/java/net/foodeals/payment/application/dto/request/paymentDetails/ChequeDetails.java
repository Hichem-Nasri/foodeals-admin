package net.foodeals.payment.application.dto.request.paymentDetails;

import java.util.Date;

public record ChequeDetails(String chequeNumber, String bankName, Date deadlineDate, Date recuperationDate, String issuer) implements PaymentDetails {}
