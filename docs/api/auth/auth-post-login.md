# Login User
Validates user details and sends back a new Java Web Token associated with the user.

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
Sends a Java Web Token to the application.
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
  "username": "admin"
}
```

## Error Responses
**Condition**: If any part of the request does not match a user.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
