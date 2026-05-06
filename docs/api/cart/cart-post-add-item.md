# Add Item to Cart
Adds an item listing to a user's cart. If they already have the requested item, increments
the quantity instead.

**URL**: `/api/cart-items`

**Method**: `POST`

**Auth required**: NO

**Permissions required**: None

### Data constraints
Provide the session ID of the client.
```json
{
  "guestSessionId": "[UUID]",
  "listingInfo": "[object holding item listing data]",
  "itemQuantity": "[integer greater than 0]"
}
```

### Data example
```json
{
  "guestSessionId": "6d0f6357-9819-4081-9904-1c4c08ece6de",
  "listingInfo": {
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
  },
  "itemQuantity": 1
}
```

## Success Response
**Condition**: Item quantity is greater than 0.

**Code**: `200 OK`

### Content example
```json
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
```

## Error Responses
**Condition**: Item quantity is less than or equal to 0.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
