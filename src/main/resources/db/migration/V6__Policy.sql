ALTER TABLE orders
    ADD COLUMN policies_accepted_at TIMESTAMP WITH TIME ZONE DEFAULT now();

ALTER TABLE orders
    ALTER COLUMN policies_accepted_at DROP DEFAULT;

ALTER TABLE orders
    ALTER COLUMN policies_accepted_at SET NOT NULL;

ALTER TABLE orders
    ADD COLUMN anonymized BOOLEAN DEFAULT false NOT NULL;

ALTER TABLE orders
    ADD COLUMN anonymized_at TIMESTAMP WITH TIME ZONE;
