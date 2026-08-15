#!/bin/bash

# Script to update all localhost references to Vercel deployment URLs
# Usage: ./update-frontend-urls.sh

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# New Vercel domains (comma-separated for properties files)
VERCEL_DOMAINS="https://bellatechnologies-frontend.vercel.app,https://bella-technologies-frontend-git-main-victor-odhiambos-projects.vercel.app,https://bella-technologies-frontend-7l3o6f9fn-victor-odhiambos-projects.vercel.app,http://localhost:3000"

# Main Vercel domain for single URL replacements
MAIN_VERCEL="https://bellatechnologies-frontend.vercel.app"

echo -e "${GREEN}=== Frontend URL Update Script ===${NC}"
echo -e "${YELLOW}This will update localhost references to Vercel deployment URLs${NC}"
echo ""

# Create backup directory
BACKUP_DIR="backup_$(date +%Y%m%d_%H%M%S)"
echo -e "${YELLOW}Creating backup directory: $BACKUP_DIR${NC}"
mkdir -p "$BACKUP_DIR"

# Function to backup and update file
backup_and_update() {
    local file=$1
    local search=$2
    local replace=$3
    
    if [ -f "$file" ]; then
        # Check if file contains the search pattern
        if grep -q "$search" "$file"; then
            echo -e "${GREEN}Found in: $file${NC}"
            
            # Create backup
            cp "$file" "$BACKUP_DIR/$(basename $file).bak"
            
            # Perform replacement (macOS and Linux compatible)
            if [[ "$OSTYPE" == "darwin"* ]]; then
                # macOS
                sed -i '' "s|$search|$replace|g" "$file"
            else
                # Linux
                sed -i "s|$search|$replace|g" "$file"
            fi
            
            echo -e "${GREEN}✓ Updated: $file${NC}"
        fi
    fi
}

# Function to search and display files
search_files() {
    local pattern=$1
    echo -e "\n${YELLOW}Searching for: $pattern${NC}"
    
    # Search in Java files
    find . -type f \( -name "*.java" -o -name "*.properties" -o -name "*.yml" -o -name "*.yaml" \) \
        -not -path "*/target/*" \
        -not -path "*/build/*" \
        -not -path "*/.git/*" \
        -exec grep -l "$pattern" {} \;
}

echo -e "\n${YELLOW}Step 1: Searching for localhost references...${NC}"

# Search for all localhost patterns
echo -e "\n${GREEN}Files containing localhost:${NC}"
search_files "localhost"

echo -e "\n${YELLOW}Step 2: Updating files...${NC}"

# Update localhost:3000
echo -e "\n${YELLOW}Updating localhost:3000 references...${NC}"
for file in $(find . -type f \( -name "*.java" -o -name "*.properties" -o -name "*.yml" -o -name "*.yaml" \) \
    -not -path "*/target/*" -not -path "*/build/*" -not -path "*/.git/*"); do
    backup_and_update "$file" "http://localhost:3000" "$MAIN_VERCEL"
done

# Update localhost:5173
echo -e "\n${YELLOW}Updating localhost:5173 references...${NC}"
for file in $(find . -type f \( -name "*.java" -o -name "*.properties" -o -name "*.yml" -o -name "*.yaml" \) \
    -not -path "*/target/*" -not -path "*/build/*" -not -path "*/.git/*"); do
    backup_and_update "$file" "http://localhost:5173" "$MAIN_VERCEL"
done

# Update localhost:5174
echo -e "\n${YELLOW}Updating localhost:5174 references...${NC}"
for file in $(find . -type f \( -name "*.java" -o -name "*.properties" -o -name "*.yml" -o -name "*.yaml" \) \
    -not -path "*/target/*" -not -path "*/build/*" -not -path "*/.git/*"); do
    backup_and_update "$file" "http://localhost:5174" "$MAIN_VERCEL"
done

# Special handling for CORS configuration with multiple origins
echo -e "\n${YELLOW}Updating CORS configuration with all Vercel domains...${NC}"
for file in $(find . -type f \( -name "*.java" -o -name "*.properties" -o -name "*.yml" \) \
    -not -path "*/target/*" -not -path "*/build/*" -not -path "*/.git/*"); do
    
    if grep -q "cors.allowed.origins" "$file" || grep -q "allowedOrigins" "$file"; then
        echo -e "${GREEN}Found CORS config in: $file${NC}"
        cp "$file" "$BACKUP_DIR/$(basename $file).cors.bak"
        
        # Update the entire CORS origins line
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s|cors.allowed.origins.*|cors.allowed.origins=$VERCEL_DOMAINS|g" "$file"
            sed -i '' "s|@Value(\".*cors.allowed.origins.*\")|@Value(\"\${cors.allowed.origins:$VERCEL_DOMAINS}\")|g" "$file"
        else
            sed -i "s|cors.allowed.origins.*|cors.allowed.origins=$VERCEL_DOMAINS|g" "$file"
            sed -i "s|@Value(\".*cors.allowed.origins.*\")|@Value(\"\${cors.allowed.origins:$VERCEL_DOMAINS}\")|g" "$file"
        fi
        
        echo -e "${GREEN}✓ Updated CORS config: $file${NC}"
    fi
done

# Update OAuth2 redirect URIs
echo -e "\n${YELLOW}Updating OAuth2 redirect URIs...${NC}"
for file in $(find . -type f \( -name "*.java" -o -name "*.properties" -o -name "*.yml" \) \
    -not -path "*/target/*" -not -path "*/build/*" -not -path "*/.git/*"); do
    
    if grep -q "redirect-uri" "$file" || grep -q "redirectUri" "$file"; then
        echo -e "${GREEN}Found OAuth2 config in: $file${NC}"
        cp "$file" "$BACKUP_DIR/$(basename $file).oauth.bak"
        
        if [[ "$OSTYPE" == "darwin"* ]]; then
            sed -i '' "s|http://localhost:[0-9]*/|$MAIN_VERCEL/|g" "$file"
        else
            sed -i "s|http://localhost:[0-9]*/|$MAIN_VERCEL/|g" "$file"
        fi
        
        echo -e "${GREEN}✓ Updated OAuth2 config: $file${NC}"
    fi
done

echo -e "\n${GREEN}=== Update Complete ===${NC}"
echo -e "${YELLOW}Backups saved in: $BACKUP_DIR${NC}"
echo -e "${YELLOW}Please review the changes before committing.${NC}"

# Show summary
echo -e "\n${GREEN}=== Summary of Changes ===${NC}"
echo -e "Total backup files created: $(ls -1 $BACKUP_DIR | wc -l)"
echo -e "\n${YELLOW}To review changes:${NC}"
echo -e "git diff"
echo -e "\n${YELLOW}To restore from backup if needed:${NC}"
echo -e "cp $BACKUP_DIR/* ."

# Ask if user wants to see diff
echo -e "\n${YELLOW}Would you like to see the changes? (y/n)${NC}"
read -r response
if [[ "$response" == "y" ]]; then
    git diff
fi
