-- USERS : Wachtwoord = "Geheim123!" (bcrypt)
INSERT INTO users (email, password, role)
VALUES
    ('admin@flightmaster.nl', '$2a$12$ZvstzDXUyz0yEMNoMO/.TejYRKp19QVAxISy.wgGoJ4n/5EquC0.q', 'ADMIN'),
    ('user1@flightmaster.nl', '$2a$12$ZvstzDXUyz0yEMNoMO/.TejYRKp19QVAxISy.wgGoJ4n/5EquC0.q', 'USER'),
    ('pilot@flightmaster.nl', '$2a$12$ZvstzDXUyz0yEMNoMO/.TejYRKp19QVAxISy.wgGoJ4n/5EquC0.q', 'PILOT')
ON CONFLICT DO NOTHING;

INSERT INTO helicopters (id, call_sign, type, capacity, fuel_capacity, fuel_usage, available)
VALUES
    (1,'PH-HLC', 'Robinson R44', 3, 190, 60, true),
    (2,'PH-HLX', 'Airbus H125', 4, 250, 75, true);

INSERT INTO events (id, location, event_date, start_time, end_time, flight_time)
VALUES
    (1,'Lelystad airport airfair', DATE '2025-05-10', TIME '12:00', '17:00', 7),
    (2,'Heliport flight experience Rotterdam', DATE '2025-07-01', TIME '14:00', TIME '18:00',10);

INSERT INTO event_helicopters (event_id, helicopter_id)
VALUES
    (1, 1),
    (1, 2),
    (2, 2);

INSERT INTO flights (flight_number, start_time, event_id, helicopter_id, fuel_before, fuel_after)
VALUES
    ('E1-F1', TIME '12:00', 1, 1, 190, 183),
    ('E1-F2', TIME '12:30', 1, 2, 250, 241.25),
    ('E2-F1', TIME '14:00', 2, 2, 250, 237.5);

INSERT INTO passengers (first_name, last_name,  email, weight, flight_id, user_id)
VALUES
    ('Jan', 'Jansen', 'janjansen@ziggo.nl', 85, 1, 2),
    ('Sanne', 'de Vries', 'sannedevries@gmail.nl',62, 1, 2),
    ('Mark', 'Rensen', 'm.rensen@hotmail.com',79, 3, 3);