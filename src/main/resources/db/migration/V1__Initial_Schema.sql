CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    username VARCHAR(50) NOT NULL,
    password_hash TEXT NOT NULL,
    email TEXT,
    role VARCHAR(50) DEFAULT 'ADMIN' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,

    CONSTRAINT users_pk PRIMARY KEY (id)
);

CREATE TABLE categories (
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    name TEXT NOT NULL,
    slug TEXT NOT NULL,
    is_active BOOLEAN DEFAULT true NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,

    CONSTRAINT categories_pk PRIMARY KEY (id),
    CONSTRAINT categories_slug_unique UNIQUE (slug),
    CONSTRAINT categories_slug_regex_check CHECK (slug ~ '^[a-z0-9-]+$')
);

CREATE TABLE item_listings (
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    category_id UUID NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    image_url TEXT,
    is_active BOOLEAN DEFAULT true NOT NULL,
    quantity_sold INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,

    CONSTRAINT item_listings_pk PRIMARY KEY (id),
    CONSTRAINT item_listings_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT item_listings_quantity_sold_check CHECK (quantity_sold >= 0),
    CONSTRAINT item_listings_price_check CHECK (price > 0)
);

CREATE TABLE cart_items (
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    guest_session_id UUID NOT NULL,
    listing_id UUID NOT NULL,
    quantity INTEGER DEFAULT 1 NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,

    CONSTRAINT cart_items_pk PRIMARY KEY (id),
    CONSTRAINT cart_items_listing_id_fk FOREIGN KEY (listing_id) REFERENCES item_listings(id) ON DELETE CASCADE,
    CONSTRAINT cart_items_quantity_check CHECK (quantity >= 1)
);

CREATE TABLE orders (
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    buyer_email TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'IN_PROGRESS' NOT NULL,
    total_paid NUMERIC(10, 2) NOT NULL,
    shipping_first_name TEXT NOT NULL,
    shipping_last_name TEXT NOT NULL,
    shipping_address_line1 TEXT NOT NULL,
    shipping_address_line2 TEXT,
    shipping_city TEXT NOT NULL,
    shipping_state TEXT NOT NULL,
    shipping_postal_code TEXT NOT NULL,
    shipping_phone TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
    paid_at TIMESTAMP WITH TIME ZONE NOT NULL,
    tracking_number TEXT,
    tracking_carrier TEXT,
    shipped_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT orders_pk PRIMARY KEY (id),
    CONSTRAINT orders_buyer_email_regex_check CHECK (${emailCheck}),
    CONSTRAINT orders_status_check CHECK (status IN ('IN_PROGRESS', 'SHIPPED', 'FULFILLED', 'CANCELED')),
    CONSTRAINT orders_total_paid_check CHECK (total_paid > 0),
    CONSTRAINT orders_shipping_phone_check CHECK (shipping_phone ~ '^[0-9\-\(\) ]{7,20}$')
);

CREATE TABLE order_items (
    id UUID DEFAULT gen_random_uuid() NOT NULL,
    order_id UUID NOT NULL,
    listing_id UUID NOT NULL,
    listing_title TEXT NOT NULL,
    listing_description TEXT,
    listing_image_url TEXT,
    listing_price NUMERIC(10, 2) NOT NULL,
    quantity INTEGER NOT NULL,

    CONSTRAINT order_items_pk PRIMARY KEY (id),
    CONSTRAINT order_items_order_id_fk FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT order_items_listing_id_fk FOREIGN KEY (listing_id) REFERENCES item_listings(id) ON DELETE NO ACTION,
    CONSTRAINT order_items_quantity_check CHECK (quantity > 0)
);
