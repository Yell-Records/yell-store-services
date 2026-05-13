# Create PayPal Order
Sends a request to PayPal that creates a new order and attaches the order ID to an order entity. A PayPal order is 
PayPal's structured object that represents "_a user intends to buy something, but has not paid yet._"

**URL**: `/api/orders/:id/paypal/create`

**URL parameters**: `id=[UUID]` where `id` is the order ID.

**Method**: `POST`

**Auth required**: NO

**Permissions required**: None

**Data constraints**: None

**Data example**: `{}`

## Success Response
**Condition**: Order exists and PayPal request succeeds.

**Code**: `200 OK`

### Content example
```json
{
  "id": "4GS918977Y9717925"
}
```


## Error Responses
**Condition**: Order does not exist.

**Code**: `404 NOT FOUND`

**Content**: `{}`

### OR
**Condition**: PayPal request fails.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
