# Item Listings Table

## Table of Contents
- [Schema](#Schema)
- [Purpose](#purpose)
- [Lifecycle](#lifecycle)
    - [Row Creation](#row-creation)
    - [Row Updates](#row-updates)
    - [Row Deletion](#row-deletion)
- [Important Columns](#important-columns)
- [Relationships](#relationships)
- [Invariants](#invariants)
- [Access Patterns](#access-patterns)
- [Operational Notes](#operational-notes)

## 📄Schema
- Table name: `item_listings`

| Column Name   | Datatype                   | Nullable | Default             | Description                               |
|---------------|----------------------------|----------|---------------------|-------------------------------------------|
| id            | PK `UUID`                  | No       | `gen_random_uuid()` | Unique ID for the listing.                |
| title         | `TEXT`                     | No       |                     | Name of the listing.                      |
| description   | `TEXT`                     | Yes      |                     | Description of the listing.               |
| price         | `NUMERIC(10,2)`            | No       |                     | How much the item costs.                  |
| image_url     | `TEXT`                     | Yes      |                     | Image URL of the product.                 |
| created_at    | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             | When the listing was created.             |
| updated_at    | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             | When the listing was last updated.        |
| is_active     | `BOOLEAN`                  | No       | true                | If the listing is for sale.               |
| quantity_sold | `INTEGER`                  | No       | 0                   | Amount sold.                              |
| category_id   | FK `UUID`                  | No       |                     | ID of category this listing belongs to.   |

## 🎯Purpose
Stores information on products currently being sold.

## ⏱️Lifecycle
### ➕Row Creation
When a system admin submits a form containing information for a new item listing.

### 🔄Row Updates
Admins can update the item listings any time by setting a new title, description, price, or if it is active. Quantity 
sold is incremented only when an order with this listing is marked as **FULFILLED**.

### 🗑️Row Deletion
No entries are hard-deleted. They are set to "not active" and instead is removed visibly except from the seller.

## 📌Important Columns
- `id` - Primary key to access the item listing with.

## 🤝Relationships
- Belongs to: **categories** - Item listings are grouped under a category.

## 🔒Invariants
1. `price` must be a number greater than zero.
2. `quantity_sold` is always a positive number.

## 🔍Access Patterns
- Fetch ALL item listings.
- Fetch item listings by associated category slug.

## ⚙️Operational Notes
None.