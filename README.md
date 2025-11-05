# 📱 Jetpack Compose Member & Avatar App  
**Android App built with Jetpack Compose + MVVM + Room + OpenAI API + Firebase Auth**

---

## 🗓️ 更新日期  
**2025/11/05**

---

## ⚙️ 專案啟動快速手冊  

---

### ☑️ 1️⃣ OpenAI 金鑰設定  

在 `local.properties` 新增：

```gradle
OPENAI_API_KEY=你的OpenAI金鑰
```

在 `app/build.gradle` 加入：

```gradle
buildConfigField("String", "OPENAI_API_KEY", "\"${properties["OPENAI_API_KEY"]}\"")
```

---

### ☑️ 2️⃣ Firebase 設定  

1. 於 Firebase Console 建立 Android 專案  
2. 下載 `google-services.json` 放入 `app/`  
3. 在 `app/build.gradle` 啟用：

```gradle
id("com.google.gms.google-services")
```

---

### ☑️ 3️⃣ 啟用網路權限  

在 `AndroidManifest.xml` 加入：

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

### ☑️ 4️⃣ 測試帳號登入  

- 預設測試帳號：`member`  
- 預設密碼：`member`

---

## 🧩 專案簡介  

這是一個以 **Jetpack Compose + MVVM** 為核心開發的 Android 專案，  
整合 **Firebase Authentication（登入/註冊）**、  
**Room Database（本地資料儲存）**、  
與 **OpenAI Image API（AI 頭像生成）**。  

應用重點在於資料分層結構設計、非同步流程控制、  
與即時 UI 狀態驅動的 Compose 畫面更新。

---

## 🏗️ 專案結構  

```
app/
├── manifests/
│   └── AndroidManifest.xml
│
├── com.example.jetpackcompose/
│   ├── data/                     # 資料層 (Data Layer)
│   │   ├── local/                # Room 資料庫
│   │   │   ├── AppDatabase.kt
│   │   │   ├── MemberDao.kt
│   │   │   ├── MemberEntity.kt
│   │   │   ├── AvatarDao.kt
│   │   │   └── AvatarEntity.kt
│   │   ├── remote/               # OpenAI API
│   │   │   ├── OpenAiApi.kt
│   │   │   └── RetrofitProvider.kt
│   │   ├── MemberRepository.kt
│   │   └── AvatarRepository.kt
│   │
│   ├── nav/                      # 導航系統
│   │   └── AppNav.kt
│   │
│   ├── presentation/             # 介面層 (UI Layer)
│   │   ├── auth/                 # 登入 / 註冊
│   │   ├── avatar/               # 頭像生成 / 交換
│   │   └── list/                 # 成員清單
│   │
│   └── MainActivity.kt
```

---

## ⚙️ 使用技術  

| 類別 | 技術 |
|------|------|
| 語言 | Kotlin |
| UI 框架 | Jetpack Compose (Material 3) |
| 架構模式 | MVVM (Model-View-ViewModel) |
| 狀態管理 | StateFlow / collectAsState |
| 資料庫 | Room (Local Database) |
| 登入系統 | Firebase Authentication |
| 圖片生成 | OpenAI Image API (Retrofit + OkHttp) |
| 導航系統 | Navigation Compose |
| 非同步任務 | Kotlin Coroutine + ViewModelScope |
| 相依注入 | 手動 ViewModelProvider.Factory |

---

## 🧠 功能說明  

### 🔐 登入 / 註冊  

- 使用 **Firebase Authentication** 驗證帳號與密碼  
- 註冊成功後自動導向登入畫面  
- ViewModel 維護 UI 狀態（輸入欄、錯誤訊息、登入進度）  
- 採用 `collectAsState()` 即時更新畫面  

---

### 👥 成員清單  

- 使用 **Room Database** 管理成員資料  
- 支援：
  - 新增 / 刪除 / 編輯成員  
  - 關鍵字搜尋  
  - 匯入預設資料（Seed Data）  
  - 綁定 AI 生成頭像  
- 成員刪除後，其頭像仍保留在資料庫中作為「無主圖片」  

---

### 🎨 AI 頭像生成與交換  

- 使用 **OpenAI Image API** 生成插畫風格頭像  
- Retrofit + Coroutine 實現非同步請求  
- 圖片以 `avatar_memberId.jpg` 儲存在內部儲存空間  
- 功能：
  - 生成新圖（輸入水果＋動物關鍵詞）  
  - 使用舊圖（從歷史圖庫挑選）  
  - 圖片交換（雙方皆有頭像時自動交換）  
  - 無主圖片保留（原擁有者刪除後圖片解除綁定但仍可選用）  
- 更換或交換後，List 畫面自動刷新顯示最新頭像  

---

## 🔁 資料流邏輯  

```
AvatarScreen
   │
   ▼
AvatarViewModel
   │
   ▼
AvatarRepository
   │
   ├── OpenAiApi (生成圖片)
   ├── AvatarDao (Room 資料庫)
   └── FileSystem (本地儲存)
```

UI ↔ ViewModel 透過 **StateFlow** 實現即時同步，  
資料層使用 `suspend` 函式與 Coroutine 管理任務。

---

## 📦 主要依賴 (Gradle)

```gradle
// Jetpack Compose
implementation("androidx.compose.ui:ui:1.6.0")
implementation("androidx.compose.material3:material3:1.2.0")
implementation("androidx.navigation:navigation-compose:2.7.6")

// Lifecycle + ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Room Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Firebase Auth
implementation("com.google.firebase:firebase-auth:23.0.0")
implementation("com.google.gms:google-services:4.4.2")

// Network & OpenAI
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")

// Kotlin Coroutine
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

---

## 🧩 架構設計重點  

- **單向資料流 (Unidirectional Data Flow)**  
  View → ViewModel → Repository → Database / API  
  > ViewModel 作為唯一資料來源（Single Source of Truth）

- **分層責任明確**  
  - View：UI 顯示與事件傳遞  
  - ViewModel：邏輯與狀態處理  
  - Repository：資料存取與同步控制  

- **圖片交換安全設計**  
  - 同步更新雙方頭像  
  - 避免重複綁定  
  - 保留無主圖片以利重用  

---

## 🧪 開發重點  

- 使用 `LaunchedEffect()` 與 `SavedStateHandle` 實現跨頁資料同步  
- 實作圖片緩存與本地快取  
- 刪除資料時仍保留圖片紀錄  
- 採用 `collectAsState()` 驅動 Compose UI  

---

## 👨‍💻 作者  

**Cheng Hong (Vreckan)**  
Android Developer / Jetpack Compose Learner  

🔗 GitHub: [github.com/Vreckan](https://github.com/Vreckan)

---

© 2025 Cheng Hong (Vreckan) — All rights reserved.
