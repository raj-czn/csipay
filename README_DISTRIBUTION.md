# CSIPay SoftPOS SDK Distribution Guide

## Build the SDK

```bash
./gradlew :softpos:assembleRelease
```

Output: `softpos/build/outputs/aar/softpos-release.aar`

---

## Option 1: Direct AAR Integration (Recommended)

### Distribute
Share these files with clients:
- `softpos-release.aar` (your wrapper)
- All AARs from `softpos/libs/` folder (vendor dependencies)

### Client Integration
```gradle
// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs 'libs'
        }
    }
}

// app/build.gradle
dependencies {
    implementation(name: 'softpos-release', ext: 'aar')
    implementation(name: 'ingenico-usdk-13.11.0', ext: 'aar')
    implementation(name: 'ingenico-usdk-wrapper-2.0.0', ext: 'aar')
    implementation(name: 'iPclBridge', ext: 'aar')
    implementation(name: 'PaymentSDK-3.68.1-sdi', ext: 'aar')
    implementation(name: 'PclServiceLib_2.21.02', ext: 'aar')
    implementation(name: 'PclUtilities_2.21.02', ext: 'aar')
    implementation(name: 'printsdk-3.0.0', ext: 'aar')
    implementation(name: 'rba_sdk', ext: 'aar')
    implementation(name: 'retail-types-release-22.01.06.01-0010', ext: 'aar')
    implementation(name: 'roamreaderunifiedapi-2.5.3.100-release', ext: 'aar')
    implementation(name: 'triposmobilesdk-release', ext: 'aar')
    implementation(name: 'UpdateServiceLib-0.1.293', ext: 'aar')
    implementation(name: 'ux-server-release-22.01.06.01-0010', ext: 'aar')
}
```

---

## Option 2: Maven Repository

### Publish to Local Maven (Testing)
```bash
./gradlew :softpos:publishToMavenLocal
./gradlew :softpos:publishVendorAars
```

### Publish to GitHub Packages
```bash
./gradlew :softpos:publishReleasePublicationToGitHubPackagesRepository
./gradlew :softpos:publishVendorAars
```

### Client Integration
```gradle
// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/raj-czn/softpos-sdk")
            credentials {
                username = project.findProperty("gpr.user")
                password = project.findProperty("gpr.token")
            }
        }
    }
}

// app/build.gradle
dependencies {
    implementation 'com.csipay:softpos-sdk:1.0.0'
}
```

---

## Notes

- AGP doesn't support bundling AARs inside AARs
- Clients must include all vendor AARs alongside your wrapper AAR
- For Maven distribution, vendor AARs are published separately
