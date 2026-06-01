# Refresh current user session
Uses the refresh cookie in the request to re-validate the access token cookie.

**URL**: `/api/auth/refresh`

**Method**: `POST`

**Auth required**: YES

**Permissions required**: None

**Data constraints**: None

**Data example**: `{}`

## Success Response
**Condition**: Refresh cookie exists and is valid.

**Code**: `200 OK`

**Content**: `{}`

## Error Response
**Condition**: No refresh cookie exists.

**Code**: `401 UNAUTHORIZED`

**Content**: `{}`
