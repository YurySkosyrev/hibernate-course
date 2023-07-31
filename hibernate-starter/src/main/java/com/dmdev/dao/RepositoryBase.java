package com.dmdev.dao;

import com.dmdev.entity.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class RepositoryBase<K extends Serializable, E extends BaseEntity> implements Repository<K, E> {

    private final EntityManager entityManager;
    private final Class<E> clazz;

    @Override
    public E save(E entity) {

        entityManager.persist(entity);
        return entity;
    }

    @Override
    public void delete(K id) {

        entityManager.remove(id);
        entityManager.flush();
    }

    @Override
    public void update(E entity) {

        entityManager.merge(entity);

    }

    @Override
    public Optional<E> findById(K id) {

        return Optional.ofNullable(entityManager.find(clazz, id));
    }

    @Override
    public List<E> findAll() {

        CriteriaQuery<E> criteria = entityManager.getCriteriaBuilder().createQuery(clazz);
        criteria.from(clazz);

        return entityManager.createQuery(criteria)
                .getResultList();
    }
}
