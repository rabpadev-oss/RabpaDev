# RabpaDev - Instagram WebView App

Aplikasi Android native Kotlin yang membuka Instagram menggunakan WebView fullscreen dengan fitur lengkap.

## Fitur

- 🌐 Instagram WebView fullscreen
- 🔐 Mendukung login Instagram dengan session persistence
- 🍪 Cookie & session management
- 📤 File upload support
- 🪟 Multiple windows/popups support
- 🌙 Dark Theme dengan Material Design 3

## Toolbar

| Ikon | Fungsi | URL |
|------|--------|-----|
| 🏠 Home | Buka halaman utama Instagram | https://www.instagram.com/ |
| 📧 Email | Buka Accounts Center Profiles | https://accountscenter.instagram.com/profiles/ |
| 🔐 A2F | Buka Two-Factor Authentication | https://accountscenter.instagram.com/password_and_security/two_factor/ |
| 🔄 Refresh | Reload halaman aktif | - |
| 🗑️ Clear Data | Hapus semua data & cookies | - |

## Spesifikasi

- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35 (Android 15)
- **Language**: Kotlin
- **Architecture**: Single Activity + WebView
- **Theme**: Material Design 3 Dark

## Build

### Android Studio
1. Buka project di Android Studio
2. Biarkan Gradle sync selesai
3. Run → Run 'app' atau Build → Build APK(s)

### Command Line
```bash
chmod +x gradlew
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### GitHub Actions
Push ke branch `main` atau `master` akan otomatis build APK debug dan upload sebagai artifact.

## Fitur Clear Data

Tombol **Clear Data** (di overflow menu) berguna untuk:
- Menghapus semua cookies Instagram
- Reset session login
- Hapus LocalStorage & cache
- Ganti akun Instagram tanpa hapus data aplikasi dari Settings Android

## Catatan Gradle Wrapper

Jika build gagal karena `gradle-wrapper.jar`, jalankan:
```bash
gradle wrapper --gradle-version 8.7
```
atau buka project di Android Studio dan biarkan otomatis diperbarui.
