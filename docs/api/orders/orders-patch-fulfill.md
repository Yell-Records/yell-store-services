# Set Order Status to 'Fulfilled'
Updates an order's status to _FULFILLED_, indicating the merchant received information that the package was delivered.

**URL**: `/api/orders/:id/fulfill`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

**Data constraints**: None

**Data example**: `{}`

## Success Response
**Condition**: Order exists and status is _SHIPPED_.

**Code**: `200 OK`

**Content**: `{}`

## Error Responses
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Order status is not _SHIPPED_.

**Code**: `409 CONFLICT`

**Content**: `{}`
