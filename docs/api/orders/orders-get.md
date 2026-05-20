# Get all Orders sorted by Status
Retrieves every placed order, either unfinished or finished.

Unfinished orders are sorted by creation date descending, and finished orders are sorted by `paidAt`.

**URL**: `/api/orders`

**Request parameters**: `unfinished=[boolean required]` where `unfinished` is to get either finished or 
unfinished orders. Unfinished orders are orders with a status of _IN_PROGRESS_.

**Method**: `GET`

**Auth required**: YES

**Permissions required**: ADMIN

## Success Response
**Condition**: Always.

**Code**: `200 OK`

### Content example
```json
[
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
]
```
