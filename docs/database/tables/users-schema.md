# Users Table

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
- Table name: `users`

| Column Name   | Datatype                   | Nullable | Default             | Description                       |
|---------------|----------------------------|----------|---------------------|-----------------------------------|
| id            | PK `UUID`                  | No       | `gen_random_uuid()` | Primary identifier for this user. |
| username      | `VARCHAR(50)`              | No       |                     | Text identifier for the user.     |
| password_hash | `TEXT`                     | No       |                     | The user's hashed password.       |
| email         | `TEXT`                     | Yes      |                     | Email associated with this user.  |
| role          | `VARCHAR(50)`              | No       | ADMIN               | Level of privilege this user has. |
| created_at    | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             |                                   |
| updated_at    | `TIMESTAMP WITH TIME ZONE` | No       | `now()`             |                                   |

## 🎯Purpose
Holds only administrator accounts.

## ⏱️Lifecycle
### ➕Row Creation
An admin account is initially created with a temporary password. Upon login, the admin is prompted to change the password.

### 🔄Row Updates
Admins can update their email or password.

### 🗑️Row Deletion
Every entity is permanent.

## 📌Important Columns
- `id` - A static reference to the user object. This is immutable and will never change.
- `role` - Provides access to certain controller functions and API calls depending on their privilege level.

## 🤝Relationships
None.

## 🔒Invariants
None.

## 🔍Access Patterns
1. Fetch a user by `id` (primary key lookup).
2. Checking a user's `role` for access permissions.
3. Checking if a user's `password_hash` matches against a raw string for login.

## ⚙️Operational Notes
None.