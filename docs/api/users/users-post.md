# Create new user
Creates a new user account.

**URL**: `/api/users`

**Method**: `POST`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
```json
{
  "username": "[string max length 50]",
  "rawPassword": "[string]",
  "email": "[string or null]"
}
```

### Data example
```json
{
  "username": "testuser",
  "rawPassword": "test123",
  "email": null
}
```

## Success Response
**Condition**: Username does not exist.

**Code**: `201 CREATED`

### Content example
```json
{
  "id": "3b5e9e1b-d7e8-47a2-825a-7a0ad0881c8a",
  "username": "testuser",
  "email": null,
  "createdAt": "2026-05-05T22:00:00Z",
  "role": "ADMIN"
}
```

## Error Response
**Condition**: Username already exists.

**Code**: `409 CONFLICT`

**Content**: `{}`
