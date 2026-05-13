# Capture PayPal Order
Captures payment of an order through PayPal. "Capturing" checks to make sure the user authorized payment on PayPal's end.

**URL**: `/api/orders/:id/paypal/capture`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `POST`

**Auth required**: NO

**Permissions Required**: None

**Data constraints**: None

**Data example**: `{}`

## Success Response
### Conditions
1. Capture request comes back with status _COMPLETED_
2. Order exists
3. Order status is _AWAITING_PAYMENT_
4. Order status is not _PAID_

**Code**: `200 OK`

### Content example
```json
{
  "id": "b81f19e1-e291-40a0-b17e-ba7740d93efe",
  "buyerEmail": "user@test.com",
  "status": "PAID",
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
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Order status is _PAID_.

**Code**: `409 CONFLICT`

**Content**: `{}`

### OR
**Condition**: Order status is not _AWAITING_PAYMENT_.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
