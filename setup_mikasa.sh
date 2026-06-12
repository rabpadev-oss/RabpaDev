#!/bin/sh
# ================================================================
# Mikasa IG Creat - Auto Setup Script
# Jalankan dari dalam folder ~/rabpadev
# ================================================================

BASE="$HOME/rabpadev"
cd "$BASE" || { echo "ERROR: folder ~/rabpadev tidak ditemukan!"; exit 1; }

echo "=== Mikasa IG Creat Setup Script ==="
echo "Working dir: $(pwd)"

# ── Buat direktori ──────────────────────────────────────────────
mkdir -p app/src/main/kotlin/com/rabpadev/app
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/res/menu
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/xml
echo "[1/12] Direktori siap"

# ── AndroidManifest.xml ─────────────────────────────────────────
cat > app/src/main/AndroidManifest.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="28" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MikasaIG"
        android:usesCleartextTraffic="false"
        android:networkSecurityConfig="@xml/network_security_config">
        <activity android:name=".SplashActivity" android:exported="true"
            android:theme="@style/Theme.MikasaIG.Splash" android:noHistory="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".MainActivity" android:exported="false"
            android:configChanges="orientation|screenSize|keyboardHidden"
            android:windowSoftInputMode="adjustResize" />
        <activity android:name=".SettingsActivity" android:exported="false"
            android:windowSoftInputMode="adjustResize" />
        <activity android:name=".AuthenticatorActivity" android:exported="false" />
        <provider android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false" android:grantUriPermissions="true">
            <meta-data android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
    </application>
</manifest>
EOF
echo "[2/12] AndroidManifest.xml"

# ── build.gradle.kts ────────────────────────────────────────────
cat > app/build.gradle.kts << 'EOF'
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}
android {
    namespace = "com.rabpadev.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.rabpadev.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
    buildFeatures { viewBinding = true }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
EOF
echo "[3/12] build.gradle.kts"

# ── gradle.properties ───────────────────────────────────────────
cat > gradle.properties << 'EOF'
android.useAndroidX=true
android.enableJetifier=true
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
kotlin.code.style=official
EOF
echo "[4/12] gradle.properties"

# ── strings.xml ─────────────────────────────────────────────────
cat > app/src/main/res/values/strings.xml << 'EOF'
<resources>
    <string name="app_name">Mikasa IG Creat</string>
    <string name="menu_home">Home</string>
    <string name="menu_email">Email</string>
    <string name="menu_a2f">A2F</string>
    <string name="menu_refresh">Refresh</string>
    <string name="menu_autofill">Auto Fill</string>
    <string name="menu_authenticator">Authenticator</string>
    <string name="menu_settings">Settings</string>
    <string name="menu_clear_data">Clear Data</string>
    <string name="exit_title">Keluar Aplikasi</string>
    <string name="exit_message">Apakah Anda yakin ingin keluar?</string>
    <string name="clear_data_title">Hapus Data</string>
    <string name="clear_data_message">Hapus semua data Instagram yang tersimpan?</string>
    <string name="clear_data_success">Data berhasil dihapus</string>
    <string name="autofill_title">Auto Fill Data</string>
    <string name="autofill_hint_name">Nama Lengkap</string>
    <string name="autofill_hint_username">Username Instagram</string>
    <string name="autofill_hint_email">Email</string>
    <string name="autofill_hint_phone">Nomor HP</string>
    <string name="autofill_hint_birthday">Tanggal Lahir (DD/MM/YYYY)</string>
    <string name="autofill_hint_password">Password</string>
    <string name="autofill_btn_fill">Isi Otomatis</string>
    <string name="autofill_btn_save">Simpan Profil</string>
    <string name="autofill_saved">Profil berhasil disimpan!</string>
    <string name="autofill_filled">Form berhasil diisi!</string>
    <string name="autofill_empty">Isi minimal nama dan email dulu.</string>
    <string name="yes">Ya</string>
    <string name="no">Tidak</string>
    <string name="cancel">Batal</string>
    <string name="ok">OK</string>
    <string name="permission_denied">Izin akses penyimpanan ditolak</string>
    <string name="splash_title">Mikasa IG Creat</string>
    <string name="splash_subtitle">by : mas ochid</string>
    <string name="settings_title">Settings</string>
    <string name="settings_saved">Pengaturan disimpan</string>
    <string name="settings_clear_cookies">Clear Cookies</string>
    <string name="settings_clear_cache">Clear Cache</string>
    <string name="settings_clear_session">Clear Session</string>
    <string name="photo_selected">Foto profil dipilih</string>
    <string name="cookies_cleared">Cookies berhasil dihapus</string>
    <string name="cache_cleared">Cache berhasil dihapus</string>
    <string name="session_cleared">Session berhasil dihapus</string>
    <string name="authenticator_title">Authenticator</string>
    <string name="authenticator_add">Tambah Akun</string>
    <string name="authenticator_invalid_secret">Secret key tidak valid</string>
    <string name="authenticator_added">Akun authenticator ditambahkan</string>
    <string name="authenticator_deleted">Akun dihapus</string>
    <string name="authenticator_empty">Belum ada akun.\nTap + untuk menambah.</string>
    <string name="authenticator_copy">Kode disalin ke clipboard</string>
    <string name="delete_confirm">Hapus akun ini?</string>
</resources>
EOF
echo "[5/12] strings.xml"

# ── colors.xml ──────────────────────────────────────────────────
cat > app/src/main/res/values/colors.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary">#1A1A2E</color>
    <color name="primary_variant">#16213E</color>
    <color name="secondary">#00BFFF</color>
    <color name="secondary_variant">#0080FF</color>
    <color name="background">#0A0A12</color>
    <color name="surface">#12121E</color>
    <color name="surface_variant">#1E1E30</color>
    <color name="on_primary">#FFFFFF</color>
    <color name="on_secondary">#FFFFFF</color>
    <color name="on_background">#E0E8FF</color>
    <color name="on_surface">#E0E8FF</color>
    <color name="progress_color">#00BFFF</color>
    <color name="status_bar">#0A0A12</color>
    <color name="white">#FFFFFF</color>
    <color name="black">#000000</color>
    <color name="neon_blue">#00BFFF</color>
    <color name="totp_green">#00E676</color>
    <color name="totp_red">#FF5252</color>
    <color name="totp_yellow">#FFD740</color>
    <color name="card_bg">#161625</color>
    <color name="divider">#2A2A40</color>
</resources>
EOF
echo "[6/12] colors.xml"

# ── themes.xml ──────────────────────────────────────────────────
cat > app/src/main/res/values/themes.xml << 'EOF'
<resources>
    <style name="Theme.MikasaIG" parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryVariant">@color/primary_variant</item>
        <item name="colorOnPrimary">@color/on_primary</item>
        <item name="colorSecondary">@color/secondary</item>
        <item name="colorSecondaryVariant">@color/secondary_variant</item>
        <item name="colorOnSecondary">@color/on_secondary</item>
        <item name="android:colorBackground">@color/background</item>
        <item name="colorSurface">@color/surface</item>
        <item name="colorOnBackground">@color/on_background</item>
        <item name="colorOnSurface">@color/on_surface</item>
        <item name="android:statusBarColor">@color/status_bar</item>
        <item name="android:navigationBarColor">@color/background</item>
        <item name="android:windowLightStatusBar">false</item>
        <item name="android:windowLightNavigationBar">false</item>
    </style>
    <style name="Theme.MikasaIG.Splash" parent="Theme.MikasaIG">
        <item name="android:windowFullscreen">true</item>
    </style>
    <style name="ThemeOverlay.MikasaIG.AppBar" parent="ThemeOverlay.Material3.Dark">
        <item name="android:background">@color/surface</item>
        <item name="colorOnSurface">@color/on_surface</item>
    </style>
    <style name="ThemeOverlay.MikasaIG.Popup" parent="ThemeOverlay.Material3.Dark">
        <item name="android:background">@color/surface_variant</item>
    </style>
</resources>
EOF
echo "[7/12] themes.xml"

# ── xml resources ───────────────────────────────────────────────
cat > app/src/main/res/xml/network_security_config.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">instagram.com</domain>
        <domain includeSubdomains="true">cdninstagram.com</domain>
        <domain includeSubdomains="true">facebook.com</domain>
        <domain includeSubdomains="true">fbcdn.net</domain>
    </domain-config>
</network-security-config>
EOF

cat > app/src/main/res/xml/file_paths.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external_files" path="." />
    <cache-path name="cache_files" path="." />
    <files-path name="internal_files" path="." />
</paths>
EOF
echo "[8/12] XML resources"

# ── menu_main.xml ───────────────────────────────────────────────
cat > app/src/main/res/menu/menu_main.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item android:id="@+id/action_home" android:icon="@drawable/ic_home" android:title="Home" app:showAsAction="always"/>
    <item android:id="@+id/action_email" android:icon="@drawable/ic_email" android:title="Email" app:showAsAction="always"/>
    <item android:id="@+id/action_a2f" android:icon="@drawable/ic_lock" android:title="A2F" app:showAsAction="always"/>
    <item android:id="@+id/action_refresh" android:icon="@drawable/ic_refresh" android:title="Refresh" app:showAsAction="always"/>
    <item android:id="@+id/action_autofill" android:icon="@drawable/ic_autofill" android:title="Auto Fill" app:showAsAction="always"/>
    <item android:id="@+id/action_authenticator" android:icon="@drawable/ic_otp" android:title="Authenticator" app:showAsAction="never"/>
    <item android:id="@+id/action_settings" android:icon="@drawable/ic_settings" android:title="Settings" app:showAsAction="never"/>
    <item android:id="@+id/action_clear_data" android:icon="@drawable/ic_delete" android:title="Clear Data" app:showAsAction="never"/>
</menu>
EOF
echo "[9/12] menu_main.xml"

# ── Drawables ───────────────────────────────────────────────────
cat > app/src/main/res/drawable/ic_home.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M10,20v-6h4v6h5v-8h3L12,3 2,12h3v8z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_email.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M20,4L4,4c-1.1,0 -1.99,0.9 -1.99,2L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2L22,6c0,-1.1 -0.9,-2 -2,-2zM20,8l-8,5 -8,-5L4,6l8,5 8,-5v2z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_lock.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M18,8h-1L17,6c0,-2.76 -2.24,-5 -5,-5S7,3.24 7,6v2L6,8c-1.1,0 -2,0.9 -2,2v10c0,1.1 0.9,2 2,2h12c1.1,0 2,-0.9 2,-2L20,10c0,-1.1 -0.9,-2 -2,-2zM12,17c-1.1,0 -2,-0.9 -2,-2s0.9,-2 2,-2 2,0.9 2,2 -0.9,2 -2,2zM15.1,8L8.9,8L8.9,6c0,-1.71 1.39,-3.1 3.1,-3.1 1.71,0 3.1,1.39 3.1,3.1v2z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_refresh.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M17.65,6.35C16.2,4.9 14.21,4 12,4c-4.42,0 -7.99,3.58 -7.99,8s3.57,8 7.99,8c3.73,0 6.84,-2.55 7.73,-6h-2.08c-0.82,2.33 -3.04,4 -5.65,4 -3.31,0 -6,-2.69 -6,-6s2.69,-6 6,-6c1.66,0 3.14,0.69 4.22,1.78L13,11h7V4l-2.35,2.35z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_autofill.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M17,12h-5v5h5v-5zM16,1v2L8,3V1L6,1v2L5,3C3.89,3 3.01,3.9 3.01,5L3,19c0,1.1 0.89,2 2,2h14c1.1,0 2,-0.9 2,-2V5c0,-1.1 -0.9,-2 -2,-2h-1V1h-2zM19,19L5,19V8h14v11z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_delete.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M6,19c0,1.1 0.9,2 2,2h8c1.1,0 2,-0.9 2,-2V7H6v12zM19,4h-3.5l-1,-1h-5l-1,1H5v2h14V4z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_add.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M19,13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_settings.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M19.14,12.94c0.04,-0.3 0.06,-0.61 0.06,-0.94c0,-0.32 -0.02,-0.64 -0.07,-0.94l2.03,-1.58c0.18,-0.14 0.23,-0.41 0.12,-0.61l-1.92,-3.32c-0.12,-0.22 -0.37,-0.29 -0.59,-0.22l-2.39,0.96c-0.5,-0.38 -1.03,-0.7 -1.62,-0.94L14.4,2.81c-0.04,-0.24 -0.24,-0.41 -0.48,-0.41h-3.84c-0.24,0 -0.43,0.17 -0.47,0.41L9.25,5.35C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33c-0.22,-0.08 -0.47,0 -0.59,0.22L2.74,8.87C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58C4.84,11.36 4.8,11.69 4.8,12s0.02,0.64 0.07,0.94l-2.03,1.58c-0.18,0.14 -0.23,0.41 -0.12,0.61l1.92,3.32c0.12,0.22 0.37,0.29 0.59,0.22l2.39,-0.96c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54c0.05,0.24 0.24,0.41 0.48,0.41h3.84c0.24,0 0.44,-0.17 0.47,-0.41l0.36,-2.54c0.59,-0.24 1.13,-0.56 1.62,-0.94l2.39,0.96c0.22,0.08 0.47,0 0.59,-0.22l1.92,-3.32c0.12,-0.22 0.07,-0.47 -0.12,-0.61L19.14,12.94zM12,15.6c-1.98,0 -3.6,-1.62 -3.6,-3.6s1.62,-3.6 3.6,-3.6s3.6,1.62 3.6,3.6S13.98,15.6 12,15.6z"/></vector>
EOF
cat > app/src/main/res/drawable/ic_otp.xml << 'EOF'
<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24" android:tint="@color/on_primary"><path android:fillColor="@android:color/white" android:pathData="M11.5,2C6.81,2 3,5.81 3,10.5S6.81,19 11.5,19h0.5v3c4.86,-2.34 8,-7 8,-11.5C20,5.81 16.19,2 11.5,2zM12.5,16.5h-2v-2h2V16.5zM12.5,13h-2C10.5,9.5 7.5,10 7.5,7.5C7.5,5.57 9.07,4 11,4s3.5,1.57 3.5,3.5C14.5,10 11.5,9.5 12.5,13z"/></vector>
EOF
echo "[10/12] Drawables"

# ── Layouts ─────────────────────────────────────────────────────
cat > app/src/main/res/layout/activity_splash.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="match_parent"
    android:background="@color/background">
    <ImageView android:id="@+id/ivSplashIcon"
        android:layout_width="120dp" android:layout_height="120dp"
        android:src="@mipmap/ic_launcher" android:scaleType="fitCenter"
        android:layout_marginBottom="24dp"
        app:layout_constraintBottom_toTopOf="@id/tvSplashTitle"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" app:layout_constraintVertical_chainStyle="packed"/>
    <TextView android:id="@+id/tvSplashTitle"
        android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:text="@string/splash_title" android:textColor="@color/neon_blue"
        android:textSize="28sp" android:textStyle="bold" android:letterSpacing="0.05"
        android:layout_marginBottom="8dp"
        app:layout_constraintBottom_toTopOf="@id/tvSplashSubtitle"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/ivSplashIcon"/>
    <TextView android:id="@+id/tvSplashSubtitle"
        android:layout_width="wrap_content" android:layout_height="wrap_content"
        android:text="@string/splash_subtitle" android:textColor="@color/on_background"
        android:textSize="16sp" android:alpha="0.7"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvSplashTitle"/>
</androidx.constraintlayout.widget.ConstraintLayout>
EOF

cat > app/src/main/res/layout/activity_main.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="match_parent">
    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appBarLayout" android:layout_width="match_parent"
        android:layout_height="wrap_content" android:theme="@style/ThemeOverlay.MikasaIG.AppBar">
        <com.google.android.material.appbar.MaterialToolbar android:id="@+id/toolbar"
            android:layout_width="match_parent" android:layout_height="?attr/actionBarSize"
            app:popupTheme="@style/ThemeOverlay.MikasaIG.Popup"/>
        <com.google.android.material.progressindicator.LinearProgressIndicator
            android:id="@+id/progressBar" android:layout_width="match_parent"
            android:layout_height="wrap_content" android:visibility="gone"
            app:trackColor="@android:color/transparent"
            app:indicatorColor="@color/progress_color" app:trackThickness="3dp"/>
    </com.google.android.material.appbar.AppBarLayout>
    <WebView android:id="@+id/webView" android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"/>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
EOF

cat > app/src/main/res/layout/activity_authenticator.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="match_parent"
    android:background="@color/background">
    <com.google.android.material.appbar.AppBarLayout android:layout_width="match_parent"
        android:layout_height="wrap_content" android:theme="@style/ThemeOverlay.MikasaIG.AppBar">
        <com.google.android.material.appbar.MaterialToolbar android:id="@+id/toolbar"
            android:layout_width="match_parent" android:layout_height="?attr/actionBarSize"
            app:title="Authenticator" app:navigationIconTint="@color/on_primary"/>
    </com.google.android.material.appbar.AppBarLayout>
    <LinearLayout android:layout_width="match_parent" android:layout_height="match_parent"
        android:orientation="vertical" app:layout_behavior="@string/appbar_scrolling_view_behavior">
        <TextView android:id="@+id/tvEmpty" android:layout_width="match_parent"
            android:layout_height="0dp" android:layout_weight="1" android:gravity="center"
            android:text="@string/authenticator_empty" android:textColor="@color/on_surface"
            android:textSize="15sp" android:alpha="0.5" android:visibility="gone" android:padding="32dp"/>
        <androidx.recyclerview.widget.RecyclerView android:id="@+id/recyclerView"
            android:layout_width="match_parent" android:layout_height="0dp"
            android:layout_weight="1" android:padding="8dp" android:clipToPadding="false"/>
    </LinearLayout>
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAdd" android:layout_width="wrap_content"
        android:layout_height="wrap_content" android:layout_gravity="bottom|end"
        android:layout_margin="16dp" android:contentDescription="Add"
        app:srcCompat="@drawable/ic_add" app:tint="@color/white"/>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
EOF

cat > app/src/main/res/layout/item_totp.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="wrap_content"
    android:layout_margin="6dp" app:cardBackgroundColor="@color/card_bg"
    app:cardCornerRadius="12dp" app:strokeColor="@color/divider" app:strokeWidth="1dp">
    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
        android:orientation="vertical" android:padding="16dp">
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
            android:orientation="horizontal" android:gravity="center_vertical" android:layout_marginBottom="8dp">
            <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                android:layout_weight="1" android:orientation="vertical">
                <TextView android:id="@+id/tvAccountName" android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:textColor="@color/neon_blue"
                    android:textSize="14sp" android:textStyle="bold" android:text="Account"/>
                <TextView android:id="@+id/tvTotpCode" android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:textColor="@color/totp_green"
                    android:textSize="32sp" android:textStyle="bold" android:letterSpacing="0.2"
                    android:text="000 000" android:layout_marginTop="4dp"/>
            </LinearLayout>
            <LinearLayout android:layout_width="wrap_content" android:layout_height="wrap_content"
                android:orientation="vertical" android:gravity="center_horizontal">
                <com.google.android.material.progressindicator.CircularProgressIndicator
                    android:id="@+id/progressCountdown" android:layout_width="44dp"
                    android:layout_height="44dp" app:trackThickness="4dp"
                    app:indicatorSize="44dp" app:indicatorColor="@color/totp_green"
                    app:trackColor="@color/divider" android:layout_marginBottom="4dp"/>
                <TextView android:id="@+id/tvCountdown" android:layout_width="wrap_content"
                    android:layout_height="wrap_content" android:textColor="@color/on_surface"
                    android:textSize="13sp" android:text="30s"/>
            </LinearLayout>
        </LinearLayout>
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
            android:orientation="horizontal">
            <com.google.android.material.button.MaterialButton android:id="@+id/btnCopy"
                style="@style/Widget.Material3.Button.OutlinedButton"
                android:layout_width="0dp" android:layout_height="wrap_content"
                android:layout_weight="1" android:layout_marginEnd="8dp" android:text="Copy"/>
            <com.google.android.material.button.MaterialButton android:id="@+id/btnDelete"
                style="@style/Widget.Material3.Button.OutlinedButton"
                android:layout_width="wrap_content" android:layout_height="wrap_content"
                android:text="Hapus" app:strokeColor="@color/totp_red" android:textColor="@color/totp_red"/>
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
EOF

cat > app/src/main/res/layout/dialog_add_totp.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="wrap_content"
    android:orientation="vertical" android:padding="16dp">
    <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilTotpName"
        style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
        android:layout_width="match_parent" android:layout_height="wrap_content"
        android:layout_marginBottom="12dp" android:hint="Nama Akun (misal: Instagram)">
        <com.google.android.material.textfield.TextInputEditText android:id="@+id/etTotpName"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:inputType="text" android:imeOptions="actionNext"/>
    </com.google.android.material.textfield.TextInputLayout>
    <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilTotpSecret"
        style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
        android:layout_width="match_parent" android:layout_height="wrap_content"
        android:hint="Secret Key (Base32)">
        <com.google.android.material.textfield.TextInputEditText android:id="@+id/etTotpSecret"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:inputType="textVisiblePassword" android:fontFamily="monospace"
            android:imeOptions="actionDone"/>
    </com.google.android.material.textfield.TextInputLayout>
</LinearLayout>
EOF

cat > app/src/main/res/layout/dialog_autofill.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="wrap_content" android:padding="8dp">
    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
        android:orientation="vertical" android:padding="8dp">
        <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilName"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:layout_marginBottom="12dp" android:hint="Nama Lengkap"
            app:startIconDrawable="@drawable/ic_autofill">
            <com.google.android.material.textfield.TextInputEditText android:id="@+id/etName"
                android:layout_width="match_parent" android:layout_height="wrap_content"
                android:inputType="textPersonName" android:imeOptions="actionNext"/>
        </com.google.android.material.textfield.TextInputLayout>
        <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilUsername"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:layout_marginBottom="12dp" android:hint="Username Instagram"
            app:startIconDrawable="@drawable/ic_home">
            <com.google.android.material.textfield.TextInputEditText android:id="@+id/etUsername"
                android:layout_width="match_parent" android:layout_height="wrap_content"
                android:inputType="text" android:imeOptions="actionNext"/>
        </com.google.android.material.textfield.TextInputLayout>
        <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilEmail"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:layout_marginBottom="12dp" android:hint="Email"
            app:startIconDrawable="@drawable/ic_email">
            <com.google.android.material.textfield.TextInputEditText android:id="@+id/etEmail"
                android:layout_width="match_parent" android:layout_height="wrap_content"
                android:inputType="textEmailAddress" android:imeOptions="actionNext"/>
        </com.google.android.material.textfield.TextInputLayout>
        <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilPhone"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:layout_marginBottom="12dp" android:hint="Nomor HP">
            <com.google.android.material.textfield.TextInputEditText android:id="@+id/etPhone"
                android:layout_width="match_parent" android:layout_height="wrap_content"
                android:inputType="phone" android:imeOptions="actionNext"/>
        </com.google.android.material.textfield.TextInputLayout>
        <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilBirthday"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:layout_marginBottom="12dp" android:hint="Tanggal Lahir (DD/MM/YYYY)">
            <com.google.android.material.textfield.TextInputEditText android:id="@+id/etBirthday"
                android:layout_width="match_parent" android:layout_height="wrap_content"
                android:inputType="date" android:imeOptions="actionNext"/>
        </com.google.android.material.textfield.TextInputLayout>
        <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilPassword"
            style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
            android:layout_width="match_parent" android:layout_height="wrap_content"
            android:layout_marginBottom="8dp" android:hint="Password"
            app:endIconMode="password_toggle" app:startIconDrawable="@drawable/ic_lock">
            <com.google.android.material.textfield.TextInputEditText android:id="@+id/etPassword"
                android:layout_width="match_parent" android:layout_height="wrap_content"
                android:inputType="textPassword" android:imeOptions="actionDone"/>
        </com.google.android.material.textfield.TextInputLayout>
    </LinearLayout>
</ScrollView>
EOF

cat > app/src/main/res/layout/activity_settings.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" android:layout_height="match_parent"
    android:background="@color/background">
    <com.google.android.material.appbar.AppBarLayout android:layout_width="match_parent"
        android:layout_height="wrap_content" android:theme="@style/ThemeOverlay.MikasaIG.AppBar">
        <com.google.android.material.appbar.MaterialToolbar android:id="@+id/toolbar"
            android:layout_width="match_parent" android:layout_height="?attr/actionBarSize"
            app:title="Settings" app:navigationIconTint="@color/on_primary"/>
    </com.google.android.material.appbar.AppBarLayout>
    <ScrollView android:layout_width="match_parent" android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
        <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
            android:orientation="vertical" android:padding="16dp">
            <com.google.android.material.card.MaterialCardView android:layout_width="match_parent"
                android:layout_height="wrap_content" android:layout_marginBottom="12dp"
                app:cardBackgroundColor="@color/card_bg" app:cardCornerRadius="12dp"
                app:strokeColor="@color/divider" app:strokeWidth="1dp">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                    android:orientation="vertical" android:padding="16dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                        android:text="Password Autofill" android:textColor="@color/neon_blue"
                        android:textSize="13sp" android:textStyle="bold" android:layout_marginBottom="12dp"/>
                    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:orientation="horizontal" android:gravity="center_vertical"
                        android:layout_marginBottom="12dp">
                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Enable Password Autofill" android:textColor="@color/on_surface" android:textSize="15sp"/>
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Isi password otomatis pada form login"
                                android:textColor="@color/on_surface" android:textSize="12sp" android:alpha="0.6"/>
                        </LinearLayout>
                        <com.google.android.material.switchmaterial.SwitchMaterial
                            android:id="@+id/switchPasswordAutofill"
                            android:layout_width="wrap_content" android:layout_height="wrap_content"/>
                    </LinearLayout>
                    <com.google.android.material.textfield.TextInputLayout android:id="@+id/tilDefaultPassword"
                        style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                        android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:hint="Default Password" app:endIconMode="password_toggle">
                        <com.google.android.material.textfield.TextInputEditText android:id="@+id/etDefaultPassword"
                            android:layout_width="match_parent" android:layout_height="wrap_content"
                            android:inputType="textPassword"/>
                    </com.google.android.material.textfield.TextInputLayout>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
            <com.google.android.material.card.MaterialCardView android:layout_width="match_parent"
                android:layout_height="wrap_content" android:layout_marginBottom="12dp"
                app:cardBackgroundColor="@color/card_bg" app:cardCornerRadius="12dp"
                app:strokeColor="@color/divider" app:strokeWidth="1dp">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                    android:orientation="vertical" android:padding="16dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                        android:text="Auto Profile Picture" android:textColor="@color/neon_blue"
                        android:textSize="13sp" android:textStyle="bold" android:layout_marginBottom="12dp"/>
                    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:orientation="horizontal" android:gravity="center_vertical"
                        android:layout_marginBottom="12dp">
                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Enable Auto Profile Picture"
                                android:textColor="@color/on_surface" android:textSize="15sp"/>
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Upload foto profil otomatis"
                                android:textColor="@color/on_surface" android:textSize="12sp" android:alpha="0.6"/>
                        </LinearLayout>
                        <com.google.android.material.switchmaterial.SwitchMaterial
                            android:id="@+id/switchAutoProfile"
                            android:layout_width="wrap_content" android:layout_height="wrap_content"/>
                    </LinearLayout>
                    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:orientation="horizontal" android:gravity="center_vertical">
                        <ImageView android:id="@+id/ivProfilePreview"
                            android:layout_width="56dp" android:layout_height="56dp"
                            android:scaleType="centerCrop" android:background="@color/surface_variant"
                            android:layout_marginEnd="12dp"/>
                        <com.google.android.material.button.MaterialButton android:id="@+id/btnChoosePhoto"
                            style="@style/Widget.Material3.Button.OutlinedButton"
                            android:layout_width="wrap_content" android:layout_height="wrap_content"
                            android:text="Pilih Foto Profil"/>
                    </LinearLayout>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
            <com.google.android.material.card.MaterialCardView android:layout_width="match_parent"
                android:layout_height="wrap_content" android:layout_marginBottom="12dp"
                app:cardBackgroundColor="@color/card_bg" app:cardCornerRadius="12dp"
                app:strokeColor="@color/divider" app:strokeWidth="1dp">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                    android:orientation="vertical" android:padding="16dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                        android:text="Authenticator" android:textColor="@color/neon_blue"
                        android:textSize="13sp" android:textStyle="bold" android:layout_marginBottom="8dp"/>
                    <com.google.android.material.button.MaterialButton android:id="@+id/btnOpenAuthenticator"
                        style="@style/Widget.Material3.Button.OutlinedButton"
                        android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:text="Kelola Authenticator Accounts"/>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
            <com.google.android.material.card.MaterialCardView android:layout_width="match_parent"
                android:layout_height="wrap_content" android:layout_marginBottom="12dp"
                app:cardBackgroundColor="@color/card_bg" app:cardCornerRadius="12dp"
                app:strokeColor="@color/divider" app:strokeWidth="1dp">
                <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content"
                    android:orientation="vertical" android:padding="16dp">
                    <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                        android:text="Clear Data" android:textColor="@color/neon_blue"
                        android:textSize="13sp" android:textStyle="bold" android:layout_marginBottom="8dp"/>
                    <com.google.android.material.button.MaterialButton android:id="@+id/btnClearCookies"
                        style="@style/Widget.Material3.Button.OutlinedButton"
                        android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:layout_marginBottom="8dp" android:text="Clear Cookies"/>
                    <com.google.android.material.button.MaterialButton android:id="@+id/btnClearCache"
                        style="@style/Widget.Material3.Button.OutlinedButton"
                        android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:layout_marginBottom="8dp" android:text="Clear Cache"/>
                    <com.google.android.material.button.MaterialButton android:id="@+id/btnClearSession"
                        style="@style/Widget.Material3.Button.OutlinedButton"
                        android:layout_width="match_parent" android:layout_height="wrap_content"
                        android:text="Clear Session"/>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
            <com.google.android.material.button.MaterialButton android:id="@+id/btnSaveSettings"
                android:layout_width="match_parent" android:layout_height="wrap_content"
                android:layout_marginTop="8dp" android:text="Simpan Settings"/>
        </LinearLayout>
    </ScrollView>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
EOF
echo "[11/12] Layouts"

echo "[12/12] Git commit & push..."
git add .
git commit -m "feat: Mikasa IG Creat v2 - Splash, Authenticator TOTP, Settings, AutoProfile, PasswordAutofill"
git push

echo ""
echo "=== SELESAI! ==="
echo "Cek GitHub Actions untuk melihat build progress."

# ── Kotlin Source Files ─────────────────────────────────────────
cat > app/src/main/kotlin/com/rabpadev/app/AppSettings.kt << 'EOF'
package com.rabpadev.app
import android.content.Context
object AppSettings {
    private const val PREFS = "mikasa_settings"
    private const val KEY_PASSWORD_AUTOFILL = "password_autofill_enabled"
    private const val KEY_DEFAULT_PASSWORD = "default_password"
    private const val KEY_AUTO_PROFILE = "auto_profile_enabled"
    private const val KEY_PROFILE_URI = "profile_photo_uri"
    fun isPasswordAutofillEnabled(ctx: Context): Boolean = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PASSWORD_AUTOFILL, false)
    fun setPasswordAutofillEnabled(ctx: Context, v: Boolean) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_PASSWORD_AUTOFILL, v).apply()
    fun getDefaultPassword(ctx: Context): String = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_DEFAULT_PASSWORD, "") ?: ""
    fun setDefaultPassword(ctx: Context, v: String) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_DEFAULT_PASSWORD, v).apply()
    fun isAutoProfileEnabled(ctx: Context): Boolean = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_AUTO_PROFILE, false)
    fun setAutoProfileEnabled(ctx: Context, v: Boolean) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_AUTO_PROFILE, v).apply()
    fun getProfilePhotoUri(ctx: Context): String = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PROFILE_URI, "") ?: ""
    fun setProfilePhotoUri(ctx: Context, v: String) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_PROFILE_URI, v).apply()
}
EOF

cat > app/src/main/kotlin/com/rabpadev/app/TotpAccount.kt << 'EOF'
package com.rabpadev.app
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
data class TotpAccount(val id: String, val name: String, val secret: String)
object TotpRepository {
    private const val PREFS = "totp_accounts"
    private const val KEY = "accounts_json"
    fun getAll(ctx: Context): MutableList<TotpAccount> {
        val json = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<TotpAccount>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(TotpAccount(o.getString("id"), o.getString("name"), o.getString("secret")))
        }
        return list
    }
    fun save(ctx: Context, accounts: List<TotpAccount>) {
        val arr = JSONArray()
        for (a in accounts) { val o = JSONObject(); o.put("id", a.id); o.put("name", a.name); o.put("secret", a.secret); arr.put(o) }
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }
    fun add(ctx: Context, a: TotpAccount) { val l = getAll(ctx); l.add(a); save(ctx, l) }
    fun delete(ctx: Context, id: String) { save(ctx, getAll(ctx).filter { it.id != id }) }
}
EOF

cat > app/src/main/kotlin/com/rabpadev/app/TotpGenerator.kt << 'EOF'
package com.rabpadev.app
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow
object TotpGenerator {
    private const val DIGITS = 6
    private const val PERIOD = 30L
    fun decodeBase32(input: String): ByteArray {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        val clean = input.uppercase().replace("=", "").replace(" ", "")
        val bytes = ByteArray(clean.length * 5 / 8)
        var buf = 0; var bits = 0; var idx = 0
        for (ch in clean) {
            val v = chars.indexOf(ch); if (v < 0) throw IllegalArgumentException("Bad char: $ch")
            buf = buf shl 5; buf = buf or v; bits += 5
            if (bits >= 8) { bytes[idx++] = (buf shr (bits - 8)).toByte(); bits -= 8 }
        }
        return bytes
    }
    fun generate(secret: String): String {
        val step = System.currentTimeMillis() / 1000 / PERIOD
        val key = decodeBase32(secret)
        val msg = ByteArray(8); var t = step
        for (i in 7 downTo 0) { msg[i] = (t and 0xFF).toByte(); t = t ushr 8 }
        val mac = Mac.getInstance("HmacSHA1"); mac.init(SecretKeySpec(key, "RAW"))
        val hash = mac.doFinal(msg)
        val off = hash[hash.size - 1].toInt() and 0x0F
        val code = ((hash[off].toInt() and 0x7F) shl 24) or ((hash[off+1].toInt() and 0xFF) shl 16) or ((hash[off+2].toInt() and 0xFF) shl 8) or (hash[off+3].toInt() and 0xFF)
        val otp = code % 10.0.pow(DIGITS.toDouble()).toInt()
        return otp.toString().padStart(DIGITS, '0').let { "${it.substring(0,3)} ${it.substring(3)}" }
    }
    fun secondsRemaining(): Int = (PERIOD - (System.currentTimeMillis() / 1000) % PERIOD).toInt()
    fun isValidSecret(secret: String): Boolean = try { val c = secret.uppercase().replace("=","").replace(" ",""); c.isNotEmpty() && decodeBase32(c).isNotEmpty() } catch (e: Exception) { false }
}
EOF

cat > app/src/main/kotlin/com/rabpadev/app/TotpAdapter.kt << 'EOF'
package com.rabpadev.app
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
class TotpAdapter(private val accounts: MutableList<TotpAccount>, private val onDelete: (TotpAccount) -> Unit) : RecyclerView.Adapter<TotpAdapter.VH>() {
    private val handler = Handler(Looper.getMainLooper())
    private var ticker: Runnable? = null
    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvAccountName)
        val tvCode: TextView = v.findViewById(R.id.tvTotpCode)
        val tvCountdown: TextView = v.findViewById(R.id.tvCountdown)
        val progress: CircularProgressIndicator = v.findViewById(R.id.progressCountdown)
        val btnCopy: MaterialButton = v.findViewById(R.id.btnCopy)
        val btnDelete: MaterialButton = v.findViewById(R.id.btnDelete)
    }
    override fun onCreateViewHolder(p: ViewGroup, t: Int) = VH(LayoutInflater.from(p.context).inflate(R.layout.item_totp, p, false))
    override fun getItemCount() = accounts.size
    override fun onBindViewHolder(h: VH, pos: Int) {
        val acc = accounts[pos]
        h.tvName.text = acc.name
        fun update() {
            try {
                val code = TotpGenerator.generate(acc.secret)
                val secs = TotpGenerator.secondsRemaining()
                h.tvCode.text = code; h.tvCountdown.text = "${secs}s"
                h.progress.max = 30; h.progress.progress = secs
                val color = when { secs <= 5 -> ContextCompat.getColor(h.itemView.context, R.color.totp_red); secs <= 10 -> ContextCompat.getColor(h.itemView.context, R.color.totp_yellow); else -> ContextCompat.getColor(h.itemView.context, R.color.totp_green) }
                h.tvCode.setTextColor(color); h.progress.setIndicatorColor(color)
            } catch (e: Exception) { h.tvCode.text = "ERROR" }
        }
        update()
        h.btnCopy.setOnClickListener {
            val raw = TotpGenerator.generate(acc.secret).replace(" ", "")
            val cm = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("TOTP", raw))
            Toast.makeText(it.context, it.context.getString(R.string.authenticator_copy), Toast.LENGTH_SHORT).show()
        }
        h.btnDelete.setOnClickListener { onDelete(acc) }
    }
    fun startTicking() { ticker = object : Runnable { override fun run() { notifyDataSetChanged(); handler.postDelayed(this, 1000) } }; handler.post(ticker!!) }
    fun stopTicking() { ticker?.let { handler.removeCallbacks(it) } }
}
EOF

cat > app/src/main/kotlin/com/rabpadev/app/SplashActivity.kt << 'EOF'
package com.rabpadev.app
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java)); finish()
        }, 2000)
    }
}
EOF

cat > app/src/main/kotlin/com/rabpadev/app/AuthenticatorActivity.kt << 'EOF'
package com.rabpadev.app
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rabpadev.app.databinding.ActivityAuthenticatorBinding
import java.util.UUID
class AuthenticatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticatorBinding
    private lateinit var adapter: TotpAdapter
    private val accounts = mutableListOf<TotpAccount>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        accounts.addAll(TotpRepository.getAll(this))
        adapter = TotpAdapter(accounts) { acc ->
            MaterialAlertDialogBuilder(this).setTitle(getString(R.string.delete_confirm))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    TotpRepository.delete(this, acc.id)
                    accounts.removeAll { it.id == acc.id }
                    adapter.notifyDataSetChanged(); updateEmpty()
                    Toast.makeText(this, getString(R.string.authenticator_deleted), Toast.LENGTH_SHORT).show()
                }.setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { showAddDialog() }
        updateEmpty()
    }
    private fun updateEmpty() {
        binding.tvEmpty.visibility = if (accounts.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (accounts.isEmpty()) View.GONE else View.VISIBLE
    }
    private fun showAddDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_totp, null)
        val tilName = view.findViewById<TextInputLayout>(R.id.tilTotpName)
        val tilSecret = view.findViewById<TextInputLayout>(R.id.tilTotpSecret)
        val etName = view.findViewById<TextInputEditText>(R.id.etTotpName)
        val etSecret = view.findViewById<TextInputEditText>(R.id.etTotpSecret)
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.authenticator_add)).setView(view)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val name = etName.text.toString().trim()
                val secret = etSecret.text.toString().trim().uppercase().replace(" ", "")
                if (name.isEmpty()) { tilName.error = "Nama tidak boleh kosong"; return@setPositiveButton }
                if (!TotpGenerator.isValidSecret(secret)) { tilSecret.error = getString(R.string.authenticator_invalid_secret); return@setPositiveButton }
                val acc = TotpAccount(UUID.randomUUID().toString(), name, secret)
                TotpRepository.add(this, acc); accounts.add(acc)
                adapter.notifyItemInserted(accounts.size - 1); updateEmpty()
                Toast.makeText(this, getString(R.string.authenticator_added), Toast.LENGTH_SHORT).show()
            }.setNegativeButton(getString(R.string.cancel), null).show()
    }
    override fun onResume() { super.onResume(); adapter.startTicking() }
    override fun onPause() { super.onPause(); adapter.stopTicking() }
}
EOF

cat > app/src/main/kotlin/com/rabpadev/app/SettingsActivity.kt << 'EOF'
package com.rabpadev.app
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rabpadev.app.databinding.ActivitySettingsBinding
import android.webkit.CookieManager
import android.webkit.WebStorage
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var selectedPhotoUri: String = ""
    private val photoPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try { contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (e: Exception) {}
            selectedPhotoUri = it.toString()
            binding.ivProfilePreview.setImageURI(it)
            AppSettings.setProfilePhotoUri(this, selectedPhotoUri)
            Toast.makeText(this, getString(R.string.photo_selected), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.switchPasswordAutofill.isChecked = AppSettings.isPasswordAutofillEnabled(this)
        binding.etDefaultPassword.setText(AppSettings.getDefaultPassword(this))
        binding.switchAutoProfile.isChecked = AppSettings.isAutoProfileEnabled(this)
        selectedPhotoUri = AppSettings.getProfilePhotoUri(this)
        if (selectedPhotoUri.isNotEmpty()) { try { binding.ivProfilePreview.setImageURI(Uri.parse(selectedPhotoUri)) } catch (e: Exception) { selectedPhotoUri = "" } }
        binding.btnChoosePhoto.setOnClickListener { photoPicker.launch("image/*") }
        binding.btnOpenAuthenticator.setOnClickListener { startActivity(Intent(this, AuthenticatorActivity::class.java)) }
        binding.btnClearCookies.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Clear Cookies").setMessage("Hapus semua cookies?")
                .setPositiveButton(getString(R.string.yes)) { _, _ -> CookieManager.getInstance().removeAllCookies(null); CookieManager.getInstance().flush(); Toast.makeText(this, getString(R.string.cookies_cleared), Toast.LENGTH_SHORT).show() }
                .setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.btnClearCache.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Clear Cache").setMessage("Hapus semua cache?")
                .setPositiveButton(getString(R.string.yes)) { _, _ -> WebStorage.getInstance().deleteAllData(); Toast.makeText(this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show() }
                .setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.btnClearSession.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Clear Session").setMessage("Hapus session login?")
                .setPositiveButton(getString(R.string.yes)) { _, _ -> CookieManager.getInstance().removeSessionCookies(null); CookieManager.getInstance().flush(); Toast.makeText(this, getString(R.string.session_cleared), Toast.LENGTH_SHORT).show() }
                .setNegativeButton(getString(R.string.cancel), null).show()
        }
        binding.btnSaveSettings.setOnClickListener {
            AppSettings.setPasswordAutofillEnabled(this, binding.switchPasswordAutofill.isChecked)
            AppSettings.setDefaultPassword(this, binding.etDefaultPassword.text.toString())
            AppSettings.setAutoProfileEnabled(this, binding.switchAutoProfile.isChecked)
            if (selectedPhotoUri.isNotEmpty()) AppSettings.setProfilePhotoUri(this, selectedPhotoUri)
            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show()
        }
    }
}
EOF

cat > app/src/main/kotlin/com/rabpadev/app/MainActivity.kt << 'EOF'
package com.rabpadev.app
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.rabpadev.app.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val URL_HOME = "https://www.instagram.com/"
    private val URL_EMAIL = "https://accountscenter.instagram.com/profiles/"
    private val URL_A2F = "https://accountscenter.instagram.com/password_and_security/two_factor/"
    private val PREFS_AF = "rabpadev_autofill"
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private val filePicker: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val uris: Array<Uri>? = when { data?.clipData != null -> Array(data.clipData!!.itemCount) { i -> data.clipData!!.getItemAt(i).uri }; data?.data != null -> arrayOf(data.data!!); else -> null }
            fileUploadCallback?.onReceiveValue(uris)
        } else fileUploadCallback?.onReceiveValue(null)
        fileUploadCallback = null
    }
    private val permLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { if (!it) Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show() }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        setupWebView(); setupBack()
        if (savedInstanceState == null) binding.webView.loadUrl(URL_HOME) else binding.webView.restoreState(savedInstanceState)
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val wv = binding.webView
        CookieManager.getInstance().apply { setAcceptCookie(true); setAcceptThirdPartyCookies(wv, true) }
        wv.settings.apply {
            javaScriptEnabled = true; domStorageEnabled = true; databaseEnabled = true
            setSupportMultipleWindows(true); javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true; useWideViewPort = true; setSupportZoom(true)
            builtInZoomControls = true; displayZoomControls = false; cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = true; allowContentAccess = true; mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            userAgentString = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
        }
        wv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView?, r: WebResourceRequest?): Boolean {
                val url = r?.url?.toString() ?: return false
                return when {
                    url.startsWith("https://www.instagram.com") || url.startsWith("https://accountscenter.instagram.com") || url.startsWith("https://www.facebook.com") || url.startsWith("https://l.instagram.com") || url.startsWith("https://i.instagram.com") -> { v?.loadUrl(url); false }
                    url.startsWith("intent://") -> { try { val i = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); if (i.resolveActivity(packageManager) != null) startActivity(i) } catch (e: Exception) {}; true }
                    else -> false
                }
            }
            override fun onPageStarted(v: WebView?, url: String?, f: android.graphics.Bitmap?) { super.onPageStarted(v, url, f); binding.progressBar.visibility = View.VISIBLE; binding.progressBar.progress = 0 }
            override fun onPageFinished(v: WebView?, url: String?) {
                super.onPageFinished(v, url); binding.progressBar.visibility = View.GONE; CookieManager.getInstance().flush()
                if (AppSettings.isPasswordAutofillEnabled(this@MainActivity)) { val p = AppSettings.getDefaultPassword(this@MainActivity); if (p.isNotEmpty()) injectPassword(p) }
            }
        }
        wv.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(v: WebView?, p: Int) { binding.progressBar.progress = p; if (p == 100) binding.progressBar.visibility = View.GONE }
            override fun onShowFileChooser(webView: WebView?, cb: ValueCallback<Array<Uri>>?, params: FileChooserParams?): Boolean {
                fileUploadCallback?.onReceiveValue(null); fileUploadCallback = cb
                if (AppSettings.isAutoProfileEnabled(this@MainActivity)) {
                    val uri = AppSettings.getProfilePhotoUri(this@MainActivity)
                    if (uri.isNotEmpty()) { try { fileUploadCallback?.onReceiveValue(arrayOf(Uri.parse(uri))); fileUploadCallback = null; return true } catch (e: Exception) {} }
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE); return true }
                val intent = params?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply { type = "*/*"; addCategory(Intent.CATEGORY_OPENABLE); putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) }
                filePicker.launch(intent); return true
            }
            override fun onCreateWindow(v: WebView?, d: Boolean, u: Boolean, msg: Message?): Boolean {
                val nw = WebView(this@MainActivity); nw.settings.javaScriptEnabled = true
                (msg?.obj as? WebView.WebViewTransport)?.webView = nw; msg?.sendToTarget(); return true
            }
        }
    }
    private fun setupBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { if (binding.webView.canGoBack()) binding.webView.goBack() else showExitDialog() }
        })
    }
    private fun escape(s: String) = s.replace("\\", "\\\\").replace("'", "\\'")
    private fun injectPassword(pwd: String) {
        binding.webView.evaluateJavascript("""(function(){var els=document.querySelectorAll('input[type="password"]');if(!els.length)return;var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;els.forEach(function(el){s.call(el,'${escape(pwd)}');el.dispatchEvent(new Event('input',{bubbles:true}));el.dispatchEvent(new Event('change',{bubbles:true}));});})();""", null)
    }
    private fun showAutoFillDialog() {
        val p = getSharedPreferences(PREFS_AF, Context.MODE_PRIVATE)
        val v = LayoutInflater.from(this).inflate(R.layout.dialog_autofill, null)
        val etName = v.findViewById<TextInputEditText>(R.id.etName); val etUser = v.findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = v.findViewById<TextInputEditText>(R.id.etEmail); val etPhone = v.findViewById<TextInputEditText>(R.id.etPhone)
        val etBday = v.findViewById<TextInputEditText>(R.id.etBirthday); val etPwd = v.findViewById<TextInputEditText>(R.id.etPassword)
        etName.setText(p.getString("af_name","")); etUser.setText(p.getString("af_username",""))
        etEmail.setText(p.getString("af_email","")); etPhone.setText(p.getString("af_phone",""))
        etBday.setText(p.getString("af_birthday",""))
        val sp = p.getString("af_password",""); etPwd.setText(if (!sp.isNullOrEmpty()) sp else AppSettings.getDefaultPassword(this))
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.autofill_title)).setView(v)
            .setPositiveButton(getString(R.string.autofill_btn_fill)) { _, _ ->
                val name = etName.text.toString().trim(); val email = etEmail.text.toString().trim()
                if (name.isEmpty() && email.isEmpty()) { Toast.makeText(this, getString(R.string.autofill_empty), Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                p.edit().putString("af_name",name).putString("af_username",etUser.text.toString().trim()).putString("af_email",email).putString("af_phone",etPhone.text.toString().trim()).putString("af_birthday",etBday.text.toString().trim()).putString("af_password",etPwd.text.toString().trim()).apply()
                injectAll(name, etUser.text.toString().trim(), email, etPhone.text.toString().trim(), etBday.text.toString().trim(), etPwd.text.toString().trim())
                Toast.makeText(this, getString(R.string.autofill_filled), Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton(getString(R.string.autofill_btn_save)) { _, _ ->
                p.edit().putString("af_name",etName.text.toString().trim()).putString("af_username",etUser.text.toString().trim()).putString("af_email",etEmail.text.toString().trim()).putString("af_phone",etPhone.text.toString().trim()).putString("af_birthday",etBday.text.toString().trim()).putString("af_password",etPwd.text.toString().trim()).apply()
                Toast.makeText(this, getString(R.string.autofill_saved), Toast.LENGTH_SHORT).show()
            }.setNegativeButton(getString(R.string.cancel), null).show()
    }
    private fun injectAll(name: String, user: String, email: String, phone: String, bday: String, pwd: String) {
        binding.webView.evaluateJavascript("""
(function(){function f(sels,v){if(!v)return;var s=Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype,'value').set;sels.forEach(function(sel){document.querySelectorAll(sel).forEach(function(el){s.call(el,v);el.dispatchEvent(new Event('input',{bubbles:true}));el.dispatchEvent(new Event('change',{bubbles:true}));});});}
f(['input[name="name"]','input[name="fullName"]','input[placeholder*="ame"]','input[autocomplete="name"]'],'${escape(name)}');
f(['input[name="username"]','input[placeholder*="sername"]','input[autocomplete="username"]'],'${escape(user)}');
f(['input[type="email"]','input[name="email"]','input[name="emailOrPhone"]'],'${escape(email)}');
f(['input[type="tel"]','input[name="phone"]','input[placeholder*="hone"]'],'${escape(phone)}');
f(['input[name="birthday"]','input[placeholder*="birth"]','input[placeholder*="lahir"]'],'${escape(bday)}');
f(['input[type="password"]','input[name="password"]','input[autocomplete="current-password"]'],'${escape(pwd)}');})();""", null)
    }
    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.clear_data_title)).setMessage(getString(R.string.clear_data_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                CookieManager.getInstance().removeAllCookies(null); CookieManager.getInstance().flush()
                WebStorage.getInstance().deleteAllData()
                binding.webView.clearCache(true); binding.webView.clearHistory(); binding.webView.clearFormData(); binding.webView.clearSslPreferences()
                binding.webView.loadUrl(URL_HOME)
                Toast.makeText(this, getString(R.string.clear_data_success), Toast.LENGTH_SHORT).show()
            }.setNegativeButton(getString(R.string.no), null).show()
    }
    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this).setTitle(getString(R.string.exit_title)).setMessage(getString(R.string.exit_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }.setNegativeButton(getString(R.string.no), null).show()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean { menuInflater.inflate(R.menu.menu_main, menu); return true }
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_home -> { binding.webView.loadUrl(URL_HOME); true }
        R.id.action_email -> { binding.webView.loadUrl(URL_EMAIL); true }
        R.id.action_a2f -> { binding.webView.loadUrl(URL_A2F); true }
        R.id.action_refresh -> { binding.webView.reload(); true }
        R.id.action_autofill -> { showAutoFillDialog(); true }
        R.id.action_authenticator -> { startActivity(Intent(this, AuthenticatorActivity::class.java)); true }
        R.id.action_settings -> { startActivity(Intent(this, SettingsActivity::class.java)); true }
        R.id.action_clear_data -> { showClearDataDialog(); true }
        else -> super.onOptionsItemSelected(item)
    }
    override fun onSaveInstanceState(out: Bundle) { super.onSaveInstanceState(out); binding.webView.saveState(out) }
    override fun onResume() { super.onResume(); binding.webView.resumeTimers(); binding.webView.onResume() }
    override fun onPause() { super.onPause(); binding.webView.pauseTimers(); binding.webView.onPause(); CookieManager.getInstance().flush() }
    override fun onDestroy() { super.onDestroy(); binding.webView.destroy() }
}
EOF

echo ""
echo "=== Semua file Kotlin selesai ==="
echo "Melakukan git commit & push..."
git add .
git commit -m "feat: Mikasa IG Creat v2 - Splash, TOTP Authenticator, Settings, AutoProfile, PasswordAutofill"
git push
echo ""
echo "=============================="
echo "  SETUP SELESAI!"
echo "  Cek GitHub Actions untuk build APK."
echo "=============================="
