ALTER TABLE orders
    ADD COLUMN order_number BIGINT;

CREATE SEQUENCE orders_order_number_seq START WITH 10482812;

UPDATE orders
SET order_number = nextval('orders_order_number_seq')
WHERE order_number IS NULL;

ALTER TABLE orders
    ALTER COLUMN order_number SET DEFAULT nextval('orders_order_number_seq');

ALTER TABLE orders
    ALTER COLUMN order_number SET NOT NULL;

ALTER TABLE orders
    ADD CONSTRAINT orders_order_number_unique UNIQUE (order_number);
