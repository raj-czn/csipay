# Publish to Maven Repository

## Published Packages

**GitHub Packages:**
- `com.csipay:softpos-sdk:1.0.1` (wrapper SDK)
- 12 vendor AARs under `com.csipay.vendor` group
- `iPclBridge` requires local Maven (GitHub doesn't allow uppercase)

---

## Local Maven (Development)

### Publish Vendor AARs
```bash
./gradlew :softpos:publishVendorAarsToLocal
```

### Publish Wrapper SDK
```bash
./gradlew :softpos:publishToMavenLocal
```

---

## GitHub Packages (Production)

### Publish Vendor AARs
```bash
./publish-vendors.sh
```

### Publish Wrapper SDK
```bash
./gradlew :softpos:publishReleasePublicationToGitHubPackagesRepository
```

---

## Client Integration

### From GitHub Packages
```gradle
// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()  // For iPclBridge
        maven {
            url = uri("https://maven.pkg.github.com/raj-czn/softpos-sdk")
            credentials {
                username = providers.gradleProperty("gpr.user").getOrElse(System.getenv("GITHUB_ACTOR"))
                password = providers.gradleProperty("gpr.token").getOrElse(System.getenv("GITHUB_TOKEN"))
            }
        }
    }
}

// app/build.gradle
dependencies {
    implementation 'com.csipay:softpos-sdk:1.0.1'
}
```

**Note:** Third-party developers need:
1. GitHub token with `read:packages` scope
2. Local Maven with iPclBridge: `./gradlew :softpos:publishVendorAarsToLocal`

All 13 vendor AARs are automatically included as transitive dependencies.
