#!/bin/bash

# Port of the Spring Boot application
PORT=8085

echo "Starting Delis Divin Remote Access Tunnels..."
echo "Press Ctrl+C to stop all tunnels."

# Cleanup handler
cleanup() {
    echo "Stopping all tunnels..."
    kill $(jobs -p) 2>/dev/null
    exit
}
trap cleanup SIGINT SIGTERM

# 1. Run Localhost.run in a loop
(
    while true; do
        echo "[Localhost.run] Establishing connection..."
        ssh -o StrictHostKeyChecking=no -R 80:localhost:$PORT nokey@localhost.run 2>&1
        echo "[Localhost.run] Connection lost. Reconnecting in 5s..."
        sleep 5
    done
) &

# 2. Run Serveo.net in a loop
(
    while true; do
        echo "[Serveo.net] Establishing connection..."
        ssh -o StrictHostKeyChecking=no -R delisdivin:80:localhost:$PORT serveo.net 2>&1
        echo "[Serveo.net] Connection lost. Reconnecting in 5s..."
        sleep 5
    done
) &

# 3. Run Localtunnel in a loop
(
    while true; do
        echo "[Localtunnel] Establishing connection..."
        npx lt --port $PORT --subdomain delisdivin > /tmp/localtunnel_url.log 2>&1
        echo "[Localtunnel] Connection lost. Reconnecting in 5s..."
        sleep 5
    done
) &

# 4. Run Pinggy.io in a loop (SSH-based)
(
    while true; do
        echo "[Pinggy.io] Establishing connection..."
        ssh -o StrictHostKeyChecking=no -o ServerAliveInterval=30 -R 80:localhost:$PORT pinggy.io 2>&1
        echo "[Pinggy.io] Connection lost. Reconnecting in 5s..."
        sleep 5
    done
) &

# Wait for all background jobs
wait

