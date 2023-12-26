package com.example.du_an_tn_zingmp_3.service;

import com.example.du_an_tn_zingmp_3.model.Roles;

public interface IRoleService extends IGeneralService <Roles> {
    Iterable<Roles> findAll();

    void save(Roles role);

    Roles findByName(String name);
}
