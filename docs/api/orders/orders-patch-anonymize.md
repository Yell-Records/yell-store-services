# Anonymize Customer Data on Order
Anonymizes customer information on an order. The order state must not be in-progress.

**URL**: `/api/orders/:id/anonymize`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

**Data constraints**: None

**Data example**: `{}`

## Success Responses
**Condition**: Order is within a valid status and has not been anonymized.

**Code**: `200 OK`

### Content example
```json
{
  "id": "b81f19e1-e291-40a0-b17e-ba7740d93efe",
  "buyerEmail": "deleted+10482812@example.com",
  "orderNumber": 10482812,
  "status": "FULFILLED",
  "subtotal": 30.23,
  "tax": 1.0,
  "shippingCost": 5.0,
  "totalPaid": 36.23,
  "createdAt": "2026-05-05T22:00:00Z",
  "shippingFirstname": "Deleted",
  "shippingLastname": "User",
  "shippingAddressLine1": "Anonymized",
  "shippingAddressLine2": null,
  "shippingCity": "Anonymized",
  "shippingState": "NY",
  "shippingPostalCode": "00000",
  "shippingPhone": "0000000000",
  "orderItems": [
    {
      "id": "3b6b4665-2619-4c71-a50e-738d32d8e133",
      "listingId": "ec41e231-2776-41f2-91b4-dc765860041c",
      "listingTitle": "Test Vinyl",
      "listingDescription": "Test vinyl description",
      "listingImageUrl": "http://localhost:8080/uploads/test-vinyl.png",
      "listingPrice": 36.23,
      "quantity": 1
    }
  ],
  "trackingNumber": "1Z9999999999999999",
  "paidAt": "2026-05-05T22:00:00Z",
  "shippedAt": "2026-05-05T22:00:00Z",
  "policiesAcceptedAt": "2026-05-05T22:00:00Z",
  "anonymized": true,
  "anonymizedAt": "2026-05-06T22:00:00Z"
}
```

### OR
**Condition**: Order was already anonymized.

**Code**: `204 NO CONTENT`

**Content**: `{}`

## Error Responses
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Order status is not in valid state.

**Code**: `409 CONFLICT`

**Content**: `{}`


