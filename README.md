# SpringBoot Movie API 🎬

Welcome to the **SpringBoot Movie API**! This is a **CRUD API** for managing movies, complete with **JWT-based authentication**, **role-based authorization**, and **email verification**. This project was built as part of my learning journey, following a tutorial while adding my own enhancements and understanding.

---

## Features:

### **1. Movie Management**
- **CRUD Operations**: Create, Read, Update, and Delete movies.
- **Movie DTO**: Includes an image URL for movie posters (not stored in the database).
- **Paging and Sorting**: Display movies with pagination and sorting for large datasets.

### **2. Authentication & Authorization**
- **JWT Authentication**: Secure access to endpoints using JSON Web Tokens (JWT).
- **Role-Based Access**:
  - **Admin**: Can add, update, and delete movies.
  - **Regular User**: Can only view movies.
- **Email Verification**: Users must verify their email using an OTP sent to their inbox.
- **Token Expiry**:
  - **Access Token**: Expires after 25 minutes.
  - **Refresh Token**: Expires after 30 minutes (used to generate a new access token).

### **3. Password Management**
- **Forgot Password**: Users can reset their password via email.

### **4. Scalability**
- **Paging and Sorting**: Efficiently handle large datasets of movies.

---

## Technologies Used:

- **Spring Boot**: Backend framework for building the API.
- **Spring Data JPA**: For database interactions.
- **Spring Security**: For authentication and authorization.
- **JWT**: For secure token-based authentication.
- **MySQL**: Structured database for development.
- **Java Mail Sender**: For sending OTPs and password reset emails.
- **DTOs (Data Transfer Objects)**: For structured data transfer between layers.

---

## Getting Started 🚀

### **Prerequisites**
- Java 17 or higher.
- Maven (for dependency management).
- An IDE (preferably IntelliJ IDEA).
- MySQL database (or any other database of your choice).

### **Setup**
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Favourite101/SpringBoot-Movie-API.git
   cd SpringBoot-Movie-API
   ```

2. **Build the Project**:
   ```bash
   mvn clean install -U -DskipTests
   ```

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```
   - Or simply click on the **Run** icon in your IDE or press **Ctrl + F5**.

4. **Access the API**:
   - The API will be available at `http://localhost:8080`.

---

## API Endpoints

### **Authentication**
| **Endpoint**               | **Method** | **Description**                          |
|----------------------------|------------|------------------------------------------|
| `/api/v1/auth/register`    | `POST`     | Register a new user.                     |
| `/api/v1/auth/login`       | `POST`     | Authenticate and get JWT tokens.         |
| `/api/v1/auth/refresh`     | `POST`     | Generate a new access token.             |
| `/forgotPassword/verifyEmail/{email}`| `POST`     | Verify email to reset password.                  |
| `/forgotPassword/verifyOtp/{otp}/{email}`      | `POST`     | Verify OTP to reset password.                |
| `/forgotPassword/changePassword/{email}/{otpToken}` | `POST` | Change password. |

### **Movies**
| **Endpoint**               | **Method** | **Description**                          | **Access**       |
|----------------------------|------------|------------------------------------------|------------------|
| `/api/v1/movie/all`        | `GET`      | Get all movies.                          | **All Users**    |
| `/api/v1/movie/{id}`      | `GET`      | Get a movie by ID.                       | **All Users**    |
| `/api/v1/movie/allMoviesPage?pageNumber={pageNumber}&pageSize={pageSize}` | Get all movies(with paging) | **All Users** |
| `/api/v1/movie/allMoviesPageSort?sortBy={sortBy}&dir={asc/desc}` | Get all movies(with paging and sorting) | **All Users** |
| `/file/upload`        | `POST`      | Upload poster/image.                          | **Admin Only**    |
| `/file/{fileName}`        | `GET`      | Display poster/image.                          | **Admin Only**    |
| `/api/v1/movie/add-movie`           | `POST`     | Add a new movie.                         | **Admin Only**   |
| `/api/v1/movie/update/{id}`      | `PUT`      | Update a movie by ID.                    | **Admin Only**   |
| `/api/v1/movie/delete/{id}`      | `DELETE`   | Delete a movie by ID.                    | **Admin Only**   |

---

## Example Requests

### **Register a User**
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Some User",
  "email": "user@example.com",
  "username": "someUsername",
  "password": "password123"
}
```

### **Login**
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "someUsername",
  "password": "password123"
}
```

### **Add a Movie (Admin Only)**
```bash
POST /api/v1/movies
Content-Type: application/json
Authorization: Bearer <access-token>

- **movieDTO**: A JSON string representing the movie details.
  ```json
  {
    "releaseYear": 2023,
    "title": "Echo Chamber",
    "genre": "Psychological Drama",
    "director": "Ari Aster",
    "studio": "A24",
    "movieCast": ["Joaquin Phoenix", "Tilda Swinton", "Lakeith Stanfield"]
  }
- image: The movie poster file (e.g., echo_chamber.jpg).
```

### **Get All Movies**
```bash
GET /api/v1/movie/all
Authorization: Bearer <access-token>
```

---

## Security

### **JWT Tokens**
- **Access Token**: Expires after 25 minutes.
- **Refresh Token**: Expires after 30 minutes (used to generate a new access token).

### **Role-Based Access**
- **Admin**: Can add, update, and delete movies.
- **Regular User**: Can only view movies.

### **Email Verification**
- Users must verify their email using an OTP sent to their inbox in order to reset their passwords.

---

## Troubleshooting 🛠️
If you encounter issues while setting up the project, try the following steps:

### 1. Invalidating Caches
In IntelliJ IDEA:
  1. Go to File → Invalidate Caches / Restart.
  2. Click Invalidate and Restart.

### 2. Enabling Annotation Processing
In IntelliJ IDEA:
  1. Go to File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors.
  2. Check Enable annotation processing.
  3. Ensure Obtain processors from project classpath is selected.

### 3. Checking Dependencies
  Ensure your pom.xml file includes all required dependencies. Refer to the pom.xml file in the repository for the complete list.

### 4. Configuring MySQL
  Ensure your application.properties or application.yml file is correctly configured with your MySQL database credentials. Refer to the application.yml file in the repository for the correct configuration.

### 5. Reimporting Maven Dependencies
In IntelliJ IDEA:
  1. Right-click on the project root.
  2. Select Maven → Reimport.

---

## Future Enhancements

- **Integration with External APIs**: Fetch movie data from external sources (e.g., IMDb).
- **Advanced Search**: Add filters for genre, release year, etc.
- **Dockerization**: Containerize the application for easy deployment.

---

## Contributing 🤝

Contributions are welcome! If you'd like to contribute, please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request.

---

Enjoy building and exploring the **SpringBoot Movie API**! If you have any questions or feedback, feel free to reach out!

---
