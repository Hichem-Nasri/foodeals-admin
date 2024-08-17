package net.foodeals.common.contracts;

import org.springframework.data.domain.Page;

import java.util.List;

public interface CrudService<T, ID, Dto> {
    List<T> findAll();

    Page<T> findAll(Integer pageNumber, Integer pageSize);

    T findById(ID id);

    T create(Dto dto);

    T update(ID id, Dto dto);

    void delete(ID id);

}

