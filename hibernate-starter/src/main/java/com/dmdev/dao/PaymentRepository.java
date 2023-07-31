package com.dmdev.dao;

import com.dmdev.entity.Payment;
import org.hibernate.SessionFactory;


public class PaymentRepository extends RepositoryBase<Long, Payment> {

    public PaymentRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Payment.class);
    }

}
