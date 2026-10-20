# Barber Booking

A full-stack appointment-booking application for a barbershop. The Spring Boot
backend exposes a session-based JSON API, and the React/Vite frontend provides
customer and provider workflows.

## Prerequisites

- Java 21
- PostgreSQL
- Node.js and npm

## 1. Create the database

Create an empty PostgreSQL database named `barberbooking`:

```bash
createdb barberbooking
```

## 2. Configure local database credentials

Copy the example environment file:

```bash
cp .env.example .env
```

Open `.env` and set the values for your local PostgreSQL account:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/barberbooking
DB_USER=your_postgres_user
DB_PASSWORD=your_postgres_password
```

## 3. Start the backend

From the project root, start Spring Boot:

```bash
./mvnw spring-boot:run
```

The backend starts at `http://localhost:8080`.

## 4. Start the frontend

In a second terminal, install the frontend dependencies and start Vite:

```bash
cd frontend
npm ci
npm run dev
```

Open the URL printed by Vite (normally `http://localhost:5173`). The Vite
development server proxies `/api` requests to the Spring Boot backend at
`http://localhost:8080`.

To produce a production frontend build, run:

```bash
cd frontend
npm run build
```

## Run with Docker Compose

Docker Compose starts the Spring Boot API and PostgreSQL together. The database
configuration is supplied to the app container automatically.

Install and start Docker Desktop before running these commands.

```bash
docker compose up --build
```

The API is available at `http://localhost:8080`. Stop the containers with
`docker compose down`. To also remove the PostgreSQL volume and reset all data:

```bash
docker compose down -v
```

## Continuous integration

GitHub Actions automatically builds and tests the backend on every push and
pull request. The workflow is defined in
`.github/workflows/ci.yml`. It starts a temporary PostgreSQL database, sets the
database environment variables, and runs:

```bash
./mvnw test
```

After pushing the repository to GitHub, open the repository's **Actions** tab
to see whether a workflow run passed or failed.

## Application workflows

- Customers can log in, browse and filter available slots, book an appointment,
  view their appointments, and cancel a booked appointment.
- Providers can log in, view their available slots and appointments, create
  availability, and remove an unbooked slot.
- Authentication uses the session cookie created by `POST /api/auth/login`.
  Keep that cookie when calling protected endpoints directly.

The seeded users are:

| Role | Email |
| --- | --- |
| Provider | `alex@barberbook.com` |
| Provider | `marcus@barberbook.com` |
| Customer | `john@example.com` |
| Customer | `jane@example.com` |

All seeded accounts use the password `password`.

## API

### Public endpoints

| Method | URL | Description |
| --- | --- | --- |
| `GET` | `/api/services` | Lists the services offered by the barber shop. |
| `GET` | `/api/slots` | Lists available slots. Supports optional `providerId`, `serviceId`, `date` (`YYYY-MM-DD`), `page` (default `0`), and `size` (default `10`, maximum `100`) query parameters. |
| `POST` | `/api/auth/login` | Authenticates a user and creates a session. Send JSON with `email` and `password`. |

Example login request (store the session cookie in `cookies.txt`):

```bash
curl -i -c cookies.txt \
  -H 'Content-Type: application/json' \
  -d '{"email":"john@example.com","password":"password"}' \
  http://localhost:8080/api/auth/login
```

### Customer endpoints

These require a logged-in customer session.

| Method | URL | Description |
| --- | --- | --- |
| `POST` | `/api/appointments` | Books a slot. Send JSON with `slotId`. Returns `201 Created`. |
| `GET` | `/api/appointments/me` | Lists the customer's appointments. |
| `DELETE` | `/api/appointments/{appointmentId}` | Cancels one of the customer's booked appointments. Returns `204 No Content`. |

Booking and cancellation publish a notification after the database transaction
commits. The app sends it to its mock notification endpoint, which records the
confirmation in the backend logs. If the notification call times out or fails,
the app retries it and then logs a fallback error without undoing the successful
booking or cancellation.

### Provider endpoints

These require a logged-in provider session.

| Method | URL | Description |
| --- | --- | --- |
| `GET` | `/api/provider/slots` | Lists the provider's available slots. |
| `POST` | `/api/provider/slots` | Creates a slot. Send `serviceId`, `slotDate`, `startTime`, and `endTime`. Returns `201 Created`. |
| `DELETE` | `/api/provider/slots/{slotId}` | Removes an unbooked slot owned by the provider. Returns `204 No Content`. |
| `GET` | `/api/appointments/provider` | Lists appointments for the provider's slots. |

### Operations endpoints

| Method | URL | Description |
| --- | --- | --- |
| `GET` | `/health` | Confirms that the application can query PostgreSQL. Returns `200 OK` when the database is available, otherwise `503 Service Unavailable`. |
| `GET` | `/metrics` | Returns the number of successful bookings since the application last started. |

## Database initialization

On startup, Spring runs the SQL files in `src/main/resources/`:

- `schema.sql` creates the tables.
- `seed.sql` inserts sample users, providers, services, and available slots.

> `schema.sql` drops and recreates the tables on every startup.
> Running the backend therefore deletes the existing data in the configured
> database and replaces it with the seed data.
