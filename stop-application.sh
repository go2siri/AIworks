#!/bin/bash

# Insurance Quote Application - Shutdown Script

echo "🛑 Stopping Insurance Quote Full Stack Application"
echo "================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to stop process by PID
stop_process() {
    local pid_file=$1
    local service_name=$2
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 "$pid" 2>/dev/null; then
            echo -e "${YELLOW}🛑 Stopping $service_name (PID: $pid)...${NC}"
            kill "$pid"
            
            # Wait for process to stop
            local attempt=1
            while kill -0 "$pid" 2>/dev/null && [ $attempt -le 10 ]; do
                echo -e "${BLUE}   Waiting for $service_name to stop... (attempt $attempt/10)${NC}"
                sleep 1
                attempt=$((attempt + 1))
            done
            
            # Force kill if still running
            if kill -0 "$pid" 2>/dev/null; then
                echo -e "${RED}⚠️  Force killing $service_name...${NC}"
                kill -9 "$pid" 2>/dev/null
            fi
            
            echo -e "${GREEN}✅ $service_name stopped${NC}"
        else
            echo -e "${YELLOW}⚠️  $service_name was not running${NC}"
        fi
        rm -f "$pid_file"
    else
        echo -e "${YELLOW}⚠️  No PID file found for $service_name${NC}"
    fi
}

# Stop Frontend
stop_process "logs/frontend.pid" "Frontend"

# Stop Backend  
stop_process "logs/backend.pid" "Backend"

# Also kill any remaining processes on the ports
echo -e "${BLUE}🔍 Checking for remaining processes on ports...${NC}"

# Kill processes on port 4201 (Frontend)
if lsof -ti:4201 >/dev/null 2>&1; then
    echo -e "${YELLOW}🛑 Killing remaining processes on port 4201...${NC}"
    lsof -ti:4201 | xargs kill -9 2>/dev/null
fi

# Kill processes on port 8080 (Backend)
if lsof -ti:8080 >/dev/null 2>&1; then
    echo -e "${YELLOW}🛑 Killing remaining processes on port 8080...${NC}"
    lsof -ti:8080 | xargs kill -9 2>/dev/null
fi

echo
echo -e "${GREEN}✅ Insurance Quote Application stopped successfully!${NC}"
echo -e "${BLUE}📋 Log files preserved in logs/ directory${NC}"
echo