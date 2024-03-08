This repository stores the source of the Spring Boot backend of website `www.DevInternHub.com`.
The API document for the website's backend server is available at http://www.devinternhub.com:8080/swagger-ui/index.html.

The application is configured to connect to a MariaDB.
To run the application, set up a MariaDB and change the following attributes of 'DevInternHubBackend/src/main/resources/application.properties':

spring.datasource.url=jdbc:mariadb://{database url}:3306/devinternhubdb
spring.datasource.username={ username to access the database }
spring.datasource.password={ the password }

Once running, the application listens on port 8080 and exposes a set of rest APIs.
The API document is available at http://{application url, such as localhost}:8080/swagger-ui/index.html
