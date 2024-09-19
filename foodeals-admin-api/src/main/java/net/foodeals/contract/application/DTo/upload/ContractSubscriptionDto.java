package net.foodeals.contract.application.DTo.upload;

import lombok.Data;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class ContractSubscriptionDto {

    private Float duration;

    private Float annualPayment;

    private Integer numberOfDueDates;
}

