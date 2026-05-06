# Create New Category
Creates a new category.

**URL**: `/api/categories`

**Method**: `POST`

**Auth required**: YES

**Permissions required**: ADMIN

### Data constraints
Provide a name and a slug.

```json
{
  "name": "[string]",
  "slug": "[string matching regex '^[a-z0-9-]+$']"
}
```

### Data example

```json
{
  "name": "Elvis Presley",
  "slug": "elvis-presley"
}
```

## Success Response
**Condition**: Slug is valid and unique.

**Code**: `201 CREATED`

### Content example

```json
{
  "id": "d801f9dc-487f-4898-899b-4d3400dab296",
  "name": "Elvis Presley",
  "slug": "elvis-presley",
  "isActive": true,
  "createdAt": "2026-05-05T22:00:00Z"
}
```

## Error Responses
**Condition**: Slug already exists.

**Code**: `403 FORBIDDEN`

**Content**: `{}`

### Or

**Condition**: Slug does not match regex.

**Code**: `400 BAD REQUEST`

**Content**: `{}`
