package net.foodeals.order.application.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.foodeals.order.application.dtos.requests.TransactionRequest;
import net.foodeals.order.application.services.OrderService;
import net.foodeals.order.application.services.TransactionService;
import net.foodeals.order.domain.entities.Transaction;
import net.foodeals.order.domain.exceptions.TransactionNotFoundException;
import net.foodeals.order.domain.repositories.TransactionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final OrderService orderService;

    private final ModelMapper mapper;

    @Override
    @Transactional
    public List<Transaction> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Page<Transaction> findAll(Integer pageNumber, Integer pageSize) {
        return repository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    @Transactional
    public Page<Transaction> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public Transaction findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    @Override
    @Transactional
    public Transaction create(TransactionRequest request) {
        final Transaction transaction = mapper.map(request, Transaction.class);
        transaction.setOrder(
                orderService.findById(request.orderId())
        );
        return repository.save(transaction);
    }

    @Override
    @Transactional
    public Transaction update(UUID id, TransactionRequest request) {
        final Transaction existingTransaction = findById(id);
        mapper.map(request, existingTransaction);
        return repository.save(existingTransaction);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (repository.existsById(id))
            throw new TransactionNotFoundException(id);

        repository.softDelete(id);
    }
}