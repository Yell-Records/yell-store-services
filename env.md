# Environment Variables
If no profile is specified, the application defaults to **dev**.

## Development (dev)
Default profile.

### Required
- `DB_URL` - Local Postgres URL
- `DB_USERNAME` - Username for database user
- `DB_PASSWORD` - Password for database user
- `PAYPAL_CLIENT_ID` - Client ID for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>
- `PAYPAL_CLIENT_SECRET` - Client secret for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>

### Optional
- `IMAGE_PROVIDER`
  - Default: LOCAL
  - Accepted values: LOCAL, S3
  - Determines how images are provided and exposed
- `IMAGE_UPLOAD_DIR`
  - Default: uploads
  - Path where images are stored
- `IMAGE_BUCKET`
  - Blank default
  - Not needed if `IMAGE_PROVIDER=LOCAL`
  - Currently not used, but will be used for S3 purposes
- `IMAGE_BASE_URL`
  - Blank default
  - Not needed if `IMAGE_PROVIDER=LOCAL`
  - Currently not used, but will be used for S3 purposes
- `JWT_SECRET`
  - Default: 5ffSrvavg96EzEcIf04juxRFgA1vudQ7WUOjbO2LgGk
  - Secret to use when generating Java Web Tokens for admins logging in
  - Encryption strength must be 256 bits

## Production (prod)
Profile for public use, or when `SPRING_PROFILES_ACTIVE=prod`.

### Required
- `DB_URL` - Postgres database URL
- `DB_USERNAME` - Production account username for database
- `DB_PASSWORD` - Production account password for database
- `IMAGE_BASE_URL` - _TODO_
- `IMAGE_BUCKET` - _TODO_
- `IMAGE_PROVIDER` - Should be set to S3, but can be set to LOCAL if needed
- `IMAGE_UPLOAD_DIR` - Directory for image uploads
- `JWT_SECRET` - 256-bit encryption secret for generating Java Web Tokens
- `PAYPAL_CLIENT_ID` - Client ID for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>
- `PAYPAL_CLIENT_SECRET` - Client secret for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>
