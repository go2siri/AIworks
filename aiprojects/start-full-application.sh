#!/bin/bash

# Insurance Quote Application - Full Stack Startup Script

echo "🚀 Starting Insurance Quote Full Stack Application"
echo "=================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if a port is available
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        echo -e "${RED}❌ Port $1 is already in use${NC}"
        return 1
    else
        echo -e "${GREEN}✅ Port $1 is available${NC}"
        return 0
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}⏳ Waiting for $service_name to be ready...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s $url > /dev/null 2>&1; then
            echo -e "${GREEN}✅ $service_name is ready!${NC}"
            return 0
        fi
        echo -e "${BLUE}   Attempt $attempt/$max_attempts - waiting for $service_name...${NC}"
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}❌ $service_name failed to start within expected time${NC}"
    return 1
}

# Check prerequisites
printf "${BLUE}🔍 Checking prerequisites...${NC}\n"

# Set Java environment
export JAVA_HOME=/home/aiworks/.local/jdk-21.0.2
export PATH=$JAVA_HOME/bin:$PATH

# Check if Java is installed
if [ ! -f "$JAVA_HOME/bin/java" ]; then
    printf "${RED}❌ Java is not installed or JAVA_HOME is not set correctly.${NC}\n"
    printf "${RED}   Expected: /home/aiworks/.local/jdk-21.0.2${NC}\n"
    exit 1
fi

# Test Java execution
if ! $JAVA_HOME/bin/java -version > /dev/null 2>&1; then
    printf "${RED}❌ Java execution failed${NC}\n"
    exit 1
fi

printf "${GREEN}✅ Java $(java -version 2>&1 | head -n 1 | cut -d' ' -f3 | tr -d '\"') detected${NC}\n"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js is not installed. Please install Node.js.${NC}"
    exit 1
fi

# Set Maven environment
export M2_HOME=/home/aiworks/.local/apache-maven-3.9.4
export PATH=$M2_HOME/bin:$PATH

# Check if Maven is installed (optional, we can use wrapper)
if ! command -v mvn &> /dev/null; then
    echo -e "${YELLOW}⚠️  Maven not found. Will use Maven wrapper.${NC}"
else
    echo -e "${GREEN}✅ Maven $(mvn -version 2>&1 | head -n 1 | cut -d' ' -f3) detected${NC}"
fi

echo -e "${GREEN}✅ Prerequisites check completed${NC}"

# Check ports availability
echo -e "${BLUE}🔍 Checking port availability...${NC}"
check_port 8080 # Backend
check_port 4201 # Frontend

# Create logs directory
mkdir -p logs

# Start Backend
echo -e "${BLUE}🚀 Starting Spring Boot Backend (Port 8080)...${NC}"
cd ../insurance-quote-backend

# Build the application first
echo -e "${YELLOW}🔨 Building backend application...${NC}"
if command -v mvn &> /dev/null; then
    mvn clean package -DskipTests > ../aiprojects/logs/backend-build.log 2>&1
else
    ./mvnw clean package -DskipTests > ../aiprojects/logs/backend-build.log 2>&1
fi

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Backend build successful${NC}"
else
    echo -e "${RED}❌ Backend build failed. Check logs/backend-build.log${NC}"
    exit 1
fi

# Start the backend application
echo -e "${YELLOW}🚀 Starting backend server...${NC}"
if command -v mvn &> /dev/null; then
    nohup mvn spring-boot:run > ../aiprojects/logs/backend.log 2>&1 &
else
    nohup ./mvnw spring-boot:run > ../aiprojects/logs/backend.log 2>&1 &
fi

BACKEND_PID=$!
echo $BACKEND_PID > ../aiprojects/logs/backend.pid
echo -e "${GREEN}✅ Backend started with PID: $BACKEND_PID${NC}"

cd ../aiprojects

# Wait for backend to be ready
wait_for_service "http://localhost:8080/api/actuator/health" "Backend API"

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Backend failed to start. Check logs/backend.log${NC}"
    kill $BACKEND_PID 2>/dev/null
    exit 1
fi

# Start Frontend
echo -e "${BLUE}🚀 Starting Angular Frontend (Port 4201)...${NC}"

# Install dependencies if node_modules doesn't exist
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}📦 Installing frontend dependencies...${NC}"
    npm install > logs/frontend-install.log 2>&1
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Frontend dependency installation failed. Check logs/frontend-install.log${NC}"
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
fi

echo -e "${YELLOW}🚀 Starting frontend server...${NC}"
nohup npm run start -- --port 4201 > logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > logs/frontend.pid
echo -e "${GREEN}✅ Frontend started with PID: $FRONTEND_PID${NC}"

cd ..

# Wait for frontend to be ready
wait_for_service "http://localhost:4201" "Frontend Application"

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Frontend failed to start. Check logs/frontend.log${NC}"
    kill $BACKEND_PID 2>/dev/null
    kill $FRONTEND_PID 2>/dev/null
    exit 1
fi

# Display application information
echo
echo -e "${GREEN}🎉 Insurance Quote Application Started Successfully!${NC}"
echo "=============================================="
echo -e "${BLUE}📋 Application URLs:${NC}"
echo -e "   🖥️  Frontend:      ${GREEN}http://localhost:4201${NC}"
echo -e "   🔧 Backend API:    ${GREEN}http://localhost:8080/api${NC}"
echo -e "   🗄️  H2 Console:    ${GREEN}http://localhost:8080/api/h2-console${NC}"
echo -e "   📚 API Docs:       ${GREEN}http://localhost:8080/api/swagger-ui/index.html${NC}"
echo -e "   ❤️  Health Check:  ${GREEN}http://localhost:8080/api/actuator/health${NC}"
echo
echo -e "${BLUE}📋 Process Information:${NC}"
echo -e "   Backend PID:  ${YELLOW}$BACKEND_PID${NC}"
echo -e "   Frontend PID: ${YELLOW}$FRONTEND_PID${NC}"
echo
echo -e "${BLUE}📋 Log Files:${NC}"
echo -e "   Backend:  ${YELLOW}logs/backend.log${NC}"
echo -e "   Frontend: ${YELLOW}logs/frontend.log${NC}"
echo
echo -e "${BLUE}🗄️  Database Information:${NC}"
echo -e "   URL:      ${YELLOW}jdbc:h2:mem:insurance_quote_db${NC}"
echo -e "   Username: ${YELLOW}sa${NC}"
echo -e "   Password: ${YELLOW}password${NC}"
echo
echo -e "${YELLOW}💡 Useful Commands:${NC}"
echo "   Stop Application:     ./stop-application.sh"
echo "   View Backend Logs:    tail -f logs/backend.log"
echo "   View Frontend Logs:   tail -f logs/frontend.log"
echo "   Run Tests:            cd aiprojects && npm run test:e2e"
echo
echo -e "${GREEN}✨ Ready to use! Open http://localhost:4201 in your browser${NC}"