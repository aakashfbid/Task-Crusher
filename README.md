# ⚡ TaskCrusher — Android App

A premium, dark-themed task management app with bulletproof alarms, personality modes, and a procrastination pet.

---

## 🚀 Quick Start (Android Studio)

### Prerequisites
- [Android Studio Hedgehog or newer](https://developer.android.com/studio) (free)
- JDK 11+ (bundled with Android Studio)
- Android device or emulator (API 26+)

### Steps

1. **Extract** this ZIP anywhere on your computer
2. **Open Android Studio** → File → Open → select the `TaskCrusher` folder
3. **Wait** for Gradle sync to complete (~2-5 min first time, downloads dependencies)
4. **Add MPAndroidChart repository** — open `settings.gradle` and add inside `dependencyResolutionManagement > repositories`:
   ```
   maven { url 'https://jitpack.io' }
   ```
5. **Run** → click the green ▶ Play button
6. Select your device/emulator → Done!

---

## 📱 Features

### Part 1: Core Task Engine
- **Brain Dump FAB** — Giant "+ Brain Dump" button, add task + description + alarm in seconds
- **Bulletproof Alarms** — `setExactAndAllowWhileIdle` + BroadcastReceiver + Foreground Service
- **Doze Mode bypass** — Alarms ring even when the phone is idle or the app is killed
- **Full-screen popup** — Wakes screen with `FLAG_TURN_SCREEN_ON` + `FLAG_KEEP_SCREEN_ON`
- **Action Loop:**
  - ✅ "Accept & Crush It" — stops alarm, marks task complete
  - 💤 "Snooze / Do Later" — +1 Hour, +2 Hours, or Custom Time picker; increments snooze counter

### Part 2: UI / Color Psychology
- **Deep Navy/Charcoal** base (`#121212`)
- **Neon Cyan** (`#00FFFF`) + **Electric Purple** (`#8A2BE2`) accents
- **Glassmorphism** task cards (semi-transparent, glowing borders)
- **Urgency Gradient** on task cards AND alarm popup:
  - 0 snoozes → Calming Cyan
  - 1–2 snoozes → Warning Orange
  - 3+ snoozes → Pulsing Crimson Red (with animation)

### Part 3: Viral & Retention Features
- **Personality Modes** (Settings):
  - 🔥 Hype Man — motivational phrases
  - 😈 The Roaster — escalating shame with snooze count
- **Procrastination Pet** 🐉 — lives on the home screen
  - Thriving dragon → worried → stressed → 👹 Procrastination Monster
- **Weekly/Monthly Wrap-Up:**
  - Headline summary (tasks crushed vs snoozed)
  - Bar chart (completions by day)
  - Pie chart (crushed vs snoozed ratio)
  - Most-snoozed task callout
  - **📸 Save as JPG** button

---

## ⚠️ Important: Exact Alarm Permission (Android 12+)

On Android 12+, the app will prompt you to allow **"Schedule Exact Alarms"** in Settings. This is required for bulletproof alarms. Without it, alarms may not fire.

Path: Settings → Apps → TaskCrusher → Alarms & Reminders → Allow

---

## 🏗️ Architecture

```
com.taskcrusher/
├── data/
│   ├── database/     TaskDatabase, TaskDao (Room)
│   ├── models/       Task, UrgencyLevel
│   └── repository/   TaskRepository
├── service/          AlarmService (ForegroundService)
├── receiver/         AlarmReceiver, BootReceiver
├── ui/
│   ├── activities/   Main, AddTask, AlarmPopup, Settings, WeeklyWrapUp
│   ├── adapters/     TaskAdapter
│   └── TaskViewModel
└── utils/            AlarmScheduler, PersonalityManager
```

---

## 📦 Dependencies

| Library | Purpose |
|---------|---------|
| Room 2.6 | Local database |
| MPAndroidChart | Weekly stats charts |
| Material Components | UI components |
| Coroutines | Async operations |
| Lottie | Animations (future use) |
| WorkManager | Backup scheduling |

---

## 🔧 Troubleshooting

**Alarm doesn't ring?**
- Check: Settings → Apps → TaskCrusher → Battery → Unrestricted
- Check: Alarms & Reminders permission granted
- Disable any aggressive battery optimizer apps (MIUI, OneUI, etc.)

**Build fails?**
- Make sure you added `maven { url 'https://jitpack.io' }` to settings.gradle
- File → Invalidate Caches → Restart

**Charts not showing?**
- Jitpack dependency requires internet during first build

---

Built with ❤️ by TaskCrusher
