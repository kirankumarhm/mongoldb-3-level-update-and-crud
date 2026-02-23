package com.example.product.service;

import com.example.product.strategy.ListElementUpdateStrategy;
import com.example.product.strategy.NestedFieldUpdateStrategy;
import com.example.product.strategy.SimpleFieldUpdateStrategy;
import com.example.product.strategy.UpdateStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generic service for applying dynamic partial updates using Strategy Pattern.
 * Supports simple fields, nested objects, and list elements.
 *
 * @param <T> The type of the main entity being updated.
 */
@Service
public class GenericUpdateService<T> {

    private static final Logger log = LoggerFactory.getLogger(GenericUpdateService.class);
    
    private final List<UpdateStrategy> strategies = new ArrayList<>();
    private final ListElementUpdateStrategy listStrategy;

    public GenericUpdateService(SimpleFieldUpdateStrategy simpleStrategy,
                                NestedFieldUpdateStrategy nestedStrategy,
                                ListElementUpdateStrategy listStrategy) {
        this.listStrategy = listStrategy;
        strategies.add(listStrategy);
        strategies.add(nestedStrategy);
        strategies.add(simpleStrategy);
    }

    public void configure(String nestedListFieldName, String nestedListIdFieldName) {
        listStrategy.configure(nestedListFieldName, nestedListIdFieldName);
    }

    public T applyUpdates(T entity, Map<String, Object> updates) {
        if (entity == null || updates == null || updates.isEmpty()) {
            return entity;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(entity);
        log.debug("Applying {} updates to entity", updates.size());

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String fieldPath = entry.getKey();
            Object fieldValue = entry.getValue();
            
            for (UpdateStrategy strategy : strategies) {
                if (strategy.canHandle(fieldPath)) {
                    strategy.applyUpdate(beanWrapper, fieldPath, fieldValue);
                    break;
                }
            }
        }
        
        return entity;
    }
}
