# MySQL Database Setup Guide

## Issue Encountered
The current MySQL root user is password-protected. To complete the database setup, please follow one of the methods below.

## Method 1: Using MySQL Command Line (Interactive)

1. **Open Command Prompt or PowerShell**

2. **Run MySQL with root user (will prompt for password):**
   ```
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
   ```
   - When prompted for password, enter your MySQL root password
   - If you forgot the root password, see "Method 3" below

3. **Once connected, run these SQL commands:**
   ```sql
   -- Create the database
   CREATE DATABASE IF NOT EXISTS job_applications_db;
   
   -- Create or update admin user
   DROP USER IF EXISTS 'admin'@'localhost';
   CREATE USER 'admin'@'localhost' IDENTIFIED BY '1234';
   
   -- Grant permissions
   GRANT ALL PRIVILEGES ON job_applications_db.* TO 'admin'@'localhost';
   FLUSH PRIVILEGES;
   
   -- Verify setup
   SHOW DATABASES;
   SELECT USER();
   ```

4. **Exit MySQL:**
   ```
   EXIT;
   ```

## Method 2: Using SQL Script File

1. **Execute the SQL script we created:**
   ```
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < D:\My Learning\System Design\system-design\high-traffic-app\setup-mysql.sql
   ```
   - Enter your MySQL root password when prompted

## Method 3: Reset MySQL Root Password

If you don't remember the root password:

### On Windows:

1. **Stop MySQL Service:**
   ```
   net stop MySQL80
   ```

2. **Start MySQL without grant tables:**
   ```
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --skip-grant-tables
   ```

3. **In another terminal, connect without password:**
   ```
   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root
   ```

4. **Reset the password:**
   ```sql
   FLUSH PRIVILEGES;
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_new_password';
   EXIT;
   ```

5. **Restart MySQL Service:**
   ```
   net start MySQL80
   ```

## Method 4: Using MySQL Workbench (GUI)

1. **Open MySQL Workbench**
2. **Click on your local MySQL connection**
3. **Enter your root password**
4. **Open a new query tab and execute:**
   ```sql
   CREATE DATABASE IF NOT EXISTS job_applications_db;
   DROP USER IF EXISTS 'admin'@'localhost';
   CREATE USER 'admin'@'localhost' IDENTIFIED BY '1234';
   GRANT ALL PRIVILEGES ON job_applications_db.* TO 'admin'@'localhost';
   FLUSH PRIVILEGES;
   ```

## Verification Steps

After you complete one of the above methods, verify the setup:

```bash
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -h localhost -u admin -p1234 -e "USE job_applications_db; SHOW TABLES;"
```

If successful, you should see an empty result (no tables yet, which is expected).

## What Happens Next

Once the database is set up:
1. Build the Spring Boot application: `mvn clean install`
2. Run the application: `mvn spring-boot:run`
3. Spring Boot will automatically create the `job_applications` table using Hibernate
4. The API will be ready at `http://localhost:8080`

## Default Credentials for Application

- **Database Name:** job_applications_db
- **Username:** admin
- **Password:** 1234
- **Host:** localhost:3306

These are already configured in `application.properties`

## Troubleshooting

- **"Access denied for user"**: Wrong password or user doesn't exist - use Method 3 to reset
- **"Can't connect to MySQL server"**: MySQL service not running - run `net start MySQL80`
- **"Unknown database"**: Execute the CREATE DATABASE command from Method 1 or 2

