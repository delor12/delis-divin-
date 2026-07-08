-- Delis Divin SaaS PostgreSQL Database Backup Simulation --
-- Date: 2026-07-06T00:00:00.541394902
-- Connection URL: jdbc:h2:mem:delis_divin;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH
-- Username: sa

CREATE DATABASE delis_divin;
CREATE TABLE cities (id SERIAL PRIMARY KEY, name VARCHAR(100) UNIQUE, country VARCHAR(100));
CREATE TABLE restaurants (id SERIAL PRIMARY KEY, name VARCHAR(100), address TEXT, city_id INTEGER);
-- End of SQL Backup Simulation --
