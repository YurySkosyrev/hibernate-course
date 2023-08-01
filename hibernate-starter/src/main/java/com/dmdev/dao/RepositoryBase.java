package com.dmdev.dao;

import com.dmdev.entity.BaseEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class RepositoryBase<K extends Serializable, E extends BaseEntity> implements Repository<K, E> {

    @Getter
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
    public Optional<E> findById(K id, Map<String, Object> properties) {

        return Optional.ofNullable(entityManager.find(clazz, id, properties));
    }

    @Override
    public List<E> findAll() {

        CriteriaQuery<E> criteria = entityManager.getCriteriaBuilder().createQuery(clazz);
        criteria.from(clazz);

        return entityManager.createQuery(criteria)
                .getResultList();
    }
}
