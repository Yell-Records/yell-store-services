# Create Order
Creates a new purchase order.

**URL**: `/api/orders`

**Method**: `POST`

**Auth required**: NO

**Permissions required**: None

### Data constraints
```json
{
  "guestSessionId": "[UUID]",
  "buyerEmail": "[string]",
  "totalPaid": "[decimal greater than 0]",
  "shippingFirstName": "[string]",
  "shippingLastName": "[string]",
  "shippingAddressLine1": "[string]",
  "shippingAddressLine2": "[string or null]",
  "shippingCity": "[string]",
  "shippingState": "[string max length of 2]",
  "shippingPostalCode": "[string]",
  "shippingPhone": "[string]"
}
```

### Data example
```json
{
  "guestSessionId": "bc531460-ab0e-441f-b350-07b7472ce8c2",
  "buyerEmail": "user@test.com",
  "totalPaid": 36.23,
  "shippingFirstName": "John",
  "shippingLastName": "Doe",
  "shippingAddressLine1": "123 Yorkshire Way",
  "shippingAddressLine2": null,
  "shippingCity": "New York",
  "shippingState": "NY",
  "shippingPostalCode": "55555",
  "shippingPhone": "5552810293"
}
```

## Success Response
**Condition**: All validations pass.

**Code**: `201 CREATED`

### Content example
```json
{
  "id": "b81f19e1-e291-40a0-b17e-ba7740d93efe",
  "buyerEmail": "user@test.com",
  "status": "IN_PROGRESS",
  "subtotal": 30.23,
  "tax": 1.0,
  "shippingCost": 5.0,
  "totalPaid": 36.23,
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
  "paidAt": "2026-05-05T22:00:00Z",
  "shippedAt": null
}
```

## Error Responses
**Condition**: Total paid is equal to or below 0.

**Code** `400 BAD REQUEST`

**Content**: `{}`

### OR
**Condition**: Guest has no cart items.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
