package net.foodeals.contract.infrastructure.controllers;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Header;
import net.foodeals.contract.application.DTo.upload.CreateANewContractDto;
import net.foodeals.contract.application.DTo.upload.UpdateContractDto;
import net.foodeals.contract.application.service.ContractService;
import net.foodeals.contract.domain.entities.Contract;
import net.foodeals.contract.domain.entities.enums.ContractStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class ContractController {

    private final ContractService contractService;

    ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @GetMapping("Contracts/Document/{id}")
    public ResponseEntity<byte []> getContractDocument(@PathVariable("id") UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-Disposition", "attachment; filename=contract.pdf");

        return new ResponseEntity<byte []>(this.contractService.getContractDocument(id), headers, HttpStatus.OK);
    }
}
