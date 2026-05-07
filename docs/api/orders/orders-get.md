# Get all Orders sorted by Status
Retrieves every placed order, either unfinished or finished.

Unfinished orders are sorted by creation date descending, and finished orders are sorted by `paidAt`.

**URL**: `/api/orders`

**Request parameters**: `unfinished=[boolean required]` where `unfinished` is to get either finished or 
unfinished orders. Unfinished orders are orders with a status of _IN_PROGRESS_.

**Method**: `GET`

**Auth required**: YES

**Permissions required**: ADMIN

## Success Response
**Condition**: Always.

**Code**: `200 OK`

**Content**: `[]`
