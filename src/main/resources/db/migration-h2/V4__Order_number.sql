ALTER TABLE orders
    ADD COLUMN order_number BIGINT GENERATED ALWAYS AS IDENTITY (START WITH 10482812);

ALTER TABLE orders
    ADD CONSTRAINT orders_order_number_unique UNIQUE (order_number);
