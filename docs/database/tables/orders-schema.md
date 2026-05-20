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

| Column Name            | Datatype                              | Nullable | Default                 | Description                                       |
|------------------------|---------------------------------------|----------|-------------------------|---------------------------------------------------|
| id                     | PK `UUID`                             | No       | `gen_random_uuid()`     | Identifier for the order.                         |
| buyer_email            | `TEXT`                                | No       |                         | Email for non-user.                               |
| order_number           | `BIGINT GENERATED ALWAYS AS IDENTITY` | No       | `(START WITH 10482812)` | Human-friendly order ID.                          |
| status                 | `VARCHAR(50)`                         | No       | AWAITING_PAYMENT        | Status of the order.                              |
| total_paid             | `NUMERIC(10,2)`                       | Yes      |                         | How much the buyer paid in total.                 |
| created_at             | `TIMESTAMP WITH TIME ZONE`            | No       | `now()`                 | When the order was made.                          |
| shipping_first_name    | `TEXT`                                | No       |                         | First Name of the buyer.                          |
| shipping_last_name     | `TEXT`                                | No       |                         | Last Name of the buyer.                           |
| shipping_address_line1 | `TEXT`                                | No       |                         | Shipping address details: Street, number, etc.    |
| shipping_address_line2 | `TEXT`                                | Yes      |                         | Second part of the address, such as Apt 2.        |
| shipping_city          | `TEXT`                                | No       |                         | City to ship the items to.                        |
| shipping_state         | `TEXT`                                | No       |                         | U.S. State to ship the items to.                  |
| shipping_postal_code   | `TEXT`                                | No       |                         | ZIP code of the shipment.                         |
| shipping_phone         | `TEXT`                                | No       |                         | Phone number associated with the order.           |
| tracking_number        | `TEXT`                                | Yes      |                         | Tracking number of package.                       |
| shipped_at             | `TIMESTAMP WITH TIME ZONE`            | Yes      |                         | Date the package was shipped.                     |
| paypal_order_id        | `TEXT`                                | Yes      |                         | ID of order created through PayPal.               |
| paypal_capture_id      | `TEXT`                                | Yes      |                         | Capture ID of completed purchase through PayPal.  |
| paid_at                | `TIMESTAMP WITH TIME ZONE`            | Yes      |                         | When the order was paid.                          |
| guest_session_id       | `UUID`                                | No       |                         | Session ID associated with purchase.              |
| subtotal               | `NUMERIC(10,2)`                       | No       |                         | Raw total price of order items in this order.     |
| tax                    | `NUMERIC(10,2)`                       | No       |                         | Tax amount.                                       |
| shipping_cost          | `NUMERIC(10,2)`                       | No       |                         | Shipping price.                                   |
| policies_accepted_at   | `TIMESTAMP WITH TIME ZONE`            | No       |                         | Date when user agreed to policies.                |
| anonymized             | `BOOLEAN`                             | No       | false                   | If the admin has anonymized this customer's data. |
| anonymized_at          | `TIMESTAMP WITH TIME ZONE`            | Yes      |                         | Date of anonymization.                            |

## 🎯Purpose
An **order** entity represents a finalized purchase initiated by a buyer. It captures the high‑level details of a 
transaction at the moment checkout is completed, including the buyer, pricing totals, and the overall lifecycle 
state of the order. Each order acts as the parent record for the individual items purchased (represented by the order 
items table).

## ⏱️Lifecycle
### ➕Row Creation
An order is created when a client provides shipping information and proceeds to payment screen.

### 🔄Row Updates
- Guests can update their orders if they navigate back to the shipping form at checkout.
- The administrators can update order statuses.

### 🗑️Row Deletion
There is an automated cron job called **Stale Order Cleanup** that deletes orders if the following conditions are met:
1. Order creation is past the set amount of cutoff days
2. Order status is _AWAITING_PAYMENT_

Should a user navigate out of checkout after they type in their shipping information, the order becomes stale.

Otherwise, entities should **NEVER** get deleted for financial history.

## 📌Important Columns
- `status` -  Current state of the order.
- `buyer_email` - Will be used to communicate with the buyer.
- `guest_session_id` - Used for security purposes when updating details on an order.
- `paypal_order_id` - Order ID for PayPal. This ID is created before the user authorizes payment.
- `paypal_capture_id` - Capture ID for PayPal. This is set once the user completes and authorizes payment.

## 🤝Relationships
- Has many: **order items** - Individual items associated with the order.

## 🔒Invariants
1. `total_paid` - Must be a positive number greater than 0.
2. `status` - Must be one of the following values:
   1. _AWAITING_PAYMENT_ - Order has been created, but payment was not received.
   2. _PAID_ - Payment through PayPal was processed and is awaiting fulfillment.
   3. _IN_PROGRESS_ - Merchant is assembling order.
   4. _SHIPPED_ - Package with items shipped.
   5. _FULFILLED_ - Package was delivered and order is complete.
   6. _CANCELED_ - Order was canceled due to an issue.
3. `order_number` - Must be unique.

## 🔍Access Patterns
- Fetch all in progress orders
- Fetch all orders not currently in progress
- Fetch order by ID

## ⚙️Operational Notes
None.