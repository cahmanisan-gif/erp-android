-keepattributes JavascriptInterface
-keepclassmembers class com.rajavavapor.app.WebAppInterface {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.rajavavapor.app.WebAppInterface { *; }

# Data models (Gson reflection)
-keep class com.rajavavapor.app.data.** { *; }

# ML Kit Barcode
-keep class com.google.mlkit.** { *; }
