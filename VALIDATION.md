# Input Validation Rules

## ProductRequestDto

| Field | Required | Validation Rules |
|-------|----------|------------------|
| `name` | ✅ Yes | - Not blank<br>- Length: 2-100 characters |
| `description` | ❌ No | - Max length: 500 characters |
| `price` | ✅ Yes | - Not null<br>- Must be > 0 |
| `category` | ✅ Yes | - Not blank |
| `available` | ❌ No | - Boolean |
| `tags` | ❌ No | - List of strings |
| `manufacturer` | ❌ No | - Max length: 100 characters |
| `modelNumber` | ❌ No | - Any string |
| `sku` | ❌ No | - Pattern: `^[A-Z0-9-]+$` (uppercase, numbers, hyphens only) |
| `status` | ❌ No | - Any string |
| `productDetails` | ❌ No | - List of ProductDetailsRequestDto |

## ProductDetailsRequestDto

| Field | Required | Validation Rules |
|-------|----------|------------------|
| `detailId` | ✅ Yes | - Not blank |
| `weight` | ❌ No | - Any string |
| `dimensions` | ❌ No | - Any string |
| `color` | ❌ No | - Any string |
| `material` | ❌ No | - Any string |
| `logistics` | ❌ No | - ProductLogisticsRequestDto |

## ProductLogisticsRequestDto

| Field | Required | Validation Rules |
|-------|----------|------------------|
| `shippingWeightKg` | ❌ No | - Must be > 0 (if provided) |
| `volumeCubicCm` | ❌ No | - Any string |
| `countryOfOrigin` | ❌ No | - Max length: 100 characters |
| `customsCode` | ❌ No | - Any string |

## Example Valid Request

```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "category": "Electronics",
  "available": true,
  "tags": ["laptop", "computer"],
  "manufacturer": "TechCorp",
  "modelNumber": "TC-2024",
  "sku": "LAPTOP-001",
  "status": "Active",
  "productDetails": [
    {
      "detailId": "spec-1",
      "weight": "2.5kg",
      "dimensions": "35x25x2cm",
      "color": "Silver",
      "material": "Aluminum",
      "logistics": {
        "shippingWeightKg": 3.0,
        "volumeCubicCm": "5000",
        "countryOfOrigin": "USA",
        "customsCode": "8471.30"
      }
    }
  ]
}
```

## Example Validation Error Response

```json
{
  "timestamp": "2024-02-22T20:15:30",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/products",
  "details": [
    "name: Product name is required",
    "price: Price is required",
    "category: Category is required"
  ]
}
```
