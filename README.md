# User Management Application

This is a Spring Boot application for managing users through a RESTful API server. It allows registering users, handling logins, and returning the occurrence of each email domain.

## Requirements

- Java 8 or higher
- Gradle
- Spring Boot

## Dependencies

The project utilizes the following dependencies:

- Spring Web
- Spring Data JPA
- Spring Security
- H2 Database
- Lombok
- ModelMapper
- jjwt (JSON Web Token)

## Update property File

Ensure your `application.properties` file includes the following configurations:

```properties
jwt.acterio.secret=$2a$10$gSARwB18fyNoonrsCXr0KOAPPu5I2RxQ5bs.9lqQCLe
jwt.acterio.expiration=1000000
```

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/heshawa/user-management
   ```
2. Navigate to the project directory:
   ```bash 
   cd user-management
   ```
3. Build the project using Gradle:
   ```bash
   ./gradlew clean build
   ```
4. Run the application:
   ```bash
   ./gradlew bootRun
   ```


## Executing Tests

To run tests, execute the following command in the project directory:
```bash
./gradlew test
```

## Endpoints

### Register User

POST /api/user/register

- **Description**: Register new users.
- **Request Body**:

```json
{
  "username": "test1",
  "firstName": "Test",
  "lastName": "UserA",
  "emailAddress": "testUser@acterio.com",
  "password": "passis@123"
}
```

### User Login

POST /api/user/login
- **Description**: Login with username. Returns bearer token on successful login. Will return unauthorized status on login failure.
- **Request Body**:

```json
{
  "username": "test1",
  "password": "passis@123"
}
```

### User Login with Email Address

POST /api/user/login
- **Description**: Description: Login with email address. Returns bearer token on successful login. Will return unauthorized status on login failure.
- **Request Body**:
```json
{
  "email": "testUser@acterio.com",
  "password": "passis@123"
}
```

### Get user by Id

GET /api/user/{userId}


- **Description**: Get user by providing user ID. Authorization required.
- **Authorization Header**: Bearer Token

### Update User

PUT /api/user/update
- **Description**: API to update existing user details or change password. Authorization required.
- **Authorization Header**: Bearer Token
- **Request Body**:

```json
{
    "username": "admin",
    "firstName": "System",
    "lastName": "Administrator",
    "emailAddress": "admin@acterio.com",
    "password": "pass123"
}
```

### Delete User

DELETE /api/user/{userId}

- **Description**: Delete user with specified username. Authentication required.
- **Authorization Header**: Bearer Token

### Get Domain Counts

GET /api/user/domain
- **Description**: Get domain-wise users count. Authorization required.
- **Authorization Header**: Bearer Token
