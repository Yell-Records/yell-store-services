# Set Order Status to 'Canceled'
Sets an order status to _CANCELED_.

**URL**: `/api/orders/:id/cancel`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

**Data constraints**: None

**Data example**: `{}`

## Success Response
**Condition**: Order state is pre-shipping (ex: awaiting payment, paid, in-progress)

**Code**: `200 OK`

**Content**: `{}`

## Error Responses
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Order state is 'shipped' or 'fulfilled'.

**Code**: `409 CONFLICT`

**Content**: `{}`
