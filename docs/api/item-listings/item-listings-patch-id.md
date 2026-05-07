# Update Item Listing by ID
Changes properties on an existing item listing.

**URL**: `/api/item-listings/:id`

**URL parameters**: `id=[UUID]` where `id` is the ID of the item listing.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
```json
{
  "title": "[string or null]",
  "description": "[string or null]",
  "price": "[decimal greater than 0]",
  "imageUrl": "[string or null]",
  "categorySlug": "[string or null, slug must exist in categories]",
  "isActive": "[boolean or null]"
}
```

### Data example
Updating an item listing related to The Beatles:

```json
{
  "title": "NEW Beatles"
}
```

## Success Responses
**Condition**: Data contains valid modifications.

**Code**: `200 OK`

**Content**: `{}`

### Or

**Condition**: Data contains no modifications.

**Code**: `204 NO CONTENT`

**Content**: `{}`

## Error Responses
**Condition**: Item listing does not exist OR category slug does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Price is less than or equal to 0.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
