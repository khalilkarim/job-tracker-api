# Job Tracker API

A central portal for applicants to stay up to date and stay ready for their job prospects. Get instant resume optimization and interview prep for all your applications.

---

## Features

- **Application Management** — create, track, update and delete job applications in one place
- **Application Status Tracking** — track every application through Applied, Interview, Offer and Rejected stages
- **AI Powered Analysis** — get instant resume tailoring suggestions and interview prep questions powered by Google Gemini
- **Personal Dashboard** — real time stats showing total applications, status breakdown and success rate
- **Secure User Accounts** — JWT authentication ensures every user only sees their own data
- **Dockerized** — runs anywhere with a single command

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot |
| Security | Spring Security + JWT |
| Database | MySQL + Spring Data JPA |
| AI Integration | Google Gemini API |
| Testing | JUnit + Mockito |
| Containerization | Docker + Docker Compose |
| Build Tool | Maven |

---

## Architecture

```
Client
  ↓
REST API (Spring Boot)
  ↓
JWT Auth Filter → Security Context
  ↓
Controller Layer
  ↓
Service Layer (Business Logic)
  ↓
Repository Layer (Spring Data JPA)
  ↓
MySQL Database
```

---

## API Endpoints

### Auth
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /auth/register | Create new account | No |
| POST | /auth/login | Login, get JWT token | No |

### Applications
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /applications | Create new application | Yes |
| GET | /applications | Get all my applications | Yes |
| GET | /applications/{id} | Get one application | Yes |
| PUT | /applications/{id} | Update application | Yes |
| PATCH | /applications/{id}/status | Update status | Yes |
| DELETE | /applications/{id} | Delete application | Yes |
| POST | /applications/{id}/analyze | AI analysis | Yes |

### Dashboard
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /dashboard | Get stats and success rate | Yes |

---

## Getting Started

### Prerequisites
- Docker Desktop installed
- Gemini API key (free at aistudio.google.com)

### Environment Variables
```bash
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=your_jwt_secret_min_32_chars
export GEMINI_API_KEY=your_gemini_api_key
```

### Run with Docker
```bash
# clone the repo
git clone https://github.com/YOUR_USERNAME/job-tracker-api.git
cd job-tracker-api

# set environment variables (see above)

# start everything
docker-compose up --build
```

API is live at `http://localhost:8080`

---

### Run Locally

**Prerequisites:**
- Java 17
- Maven
- MySQL 8.0

```bash
# clone the repo
git clone https://github.com/YOUR_USERNAME/job-tracker-api.git
cd job-tracker-api

# set environment variables
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret
export GEMINI_API_KEY=your_key

# create database
mysql -u root -p -e "CREATE DATABASE job_tracker;"

# run the app
mvn spring-boot:run
```

---

## Running Tests

```bash
mvn test
```

Current test coverage:
- AuthService — register and login flows
- ApplicationService — CRUD and ownership validation
- DashboardService — stats calculation and edge cases

---

## Authentication

All endpoints except `/auth/**` require a valid JWT token:

```
Authorization: Bearer your_jwt_token
```

Get your token by calling `/auth/login`.

---

## Example Requests

**Register:**
```json
POST /auth/register
{
    "name": "John Smith",
    "email": "john@gmail.com",
    "password": "secret123"
}
```

**Create Application:**
```json
POST /applications
Authorization: Bearer your_token

{
    "companyName": "Google",
    "jobTitle": "Backend Engineer",
    "jobDescription": "We are looking for...",
    "notes": "Found on LinkedIn",
    "appliedDate": "2024-01-15"
}
```

**AI Analysis:**
```
POST /applications/1/analyze
Authorization: Bearer your_token
```

Returns resume tailoring suggestions and interview prep questions specific to the job description.

---

## Project Structure

```
src/main/java/com/jobtracker/api/
├── controller/        REST endpoints
├── service/           Business logic
├── repository/        Database access
├── model/             JPA entities
├── dto/               Request/response objects
├── mapper/            Entity to DTO conversion
├── security/          JWT filter and config
└── exception/         Custom exceptions and global handler
```

---

## Environment Variables Reference

| Variable | Description |
|----------|-------------|
| DB_PASSWORD | MySQL root password |
| JWT_SECRET | Secret key for JWT signing (min 32 chars) |
| GEMINI_API_KEY | Google Gemini API key |
| SPRING_DATASOURCE_URL | Database URL (auto-set by Docker) |
| SPRING_DATASOURCE_USERNAME | Database username (auto-set by Docker) |
