📱 Jetpack Compose Member & Avatar App

  Android App built with Jetpack Compose + MVVM + Room + OpenAI API +
  Firebase Auth

------------------------------------------------------------------------

🗓️ 更新日期

2025/11/05

------------------------------------------------------------------------

🧩 專案簡介

這是一個以 Jetpack Compose + MVVM 為核心開發的 Android 專案，
整合 Firebase Authentication（登入/註冊）、
Room Database（本地資料儲存）、
與 OpenAI Image API（AI 頭像生成）。

應用重點在於資料分層結構設計、非同步流程控制、
與即時 UI 狀態驅動的 Compose 畫面更新。

------------------------------------------------------------------------

🏗️ 專案結構

app/ ├── manifests/ │ └── AndroidManifest.xml │ ├──
com.example.jetpackcompose/ │ │ ├── data/ # 資料層 (Data Layer) │ │ ├──
local/ # Room 資料庫 │ │ │ ├── AppDatabase.kt │ │ │ ├── MemberDao.kt │ │
│ ├── MemberEntity.kt │ │ │ ├── AvatarDao.kt │ │ │ └── AvatarEntity.kt │
│ │ │ │ ├── remote/ # OpenAI API │ │ │ ├── OpenAiApi.kt │ │ │ └──
RetrofitProvider.kt │ │ │ │ │ ├── MemberRepository.kt │ │ └──
AvatarRepository.kt │ │ │ ├── nav/ # 導航系統 │ │ └── Nav.kt │ │ │ ├──
presentation/ # 介面層 (UI Layer) │ │ ├── auth/ # 登入 / 註冊 │ │ │ ├──
LoginContent.kt │ │ │ ├── LoginScreen.kt │ │ │ ├── LoginViewModel.kt │ │
│ ├── RegisterContent.kt │ │ │ ├── RegisterScreen.kt │ │ │ └──
RegisterViewModel.kt │ │ │ │ │ ├── avatar/ # 頭像生成 / 交換 │ │ │ ├──
AvatarContent.kt │ │ │ ├── AvatarScreen.kt │ │ │ └── AvatarViewModel.kt
│ │ │ │ │ └── list/ # 成員清單 │ │ ├── ListContent.kt │ │ ├──
ListScreen.kt │ │ └── ListViewModel.kt │ │ │ └── MainActivity.kt

------------------------------------------------------------------------

⚙️ 使用技術

  類別         技術
  ------------ -----------------------------------
  語言         Kotlin
  UI 框架      Jetpack Compose (Material 3)
  架構模式     MVVM (Model-View-ViewModel)
  狀態管理     StateFlow / collectAsState
  資料庫       Room (Local Database)
  登入系統     Firebase Authentication
  圖片生成     OpenAI Image API (Retrofit)
  導航系統     Navigation Compose
  非同步任務   Kotlin Coroutine + ViewModelScope

------------------------------------------------------------------------

🧠 功能說明

🔐 登入 / 註冊

-   使用 Firebase Authentication 驗證帳號。
-   註冊後自動導向登入畫面。
-   ViewModel 維護 UI 狀態（輸入、錯誤提示、登入進度）。

👥 成員清單

-   使用 Room 管理本地成員資料。
-   支援搜尋、新增、刪除、編輯。
-   每個成員可綁定一張 AI 生成的頭像。

🧠 頭像生成與交換

-   使用 OpenAI Image API 生成插畫風格頭像。
-   支援：
    -   新生成頭像
    -   使用歷史圖庫中曾生成的圖片
    -   與他人交換頭像（雙向交換）
-   支援無主圖片顯示（memberId = null 狀態）。
-   每次更換後 UI 會自動刷新。

------------------------------------------------------------------------

📦 主要依賴

(Gradle dependencies section omitted for brevity)

------------------------------------------------------------------------

🧩 架構設計重點

AvatarScreen → AvatarViewModel → AvatarRepository → RetrofitProvider →
OpenAiApi ↑ ↓ Room ← AvatarDao ← AppDatabase

------------------------------------------------------------------------

🧑‍💻 作者

Cheng Hong (Vreckan) Android Developer / Jetpack Compose Learner Taiwan
↔ Canada GitHub: github.com/Vreckan

------------------------------------------------------------------------

© 2025 Cheng Hong (Vreckan) — All rights reserved.
