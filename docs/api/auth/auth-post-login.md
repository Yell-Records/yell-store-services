# Login User
Validates user details and attaches authenticated cookies to the response.

**URL**: `/api/auth/login`

**Method**: `POST`

**Auth required**: NO

**Permissions required**: None

### Data constraints
Provide a username and password to validate.
```json
{
  "username": "[string]",
  "rawPassword": "[string]"
}
```

### Data example
All fields must be present.
```json
{
  "username": "admin",
  "rawPassword": "IloveToTravel"
}
```

## Success Response
**Condition**: If the username exists and the raw password matches the current hashed version.

**Code**: `200 OK`

### Content example
Sends the authenticated user details back.
```json
{
  "id": "3b5e9e1b-d7e8-47a2-825a-7a0ad0881c8a",
  "username": "admin",
  "email": "admin@yellrecords.com",
  "createdAt": "2026-05-05T22:00:00Z",
  "role": "ADMIN"
}
```

## Error Responses
**Condition**: If any part of the request does not match a user.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
