#!/bin/bash

if [ ! -d "node_modules" ]; then
  echo "Node modules not found. Running 'npm install'..."
  npm install
fi

echo "Running 'ng build'..."
ng build

echo "Starting the server..."
node server.js

echo "Server is running. Press Ctrl+C to stop."
while true; do
  sleep 1
done
