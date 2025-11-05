package com.example.product.service;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Generic service for applying dynamic partial updates to any Java object.
 * It uses Spring's BeanWrapper to access and modify properties, including nested
 * properties and elements within lists (identified by a unique ID field).
 * Updated to support deeper nesting levels within list elements.
 *
 * @param <T> The type of the main entity being updated.
 */
@Service
public class GenericUpdateService<T> {

    private String nestedListFieldName;
    private String nestedListIdFieldName;

    /**
     * Initializes the GenericUpdateService.
     *
     * @param nestedListFieldName   The name of the field that holds the List of nested objects (e.g., "productDetails").
     * @param nestedListIdFieldName The name of the unique ID field within the nested objects (e.g., "detailId").
     */
    public void configure(String nestedListFieldName, String nestedListIdFieldName) {
        this.nestedListFieldName = nestedListFieldName;
        this.nestedListIdFieldName = nestedListIdFieldName;
    }

    /**
     * Applies a map of updates to a given entity object.
     * This method handles both top-level fields and fields within a nested list,
     * including arbitrary depths within the nested list elements.
     *
     * @param entity  The entity object to which updates should be applied.
     * @param updates A map where keys are field paths (e.g., "name", "productDetails.spec-A.logistics.shippingWeightKg")
     * and values are the new values for those fields.
     * @return The updated entity object.
     */
    public T applyUpdates(T entity, Map<String, Object> updates) {
        if (entity == null || updates == null || updates.isEmpty()) {
            return entity;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(entity);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String fieldPath = entry.getKey();
            Object fieldValue = entry.getValue();

            // Check if the field path refers to an element within the configured nested list
            // e.g., "productDetails.v1-spec.logistics.shippingWeightKg"
            String listPathPrefix = nestedListFieldName + ".";
            if (fieldPath.startsWith(listPathPrefix)) {
                // Try to extract the ID and the remaining path
                String pathAfterListField = fieldPath.substring(listPathPrefix.length());
                int firstDotIndex = pathAfterListField.indexOf('.');

                if (firstDotIndex != -1) {
                    String nestedIdValue = pathAfterListField.substring(0, firstDotIndex);
                    String remainingNestedPath = pathAfterListField.substring(firstDotIndex + 1);

                    List<?> nestedList = (List<?>) beanWrapper.getPropertyValue(nestedListFieldName);

                    if (nestedList != null) {
                        for (Object nestedObject : nestedList) {
                            BeanWrapper nestedBeanWrapper = new BeanWrapperImpl(nestedObject);
                            Object currentNestedId = nestedBeanWrapper.getPropertyValue(nestedListIdFieldName);

                            if (currentNestedId != null && currentNestedId.equals(nestedIdValue)) {
                                // Found the specific nested object to update
                                // Now, recursively apply the remaining path to this nested object
                                applySingleUpdate(nestedBeanWrapper, remainingNestedPath, fieldValue);
                                break; // Move to next update entry after successful update
                            }
                        }
                    }
                } else {
                    // This case means something like "productDetails.someId" which is not a valid field update,
                    // but rather a list element lookup. We'll ignore it or log an error.
                    System.err.println("Invalid path format for list element update: " + fieldPath);
                }
            } else {
                // It's a top-level field update
                applySingleUpdate(beanWrapper, fieldPath, fieldValue);
            }
        }
        return entity;
    }

    /**
     * Applies a single update to a field at a given path using BeanWrapper.
     * This method can traverse nested objects.
     *
     * @param currentBeanWrapper The BeanWrapper representing the current object to modify.
     * @param fieldPath The path to the field (e.g., "name", "logistics.shippingWeightKg").
     * @param value The new value for the field.
     */
    private void applySingleUpdate(BeanWrapper currentBeanWrapper, String fieldPath, Object value) {
        try {
            // Check if the path contains further nesting (e.g., "logistics.shippingWeightKg")
            if (currentBeanWrapper.isWritableProperty(fieldPath)) {
                currentBeanWrapper.setPropertyValue(fieldPath, value);
            } else {
                // This means the fieldPath itself is not directly a property,
                // but it might contain dot notation for nested properties.
                // BeanWrapper's setPropertyValue usually handles this, but explicit check for safety.
                String[] pathParts = fieldPath.split("\\.", 2); // Split only on the first dot

                if (pathParts.length == 2) {
                    String currentFieldName = pathParts[0]; // e.g., "logistics"
                    String remainingPath = pathParts[1];    // e.g., "shippingWeightKg"

                    Object nestedObject = currentBeanWrapper.getPropertyValue(currentFieldName);

                    if (nestedObject == null) {
                        // If the nested object is null, try to instantiate it if it's a known type
                        // This requires convention or more complex logic. For simplicity,
                        // assume the object already exists for deeper updates.
                        // Or, you could get the property type and try to instantiate it via reflection.
                        System.err.println("Cannot update null nested object for path: " + fieldPath + ". Field: " + currentFieldName);
                        return;
                    }

                    BeanWrapper nestedBeanWrapper = new BeanWrapperImpl(nestedObject);
                    applySingleUpdate(nestedBeanWrapper, remainingPath, value); // Recursive call
                } else {
                    // This case should ideally not be reached if isWritableProperty handles simple paths correctly
                    System.err.println("Could not resolve field path: " + fieldPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Error applying update for path '" + fieldPath + "': " + e.getMessage());
            // Log the error but continue processing other updates
        }
    }
}