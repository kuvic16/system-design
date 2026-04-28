-- MySQL Setup Script for Job Application API
-- This script creates the database and sets up the admin user

-- Create the database
CREATE DATABASE IF NOT EXISTS job_applications_db;

-- Create admin user with required password and permissions
-- First, drop the user if they exist
DROP USER IF EXISTS 'admin'@'localhost';

-- Create the admin user with password '1234'
CREATE USER 'admin'@'localhost' IDENTIFIED BY '1234';

-- Grant all privileges on the job_applications_db database to admin user
GRANT ALL PRIVILEGES ON job_applications_db.* TO 'admin'@'localhost';

-- Flush privileges to ensure they take effect
FLUSH PRIVILEGES;

-- Verify the setup
SELECT 'Database setup complete!' AS Status;
SHOW DATABASES LIKE '%job%';
SELECT USER();

