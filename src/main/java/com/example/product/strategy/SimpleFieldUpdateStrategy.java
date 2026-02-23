package com.example.product.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.stereotype.Component;

@Component
public class SimpleFieldUpdateStrategy implements UpdateStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(SimpleFieldUpdateStrategy.class);

    @Override
    public boolean canHandle(String fieldPath) {
        return !fieldPath.contains(".");
    }

    @Override
    public void applyUpdate(BeanWrapper beanWrapper, String fieldPath, Object value) {
        try {
            if (beanWrapper.isWritableProperty(fieldPath)) {
                beanWrapper.setPropertyValue(fieldPath, value);
                log.debug("Updated simple field: {} = {}", fieldPath, value);
            } else {
                log.warn("Field not writable: {}", fieldPath);
            }
        } catch (Exception e) {
            log.error("Error updating field '{}': {}", fieldPath, e.getMessage());
        }
    }
}
