# Orders Table

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
- Table name: `orders`

| Column Name            | Datatype                   | Nullable | Default             | Description                                    |
|------------------------|----------------------------|----------|---------------------|------------------------------------------------|
| id                     | PK `UUID`                  | No       | `gen_random_uuid()` | Identifier for the order.                      |
| buyer_email            | `TEXT`                     | No       |                     | Email for non-user.                            |
| status                 | `VARCHAR(50)`              | No       | IN_PROGRESS         | Status of the order.                           |
| total_paid             | `NUMERIC(10,2)`            | No       |                     | How much the buyer paid in total.              |
| created_at             | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             | When the order was made.                       |
| shipping_first_name    | `TEXT`                     | No       |                     | First Name of the buyer.                       |
| shipping_last_name     | `TEXT`                     | No       |                     | Last Name of the buyer.                        |
| shipping_address_line1 | `TEXT`                     | No       |                     | Shipping address details: Street, number, etc. |
| shipping_address_line2 | `TEXT`                     | Yes      |                     | Second part of the address, such as Apt 2.     |
| shipping_city          | `TEXT`                     | No       |                     | City to ship the items to.                     |
| shipping_state         | `TEXT`                     | No       |                     | U.S. State to ship the items to.               |
| shipping_postal_code   | `TEXT`                     | No       |                     | ZIP code of the shipment.                      |
| shipping_phone         | `TEXT`                     | No       |                     | Phone number associated with the order.        |
| tracking_number        | `TEXT`                     | Yes      |                     | Tracking number of package.                    |
| tracking_carrier       | `TEXT`                     | Yes      |                     | Carrier of delivery service for package.       |
| shipped_at             | `TIMESTAMP WITH TIME ZONE` | Yes      |                     | Date the package was shipped.                  |

## 🎯Purpose
An **order** entity represents a finalized purchase initiated by a buyer. It captures the high‑level details of a 
transaction at the moment checkout is completed, including the buyer, pricing totals, and the overall lifecycle 
state of the order. Each order acts as the parent record for the individual items purchased (represented by the order 
items table).

## ⏱️Lifecycle
### ➕Row Creation
An order is created when a client provides shipping information and confirms payment at cart checkout.

### 🔄Row Updates
The order `status` will be updated from _IN_PROGRESS_ to _FULFILLED_ when all associated **order items** have all reached
SHIPPED status.

### 🗑️Row Deletion
Explain whether rows are permanent, soft-deleted, or cleaned up.

## 📌Important Columns
- `status` -  Current state of the order.
- `buyer_email` - Will be used to communicate with the buyer.

## 🤝Relationships
- Belongs to: **users** - Only users can create an order.
- Has many: **order items** - Individual items associated with the order.

## 🔒Invariants
1. `total_paid` - Must be a positive number greater than 0.
2. `status` - Must be one of the following values:
   1. _IN_PROGRESS_ - There are order items that haven't shipped.
   2. _SHIPPED_ - Package with items shipped.
   3. _FULFILLED_ - Package was delivered and order is complete.
   4. _CANCELED_ - Order was canceled due to an issue.

## 🔍Access Patterns
- Fetch all in progress orders
- Fetch all orders not currently in progress

## ⚙️Operational Notes
None.