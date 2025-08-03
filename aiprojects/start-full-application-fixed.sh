#!/bin/bash

# Insurance Quote Application - Fixed Startup Script

echo "🚀 Starting Insurance Quote Full Stack Application"
echo "=================================================="

# Set Java and Maven environment
export JAVA_HOME=/home/aiworks/.local/jdk-21.0.2
export M2_HOME=/home/aiworks/.local/apache-maven-3.9.4
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH

echo "🔍 Checking prerequisites..."

# Check Java
if [ ! -f "$JAVA_HOME/bin/java" ]; then
    echo "❌ Java not found at $JAVA_HOME/bin/java"
    exit 1
fi

# Test Java execution
if ! $JAVA_HOME/bin/java -version > /dev/null 2>&1; then
    echo "❌ Java execution failed"
    exit 1
fi

JAVA_VERSION=$($JAVA_HOME/bin/java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo "✅ Java $JAVA_VERSION detected"

# Check Node.js
if ! command -v node > /dev/null 2>&1; then
    echo "❌ Node.js is not installed"
    exit 1
fi

NODE_VERSION=$(node -v)
echo "✅ Node.js $NODE_VERSION detected"

# Check Maven
if [ -f "$M2_HOME/bin/mvn" ]; then
    MAVEN_VERSION=$($M2_HOME/bin/mvn -version 2>&1 | head -n 1 | cut -d' ' -f3)
    echo "✅ Maven $MAVEN_VERSION detected"
fi

echo "✅ Prerequisites check completed"

# Function to check if port is available
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "❌ Port $1 is already in use"
        return 1
    else
        echo "✅ Port $1 is available"
        return 0
    fi
}

# Check ports
echo "🔍 Checking port availability..."
check_port 8080
check_port 4201

# Create logs directory
mkdir -p logs

echo "🚀 Starting Spring Boot Backend (Port 8080)..."
cd /home/aiworks/insurance-quote-backend

# Build backend
echo "🔨 Building backend application..."
if ./mvnw clean package -DskipTests > ../logs/backend-build.log 2>&1; then
    echo "✅ Backend build successful"
else
    echo "❌ Backend build failed. Check logs/backend-build.log"
    exit 1
fi

# Start backend
echo "🚀 Starting backend server..."
nohup ./mvnw spring-boot:run > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > ../logs/backend.pid
echo "✅ Backend started with PID: $BACKEND_PID"

# Wait for backend
echo "⏳ Waiting for backend to be ready..."
cd ..
for i in {1..30}; do
    if curl -s http://localhost:8080/api/actuator/health > /dev/null 2>&1; then
        echo "✅ Backend is ready!"
        break
    fi
    echo "   Attempt $i/30 - waiting for backend..."
    sleep 2
    if [ $i -eq 30 ]; then
        echo "❌ Backend failed to start. Check logs/backend.log"
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
done

echo "🚀 Starting Angular Frontend (Port 4201)..."
cd /home/aiworks/aiprojects

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    if npm install > ../logs/frontend-install.log 2>&1; then
        echo "✅ Dependencies installed"
    else
        echo "❌ Dependency installation failed. Check logs/frontend-install.log"
        kill $BACKEND_PID 2>/dev/null
        exit 1
    fi
fi

# Start frontend
echo "🚀 Starting frontend server..."
nohup npm run start -- --port 4201 > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../logs/frontend.pid
echo "✅ Frontend started with PID: $FRONTEND_PID"

# Wait for frontend
echo "⏳ Waiting for frontend to be ready..."
for i in {1..30}; do
    if curl -s http://localhost:4201 > /dev/null 2>&1; then
        echo "✅ Frontend is ready!"
        break
    fi
    echo "   Attempt $i/30 - waiting for frontend..."
    sleep 2
    if [ $i -eq 30 ]; then
        echo "❌ Frontend failed to start. Check logs/frontend.log"
        kill $BACKEND_PID 2>/dev/null
        kill $FRONTEND_PID 2>/dev/null
        exit 1
    fi
done

# Success message
echo ""
echo "🎉 Insurance Quote Application Started Successfully!"
echo "=============================================="
echo "📋 Application URLs:"
echo "   🖥️  Frontend:      http://localhost:4201"
echo "   🔧 Backend API:    http://localhost:8080/api"
echo "   🗄️  H2 Console:    http://localhost:8080/api/h2-console"
echo "   📚 API Docs:       http://localhost:8080/api/swagger-ui/index.html"
echo "   ❤️  Health Check:  http://localhost:8080/api/actuator/health"
echo ""
echo "📋 Process Information:"
echo "   Backend PID:  $BACKEND_PID"
echo "   Frontend PID: $FRONTEND_PID"
echo ""
echo "📋 Log Files:"
echo "   Backend:  logs/backend.log"
echo "   Frontend: logs/frontend.log"
echo ""
echo "🗄️  Database Information:"
echo "   URL:      jdbc:h2:mem:insurance_quote_db"
echo "   Username: sa"
echo "   Password: password"
echo ""
echo "💡 Useful Commands:"
echo "   Stop Application:     ./stop-application.sh"
echo "   View Backend Logs:    tail -f logs/backend.log"
echo "   View Frontend Logs:   tail -f logs/frontend.log"
echo "   Run Tests:            cd aiprojects && npm run test:e2e"
echo ""
echo "✨ Ready to use! Open http://localhost:4201 in your browser"