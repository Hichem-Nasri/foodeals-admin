package net.foodeals.common.contracts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudService<Response, ID, Argument> {
    List<Response> findAll();

    Page<Response> findAll(Integer pageNumber, Integer pageSize);

    Page<Response> findAll(Pageable pageable);

    Response findById(ID id);

    Response create(Argument dto);

    Response update(ID id, Argument dto);

    void delete(ID id);

}

