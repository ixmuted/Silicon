-keep class com.silicon.SiliconApp { <init>(); }
-keep class com.silicon.MainActivity { <init>(); }
-keep class com.silicon.service.** { *; }
-keep class com.silicon.widget.** { *; }
-keep class * implements androidx.glance.appwidget.action.ActionCallback {
    public <init>();
}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { <init>(); }
-dontwarn androidx.glance.**
