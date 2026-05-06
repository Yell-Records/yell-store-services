# Environment Variables

## Required
- `DB_URL` - PostgreSQL connection string
- `DB_USERNAME` - PostgreSQL account username
- `DB_PASSWORD` - PostgreSQL account password
- `JWT_SECRET` - secret used to sign auth tokens
- `SPRING_PROFILES_ACTIVE` - which configuration profile to use

## Optional
- `GENERATE_PASSWORD` - displays the hashed version of the value provided in console when starting the application
- `PUBLIC_URL` - URL where images get stored (default: localhost)

## Example .env
```
DB_URL=jdbc:postgresql://localhost:5433/YellRecordsDB
DB_USERNAME=postgres
DB_PASSWORD=admin
JWT_SECRET=XTbMw465d2KtzjpSzLR9GUah7DWg1fUypFw7ydGiC1M
SPRING_PROFILES_ACTIVE=dev
```