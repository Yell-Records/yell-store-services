# Create new Artist Page
Creates a new artist page.

**URL**: `/api/artist-pages`

**Method**: `POST`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
```json
{
  "slug": "[string]",
  "name": "[string]",
  "bodyHtml": "[string]",
  "categorySlug": "[string slug of existing category]"
}
```

### Data example
```json
{
  "slug": "hank-locklin",
  "name": "Hank Locklin",
  "bodyHtml": "<h1>Hank Locklin</h1><p>Country music singer.</p>",
  "categorySlug": "hank-locklin"
}
```

## Success Response
**Condition**: Slug is valid and unique.

**Code**: `201 CREATED`

### Content example
```json
{
  "id": "53bcda5c-d3f3-4912-a667-9a315b8dd7a4",
  "slug": "hank-locklin",
  "name": "Hank Locklin",
  "bodyHtml": "<h1>Hank Locklin</h1><p>Country music singer.</p>",
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
