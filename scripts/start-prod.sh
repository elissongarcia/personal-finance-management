#!/bin/bash

# Production Startup Script for Personal Finance Management Service
# Optimized JVM settings for high performance

set -e

# Configuration
APP_NAME="personal-finance-management"
JAR_FILE="target/personal-finance-management-1.0.0.jar"
LOG_DIR="logs"
PID_FILE="app.pid"

# JVM Optimization Settings
JVM_OPTS="-server"

# Memory Settings
JVM_OPTS="$JVM_OPTS -Xms2g -Xmx4g"
JVM_OPTS="$JVM_OPTS -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m"

# Garbage Collection Optimization
JVM_OPTS="$JVM_OPTS -XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
JVM_OPTS="$JVM_OPTS -XX:G1HeapRegionSize=16m"
JVM_OPTS="$JVM_OPTS -XX:G1NewSizePercent=30"
JVM_OPTS="$JVM_OPTS -XX:G1MaxNewSizePercent=40"
JVM_OPTS="$JVM_OPTS -XX:G1MixedGCCountTarget=8"
JVM_OPTS="$JVM_OPTS -XX:+UnlockExperimentalVMOptions"
JVM_OPTS="$JVM_OPTS -XX:G1MixedGCLiveThresholdPercent=85"

# Performance Optimizations
JVM_OPTS="$JVM_OPTS -XX:+UseStringDeduplication"
JVM_OPTS="$JVM_OPTS -XX:+OptimizeStringConcat"
JVM_OPTS="$JVM_OPTS -XX:+UseCompressedOops"
JVM_OPTS="$JVM_OPTS -XX:+UseCompressedClassPointers"

# Thread and Concurrency Optimizations
JVM_OPTS="$JVM_OPTS -XX:+UseThreadPriorities"
JVM_OPTS="$JVM_OPTS -XX:ThreadPriorityPolicy=1"
JVM_OPTS="$JVM_OPTS -XX:+UseBiasedLocking"
JVM_OPTS="$JVM_OPTS -XX:BiasedLockingStartupDelay=0"

# JIT Compiler Optimizations
JVM_OPTS="$JVM_OPTS -XX:+TieredCompilation"
JVM_OPTS="$JVM_OPTS -XX:TieredStopAtLevel=1"
JVM_OPTS="$JVM_OPTS -XX:+UseAdaptiveSizePolicy"
JVM_OPTS="$JVM_OPTS -XX:AdaptiveSizePolicyWeight=90"

# Memory Management
JVM_OPTS="$JVM_OPTS -XX:+AlwaysPreTouch"
JVM_OPTS="$JVM_OPTS -XX:+UseLargePages"
JVM_OPTS="$JVM_OPTS -XX:+UseTransparentHugePages"

# Monitoring and Profiling
JVM_OPTS="$JVM_OPTS -XX:+UnlockDiagnosticVMOptions"
JVM_OPTS="$JVM_OPTS -XX:+LogVMOutput"
JVM_OPTS="$JVM_OPTS -XX:LogFile=$LOG_DIR/jvm.log"

# GC Logging
JVM_OPTS="$JVM_OPTS -Xlog:gc*:file=$LOG_DIR/gc.log:time,uptime:filecount=5,filesize=100M"

# Application Properties
APP_OPTS="--spring.profiles.active=prod"
APP_OPTS="$APP_OPTS --server.port=8080"
APP_OPTS="$APP_OPTS --logging.file.name=$LOG_DIR/application.log"

# Environment Variables
export JAVA_OPTS="$JVM_OPTS"
export SPRING_PROFILES_ACTIVE=prod

# Create log directory if it doesn't exist
mkdir -p $LOG_DIR

# Function to check if application is running
check_running() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null 2>&1; then
            echo "Application is already running with PID $PID"
            return 0
        else
            echo "Removing stale PID file"
            rm -f "$PID_FILE"
        fi
    fi
    return 1
}

# Function to start the application
start_app() {
    echo "Starting $APP_NAME with optimized JVM settings..."
    echo "JVM Options: $JVM_OPTS"
    echo "Application Options: $APP_OPTS"
    
    nohup java $JVM_OPTS -jar $JAR_FILE $APP_OPTS > $LOG_DIR/startup.log 2>&1 &
    echo $! > "$PID_FILE"
    
    echo "Application started with PID $(cat $PID_FILE)"
    echo "Logs available at: $LOG_DIR/"
    echo "GC logs available at: $LOG_DIR/gc.log"
    echo "JVM logs available at: $LOG_DIR/jvm.log"
}

# Function to stop the application
stop_app() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        echo "Stopping application with PID $PID..."
        kill $PID
        rm -f "$PID_FILE"
        echo "Application stopped"
    else
        echo "No PID file found"
    fi
}

# Function to restart the application
restart_app() {
    echo "Restarting application..."
    stop_app
    sleep 5
    start_app
}

# Function to show application status
status_app() {
    if check_running; then
        PID=$(cat "$PID_FILE")
        echo "Application is running with PID $PID"
        echo "Memory usage:"
        ps -o pid,ppid,cmd,%mem,%cpu --no-headers -p $PID
    else
        echo "Application is not running"
    fi
}

# Function to show JVM statistics
show_stats() {
    if check_running; then
        PID=$(cat "$PID_FILE")
        echo "JVM Statistics for PID $PID:"
        echo "Memory:"
        jstat -gc $PID
        echo ""
        echo "Memory Pool:"
        jstat -gccapacity $PID
        echo ""
        echo "GC Statistics:"
        jstat -gcutil $PID
    else
        echo "Application is not running"
    fi
}

# Function to show help
show_help() {
    echo "Usage: $0 {start|stop|restart|status|stats|help}"
    echo ""
    echo "Commands:"
    echo "  start   - Start the application with optimized JVM settings"
    echo "  stop    - Stop the application"
    echo "  restart - Restart the application"
    echo "  status  - Show application status"
    echo "  stats   - Show JVM statistics"
    echo "  help    - Show this help message"
    echo ""
    echo "JVM Optimizations:"
    echo "  - G1GC Garbage Collector"
    echo "  - Optimized memory settings"
    echo "  - Thread and concurrency optimizations"
    echo "  - JIT compiler optimizations"
    echo "  - Monitoring and profiling enabled"
}

# Main script logic
case "$1" in
    start)
        if check_running; then
            exit 1
        fi
        start_app
        ;;
    stop)
        stop_app
        ;;
    restart)
        restart_app
        ;;
    status)
        status_app
        ;;
    stats)
        show_stats
        ;;
    help|*)
        show_help
        exit 1
        ;;
esac

exit 0 