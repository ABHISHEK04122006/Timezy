# Proguard rules for Countnxt Widget

# Room Database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
-keep class com.timezy.widget.data.database.** { *; }
-keep class com.timezy.widget.data.model.** { *; }

# DataStore Preferences
-keep class androidx.datastore.preferences.protobuf.** { *; }

# Widget Providers & RemoteViews Helpers
-keep class com.timezy.widget.widget.** { *; }
-keep class com.timezy.widget.notifications.** { *; }

# Preserve View constructors for layout inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
