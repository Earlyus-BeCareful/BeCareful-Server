package com.becareful.becarefulserver.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void clean() {
        em.createNativeQuery("set foreign_key_checks = 0").executeUpdate();
        em.getMetamodel().getEntities().forEach(entity -> {
            String tableName = camelToSnake(entity.getName());
            em.createNativeQuery("truncate table " + tableName).executeUpdate();
        });
        em.createNativeQuery("set foreign_key_checks = 1").executeUpdate();
    }

    private String camelToSnake(String value) {
        return value.replaceAll("(?<=[a-z])[A-Z]", "_$0").toLowerCase();
    }
}
