# Update User Email
Updates the email on a user. The email is converted to lowercase before being saved.

**URL**: `/api/users/:id/email`

**URL parameters**: `id=[UUID]` where `id` is the user ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: User is authenticated and matches `id`.

### Data constraints
```json
{
  "newEmail": "[string - email that matches constraints]"
}
```

### Data example
```json
{
  "newEmail": "betteremail@test.com"
}
```

## Success Response
**Condition**: Email is valid.

**Code**: `200 OK`

**Content**: `{}`

## Error Responses
**Condition**: User does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Email does not match email regex.

**Code**: `400 BAD REQUEST`

**Content**: `{}`

### OR
**Condition**: Email is the same as the previous.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
