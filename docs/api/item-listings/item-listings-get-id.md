# Get Item Listing by ID
Retrieves an item listing associated with a specified ID.

**URL**: `/api/item-listings/:id`

**URL parameters**: `id=[UUID]` where `id` is the ID of the item listing.

**Method**: `GET`

**Auth required**: NO

**Permissions required**: None

## Success Response
**Condition**: Valid UUID and item listing exists.

**Code**: `200 OK`

### Content example
Retrieve item listing with ID `f6961f60-8efd-4dba-9d31-8c25ab332be4`:

```json
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
```

## Error Response
**Condition**: Item listing does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`
