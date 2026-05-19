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
  - Default `http://localhost:8080`
  - URL where images are hosted
- `JWT_SECRET`
  - Default: 5ffSrvavg96EzEcIf04juxRFgA1vudQ7WUOjbO2LgGk
  - Secret to use when generating Java Web Tokens for admins logging in
  - Encryption strength must be 256 bits
- `POLICIES_PATH`
  - Default: _storage/policies_
  - Filepath to where policy files are stored

## Production (prod)
Profile for public use, or when `SPRING_PROFILES_ACTIVE=prod`.

### Required
- `CORS_ALLOWED_ORIGINS` - List of URLs that can query the application. One should be the web URL of the production server.
- `DB_URL` - Postgres database URL
- `DB_USERNAME` - Production account username for database
- `DB_PASSWORD` - Production account password for database
- `IMAGE_BASE_URL` - URL where images are hosted
- `IMAGE_BUCKET` - _TODO: S3_
- `IMAGE_PROVIDER` - Should be set to S3, but can be set to LOCAL if needed (not recommended)
- `IMAGE_UPLOAD_DIR` - Directory for image uploads
- `JWT_SECRET` - 256-bit encryption secret for generating Java Web Tokens
- `PAYPAL_CLIENT_ID` - Client ID for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>
- `PAYPAL_CLIENT_SECRET` - Client secret for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>
- `POLICIES_PATH` - Filepath to where policies are stored

### Optional

#### Stale order cleanup job
This scheduled job purges order entities in a "stale" state. A stale order is in status _AWAITING_PAYMENT_ and 
is older than the set cutoff. These are considered "stale" as clients are not likely to be moving forward with these
orders.
- `JOB_STALE_ORDERS_ENABLED`
  - Default `true`
  - If this job should run.
- `JOB_STALE_ORDERS_CUTOFF_DAYS`
  - Default 3
  - The amount of days orders must be older than to be considered stale.
- `JOB_STALE_ORDERS_CRON`
  - Default `0 0 2 * * *` (every day at 2am)
  - [Cron expression](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html)
    for when this job should run.
