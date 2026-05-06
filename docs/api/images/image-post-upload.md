# Upload new image
Saves an image file. _Where_ the image is saved is based on application configuration.

**URL**: `/api/images/upload`

**Method**: `POST`

**Auth required**: YES

**Permissions required**: ADMIN

**Request parameter**: `file` containing the image file to save.

## Success Response
**Condition**: Image saved.

**Code**: `201 CREATED`

### Content example
Sends back the URL where the image got saved to.

```
http://localhost:8080/uploads/1d37cbb5-8b04-4a3e-b127-2167e1b56993-cat.png
```
