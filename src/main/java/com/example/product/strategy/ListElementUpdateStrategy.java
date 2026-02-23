package com.example.product.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListElementUpdateStrategy implements UpdateStrategy {
    
    private static final Logger log = LoggerFactory.getLogger(ListElementUpdateStrategy.class);
    
    private String listFieldName;
    private String idFieldName;

    public void configure(String listFieldName, String idFieldName) {
        this.listFieldName = listFieldName;
        this.idFieldName = idFieldName;
    }

    @Override
    public boolean canHandle(String fieldPath) {
        return listFieldName != null && fieldPath.startsWith(listFieldName + ".");
    }

    @Override
    public void applyUpdate(BeanWrapper beanWrapper, String fieldPath, Object value) {
        try {
            String pathAfterList = fieldPath.substring(listFieldName.length() + 1);
            int firstDot = pathAfterList.indexOf('.');
            
            if (firstDot == -1) {
                log.warn("Invalid list element path: {}", fieldPath);
                return;
            }
            
            String elementId = pathAfterList.substring(0, firstDot);
            String remainingPath = pathAfterList.substring(firstDot + 1);
            
            List<?> list = (List<?>) beanWrapper.getPropertyValue(listFieldName);
            
            if (list == null) {
                log.warn("List is null: {}", listFieldName);
                return;
            }
            
            for (Object element : list) {
                BeanWrapper elementWrapper = new BeanWrapperImpl(element);
                Object currentId = elementWrapper.getPropertyValue(idFieldName);
                
                if (currentId != null && currentId.equals(elementId)) {
                    applyNestedUpdate(elementWrapper, remainingPath, value);
                    log.debug("Updated list element: {} = {}", fieldPath, value);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error updating list element '{}': {}", fieldPath, e.getMessage());
        }
    }
    
    private void applyNestedUpdate(BeanWrapper wrapper, String path, Object value) {
        if (!path.contains(".")) {
            wrapper.setPropertyValue(path, value);
            return;
        }
        
        String[] parts = path.split("\\.", 2);
        Object nested = wrapper.getPropertyValue(parts[0]);
        
        if (nested != null) {
            applyNestedUpdate(new BeanWrapperImpl(nested), parts[1], value);
        }
    }
}
