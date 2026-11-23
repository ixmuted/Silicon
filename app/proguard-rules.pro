-keep class com.silicon.rotation.SiliconApp { <init>(); }
-keep class com.silicon.rotation.MainActivity { <init>(); }
-keep class com.silicon.rotation.service.** { *; }
-keep class com.silicon.rotation.widget.** { *; }
-keep class * implements androidx.glance.appwidget.action.ActionCallback {
    public <init>();
}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { <init>(); }
-dontwarn androidx.glance.**
