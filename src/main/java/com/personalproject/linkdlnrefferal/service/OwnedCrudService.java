package com.personalproject.linkdlnrefferal.service;

import java.util.List;

public interface OwnedCrudService<C, R> {

    R create(C request);

    List<R> getAll();

    R getById(Long id);

    R update(Long id, C request);

    void delete(Long id);
}
