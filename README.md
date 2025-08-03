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
# In repository root
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
AIworks/                          # Repository Root (Angular Frontend)
├── src/app/                      # Angular Application
│   ├── business-information/     # Business info component
│   ├── coverage-configurator/    # Coverage selection component
│   ├── quote-summary/            # Quote summary component
│   ├── services/                 # API services
│   ├── models/                   # TypeScript interfaces
│   └── ...
├── e2e/                          # Playwright E2E tests
│   ├── positive-scenarios.spec.ts
│   ├── negative-scenarios.spec.ts
│   ├── edge-cases.spec.ts
│   └── full-stack-integration.spec.ts
├── insurance-quote-backend/      # Spring Boot Backend
│   ├── src/main/java/
│   │   ├── entity/               # JPA entities
│   │   ├── repository/           # Data repositories
│   │   ├── service/              # Business logic
│   │   ├── controller/           # REST controllers
│   │   └── ...
│   └── src/test/java/            # JUnit tests
├── angular.json                  # Angular configuration
├── package.json                  # NPM dependencies
├── PROJECT_LEARNINGS.md          # Comprehensive project documentation
├── start-full-application.sh     # Startup script
└── stop-application.sh           # Shutdown script
```

## 🧪 Testing

### Frontend E2E Tests
```bash
# Run all E2E tests
npm run test:e2e

# Run tests with UI for debugging
npm run test:e2e:ui

# Run specific test categories
npx playwright test positive-scenarios.spec.ts
npx playwright test negative-scenarios.spec.ts
npx playwright test edge-cases.spec.ts
npx playwright test full-stack-integration.spec.ts
```

### Backend Tests
```bash
cd insurance-quote-backend
mvn test
```

### Test Coverage
The application includes comprehensive test coverage:
- **Positive Scenarios**: Valid operations and data flows
- **Negative Scenarios**: Error handling and validation
- **Edge Cases**: Boundary conditions and unusual behaviors
- **Integration Tests**: Full-stack functionality
- **Multi-Browser Testing**: Chrome, Firefox, Safari compatibility

## 📊 Features

### Core Functionality
- ✅ Create and manage insurance quotes
- ✅ Business information collection and validation
- ✅ Coverage option selection with premium calculation
- ✅ Quote status workflow (Draft → Saved → Submitted → Approved/Rejected)
- ✅ Real-time form validation
- ✅ Data persistence across sessions

### Technical Features
- ✅ Responsive design for mobile and desktop
- ✅ RESTful API with proper HTTP status codes
- ✅ Comprehensive error handling and validation
- ✅ Structured logging for debugging
- ✅ CORS configuration for frontend-backend communication
- ✅ Database schema with proper relationships
- ✅ Automatic quote number generation
- ✅ Quote expiration handling

## 🎯 Data Models

### Business Information
```typescript
interface BusinessInformation {
  id?: number;
  name: string;              // Required, min 2 chars
  businessType: BusinessType; // RETAIL, RESTAURANT, etc.
  industry: Industry;        // FOOD_SERVICE, SOFTWARE, etc.
  state: string;            // 2-letter state code
}
```

### Coverage Option
```typescript
interface CoverageOption {
  id?: number;
  name: string;
  coverageType: CoverageType; // GENERAL_LIABILITY, PROPERTY, ADDITIONAL
  premium: number;
  isSelected: boolean;
}
```

### Quote
```typescript
interface Quote {
  id?: number;
  businessInformation: BusinessInformation;
  coverageOptions: CoverageOption[];
  totalPremium: number;
  status: QuoteStatus;       // DRAFT, SAVED, SUBMITTED, etc.
  quoteNumber?: string;      // Auto-generated
  validUntil?: Date;
}
```

## 📈 Quote Workflow

```
DRAFT → SAVED → SUBMITTED → APPROVED/REJECTED
  ↑       ↑        ↑           ↓
  └───────┴────────┴─────── EXPIRED
```

### Status Transitions
- **DRAFT**: Initial state, can be edited freely
- **SAVED**: Validated and saved, can be edited or submitted
- **SUBMITTED**: Under review, cannot be edited
- **APPROVED**: Final approved state
- **REJECTED**: Final rejected state
- **EXPIRED**: Quote has expired

## 🚨 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Kill processes on port 8080 (backend)
lsof -ti:8080 | xargs kill -9

# Kill processes on port 4201 (frontend)
lsof -ti:4201 | xargs kill -9
```

#### Backend Won't Start
```bash
# Check Java version
java -version

# Check Maven
mvn -version

# View backend logs
cat insurance-quote-backend/logs/logs/insurance-quote-backend.log.log
```

#### Frontend Won't Start
```bash
# Check Node.js version
node -v

# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# View frontend logs
cat frontend.log
```

## 📄 Documentation

For detailed project insights, challenges encountered, and lessons learned, see [PROJECT_LEARNINGS.md](PROJECT_LEARNINGS.md).

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Submit a pull request

## 📞 Support

For issues and questions:
1. Check the troubleshooting section above
2. Review the PROJECT_LEARNINGS.md for common issues and solutions
3. Verify all services are running
4. Check the test files for examples