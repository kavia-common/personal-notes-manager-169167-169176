androidApplication {
    namespace = "org.example.app"

    dependencies {
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.cardview:cardview:1.0.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
        implementation("androidx.activity:activity-ktx:1.9.2")
        implementation("androidx.fragment:fragment-ktx:1.8.3")
        implementation("androidx.core:core-ktx:1.13.1")

        // Persistence uses SQLiteOpenHelper (no annotation processing needed)
        implementation("androidx.legacy:legacy-support-v4:1.0.0")
    }
}
