# Update Artist Page
Updates an artist page by its ID.

**URL**: `/api/artist-pages/:id`

**URL parameters**: `id=[UUID]` where `id` is the ID of the artist page.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
All fields are optional.

```json
{
  "name": "[string]",
  "slug": "[string matching regex ^[a-z0-9-]+$]",
  "bodyHtml": "[string]",
  "categorySlug": "[string slug of existing category]"
}
```

### Data example
Updating the HTML on an existing artist page:

```json
{
  "bodyHtml": "<p>UPDATE: This page is no longer maintained.</p>"
}
```

## Success Responses
**Condition**: Property updated.

**Code**: `200 OK`

### Content example
Returns the updated entity.
```json
{
  "id": "d5204540-1e4b-4c0b-bc85-d8939407d568",
  "slug": "floyd-cramer",
  "name": "Floyd Cramer",
  "bodyHtml": "<h1>Floyd Cramer</h1><p>Floyd Cramer left an important mark on music</p>",
  "categoryId": "0e91cef8-2fa7-4b1a-94c1-c02b969289a4",
  "createdAt": "2026-05-05T22:00:00Z",
  "updatedAt": "2026-05-05T22:00:00Z"
}
```

### OR
**Condition**: Request has no fields.

**Code**: `204 NO CONTENT`

Returns the same entity.
```json
{
  "id": "d5204540-1e4b-4c0b-bc85-d8939407d568",
  "slug": "floyd-cramer",
  "name": "Floyd Cramer",
  "bodyHtml": "<h1>Floyd Cramer</h1><p>Floyd Cramer left an important mark on music</p>",
  "categoryId": "0e91cef8-2fa7-4b1a-94c1-c02b969289a4",
  "createdAt": "2026-05-05T22:00:00Z",
  "updatedAt": "2026-05-05T22:00:00Z"
}
```

## Error Responses
**Condition**: Slug does not match regex: `^[a-z0-9-]+$`.

**Code**: `400 BAD REQUEST`

**Content**: `{}`

### OR
**Condition**: Slug already exists on another artist page.

**Code**: `409 CONFLICT`

**Content**: `{}`
