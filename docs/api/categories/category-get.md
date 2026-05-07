# Get Active Categories
Retrieves every category marked as active.

**URL**: `/api/categories`

**Method**: `GET`

**Auth required**: NO

**Permissions required**: None

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
  }
]
```