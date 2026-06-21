# Aplikasi Sensor Android

Aplikasi Android berbasis Java yang menunjukkan integrasi beberapa sensor perangkat dalam satu proyek. Aplikasi ini menggunakan fitur kamera, layanan lokasi GPS, dan sensor suhu lingkungan.

## Fitur Utama

### 1. Integrasi Kamera
- Mengambil foto menggunakan kamera perangkat.
- Menggunakan Activity Result API (TakePicturePreview).
- Menampilkan hasil foto langsung pada ImageView di aplikasi.

### 2. Pelacakan Lokasi GPS
- Mengambil data latitude, longitude, dan provider secara real-time.
- Menggunakan FusedLocationProviderClient untuk akurasi dan efisiensi daya.
- Menampilkan status GPS (aktif atau tidak aktif).

### 3. Sensor Suhu Lingkungan
- Memantau suhu lingkungan secara real-time.
- Menampilkan nilai suhu dalam satuan Celsius pada tampilan aplikasi.
- Menyediakan fallback jika perangkat tidak mendukung sensor suhu.

### 4. Antarmuka Pengguna
- Dibangun menggunakan XML dengan ConstraintLayout.
- Menggunakan MaterialCardView untuk mengelompokkan fitur sensor.
- Tampilan scroll vertikal yang sederhana dan mudah digunakan.

## Teknologi yang Digunakan

- Bahasa Pemrograman: Java
- UI: XML (Android Views)
- Minimum SDK: API 24 (Android 7.0)
- Target SDK: API 36 (Android 15)
- Manajemen Dependency: Gradle Version Catalog (libs.versions.toml)
- Library Utama:
  - AndroidX AppCompat
  - Material Components
  - Google Play Services Location

## Struktur Proyek

- Logika utama: MainActivity.java
- Layout UI: activity_main.xml
- Konfigurasi izin dan manifest: AndroidManifest.xml
- Manajemen dependency: libs.versions.toml

## Cara Menjalankan

1. Clone atau unduh proyek ini.
2. Buka menggunakan Android Studio (versi Hedgehog atau lebih baru).
3. Tunggu proses sinkronisasi Gradle selesai.
4. Pastikan semua dependency berhasil diunduh otomatis.

## Menjalankan Aplikasi

1. Hubungkan perangkat Android melalui USB debugging atau gunakan emulator.
2. Berikan izin kamera dan lokasi saat diminta oleh aplikasi.
3. Klik tombol Run di Android Studio atau tekan Shift + F10.
4. Catatan: Sensor suhu lingkungan mungkin tidak tersedia di semua perangkat. Emulator dapat digunakan untuk simulasi lokasi dan sensor.

---
**Dosen Pengampu:** Dr. Sopian Alviana, S.Kom., M.Kom  
**Mata Kuliah:** Pemrograman Android  
