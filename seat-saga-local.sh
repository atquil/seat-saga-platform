#!/bin/bash

# Configuration
DB_CONTAINER_NAME="seat-saga-db"
APP_CONTAINER_NAME="seat-saga-api" # Matches the service name in docker-compose.yml
COMPOSE_FILES="-f docker-compose.yml -f docker-compose.local.yml"

show_access_points() {
    echo ""
    echo "📊 Access Points"
    echo "-------------------------------------------------------"
    echo "🎬 Application:  http://localhost:8080"
    echo "📚 Swagger UI:   http://localhost:8080/swagger-ui.html"
    echo "📊 Health Check: http://localhost:8080/actuator/health"
    echo "📄 API Docs:     http://localhost:8080/api-docs"
    echo "-------------------------------------------------------"
    echo "💡 Note: If you use a context-path like /api, add it to the URLs above."
}

show_help() {
    echo "🎬 Seat Saga Platform Management"
    echo "Usage: ./seat-saga.sh [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start       Build and start the platform"
    echo "  stop        Stop all containers"
    echo "  restart     Stop and then start the platform"
    echo "  logs        Follow application logs"
    echo "  db-check    List all tables created by Liquibase"
    echo "  urls        Show all application access points"
    echo "  clean       Stop containers and remove volumes (wipes DB data)"
}

start_app() {
    echo "🔨 Building application with Gradle..."
    ./gradlew clean bootJar -x test

    echo "🐳 Starting containers..."
    docker compose $COMPOSE_FILES up --build -d

    show_access_points
}
show_logs() {
    echo "📋 Tailing logs for container: $APP_CONTAINER_NAME..."
    docker compose logs -f $APP_CONTAINER_NAME
}

stop_app() {
    echo "🛑 Stopping Seat Saga containers..."
    docker compose $COMPOSE_FILES stop
}

check_db() {
    echo "🔍 Checking database tables in $DB_CONTAINER_NAME..."
    docker exec -it $DB_CONTAINER_NAME psql -U myuser -d seat-saga-db -c "\dt"
    echo ""
    echo "📜 Recent Liquibase migrations:"
    docker exec -it $DB_CONTAINER_NAME psql -U myuser -d seat-saga-db -c "SELECT id, author, dateexecuted FROM databasechangelog ORDER BY dateexecuted DESC LIMIT 5;"
}

# Logic to handle commands
case "$1" in
    start)    start_app ;;
    stop)     stop_app ;;
    restart)  stop_app; start_app ;;
    logs)     show_logs ;;
    db-check) check_db ;;
    urls)     show_access_points ;;
    clean)    docker compose $COMPOSE_FILES down -v ;;
    *)        show_help ;;
esac