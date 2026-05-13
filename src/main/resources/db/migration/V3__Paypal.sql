ALTER TABLE orders
    ADD COLUMN paypal_order_id TEXT;

ALTER TABLE orders
    ADD COLUMN paypal_capture_id TEXT;

ALTER TABLE orders
    ALTER COLUMN paid_at DROP NOT NULL;

ALTER TABLE orders
    DROP CONSTRAINT orders_status_check;

ALTER TABLE orders
    ADD CONSTRAINT orders_status_check
        CHECK (status IN ('AWAITING_PAYMENT', 'PAID', 'IN_PROGRESS', 'SHIPPED', 'FULFILLED', 'CANCELED'));

ALTER TABLE orders
    ALTER COLUMN status SET DEFAULT 'AWAITING_PAYMENT';

ALTER TABLE orders
    ADD COLUMN guest_session_id UUID;

ALTER TABLE orders
    ALTER COLUMN guest_session_id SET NOT NULL;

-- Subtotal
ALTER TABLE orders
    ADD COLUMN subtotal NUMERIC(10, 2);

UPDATE orders
SET subtotal = 0.00
WHERE subtotal IS NULL;

ALTER TABLE orders
    ALTER COLUMN subtotal SET NOT NULL;

-- Tax
ALTER TABLE orders
    ADD COLUMN tax NUMERIC(10, 2);

UPDATE orders
SET tax = 0.00
WHERE tax IS NULL;

ALTER TABLE orders
    ALTER COLUMN tax SET NOT NULL;

-- Shipping cost
ALTER TABLE orders
    ADD COLUMN shipping_cost NUMERIC(10, 2);

UPDATE orders
SET shipping_cost = 0.00
WHERE shipping_cost IS NULL;

ALTER TABLE orders
    ALTER COLUMN shipping_cost SET NOT NULL;

ALTER TABLE orders
    ALTER COLUMN total_paid DROP NOT NULL;
