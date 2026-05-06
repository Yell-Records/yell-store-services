# Clear all Items in Client's Cart
Deletes every cart item associated with a guest session ID.

**URL**: `/api/cart-items/guest/:guestSessionId`

**URL parameters**: `guestSessionId=[UUID]` where `guestSessionId` is the client's session ID.

**Method**: `DELETE`

**Auth required**: NO

**Permissions required**: None

## Success Response
**Condition**: Guest session ID is a valid UUID.

**Code**: `204 NO CONTENT`
