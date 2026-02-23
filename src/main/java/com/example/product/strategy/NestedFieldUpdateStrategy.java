package com.example.product.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

@Component
public class NestedFieldUpdateStrategy implements UpdateStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(NestedFieldUpdateStrategy.class);

    @Override
    public boolean canHandle(String fieldPath) {
        return fieldPath.contains(".") && !fieldPath.matches(".*\\[\\d+\\].*");
    }

    @Override
    public void applyUpdate(BeanWrapper beanWrapper, String fieldPath, Object value) {
        try {
            String[] pathParts = fieldPath.split("\\.", 2);
            String currentField = pathParts[0];
            String remainingPath = pathParts[1];
            
            Object nestedObject = beanWrapper.getPropertyValue(currentField);
            
            if (nestedObject == null) {
                log.warn("Cannot update null nested object: {}", currentField);
                return;
            }
            
            BeanWrapper nestedWrapper = new BeanWrapperImpl(nestedObject);
            
            if (remainingPath.contains(".")) {
                applyUpdate(nestedWrapper, remainingPath, value);
            } else {
                nestedWrapper.setPropertyValue(remainingPath, value);
                log.debug("Updated nested field: {} = {}", fieldPath, value);
            }
        } catch (Exception e) {
            log.error("Error updating nested field '{}': {}", fieldPath, e.getMessage());
        }
    }
}
