# Update Order by ID
Updates certain fields in an Order.

**URL**: `/api/orders/:id`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `PATCH`

**Auth required**: NO

**Permissions required**: Guest session ID must match the order being updated.

### Data constraints
If a field is null, the value does not change. The exception is `shippingAddressLine2` since that is an optional field.

```json
{
  "guestSessionId": "[UUID]",
  "buyerEmail": "[string or null]",
  "shippingFirstName": "[string or null]",
  "shippingLastName": "[string or null]",
  "shippingAddressLine1": "[string or null]",
  "shippingAddressLine2": "[string or null]",
  "shippingCity": "[string or null]",
  "shippingState": "[string max length 2 or null]",
  "shippingPostalCode": "[string max length 5 or null]",
  "shippingPhone": "[string or null]"
}
```

### Data example
```json
{
  "guestSessionId": "6d5a8d6c-2f52-4704-bc96-7a1998e852a2",
  "shippingCity": "Schaumburg",
  "shippingState": "IL"
}
```

## Success Response
**Condition**: All checks pass.

**Code**: `200 OK`

### Content example
```json
{
  "id": "b81f19e1-e291-40a0-b17e-ba7740d93efe",
  "buyerEmail": "user@test.com",
  "status": "AWAITING_PAYMENT",
  "subtotal": 30.23,
  "tax": 1.0,
  "shippingCost": 5.0,
  "totalPaid": null,
  "createdAt": "2026-05-05T22:00:00Z",
  "shippingFirstname": "John",
  "shippingLastname": "Doe",
  "shippingAddressLine1": "123 Yorkshire Way",
  "shippingAddressLine2": null,
  "shippingCity": "New York",
  "shippingState": "NY",
  "shippingPostalCode": "55555",
  "shippingPhone": "5552810293",
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
  "trackingNumber": null,
  "trackingCarrier": null,
  "paidAt": null,
  "shippedAt": null
}
```

## Error Responses
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Guest session ID does not match update request.

**Code**: `403 FORBIDDEN`

**Content**: `{}`

### OR
**Condition**: Order status is not _AWAITING_PAYMENT_.

**Code**: `403 FORBIDDEN`

**Content**: `{}`
