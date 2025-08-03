# Insurance Quote Application - Startup Guide

## Quick Start (Recommended)

Use the fixed startup script:
```bash
./start-full-application-fixed.sh
```

## Manual Start (Alternative)

### Prerequisites
Make sure Java 21 is in your PATH:
```bash
export JAVA_HOME=/home/aiworks/.local/jdk-21.0.2
export PATH=$JAVA_HOME/bin:$PATH
```

### Start Backend (Terminal 1)
```bash
cd /home/aiworks/insurance-quote-backend
./mvnw spring-boot:run
```

Wait for the message: "Started InsuranceQuoteApplication"

### Start Frontend (Terminal 2)
```bash
cd /home/aiworks/aiprojects
npm start -- --port 4201
```

Wait for: "✔ Browser application bundle generation complete."

## Application URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:4201 | Main application UI |
| Backend API | http://localhost:8080/api | REST API endpoints |
| H2 Console | http://localhost:8080/api/h2-console | Database console |
| API Documentation | http://localhost:8080/api/swagger-ui/index.html | Swagger UI |
| Health Check | http://localhost:8080/api/actuator/health | Application health |

## Database Configuration

- **URL**: `jdbc:h2:mem:insurance_quote_db`
- **Username**: `sa`
- **Password**: `password`

## Stop Application

```bash
./stop-application.sh
```

Or manually kill the processes using the PIDs shown during startup.

## Troubleshooting

### Java Issues
```bash
# Verify Java installation
$JAVA_HOME/bin/java -version

# Should show: openjdk version "21.0.2" 2024-01-16
```

### Port Issues
```bash
# Check what's using port 8080
lsof -i :8080

# Check what's using port 4201  
lsof -i :4201

# Kill processes if needed
kill -9 <PID>
```

### Build Issues
```bash
# Clean and rebuild backend
cd /home/aiworks/insurance-quote-backend
./mvnw clean compile

# Reinstall frontend dependencies
cd /home/aiworks/aiprojects
rm -rf node_modules package-lock.json
npm install
```

## Log Files

- Backend: `logs/backend.log`
- Frontend: `logs/frontend.log`
- Build: `logs/backend-build.log`

View logs in real-time:
```bash
tail -f logs/backend.log
tail -f logs/frontend.log
```