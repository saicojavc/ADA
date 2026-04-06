<div align="center">

<br/>

```
  ░█████╗░██████╗░░█████╗░
  ██╔══██╗██╔══██╗██╔══██╗
  ███████║██║░░██║███████║
  ██╔══██║██║░░██║██╔══██║
  ██║░░██║██████╔╝██║░░██║
  ╚═╝░░╚═╝╚═════╝░╚═╝░░╚═╝
```

### *An Organic LifeOS*

**Balance your Load. Honor your Energy. Live with intention.**

<br/>

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20MVVM-2E8B57?style=flat-square)
![Hilt](https://img.shields.io/badge/DI-Hilt-FF6F00?style=flat-square&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Persistence-Room-A0785A?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-C8956C?style=flat-square)

<br/>

</div>

---

## ✦ Philosophy

> *ADA is not a productivity tool. It's a living companion.*

Most task managers treat your time as a resource to be optimized. ADA treats it as something more precious — a reflection of your energy, your rhythm, and your life.

ADA is built around a single insight: **sustainable performance comes from balancing Load with Recovery.** Every feature in ADA exists to honor that balance.

The aesthetic is intentional. Warm creams, sage greens, terracotta tones, and serif typography are not decorations — they are a design philosophy. Your daily planner should feel like a journal, not a spreadsheet.

---

## ✦ Features

### 🧠 Smart Suggestion Engine — *offline intelligence*
ADA analyzes your task density, time gaps, and circadian energy curve in real time — entirely on-device, no internet required. It uses a **multi-dimensional scoring engine** with five parallel analyzers:

| Analyzer | What it measures |
|---|---|
| **Load** | Pending task volume + urgency of overdue items |
| **Time Gaps** | Free windows before your next task (ideal: 20–50 min) |
| **Circadian Rhythm** | Estimated cognitive energy by time of day (10 time slots) |
| **Wellbeing Ratio** | Completed vs. pending tasks — celebrates progress |
| **Momentum** | Active day streaks — reinforces positive habits |

All analyzers run simultaneously. The suggestion with the highest weighted score wins. A **recency penalty** prevents ADA from repeating the same type of advice.

---

### 📅 Intelligent Timeline
A unified daily view where tasks are **automatically crossed out as time passes**, giving you a continuous, honest sense of progress. ADA distinguishes between tasks the user explicitly completes and those that expire — avoiding false urgency alerts with a configurable **2-hour overdue window**.

---

### 🌿 Wellness Organism
A dedicated wellbeing space that tracks your **Balance Score** through:
- Passive sleep detection via device usage patterns
- Hardware-integrated step counting
- Daily ritual completion tracking

---

### 🕯 Organic Rituals
Curated daily habits (*Sunlight Bath*, *Brain Dump*, *Evening Release*) with satisfying **long-press interactions** designed to reduce cognitive load and anxiety.

---

### 🏷 Smart Categorization
A **predictive text algorithm** that suggests task categories from user input in real time. It normalizes text (removes accents, handles typos) and uses a **multi-category scoring system** — whichever category accumulates the most keyword matches wins.

| Category | Examples detected |
|---|---|
| **Work** | reunión, scrum, sprint, deadline, deploy, cliente |
| **Home** | limpiar, supermercado, plomero, perro, jardín |
| **Wellbeing** | yoga, meditación, skincare, nutricionista, running |
| **Maternity** | pediatra, lactancia, guardería, ecografía |
| **Personal** | banco, pasaporte, cumpleaños, viaje, curso |

---

### 🔔 Critical Alerts
A high-priority notification system that **functions like an alarm** — bypasses Do Not Disturb and renders on the lock screen — ensuring important tasks are never silently missed.

---

### 🎡 Custom UI Components
Bespoke **Wheel Pickers** for Date and Time, hand-crafted to match ADA's organic aesthetic. No Material date pickers — every interaction is designed to feel intentional.

---

## ✦ Tech Stack

```
Language        Kotlin (100%)
UI              Jetpack Compose
Architecture    Clean Architecture + MVVM
DI              Hilt
Database        Room
Async           Coroutines + Flow
Notifications   WorkManager + NotificationManager
```

---

## ✦ Architecture

ADA follows a **Modular Clean Architecture** pattern, separating concerns across clearly defined modules:

```
ada/
├── app/                        → Entry point, navigation graph
│
├── core/
│   ├── database/               → Room DAOs, entities, migrations
│   ├── domain/                 → Use cases, repository interfaces
│   ├── model/                  → Shared data models (Tarea, Ritual, etc.)
│   ├── ui/                     → Design system: colors, typography, components
│   └── notification/           → Alert engine, WorkManager workers
│
└── feature/
    ├── dashboard/              → Timeline, Smart Suggestion, Balance Score
    └── onboarding/             → First-run experience, profile setup
```

**Data flow:**
```
UI (Compose) ──► ViewModel ──► UseCase ──► Repository ──► Room / DataStore
                     ▲                          │
                     └──────── Flow ◄───────────┘
```

---

## ✦ Color Palette

ADA's visual identity uses a warm, earthy palette that evokes paper journals and botanical illustrations:

| Token | Hex | Role |
|---|---|---|
| `BaseCrema` | `#F5F0E8` | Background — soft, paper-like |
| `VerdeSalvia` | `#7A9E7E` | Primary — growth, health |
| `TerracotaSuave` | `#C8795A` | Accent — warmth, priority |
| `AmbarNeutro` | `#C8956C` | Secondary accent — energy |
| `TextoOscuro` | `#3D3530` | Body text — warm dark |

Typography uses **Serif** display fonts paired with refined body type, evoking the sensation of a handwritten personal diary.

---

## ✦ Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 26+

### Clone & run
```bash
git clone https://github.com/saicojavc/ada-lifeos.git
cd ada-lifeos
```

Open in Android Studio, sync Gradle, and run on a device or emulator with API 26+.

### API key / backend
ADA is **fully offline**. No API keys, no backend, no accounts required. All intelligence runs locally on-device.

---

## ✦ Roadmap

- [ ] Gemini API integration — AI-powered weekly reflection reports
- [ ] Cloud backup via Firebase (optional, opt-in)
- [ ] Widget support — Balance Score on home screen
- [ ] Wearable sync — step + heart rate from WearOS
- [ ] Shared rituals — export and share ritual packs

---

## ✦ Contributing

ADA is a personal project built with care. If you'd like to contribute, open an issue first to discuss what you'd like to change. PRs that align with the organic, human-centered philosophy are warmly welcome.

---

## ✦ License

```
MIT License — use it, build on it, make it yours.
```

---

<div align="center">

<br/>

*"The goal is not more tasks done.*
*The goal is a life well-lived."*

<br/>

Built with 🌿 by **Jorge Adrián Valdés Camacho**
[Portfolio](https://jorge-android-dev.web.app) · [GitHub](https://github.com/saicojavc) · [LinkedIn]([https://linkedin.com/in/saicojavc](https://www.linkedin.com/in/jorge-adri%C3%A1n-vald%C3%A9s-camacho-21b371221/))

<br/>

</div>
