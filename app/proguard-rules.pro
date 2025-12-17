# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\DRIVE bkp\Safatware\Android studio\adt_bundle_windows_x86_64_20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#-dontwarn retrofit.**
#-keep public class com.mayur.personalitydevelopment.base.**
#-keep public class com.mayur.personalitydevelopment.models.**
#-keep public class com.mayur.personalitydevelopment.Connection.**
#-keep class com.google.gson.** { *; }
#-keep class com.google.inject.** { *; }
#-keep class org.apache.http.** { *; }
#-keep class org.apache.james.mime4j.** { *; }
#-keep class javax.inject.** { *; }
#-keep class retrofit.** { *; }

#-keep interface retrofit.** { *; }

#==================================================
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class sun.misc.Unsafe { *; }
#
#-keep class com.mayur.personalitydevelopment.models.** { *; }
#-keep class com.mayur.personalitydevelopment.Utils.Utils { *; }
##-keep public class com.mayur.personalitydevelopment.Connection.**
#-keep class org.jsoup.**{*;}
#
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#-keep class com.google.android.gms.ads.** { *; }
#-keep class com.google.ads.** { *; }

