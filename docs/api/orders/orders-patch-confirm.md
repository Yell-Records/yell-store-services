# Set Order Status to 'In-Progress'
Confirms the order and changes the status to `IN_PROGRESS`.

**URL**: `/api/orders/:id/confirm`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

**Data constraints**: None

**Data example**: `{}`

## Success Response
**Condition**: Order status is _PAID_.

**Code**: `200 OK`

**Content**: `{}`

## Error Responses
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Order status is not _PAID_.

**Code**: `409 CONFLICT`

**Content**: `{}`
