ALTER TABLE orders
    ADD COLUMN paypal_order_id TEXT DEFAULT 'TEMP' NOT NULL;

ALTER TABLE orders
    ALTER COLUMN paypal_order_id DROP DEFAULT;

ALTER TABLE orders
    ADD COLUMN paypal_capture_id TEXT;

ALTER TABLE orders
    DROP CONSTRAINT orders_status_check;

ALTER TABLE orders
    ADD CONSTRAINT orders_status_check
        CHECK (status IN ('AWAITING_PAYMENT', 'IN_PROGRESS', 'SHIPPED', 'FULFILLED', 'CANCELED'));

ALTER TABLE orders
    ALTER COLUMN status SET DEFAULT 'AWAITING_PAYMENT';
