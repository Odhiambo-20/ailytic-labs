#!/bin/bash

cd ~/Documents/Desktop/Allytic-Labs/com.allyticlabs.backend

echo "=== Complete Fix - Starting Fresh ===" 

# Check if we have git
if git rev-parse --git-dir > /dev/null 2>&1; then
    echo "Git repository found. Restoring corrupted files..."
    git checkout src/main/java/com/allyticlabs/backend/security/TokenGenerator.java
    git checkout src/main/java/com/allyticlabs/backend/service/PaymentService.java
    git checkout src/main/java/com/allyticlabs/backend/config/StripeConfig.java
    git checkout src/main/java/com/allyticlabs/backend/config/MpesaConfig.java
    git checkout src/main/java/com/allyticlabs/backend/service/StripeService.java
    echo "Files restored from git!"
else
    echo "No git repository. Please share the original files or I'll need to rebuild them."
    echo ""
    echo "Run these commands to see the file sizes:"
    echo "wc -l src/main/java/com/allyticlabs/backend/security/TokenGenerator.java"
    echo "wc -l src/main/java/com/allyticlabs/backend/config/MpesaConfig.java"
fi

