# Update category
Updates information on a category.

**URL**: `/api/categories/:id`

**URL Parameters**: `id=[UUID]` where `id` is the category ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
```json
{
  "name": "[string or null]",
  "slug": "[string or null]",
  "isActive": "[boolean or null]"
}
```

### Data example
Here we want to set category with ID of 2 to inactive:
```json
{
  "isActive": false
}
```

## Success Responses
**Condition**: Property changed.

**Code**: `200 OK`

**Content**: `{}`

### Or

**Condition**: Properties unchanged / indifferent from original.

**Code**: `204 NO CONTENT`

**Content**: `{}`

## Error Responses
**Condition**: Category does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### Or
**Condition**: Slug already exists.

**Code**: `403 FORBIDDEN`

**Content**: `{}`

### Or
**Condition**: Slug does not abide by regex: `^[a-z0-9-]+$`.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
