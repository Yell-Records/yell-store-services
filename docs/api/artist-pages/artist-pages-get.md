# Get all Artist Pages
Retrieves every artist page.

**URL**: `/api/artist-pages`

**Method**: `GET`

**Auth required**: NO

**Permissions required**: None

## Success Response
**Condition**: Always.

**Code**: `200 OK`

### Content example
```json
[
  {
    "id": "d5204540-1e4b-4c0b-bc85-d8939407d568",
    "slug": "floyd-cramer",
    "name": "Floyd Cramer",
    "bodyHtml": "<h1>Floyd Cramer</h1><p>Floyd Cramer left an important mark on music</p>",
    "youtubeUrls": ["https://www.youtube.com/watch?v=dQw4w9WgXcQ"],
    "categoryId": "0e91cef8-2fa7-4b1a-94c1-c02b969289a4",
    "createdAt": "2026-05-05T22:00:00Z",
    "updatedAt": "2026-05-05T22:00:00Z"
  }
]
```
