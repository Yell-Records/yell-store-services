# Get all Item Listings
Retrieves every item listing.

**URL**: `/api/item-listings/all`

**Method**: `GET`

**Auth required**: YES

**Permissions required**: Admin

## Success Response
**Condition**: Always.

**Code**: `200 OK`

**Content**: `[]`

### Content example
```json
[
  {
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
]
```
