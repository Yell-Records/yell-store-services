# Get user by ID
Retrieves a user by their ID.

**URL**: `/api/users/:id`

**URL parameters**: `id=[UUID]` where `id` is the user ID.

**Method**: `GET`

**Auth required**: YES

**Permissions required**: ADMIN

## Success Response
**Condition**: Always.

**Code**: `200 OK`

### Content example
```json
{
  "id": "3b5e9e1b-d7e8-47a2-825a-7a0ad0881c8a",
  "username": "admin",
  "email": "admin@yellrecords.com",
  "createdAt": "2026-05-05T22:00:00Z",
  "role": "ADMIN"
}
```
