# Get Order from Order Number
Retrieves an order associated with an order number.

**URL**: `/api/orders/order-number/:orderNumber`

**URL parameters**: `orderNumber=[number]` where `orderNumber` is the order number.

**Method**: `GET`

**Auth required**: YES

**Permissions required**: ADMIN

## Success Response
**Condition**: Order exists.

**Code**: `200 OK`

### Content example
Get order with order number **10482812**:
```json
{
  "id": "b81f19e1-e291-40a0-b17e-ba7740d93efe",
  "buyerEmail": "user@test.com",
  "orderNumber": 10482812,
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
  "paidAt": "2026-05-05T22:00:00Z",
  "shippedAt": null,
  "policiesAcceptedAt": "2026-05-05T22:00:00Z",
  "anonymized": false,
  "anonymizedAt": null
}
```

## Error Response
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`
