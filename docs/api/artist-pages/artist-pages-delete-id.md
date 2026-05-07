# Delete Artist Page by ID
Deletes an artist page by its ID.

**URL**: `/api/artist-pages/:id`

**URL parameters**: `id=[UUID]` where `id` is the ID of an artist page.

**Method**: `DELETE`

**Auth required**: YES

**Permissions required**: ADMIN

## Success Response
**Condition**: Artist page exists.

**Code**: `204 NO CONTENT`

**Content**: `{}`

## Error Response
**Condition**: Artist page does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`
