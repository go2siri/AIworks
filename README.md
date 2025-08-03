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

## 💾 Database Configuration

### H2 Database Details
- **URL**: `jdbc:h2:mem:insurance_quote_db`
- **Username**: `sa`
- **Password**: `password`
- **Console**: Available at http://localhost:8080/api/h2-console

## 🔧 API Endpoints

### Quote Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/quotes` | Create new quote |
| PUT | `/api/quotes/{id}` | Update existing quote |
| GET | `/api/quotes/{id}` | Get quote by ID |
| GET | `/api/quotes` | Get all quotes (paginated) |
| DELETE | `/api/quotes/{id}` | Delete quote |
| POST | `/api/quotes/{id}/submit` | Submit quote for approval |
| POST | `/api/quotes/{id}/approve` | Approve quote |
| POST | `/api/quotes/{id}/reject` | Reject quote |
| GET | `/api/quotes/statistics` | Get quote statistics |

### Search and Filter
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/quotes/search?businessName={name}` | Search by business name |
| GET | `/api/quotes/status/{status}` | Filter by status |
| GET | `/api/quotes/state/{state}` | Filter by state |

## 🧪 Testing

### Backend Tests
```bash
cd insurance-quote-backend
mvn test
```

### Frontend E2E Tests
```bash
cd aiprojects
npm run test:e2e
```

### Integration Tests
```bash
# Start the full application first
./start-full-application.sh

# Run integration tests
cd aiprojects
npx playwright test full-stack-integration.spec.ts
```

### Test Coverage
The application includes comprehensive test coverage:
- **Positive Scenarios**: Valid operations and data flows
- **Negative Scenarios**: Error handling and validation
- **Integration Tests**: Full-stack functionality
- **Edge Cases**: Boundary conditions and error states

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

## 🔍 Logging and Debugging

### Log Files
- **Backend**: `logs/backend.log`
- **Frontend**: `logs/frontend.log`
- **Structured JSON**: `logs/backend-json.log`

### Log Levels
- **Development**: DEBUG level with SQL logging
- **Production**: INFO level with structured JSON
- **Test**: DEBUG level console only

### Debugging Tips
```bash
# View real-time backend logs
tail -f logs/backend.log

# View real-time frontend logs
tail -f logs/frontend.log

# Check application health
curl http://localhost:8080/api/actuator/health

# View H2 database console
open http://localhost:8080/api/h2-console
```

## 🛠️ Development

### Project Structure
```
insurance-quote/
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
├── logs/                         # Application logs
├── start-full-application.sh     # Startup script
└── stop-application.sh           # Shutdown script
```

### Adding New Features

1. **Backend**: Add entities, repositories, services, controllers
2. **Frontend**: Add components, services, models
3. **Tests**: Add unit tests and E2E tests
4. **Documentation**: Update API docs and README

### Code Quality
- **Backend**: Uses Spring Boot best practices, proper validation, exception handling
- **Frontend**: Uses Angular signals, reactive forms, proper error handling
- **Testing**: Comprehensive test coverage with both positive and negative scenarios

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
cat logs/backend.log
```

#### Frontend Won't Start
```bash
# Check Node.js version
node -v

# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install

# View frontend logs
cat logs/frontend.log
```

#### Database Issues
- H2 console: http://localhost:8080/api/h2-console
- Check connection settings in `application.yml`
- Restart application to reset in-memory database

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Submit a pull request

## 📞 Support

For issues and questions:
1. Check the logs in the `logs/` directory
2. Verify all services are running
3. Check the troubleshooting section
4. Review the test files for examples