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
  - Not needed if `POLICIES_PROVIDER=S3`
- `POLICIES_PROVIDER`
  - Default: LOCAL
  - Can change to S3 for AWS connection
- `POLICIES_BUCKET`
  - Not needed if `POLICIES_PROVIDER=LOCAL`
  - S3 bucket name where policies are stored
- `POLICIES_BASE_URL`
  - Not needed if `POLICIES_PROVIDER=LOCAL`
  - URL in AWS where policies are stored

## Production (prod)
Profile for public use, or when `SPRING_PROFILES_ACTIVE=prod`.

### Required
- `CORS_ALLOWED_ORIGINS` - List of URLs that can query the application. One should be the web URL of the production server.
- `DB_URL` - Postgres database URL
- `DB_USERNAME` - Production account username for database
- `DB_PASSWORD` - Production account password for database
- `IMAGE_BASE_URL` - URL where images are hosted
- `IMAGE_BUCKET` - S3 bucket name where images are hosted
- `JWT_SECRET` - 256-bit encryption secret for generating Java Web Tokens
- `MAIL_HOST` - URI of email server
- `MAIL_PORT` - SMTP port of email server
- `MAIL_USERNAME` - Mail server username
- `MAIL_PASSWORD` - Mail server password for user
- `PAYPAL_CLIENT_ID` - Client ID for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>
- `PAYPAL_CLIENT_SECRET` - Client secret for PayPal purchases.<sup>[How do I get this?](docs/paypal-setup.md)</sup>
- `POLICIES_BUCKET` - S3 bucket name where policies are stored
- `POLICIES_BASE_URL` - URL in AWS where policies are located

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

#### Cart item cleanup job
This scheduled job purges cart items associated with a guest session ID if the **maximum** timestamp date on one of
the cart items goes beyond the cutoff date. It ensures all cart item entities are not stale.
- `JOB_CART_CLEANUP_ENABLED`
  - Default `true`
  - If this job should run.
- `JOB_CART_CLEANUP_CUTOFF_DAYS`
  - Default 3
  - The amount of days cart item timestamps must be younger than to be considered stale.
- `JOB_CART_CLEANUP_CRON`
  - Default `0 0 2 * * *` (every day at 2am)
  - [Cron expression](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/support/CronExpression.html)
    for when this job should run.
