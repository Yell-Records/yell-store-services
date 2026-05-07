# Remove Cart Item for Guest
Deletes a specified cart item associated with a guest session ID.

**URL**: `/api/cart-items/guest/:guestSessionId/listing/:listingId`

**URL parameters**
- `guestSessionId=[UUID]` where `guestSessionId` is the UUID of the client's session.
- `listingId=[UUID]` where `listingId` is the UUID of the cart's item listing they want to remove.

**Method**: `DELETE`

**Auth required**: NO

**Permissions required**: None

## Success Response
**Condition**: URL parameters are not malformed.

**Code**: `204 NO CONTENT`

**Content**: `{}`
