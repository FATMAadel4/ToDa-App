# 📋 ToDo App - Microservices Project

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![JWT](https://img.shields.io/badge/JWT-Authentication-blue)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-brightgreen)

---

## 📌 Project Overview

**ToDo App** is a **microservices-based application** consisting of two core services:

1. **User Service** – Handles user registration, authentication, authorization (JWT), and profile management.
2. **Todo Service** – Manages tasks for authenticated users, including CRUD operations, search, and task prioritization.

The system uses **JWT** for authentication, and all requests to **Todo Service** must be authorized via **User Service**.

---

## 📂 Repository Structure

```
toda-app/
├── user-service/
│   ├── src/
│   ├── pom.xml
│   └── README.md          # User Service detailed docs
├── todo-service/
│   ├── src/
│   ├── pom.xml
│   └── README.md          # Todo Service detailed docs
└── README.md              # This file
```

---

## 🛠️ Technologies Used Across Services

| Technology | Purpose |
|------------|---------|
| **Java 17** | Core backend language |
| **Spring Boot** | REST API development |
| **Spring Security** | Authentication & Authorization (JWT) |
| **JWT** | Token-based security |
| **JPA / Hibernate** | ORM |
| **MySQL** | Relational database |
| **Swagger** | API documentation |
| **Lombok** | Reduce boilerplate |
| **Maven** | Dependency management |
| **JUnit & Mockito** | Unit testing |

---

## 🏗️ Services Overview

### 1️⃣ User Service

Handles **user authentication and management**.

#### ✨ Key Features:
- ✅ User registration, login, OTP activation
- ✅ JWT token generation and validation
- ✅ Profile management (update/delete)
- ✅ Secured endpoints

#### 🚀 Run Locally:

```bash
cd user-service
mvn spring-boot:run
```

**📖 Full Documentation:** [User Service README](user-service/README.md)

---

### 2️⃣ Todo Service

Handles **task management** for authenticated users.

#### ✨ Key Features:
- ✅ Create, update, delete tasks
- ✅ Search tasks by name and priority
- ✅ Pagination for task listing
- ✅ Task ownership enforced via JWT

#### 🚀 Run Locally:

```bash
cd todo-service
mvn spring-boot:run
```

**📖 Full Documentation:** [Todo Service README](todo-service/README.md)

---

## 🏗️ Architecture Diagram

```
┌─────────────┐
│   Client    │
│  Frontend   │
└──────┬──────┘
       │
       ├──────────────┐
       │              │
       ▼              ▼
┌─────────────┐  ┌─────────────┐
│    User     │  │    Todo     │
│   Service   │◄─┤   Service   │
│  (Port 8081)│  │  (Port 8080)│
└──────┬──────┘  └──────┬──────┘
       │                │
       ▼                ▼
┌─────────────┐  ┌─────────────┐
│   User DB   │  │   Todo DB   │
│   (MySQL)   │  │   (MySQL)   │
└─────────────┘  └─────────────┘
```

**Architecture Flow:**
1. **User Service** validates tokens and provides user info to **Todo Service**
2. **Todo Service** stores tasks in its own database, associated with **user IDs**
3. All task operations require valid JWT from User Service

---

## 🚀 How to Run the Full Application

### Prerequisites:
- ✅ Java 17+
- ✅ Maven 3.x
- ✅ MySQL 8.x
- ✅ Postman or Swagger UI (for testing)

---

### Step 1: Setup Databases

Create two MySQL databases:

```sql
CREATE DATABASE user_service_db;
CREATE DATABASE todo_service_db;
```

---

### Step 2: Configure Database Connection

**User Service** - `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/user_service_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

**Todo Service** - `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/todo_service_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

# User Service URL for JWT validation
user.service.url=http://localhost:8081/rest/auth/checkToken
```

---

### Step 3: Start Services

**1️⃣ Start User Service first** (Port: 8081)

```bash
cd user-service
mvn spring-boot:run
```

**2️⃣ Start Todo Service** (Port: 8080)

```bash
cd todo-service
mvn spring-boot:run
```

---

### Step 4: Test APIs

**Option 1: Swagger UI**
- User Service: `http://localhost:8081/swagger-ui.html`
- Todo Service: `http://localhost:8080/swagger-ui.html`

**Option 2: Postman**
- Import the provided Postman collection
- Test the complete flow

---

## 📡 API Overview

### 🔐 User Service APIs

**Base Path:** `/rest/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register new user |
| POST | `/login` | Login and get JWT |
| POST | `/activate` | Activate account with OTP |
| POST | `/regenerateOtp` | Resend OTP |
| POST | `/forgetPassword` | Request password reset |
| POST | `/changePassword` | Change password with OTP |
| GET | `/checkToken` | Validate JWT (Internal) |

**User Management APIs** - Base Path: `/api/user`

| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT | `/update` | Update user profile |
| DELETE | `/delete` | Delete user account |

---

### 📝 Todo Service APIs

**Base Path:** `/api/todo`

**⚠️ All endpoints require JWT authentication:**
```
Authorization: Bearer <JWT_TOKEN>
```

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/add` | Create new task |
| PATCH | `/update/{id}` | Update task |
| DELETE | `/delete/{id}` | Delete task |
| GET | `/search/{id}` | Get task by ID |
| GET | `/list` | List all tasks (paginated) |
| GET | `/search?name=...&priority=...` | Search tasks |

---

## 🧪 Testing Workflow

### Complete Flow Example:

#### 1️⃣ Register User
```http
POST http://localhost:8081/rest/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "phoneNumber": "+1234567890",
  "password": "password123"
}
```

#### 2️⃣ Activate Account
```http
POST http://localhost:8081/rest/auth/activate
Content-Type: application/json

{
  "phoneNumber": "+1234567890",
  "otp": "123456"
}
```

#### 3️⃣ Login
```http
POST http://localhost:8081/rest/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 4️⃣ Create Task
```http
POST http://localhost:8080/api/todo/add
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "Complete Project",
  "description": "Finish microservices architecture",
  "priority": "HIGH",
  "status": "IN_PROGRESS"
}
```

#### 5️⃣ List Tasks
```http
GET http://localhost:8080/api/todo/list?page=0&size=10
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 🗄️ Database Schema

### User Service Database

**Users Table:**
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    role VARCHAR(50) DEFAULT 'USER'
);
```

**OTP Table:**
```sql
CREATE TABLE otp (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    otp VARCHAR(6) NOT NULL,
    expiration_time DATETIME NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

**Tokens Table:**
```sql
CREATE TABLE tokens (
    id INT PRIMARY KEY AUTO_INCREMENT,
    token TEXT NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    expired BOOLEAN DEFAULT FALSE,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

---

### Todo Service Database

**Items Table:**
```sql
CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    item_details_id BIGINT,
    FOREIGN KEY (item_details_id) REFERENCES item_details(id) ON DELETE CASCADE
);
```

**Item Details Table:**
```sql
CREATE TABLE item_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description TEXT,
    priority VARCHAR(50),
    status VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔒 Security Features

✅ **BCrypt Password Hashing**  
✅ **JWT Token Authentication**  
✅ **Token Expiration** (1 hour)  
✅ **OTP Expiration** (5 minutes)  
✅ **Token Revocation Support**  
✅ **Role-Based Access Control**  
✅ **CORS Configuration**

---

## 📊 API Response Codes

| Code | Meaning |
|------|---------|
| **200** | Success |
| **201** | Created |
| **400** | Bad Request |
| **401** | Unauthorized |
| **403** | Forbidden |
| **404** | Not Found |
| **500** | Internal Server Error |

---

## 📝 Project Roadmap

### Current Features ✅
- [x] User registration with OTP
- [x] JWT authentication
- [x] Task CRUD operations
- [x] Task search and filtering
- [x] Pagination support

### Planned Enhancements 🚧
- [ ] Email notifications
- [ ] Refresh token mechanism
- [ ] Task sharing between users
- [ ] Task categories and tags
- [ ] File attachments for tasks
- [ ] API Gateway (Spring Cloud Gateway)
- [ ] Service Discovery (Eureka)
- [ ] Docker containerization
- [ ] Kubernetes deployment
- [ ] Distributed tracing (Zipkin)

---

## 🐛 Troubleshooting

### Common Issues:

**Issue 1: Port Already in Use**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

**Issue 2: Database Connection Failed**
- Verify MySQL is running
- Check database credentials in `application.properties`
- Ensure databases are created

**Issue 3: JWT Validation Failed**
- Ensure User Service is running
- Check `user.service.url` in Todo Service config
- Verify token is not expired

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

 
---



**⭐ If you found this project helpful, please give it a star!**

---

**🎉 Happy Coding! 🚀**