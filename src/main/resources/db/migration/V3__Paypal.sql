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
