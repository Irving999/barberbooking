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

### Provider endpoints

These require a logged-in provider session.

| Method | URL | Description |
| --- | --- | --- |
| `GET` | `/api/provider/slots` | Lists the provider's available slots. |
| `POST` | `/api/provider/slots` | Creates a slot. Send `serviceId`, `slotDate`, `startTime`, and `endTime`. Returns `201 Created`. |
| `DELETE` | `/api/provider/slots/{slotId}` | Removes an unbooked slot owned by the provider. Returns `204 No Content`. |
| `GET` | `/api/appointments/provider` | Lists appointments for the provider's slots. |

## Database initialization

On startup, Spring runs the SQL files in `src/main/resources/`:

- `schema.sql` creates the tables.
- `seed.sql` inserts sample users, providers, services, and available slots.

> `schema.sql` drops and recreates the tables on every startup.
> Running the backend therefore deletes the existing data in the configured
> database and replaces it with the seed data.
