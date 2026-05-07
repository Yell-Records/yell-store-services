# Get Cart Items by Guest Session ID
Gets cart items associated with a guest session ID.

**URL**: `/api/cart-items/:guestSessionId`

**URL parameters**: `guestSessionId=[UUID]` where `guestSessionId` is the UUID generated from the front-end client.

**Method**: `GET`

**Auth required**: NO

**Permissions required**: None

## Success Response
**Condition**: If the guest session ID is a valid UUID (doesn't have to exist).

**Code**: `200 OK`

### Content example
```json
[
  {
    "id": "e5a5bf30-e505-417d-8a94-e23c6d2c4a4f",
    "quantity": 2,
    "itemListing": {
      "id": "f6961f60-8efd-4dba-9d31-8c25ab332be4",
      "title": "Elvis: Las Vegas Tour Vinyl",
      "description": "Elvis' classic hits on vinyl!",
      "price": 19.99,
      "imageUrl": "http://localhost:8080/elvis-img.png",
      "createdAt": "2026-05-05T22:00:00Z",
      "updatedAt": "2026-05-05T22:00:00Z",
      "isActive": true,
      "quantitySold": 4,
      "categorySlug": "elvis-presley",
      "categoryName": "Elvis Presley"
    }
  }
]
```
