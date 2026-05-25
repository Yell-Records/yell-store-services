# Get Artist Page by Slug
Gets the artist page associated with a URL slug.

**URL**: `/api/artist-pages/slug/:slug`

**URL parameters**: `slug=[string]` where `slug` is the artist page slug.

**Method**: GET

**Auth required**: NO

**Permissions required**: None

## Success Response
**Condition**: If an artist page has the requested slug.

**Code**: `200 OK`

### Content example
Retrieve artist page with slug `floyd-cramer`:

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

## Error Response
**Condition**: No artist page has the requested slug.

**Code**: `404 NOT FOUND`

**Content**: `{}`
