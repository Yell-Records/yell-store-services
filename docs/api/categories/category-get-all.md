# Get ALL Categories
Retrieves every single category, whether active or inactive.

**URL**: `/api/categories/all`

**Method**: `GET`

**Auth required**: YES

**Permissions required**: ADMIN

## Success Response
**Code**: `200 OK`

### Content example
```json
[
  {
    "id": "e26bdbdf-b45e-4435-b223-b462fd6e2582",
    "name": "The Beatles",
    "slug": "the-beatles",
    "isActive": true,
    "createdAt": "2026-05-05T22:00:00Z"
  },
  {
    "id": "3e87e99c-008d-4963-a75d-0effd6dfb0eb",
    "name": "Elvis Presley",
    "slug": "elvis-presley",
    "isActive": true,
    "createdAt": "2026-05-05T22:00:00Z"
  },
  {
    "id": "d801f9dc-487f-4898-899b-4d3400dab296",
    "name": "Personal Karaoke",
    "slug": "personal-karaoke",
    "isActive": false,
    "createdAt": "2026-05-05T22:00:00Z"
  }
]
```
