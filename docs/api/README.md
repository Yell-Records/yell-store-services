# Yell Records API
A general overview of current REST endpoints the application uses.

## Open Endpoints
Open endpoints require no Authentication.

### Authentication
Endpoints for login and endpoint authorization
- [Login](auth/auth-post-login.md): `POST /api/auth/login`

### Cart Items
Endpoints related to items within a client's cart.
- [Get client's cart items](cart/cart-get-guest.md): `GET /api/cart-items/guest/:guestSessionId`
- [Add item to cart](cart/cart-post-add-item.md): `POST /api/cart-items`
- [Clear cart items for client](cart/cart-delete-all-for-guest.md): `DELETE /api/cart-items/guest/:guestSessionId`
- [Remove single item from cart](cart/cart-delete-item-for-guest.md): `DELETE /api/cart-items/guest/:guestSessionId/listing/:listingId`

### Categories
Endpoints for item listing categories.
- [Get active categories](category/category-get.md): `GET /api/categories`

## Endpoints that require Authentication
Closed endpoints require a valid Token to be included in the header request, which is usually only provided
to administrator accounts.

### Categories
- [Get ALL categories](category/category-get-all.md): `GET /api/categories/all`
- [Create new category](category/category-post.md): `POST /api/categories`
- [Update category](category/category-patch-pk.md): `PATCH /api/categories/:id`

### Images
- [Upload image](images/image-post-upload.md): `POST /api/images/upload`
