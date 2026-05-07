# Create new Item Listing
Creates a new item listing.

**URL**: `/api/item-listings`

**Method**: `POST`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
```json
{
  "title": "[string]",
  "description": "[string or null]",
  "price": "[decimal greater than 0]",
  "imageUrl": "[string url to image]",
  "categorySlug": "[string referencing existing category slug]"
}
```

### Data example
```json
{
  "title": "The Beatles Vinyl",
  "description": "All songs made by The Beatles on Vinyl!",
  "price": 2.99,
  "imageUrl": "http://localhost:8080/uploads/beatles-vinyl.png",
  "categorySlug": "the-beatles"
}
```

## Success Response
**Condition**: Price is greater than 0.

**Code**: `201 CREATED`

### Content example
```json
{
  "id": "f6961f60-8efd-4dba-9d31-8c25ab332be4",
  "title": "The Beatles Vinyl",
  "description": "All songs made by The Beatles on Vinyl!",
  "price": 2.99,
  "imageUrl": "http://localhost:8080/uploads/beatles-vinyl.png",
  "createdAt": "2026-05-05T22:00:00Z",
  "updatedAt": "2026-05-05T22:00:00Z",
  "isActive": true,
  "quantitySold": 4,
  "categorySlug": "the-beatles",
  "categoryName": "The Beatles"
}
```

## Error Response
**Condition**: Category slug does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`
