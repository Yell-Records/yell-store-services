CREATE TABLE artist_pages (
      id UUID DEFAULT gen_random_uuid() NOT NULL,
      slug TEXT NOT NULL,
      name TEXT NOT NULL,
      body_html TEXT NOT NULL,
      youtube_urls TEXT[] DEFAULT '{}' NOT NULL,
      category_id UUID NOT NULL,
      created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
      updated_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,

      CONSTRAINT artist_pages_pk PRIMARY KEY (id),
      CONSTRAINT artist_pages_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(id),
      CONSTRAINT artist_pages_slug_unique UNIQUE (slug)
);
