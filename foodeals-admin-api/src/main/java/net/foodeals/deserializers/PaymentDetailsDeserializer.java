package net.foodeals.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import net.foodeals.payment.application.dto.request.paymentDetails.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentDetailsDeserializer extends JsonDeserializer<PaymentDetails> {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public PaymentDetails deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String type = node.get("type").asText();

        switch (type) {
            case "CASH":
                Date cashDate = null;
                try {
                    cashDate = dateFormat.parse(node.get("date").asText());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return new CashDetails(cashDate);
            case "CHEQUE":
                String chequeNumber = node.get("chequeNumber").asText();
                String bankName = node.get("bankName").asText();
                Date chequeDate = null;
                try {
                    chequeDate = dateFormat.parse(node.get("chequeDate").asText());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return new ChequeDetails(chequeNumber, bankName, chequeDate);
            case "CARD":
                String cardNumber = node.get("cardNumber").asText();
                String cardHolderName = node.get("cardHolderName").asText();
                String expiryDate = node.get("expiryDate").asText();
                String cvv = node.get("cvv").asText();
                return new CardDetails(cardNumber, cardHolderName, expiryDate, cvv);
            case "BANK_TRANSFER":
                return new BankTransferDetails();
            default:
                throw new IllegalArgumentException("Unknown payment details type: " + type);
        }
    }
}