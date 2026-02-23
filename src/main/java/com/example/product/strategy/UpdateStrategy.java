package com.example.product.strategy;

import org.springframework.beans.BeanWrapper;

/**
 * Strategy interface for applying field updates.
 */
public interface UpdateStrategy {
    
    boolean canHandle(String fieldPath);
    
    void applyUpdate(BeanWrapper beanWrapper, String fieldPath, Object value);
}
