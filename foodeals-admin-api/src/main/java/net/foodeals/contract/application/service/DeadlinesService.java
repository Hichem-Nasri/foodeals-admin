package net.foodeals.contract.application.service;

import net.foodeals.contract.domain.entities.Deadlines;
import net.foodeals.contract.domain.entities.Subscription;
import net.foodeals.contract.domain.repositories.DeadlinesRepository;
import net.foodeals.payment.application.dto.response.DeadlinesDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeadlinesService {

    private final DeadlinesRepository deadlinesRepository;
    private final ModelMapper modelMapper;


    public DeadlinesService(DeadlinesRepository deadlinesRepository, ModelMapper modelMapper) {
        this.deadlinesRepository = deadlinesRepository;
        this.modelMapper = modelMapper;
    }

    public List<Deadlines> saveAll(List<Deadlines> deadlines) {
        return this.deadlinesRepository.saveAll(deadlines);
    }

    public Deadlines findById(UUID uuid){
        return  this.deadlinesRepository.findById(uuid).orElse(null);
    }

    public Deadlines save(Deadlines deadlines) {
        return this.deadlinesRepository.save(deadlines);
    }
}
