# Get Item Listings by Category
Gets item listings grouped under a specified category slug.

**URL**: `/api/item-listings/category/:slug`

**URL parameters**: `slug=[string]` where `slug` is an existing category slug.

**Auth required**: NO

**Permissions required**: NO

**Content**: `[]`

## Success Response
**Condition**: Category slug exists.

**Code**: `200 OK`

### Content example
Providing the slug `elvis-presley`:

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

## Error Response
**Condition**: Category with provided slug does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`
