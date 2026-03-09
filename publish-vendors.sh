#!/bin/bash

# Publish all vendor AARs to GitHub Packages

REPO_URL="https://maven.pkg.github.com/raj-czn/softpos-sdk"
GROUP_ID="com.csipay.vendor"
VERSION="1.0"

# Read credentials
GPR_USER=$(grep "gpr.user=" ~/.gradle/gradle.properties | cut -d'=' -f2)
GPR_TOKEN=$(grep "gpr.token=" ~/.gradle/gradle.properties | cut -d'=' -f2)

if [ -z "$GPR_USER" ] || [ -z "$GPR_TOKEN" ]; then
    echo "Error: GitHub credentials not found in ~/.gradle/gradle.properties"
    exit 1
fi

cd "$(dirname "$0")/softpos/libs"

for aar in *.aar; do
    ARTIFACT_ID="${aar%.aar}"
    echo "Publishing $ARTIFACT_ID..."
    
    curl -X PUT \
        -u "$GPR_USER:$GPR_TOKEN" \
        -H "Content-Type: application/octet-stream" \
        --data-binary "@$aar" \
        "$REPO_URL/$GROUP_ID/${ARTIFACT_ID//.//}/$VERSION/$ARTIFACT_ID-$VERSION.aar"
    
    # Create and upload POM
    POM="<project xmlns=\"http://maven.apache.org/POM/4.0.0\">
  <modelVersion>4.0.0</modelVersion>
  <groupId>$GROUP_ID</groupId>
  <artifactId>$ARTIFACT_ID</artifactId>
  <version>$VERSION</version>
  <packaging>aar</packaging>
</project>"
    
    echo "$POM" | curl -X PUT \
        -u "$GPR_USER:$GPR_TOKEN" \
        -H "Content-Type: application/xml" \
        --data-binary @- \
        "$REPO_URL/$GROUP_ID/${ARTIFACT_ID//.//}/$VERSION/$ARTIFACT_ID-$VERSION.pom"
    
    echo "✓ Published $ARTIFACT_ID"
done

echo "All vendor AARs published to GitHub Packages"
