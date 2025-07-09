## Overview
This repository contains the source code for the Spring Boot backend of the website www.DevInternHub.space. 
The API documentation for the backend server is available at: http://www.devinternhub.space:8080/swagger-ui/index.html

## QuickStart
To run the application, first set up a MariaDB server.

On macOS, execute the following commands to set up and run a MariaDB server locally:
1. Install and run MariaDB: `brew install mariadb&&service mysql start`
2. Connect to MariaDB: `mariadb`
3. Create new user: `CREATE USER 'abcdef'@'localhost' IDENTIFIED BY '1qw23er45t';`
4. Create new database: `create database devinternhubdb;`
5. Grant access to new user: `GRANT ALL ON devinternhubdb.* TO 'abcdef'@'localhost';`
6. Exit: `exit`


Then, import the Gradle project in `Eclipse IDE for Enterprise Java and Web Developers (eclipse-jee-2025-03-R-macosx-cocoa-aarch64.dmg)` and run the Java application.

Once running, the application listens on port 8080 and exposes a set of rest APIs.
The API document is available at `http://localhost:8080/swagger-ui/index.html`
