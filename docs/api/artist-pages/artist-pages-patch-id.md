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
  "youtubeUrls": "[string array]",
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

**Content**: `{}`

### OR
**Condition**: Request has no fields.

**Code**: `204 NO CONTENT`

**Content**: `{}`

## Error Responses
**Condition**: Slug does not match regex: `^[a-z0-9-]+$`.

**Code**: `400 BAD REQUEST`

**Content**: `{}`

### OR
**Condition**: Slug already exists on another artist page.

**Code**: `409 CONFLICT`

**Content**: `{}`
