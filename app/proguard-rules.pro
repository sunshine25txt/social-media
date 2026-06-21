# Focus Shield ProGuard Rules

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.focusshield.app.data.local.entity.** { *; }
-keep class com.focusshield.app.service.accessibility.SelectorConfig { *; }
-keep class com.focusshield.app.service.accessibility.SelectorEntry { *; }

# Compose
-dontwarn androidx.compose.**

# Vico Charts
-keep class com.patrykandpatrick.vico.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep accessibility service
-keep class com.focusshield.app.service.accessibility.FocusShieldAccessibilityService { *; }
