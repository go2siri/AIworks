# Insurance Quote Application - Full Stack

A comprehensive full-stack application for managing insurance quotes with Angular frontend and Spring Boot backend.

## 🏗️ Architecture Overview

### Frontend (Angular 20+)
- **Framework**: Angular with Signals and Standalone Components
- **Styling**: Modern CSS with responsive design
- **Testing**: Playwright for E2E testing
- **Features**: Reactive forms, real-time validation, responsive UI

### Backend (Spring Boot 3.2+)
- **Framework**: Spring Boot with Java 17
- **Database**: H2 in-memory database
- **Testing**: JUnit 5 with comprehensive test coverage
- **Logging**: Structured logging with Logback
- **Documentation**: OpenAPI/Swagger integration

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Maven 3.6+ (or use included wrapper)

### Start the Full Application
```bash
# Make scripts executable (if needed)
chmod +x start-full-application.sh stop-application.sh

# Start both frontend and backend
./start-full-application.sh

# Stop the application
./stop-application.sh
```

### Manual Start (Alternative)

#### Backend
```bash
cd insurance-quote-backend
mvn spring-boot:run
# or
./mvnw spring-boot:run
```

#### Frontend
```bash
cd aiprojects
npm install
npm start -- --port 4201
```

## 📋 Application URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:4201 | Main application UI |
| Backend API | http://localhost:8080/api | REST API endpoints |
| H2 Console | http://localhost:8080/api/h2-console | Database console |
| API Documentation | http://localhost:8080/api/swagger-ui/index.html | Swagger UI |
| Health Check | http://localhost:8080/api/actuator/health | Application health |

## 🛠️ Project Structure
```
AIworks/
├── aiprojects/                    # Angular Frontend
│   ├── src/app/
│   │   ├── components/           # UI components
│   │   ├── services/             # API services
│   │   ├── models/               # TypeScript interfaces
│   │   └── ...
│   └── e2e/                      # Playwright tests
├── insurance-quote-backend/       # Spring Boot Backend
│   ├── src/main/java/
│   │   ├── entity/               # JPA entities
│   │   ├── repository/           # Data repositories
│   │   ├── service/              # Business logic
│   │   ├── controller/           # REST controllers
│   │   └── ...
│   └── src/test/java/            # JUnit tests
├── start-full-application.sh     # Startup script
└── stop-application.sh           # Shutdown script
```

For detailed documentation, see the README files in each project directory.