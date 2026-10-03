INSERT INTO users
(full_name, email, phone, password_hash, role)
VALUES
('Alex Johnson', 'alex@barberbook.com', '408-555-1001', '$2a$10$MPT2bCLvtwR4DGYRJ.QiReCJGEYbPm6xx6DZP6uW/q4eDS4xg2MG6', 'PROVIDER'),
('Marcus Lee', 'marcus@barberbook.com', '408-555-1002', '$2a$10$MPT2bCLvtwR4DGYRJ.QiReCJGEYbPm6xx6DZP6uW/q4eDS4xg2MG6', 'PROVIDER'),
('John Smith', 'john@example.com', '408-555-2001', '$2a$10$MPT2bCLvtwR4DGYRJ.QiReCJGEYbPm6xx6DZP6uW/q4eDS4xg2MG6', 'CUSTOMER'),
('Jane Doe', 'jane@example.com', '408-219-4943', '$2a$10$MPT2bCLvtwR4DGYRJ.QiReCJGEYbPm6xx6DZP6uW/q4eDS4xg2MG6', 'CUSTOMER');

INSERT INTO providers (user_id) VALUES (1), (2);

INSERT INTO services
(service_name, duration_minutes, price)
VALUES
('Haircut', 30, 30.00),
('Beard Trim', 30, 20.00),
('Haircut + Beard', 30, 45.00);

INSERT INTO availability_slots
(provider_id, service_id, slot_date, start_time, end_time, status)
VALUES
(1, 1, '2026-09-25', '10:00', '10:30', 'AVAILABLE'),
(1, 1, '2026-09-25', '10:30', '11:00', 'AVAILABLE'),
(1, 3, '2026-09-25', '11:00', '11:30', 'AVAILABLE'),
(2, 2, '2026-09-25', '13:00', '13:30', 'AVAILABLE');
