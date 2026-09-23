# Barber Booking API

A small Spring Boot API web application for an online barbershop appointment booking system.

## Prerequisites

- Java 21
- PostgreSQL

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

## 3. Run the application

From the project root, start Spring Boot:

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`

## Try the API

In a second terminal, run:

```bash
curl http://localhost:8080/api/services
curl http://localhost:8080/api/slots
```

Available endpoints:

| Method | URL | Description |
| --- | --- | --- |
| `GET` | `/api/services` | Lists the services offered by the barber shop. |
| `GET` | `/api/slots` | Lists currently available appointment slots. |

## Database initialization

On startup, Spring runs the SQL files in `src/main/resources/`:

- `schema.sql` creates the tables.
- `seed.sql` inserts sample users, providers, services, and available slots.

`schema.sql` currently drops and recreates tables on startup, deleting existing local data.