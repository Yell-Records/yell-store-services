# Yell Records API
A general overview of current REST endpoints the application uses.

## Open Endpoints
Open endpoints require no Authentication.

### Authentication
Endpoints for login and endpoint authorization.
- [Login](auth/auth-post-login.md): `POST /api/auth/login`

### Cart Items
Endpoints related to items within a client's cart.
- [Get client's cart items](cart/cart-get-guest.md): `GET /api/cart-items/guest/:guestSessionId`
- [Add item to cart](cart/cart-post-add-item.md): `POST /api/cart-items`
- [Clear cart items for client](cart/cart-delete-all-for-guest.md): `DELETE /api/cart-items/guest/:guestSessionId`
- [Remove single item from cart](cart/cart-delete-item-for-guest.md): `DELETE /api/cart-items/guest/:guestSessionId/listing/:listingId`

### Categories
Endpoints for item listing categories.
- [Get active categories](categories/category-get.md): `GET /api/categories`

### Item Listings
Endpoints for products being sold.
- [Get all item listings](item-listings/item-listings-get.md): `GET /api/item-listings`
- [Get item listing by ID](item-listings/item-listings-get-id.md): `GET /api/item-listings/:id`
- [Get item listings under category](item-listings/item-listings-get-category.md): `GET /api/item-listings/category/:slug`

### Orders
Endpoints for purchase orders
- [Create / place order](orders/orders-post.md): `POST /api/orders`

## Endpoints that require Authentication
Closed endpoints require a valid Token to be included in the header request, which is usually only provided
to administrator accounts.

### Categories
- [Get ALL categories](categories/category-get-all.md): `GET /api/categories/all`
- [Create new category](categories/category-post.md): `POST /api/categories`
- [Update category](categories/category-patch-pk.md): `PATCH /api/categories/:id`

### Images
- [Upload image](images/image-post-upload.md): `POST /api/images/upload`

### Item Listings
- [Create new item listing](item-listings/item-listings-post.md): `POST /api/item-listings`
- [Update item listing](item-listings/item-listings-patch-id.md): `PATCH /api/item-listings/:id`

### Orders
- [Get orders organized by status](orders/orders-get.md): `GET /api/orders`
