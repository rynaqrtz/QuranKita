# QuranKita R8 rules
#
# Compose + Room + Navigation all ship their own consumer rules, so the defaults
# are almost enough. These are the reflective entry points in this app that R8
# cannot see on its own.

# ViewModels are instantiated by name through ViewModelProvider.
-keep class * extends androidx.lifecycle.ViewModel { <init>(); }
-keep class * extends androidx.lifecycle.AndroidViewModel { <init>(android.app.Application); }

# Room generates the DAO/Database implementations at build time and loads them
# reflectively by name.
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Everything referenced from the manifest (activities, receivers, services,
# providers) must survive shrinking.
-keep class com.example.MainActivity { *; }
-keep class * extends android.content.BroadcastReceiver { *; }
-keep class * extends android.app.Service { *; }
-keep class * extends android.content.ContentProvider { *; }

# Keep enum values used with valueOf()/entries in theme + audio settings.
-keepclassmembers enum * { *; }

# Coroutines / debug metadata that R8 warns about but does not need.
-dontwarn kotlinx.coroutines.**
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
