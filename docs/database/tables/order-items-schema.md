# Order Items Table

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
- Table name: `order_items`

| Column Name         | Datatype                   | Nullable | Default               | Description                                  |
|---------------------|----------------------------|----------|-----------------------|----------------------------------------------|
| id                  | PK `UUID`                  | No       | `gen_random_uuid()`   | Identifier for the order item.               |
| order_id            | FK `UUID`                  | No       |                       | ID of the order this item is a part of.      |
| listing_id          | FK `UUID`                  | No       |                       | ID of the listing this item originates from. |
| quantity            | `INT`                      | No       |                       | Amount of this item in the order.            |
| listing_price       | `NUMERIC(10,2)`            | No       |                       | The original price this item was bought at.  |
| listing_title       | `TEXT`                     | No       |                       | The listing's original title.                |
| listing_description | `TEXT`                     | Yes      |                       | The listing's original description.          |
| listing_image_url   | `TEXT`                     | Yes      |                       | The listing's original image URL.            |

## 🎯Purpose
Keeps track of individual items bought within an order.

## ⏱️Lifecycle
### ➕Row Creation
When an order is created, all items from the client's **cart** will be added as order item entities.

### 🔄Row Updates
No updates occur on an order item.

### 🗑️Row Deletion
If an order gets deleted, all associated order items are deleted as well.

## 📌Important Columns
- `order_id` - The order this item belongs to.

## 🤝Relationships
- Belongs to: `orders` - Order items always belong to one order.

## 🔒Invariants
1. `listing_price` must be greater than or equal to 0.
2. `quantity` must be greater than 0.

## 🔍Access Patterns
- Fetch order items by order ID.

## ⚙️Operational Notes
None.