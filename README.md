# 🍱 Bento Recommender

A minimalist Java backend application to explore Spring Boot, PostgreSQL, and Docker.  
The API lets you manage and recommend bento meals (Japanese lunch boxes) with a clean and scalable structure.

---

## 🧠 Objectives

- Practice building a clean Spring Boot API
- Learn how to connect to PostgreSQL using Spring Data JPA
- Use Docker and Docker Compose to manage your dev environment
- Structure a backend project using good architecture practices

---

## 🛠 Tech Stack

| Layer       | Technology             |
|-------------|------------------------|
| Language    | Java 17                |
| Framework   | Spring Boot 3.x        |
| Database    | PostgreSQL (Docker)    |
| ORM         | Spring Data JPA        |
| Build Tool  | Maven                  |
| Container   | Docker / Docker Compose |

---

## 🚀 Features

- [x] Add a bento (name, ingredients, calories, tags)
- [x] List all bentos
- [x] Filter bentos by type or ingredient
- [ ] Recommend random bento
- [ ] Unit tests on service layer

---

## 🗂️ Project Structure

```
bento-recommender/
├── src/main/java
│ └── com.example.bento
│ ├── controller
│ ├── service
│ ├── repository
│ └── model
├── src/main/resources
│ ├── application.properties
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## ⚙️ How to Run (Local + Docker)

### 1. Clone the repo
```bash
git clone https://github.com/yourusername/bento-recommender.git
cd bento-recommender
```
### 2. Create .env file at the root
```
POSTGRES_USER=root
POSTGRES_PASSWORD=secret
POSTGRES_DB=bento_db
```
### 3. Start PostgreSQL with Docker Compose
```
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
### 5. Run the app
```
./mvnw spring-boot:run
```

## 🔍 Sample Endpoints
* ```POST /api/bentos``` – Add a new bento
* ```GET /api/bentos``` – List all bentos
* ```GET /api/bentos?ingredient=tofu``` – Filter by ingredient

## 📚 Learning Outcomes
* Use Spring Boot annotations (```@RestController```, ```@Service```, ```@Repository```)
* Understand entity modeling with JPA
* Handle environment variables with ```.env``` and properties
* Manage PostgreSQL with Docker Compose

## 🧼 Good Practices
* ```.env``` is used to store secrets (never commit it)
* ```application.properties``` uses variable substitution for flexibility
* Docker ensures consistent DB state across environments

## 📄 License
MIT – free to use and modify for learning or production.