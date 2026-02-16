DROP TABLE IF EXISTS Closed_Request CASCADE;
DROP TABLE IF EXISTS Service_Request CASCADE;
DROP TABLE IF EXISTS Owns CASCADE;
DROP TABLE IF EXISTS Car CASCADE;
DROP TABLE IF EXISTS Mechanic CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;

CREATE TABLE Customer (
    id SERIAL PRIMARY KEY,
    fname VARCHAR(32) NOT NULL,
    lname VARCHAR(32) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    address VARCHAR(256) NOT NULL
);

CREATE TABLE Mechanic (
    id SERIAL PRIMARY KEY,
    fname VARCHAR(32) NOT NULL,
    lname VARCHAR(32) NOT NULL,
    experience INTEGER NOT NULL CHECK (experience >= 0 AND experience < 100)
);

CREATE TABLE Car (
    vin VARCHAR(16) PRIMARY KEY,
    make VARCHAR(32) NOT NULL,
    model VARCHAR(32) NOT NULL,
    year INTEGER NOT NULL CHECK (year >= 1970)
);

CREATE TABLE Owns (
    ownership_id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES Customer(id),
    car_vin VARCHAR(16) NOT NULL REFERENCES Car(vin)
);

CREATE TABLE Service_Request (
    rid SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES Customer(id),
    car_vin VARCHAR(16) NOT NULL REFERENCES Car(vin),
    date DATE NOT NULL,
    odometer INTEGER NOT NULL CHECK (odometer > 0),
    complain TEXT
);

CREATE TABLE Closed_Request (
    wid SERIAL PRIMARY KEY,
    rid INTEGER NOT NULL REFERENCES Service_Request(rid),
    mid INTEGER NOT NULL REFERENCES Mechanic(id),
    date DATE NOT NULL,
    comment TEXT,
    bill INTEGER NOT NULL CHECK (bill > 0)
);
