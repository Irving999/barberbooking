DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS availability_slots;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS providers;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE providers (
    provider_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
);

CREATE TABLE services (
    service_id SERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    duration_minutes INT NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

CREATE TABLE availability_slots (
    slot_id SERIAL PRIMARY KEY,
    provider_id INT NOT NULL,
    service_id INT NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL,

    FOREIGN KEY (provider_id)
        REFERENCES providers(provider_id),

    FOREIGN KEY (service_id)
        REFERENCES services(service_id)
);

CREATE TABLE appointments (
    appointment_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    slot_id INT NOT NULL UNIQUE,
    booked_at TIMESTAMP NOT NULL,
    appointment_status VARCHAR(20) NOT NULL,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    FOREIGN KEY (slot_id)
        REFERENCES availability_slots(slot_id)
);