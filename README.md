# 📱 Jetpack Compose Member App 2025/11/02

這是一個以 **Jetpack Compose** 為核心開發的 Android 專案，  
認證使用 **Firebase Authentication** 與 **Room Database**，  
實作登入、註冊與成員清單管理功能，並採用 **MVVM 架構** 分離邏輯與 UI。

---

## 🧩 專案架構概覽

```
app/
├── manifests/
│   └── AndroidManifest.xml
│
├── com.example.jetpackcompose/
│   ├── auth/                  # 登入與註冊功能
│   │   ├── LoginContent.kt
│   │   ├── LoginScreen.kt
│   │   ├── LoginViewModel.kt
│   │   ├── RegisterContent.kt
│   │   ├── RegisterScreen.kt
│   │   └── RegisterViewModel.kt
│   │
│   ├── data/                  # 資料層
│   │   ├── local/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── MemberDao.kt
│   │   │   └── MemberEntity.kt
│   │   └── MemberRepository.kt
│   │
│   ├── list/                  # 成員列表畫面
│   │   ├── ListContent.kt
│   │   ├── ListScreen.kt
│   │   └── ListViewModel.kt
│   │
│   ├── nav/                   # 導航控制
│   │   └── AppNav.kt
│   │
│   └── MainActivity.kt
```

---

## ⚙️ 使用技術

| 類別 | 技術 |
|------|------|
| 語言 | Kotlin |
| UI 框架 | Jetpack Compose (Material 3) |
| 架構模式 | MVVM |
| 資料庫 | Room (Local Database) |
| 登入系統 | Firebase Authentication |
| 依賴注入 | 自定義 `ViewModelProvider.Factory` (手動注入 Repository / DAO) |
| 導航系統 | Navigation Compose |
| 狀態管理 | StateFlow / collectAsState |

---

## 🧠 架構說明

- **Model 層**：  
  定義資料結構與資料操作邏輯（`MemberEntity`, `MemberDao`, `MemberRepository`）。

- **ViewModel 層**：  
  處理業務邏輯與 UI 狀態管理，使用 `StateFlow` 實現即時更新（ `ListViewModel`、`LoginViewModel`）。

- **View 層**：  
  採用 Compose 建構畫面，使用 `collectAsState()` 監聽資料變化（ `LoginContent`, `ListContent`）。

---

## 📦 主要依賴

```gradle
// Compose
implementation("androidx.compose.ui:ui:1.6.0")
implementation("androidx.compose.material3:material3:1.2.0")
implementation("androidx.navigation:navigation-compose:2.7.6")

// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Firebase
implementation("com.google.firebase:firebase-auth:23.0.0")
implementation("com.google.gms:google-services:4.4.2")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
```

## ✍️ 作者
**Cheng Hong (Vreckan)**  
Jetpack Compose Developer / Android Learner  
