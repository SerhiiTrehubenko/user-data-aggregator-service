# User Data Aggregator Service

## Description

The User Data Aggregator Service is a specialized microservice designed to gather and consolidate user data from multiple databases from variety of providers.
The service can be easily adjusted to interact with any number of databases via a simple configuration. 

## Features

- **Multiple Data Sources:** Connects to unlimited number of data sources.

- **API Contracts:** API documentation is supported by using Swagger.

## Built with
|||
| ---|--- |
| Spring Boot | ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) |
| Spring JDBC | ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) |
| Java 17 | ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) |
| Swagger | ![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white) |
| Maven | ![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white) |
|Docker | ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) |

## Installation

### Preconditions

- Java 17
- Maven
- Docker

### Start-up steps
- Clone the repository
```bash
git clone https://github.com/SerhiiTrehubenko/user-data-aggregator-service.git
```

- Provide data sources in the application.yml file.

```yml
db-connection:
  pool:
    size: 5

database-drivers:
  drivers:
    postgres: org.postgresql.Driver
    mysql: com.mysql.cj.jdbc.Driver

data-sources:
  - name: data-base-postgres 
    strategy: postgres  
    url: jdbc:postgresql://localhost:5555/db  
    table: customers  
    user: database-user  
    password: password  
    mapping:  
      id: id  
      username: login  
      name: name  
      surname: surname
```
- Build the project
```bash
mvn clean install
```

- Set up databases
```bash
docker-compose up -d
```
- Run the application
```bash
mvn spring-boot:run
```

## How to use

- Access the service's REST APIs for data retrieval.
- Go to the API documentation for more details.
## API Endpoints

### Get All Users

```http
GET /users
```

### Get Users applying filter

```http
GET /users/filter
```

#### Query Parameters

- `where`: WHERE clause similar to WHERE Sql functional. Restricted to AND, OR, LIKE.

## API Documentation

The URL of API documentation http://localhost:8080/swagger
