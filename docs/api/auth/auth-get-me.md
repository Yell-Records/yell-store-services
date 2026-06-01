# Get current authenticated user
Retrieves user details based on the current authentication from login.

**URL**: `/api/auth/me`

**Method**: `GET`

**Auth required**: YES

**Permissions required**: None

## Success Response
**Condition**: User is currently authenticated.

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

## Error Response
**Condition**: User is not authenticated.

**Code**: `401 UNAUTHORIZED`

**Content**: `{}`
