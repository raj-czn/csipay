# CSIPay SDK public API
-keep class com.csipay.softpos.SoftposClient { *; }
-keep class com.csipay.softpos.config.SoftposConfig { *; }
-keep class com.csipay.softpos.config.SoftposConfig$Builder { *; }
-keep class com.csipay.softpos.config.Environment { *; }
-keep class com.csipay.softpos.api.** { *; }
-keep class com.csipay.softpos.transaction.TransactionResult { *; }
-keep class com.csipay.softpos.transaction.TransactionType { *; }
-keep class com.csipay.softpos.transaction.TransactionStatus { *; }

# Vendor SDK classes
-keep class com.vantiv.** { *; }
-keep class com.tripos.** { *; }
-keep class com.ingenico.** { *; }
-keep class com.verifone.** { *; }
-keep class com.roam.** { *; }
-keep class io.realm.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}