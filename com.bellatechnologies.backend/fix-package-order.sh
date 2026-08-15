#!/bin/bash
set -e

echo "============================================"
echo "Fixing package declaration order..."
echo "============================================"

# Function to fix a file's package/import order
fix_file() {
    local file=$1
    if [ ! -f "$file" ]; then
        return
    fi
    
    echo "Fixing $(basename $file)..."
    
    # Create temp file with correct order
    {
        # 1. First, extract package line
        grep "^package " "$file" | head -1
        echo ""
        
        # 2. Then all imports (sorted and unique)
        grep "^import " "$file" | sort -u
        echo ""
        
        # 3. Then everything else (skip package and import lines)
        grep -v "^package " "$file" | grep -v "^import " | grep -v "^// ============" | grep -v "^// File:"
        
    } > "${file}.fixed"
    
    mv "${file}.fixed" "$file"
}

# Fix all Java files in service, repository, and config
find src/main/java/com/bellatechnologies/backend/{service,repository,config} -name "*.java" -type f | while read file; do
    fix_file "$file"
done

echo ""
echo "✓ All files fixed"
echo ""
echo "============================================"
echo "Testing compilation..."
echo "============================================"
echo ""

mvn clean compile -DskipTests

