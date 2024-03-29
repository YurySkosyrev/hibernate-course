package com.dmdev.dao;

import com.dmdev.entity.Company;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;


public class CompanyRepository extends RepositoryBase<Integer, Company> {

    public CompanyRepository(EntityManager entityManager) {
        super(entityManager, Company.class);
    }

}
