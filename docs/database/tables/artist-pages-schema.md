# Artist Pages Table

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
- Table name: `artist_pages`

| Column Name  | Datatype                   | Nullable | Default             | Description                                                  |
|--------------|----------------------------|----------|---------------------|--------------------------------------------------------------|
| id           | PK `UUID`                  | No       | `gen_random_uuid()` | Primary identifier.                                          |
| slug         | `TEXT`                     | No       |                     | URL slug for the page.                                       |
| name         | `TEXT`                     | No       |                     | Name of the artist.                                          |
| body_html    | `TEXT`                     | No       |                     | Page HTML.                                                   |
| youtube_urls | `TEXT[]`                   | No       | {}                  | List of YouTube links that will be displayed on the page.    |
| category_id  | FK `UUID`                  | No       |                     | Category ID. Displays items on the page under this category. |
| created_at   | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             |                                                              |
| updated_at   | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             |                                                              |

## 🎯Purpose
This is a dynamic HTML page table that administrators can change at any time.

## ⏱️Lifecycle
### ➕Row Creation
Admins create a new page.

### 🔄Row Updates
Admins can edit any fields on the page.

### 🗑️Row Deletion
Admins may delete a page.

## 📌Important Columns
- **slug** - Will be used in page navigation.
- **body_html** - Page content.
- **name** - Will be used when listing artist pages.

## 🤝Relationships
None.

## 🔒Invariants
1. `slug` must abide by this regex: `^[a-z0-9-]+$`
2. `slug` must be unique

## 🔍Access Patterns
- Get all artist pages
- Get artist page by slug

## ⚙️Operational Notes
None.
