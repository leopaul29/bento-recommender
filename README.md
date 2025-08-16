# 🍱 Bento Recommender API

## 📖 Project Description / プロジェクト概要

**EN:**
The Bento Recommender API is a Spring Boot application designed to manage Bento (Japanese lunch box) data, user preferences, and provide personalized meal recommendations. Users can register Bentos with their ingredients and tags, set their food preferences (likes/dislikes), and receive recommendations based on these preferences.

**JP:**
Bento Recommender API は、弁当データ・ユーザー嗜好情報を管理し、個別に最適化されたおすすめ弁当を提供する Spring Boot アプリケーションです。ユーザーは食材やタグ付きの弁当を登録し、好み・嫌いな食材を設定することで、嗜好に基づいたおすすめを受け取ることができます。

---

## 🎯 Objective

- Build a fully functional **REST API** for managing bentos and related entities
- Implement **recommendation logic** based on user preferences
- Provide **dummy dataset** for testing search and recommendation features
- Ensure API follows **clean architecture** and **best practices** in Spring Boot
- Use **PostgreSQL** with **Docker and Docker Compose** to manage your dev environment

---

## 🚀 Features

* **CRUD for Bento**: Create, read, update, and delete Bento records
* **Ingredient & Tag Management**: Store and manage reusable ingredients and tags
* **User Preferences**: Define liked tags and disliked ingredients for each user
* **Recommendation Engine**: Recommend Bentos based on user preferences
* **Dummy Data Initialization**: Pre-load the database with sample data for testing
* **API Testing**: Postman collection and MockMvc-based unit tests

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Maven**
- **Docker / Docker Compose**
- **MapStruct** for DTO ↔ Entity mapping
- **PostgreSQL** (via Docker)
- **Lombok** for boilerplate reduction
- **JUnit 5 / MockMvc / SpringBootTest** for testing
- **Postman** for API testing

---

## 📚 Learning Outcomes

By working on this project, you will:

1. Understand **Spring Boot project structure** and dependency injection
2. Learn **CRUD implementation** with Spring Data JPA
3. Use **DTOs and mappers** (MapStruct) to separate persistence and presentation layers
4. Integrate **PostgreSQL** with Docker
5. Implement **recommendation logic** using filtering and predicates
6. Write **API tests** with MockMvc and SpringBootTest
7. Manage **database initialization** with SQL scripts and dummy data
8. Use **Postman** or similar tools for API testing and debugging
9. Handle **environment variables** with ```.env``` and properties

---

## ✅ Good Practices in This Project

- **Entity vs DTO separation** → Prevents overexposing internal models
- **Service layer encapsulation** → All business logic isolated from controllers
- **Mapper layer** → Clean transformation between entities and DTOs
- **Validation annotations** (`@NotNull`, `@Size`, `@Min`) → Ensure API contract consistency
- **ControllerAdvice for global error handling** → Centralized exception management
- **Environment variables** for DB credentials → Security & flexibility
- **Idempotent SQL initialization scripts** → Reusable and safe test datasets
- **Japanese & English specs** → Supports bilingual development and documentation
- **RESTful API design** → Predictable, clean, and standard endpoint naming
- ```.env``` is used to store secrets **(never commit it)**
- ```application.properties``` uses **variable substitution** for flexibility
- **Docker** ensures consistent DB state across environments

---

## 📌 Next Steps

* **Enhance Recommendation Algorithm**
  * Add scoring system for tags and ingredients
  * Implement ranking and result limits
* **Security**
  * Add Spring Security with JWT authentication
* **Frontend**
  * Create a simple UI to visualize recommendations

---


## 📡 API Endpoints

### **Bento API**

| Method | Endpoint           | Description           |
|--------|--------------------|-----------------------|
| GET    | `/api/bentos`      | Get all bentos        |
| GET    | `/api/bentos/{id}` | Get bento by ID       |
| POST   | `/api/bentos`      | Create a new bento    |
| PUT    | `/api/bentos/{id}` | Update existing bento |
| DELETE | `/api/bentos/{id}` | Delete a bento        |

### **User Preferences API**

| Method | Endpoint                      | Description          |
|--------|-------------------------------|----------------------|
| POST   | `/api/users/{id}/preferences` | Set user preferences |

### **Recommendation API**

| Method | Endpoint                        | Description                    |
|--------|---------------------------------|--------------------------------|
| GET    | `/api/recommendations?userId=1` | Get recommendations for a user |

* **More details with the page [API Documentation](https://github.com/leopaul29/bento-recommender/wiki/4-%E2%80%90-Endpoints-(jp))**
* **See also with OpenAPI swagger at the following url http://localhost:8080/v3/api-docs**

---

## 📂 Project Structure

```
src/
 ├─ main/
 │   ├─ java/com/leopaul29/bento
 │   │   ├─ config            # Config classes (DB init, OpenAPI=Swagger)
 │   │   ├─ controllers       # REST API controllers
 │   │   ├─ dtos              # Data Transfer Objects
 │   │   ├─ entities          # JPA Entities
 │   │   ├─ exceptions        # Global Exception Handler
 │   │   ├─ Init              # Data initializer
 │   │   ├─ mappers           # MapStruct mappers
 │   │   ├─ repositories      # Spring Data repositories
 │   │   ├─ services          # Service interfaces
 │   │   └─ service/impl      # Service implementations
 │   └─ resources
 │       ├─ application.yml   # Application configuration
 │       ├─ data.sql          # Initial test data (optional)
 │       └─ ...
 └─ test/
     ├─ java/com/leopaul29/bento
     │   ├─ controllers       # API tests with MockMvc
     │   └─ services          # Unit tests
 ├── Dockerfile
 ├── docker-compose.yml
 └── README.md
```

---

## 📦 Installation & Setup
### Prerequisites

* Java 17+
* Maven 3.8+
* Docker & Docker Compose installed

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/bento-recommender.git
cd bento-recommender
```

### 2. Configure Environment Variables

Create a `.env` file at the root:

```env
POSTGRES_USER=youruser
POSTGRES_PASSWORD=yourpassword
POSTGRES_DB=bento_db
```

### 3. Start PostgreSQL with Docker

```bash
docker compose --env-file .env up -d
```

### 4. Configure Spring Boot
Create ```src/main/resources/application.properties:```
```
spring.datasource.url=jdbc:postgresql://localhost:5432/bento_db
spring.datasource.username=${POSTGRES_USER}
spring.datasource.password=${POSTGRES_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 5. Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

---

## 🧪 Running Tests

### With Maven

```bash
mvn test
```

### Example with MockMvc

```java
mockMvc.perform(get("/api/bentos/1"))
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.name").value("Test Bento"));
```

* **More details with the page [Bento test](docs/bento-test.md)
* **With postman: check the collection test results with the page [postman tests](./postman-tests/Bento-recommender.postman_test_run.json)**

---

## Documentation

[Documentation](https://github.com/leopaul29/bento-recommender/wiki)



---

## 📄 License
MIT – free to use and modify for learning or production.