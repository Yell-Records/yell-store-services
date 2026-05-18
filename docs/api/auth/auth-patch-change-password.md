# Change User Password
Changes the password on a user account.

**URL**: `/api/auth/user/:id/change-password`

**URL parameters**: `id=[UUID]` where `id` is the user ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: User is authenticated and matches `id`.

### Data constraints
```json
{
  "rawCurrent": "[string - current raw password]",
  "rawNew": "[string - new password]",
  "rawNew2": "[string - new password (same value)]"
}
```

### Data example
```json
{
  "rawCurrent": "password",
  "rawNew": "ILoveToTravel",
  "rawNew2": "ILoveToTravel"
}
```

## Success Response
**Conditions**:
1. `rawCurrent` matches the user's current password
2. `rawNew` does not match the current password
3. `rawNew2` matches `rawNew`

**Code**: `200 OK`

**Content**: `{}`

## Error Responses
**Condition**: User does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Password field validation fails.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
