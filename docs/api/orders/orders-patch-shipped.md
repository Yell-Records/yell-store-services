# Set Order Status to 'Shipped'
Updates an order with shipping information and changes the status to _SHIPPED_.

**URL**: `/api/orders/:id/shipped`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `PATCH`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
```json
{
  "trackingNumber": "[string]"
}
```

### Data example
```json
{
  "trackingNumber": "1Z9999999999999999"
}
```

## Success Response
**Condition**: Order exists and status is `IN_PROGRESS`

**Code**: `200 OK`

**Content**: `{}`

## Error Responses
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: Order status is not `IN_PROGRESS`.

**Code**: `409 CONFLICT`

**Content**: `{}`
