#!/usr/bin/env bash
set -e

echo "=== Starting MessageBoard Application ==="

BACKEND_DIR="$(dirname "$0")/backend"
FRONTEND_DIR="$(dirname "$0")/frontend"
MAVEN="/tmp/apache-maven-3.9.16/bin/mvn"

echo "[1/2] Starting backend on port 8080..."
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
$MAVEN -f "$BACKEND_DIR/pom.xml" spring-boot:run -q > /tmp/backend.log 2>&1 &
BACKEND_PID=$!
echo "  Backend PID: $BACKEND_PID"

echo "[2/2] Starting frontend on port 5173..."
npm --prefix "$FRONTEND_DIR" run dev > /tmp/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "  Frontend PID: $FRONTEND_PID"

echo ""
echo "=== Application Started ==="
echo "  Frontend: http://localhost:5173"
echo "  Backend:  http://localhost:8080"
echo ""
echo "Use 'kill $BACKEND_PID $FRONTEND_PID' to stop both."
