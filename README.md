# ScrollControl 🛡️

**Instagram Reels restriction powered by tab engagement and DM scrolling detection.**

ScrollControl is an Android digital wellbeing application that prevents infinite Reels doomscrolling in two key ways:
1. Detecting when the Instagram Reels bottom tab becomes active (indicated by Instagram's rounded pill design around the Reels icon).
2. Detecting when you start scrolling reels from Direct Messages (indicated by Instagram's floating **"Send to chat"** pill button).

The moment either trigger is seen, ScrollControl launches a translucent pop-up with a random motivational quote and blocks scrolling.

---

## 📸 Detection Triggers

### 1. Reels Tab Pill Indicator (Main App)
* **Unengaged:** Plain outline play icon without background (normal browsing allowed).
* **Engaged:** Rounded capsule/pill background around the Reels icon.
* **Result:** Triggers the motivational pop-up lock immediately.

### 2. "Send to chat" Button (Direct Messages)
* **Single Reel View:** When friends send a Reel in chat, you can watch that individual video undisturbed.
* **Scroll Attempt:** The moment you swipe up to browse further reels, Instagram displays a floating dark pill button featuring the friend's avatar and the text **"Send to chat"**.
* **Result:** `InstagramReelsBlockerService` detects this button appearing and instantly displays the pop-up modal with a prominent **"Return to Chat"** button, preventing the algorithmic spiral.

---

## ✨ Features

* **Motivational Pop-Up Blocker:**
  - Floats directly over Instagram with a translucent backdrop (`windowIsTranslucent = true`).
  - Consumes all touch events to physically block scrolling underneath.
  - Curated motivational quotes on self-mastery, focus, and valuing time.
  - Quick action buttons to return directly to your DMs (`instagram://direct-inbox`), return to Home feed, or exit to the device Home screen.
* **Customizable Protection Rules:**
  - Toggle blocking swipe-to-next reel in DMs.
  - Toggle allowing single reels in DMs.
  - Optional FYP feed blocker and Explore grid blocker.
* **Impact Dashboard:**
  - Real-time counter of reels blocked.
  - Estimated focus time saved.
  - Interactive preview buttons to test both the Reels Tab lock and DM Scroll lock screens.

---

## 🛠️ Project Structure

```
scrollcontrol/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/scrollcontrol/app/
│   │   ├── ScrollControlApp.kt
│   │   ├── data/
│   │   │   ├── MotivationalQuotes.kt
│   │   │   └── PreferencesRepository.kt
│   │   ├── service/
│   │   │   └── InstagramReelsBlockerService.kt   <-- Detects Reels Tab pill & "Send to chat"
│   │   └── ui/
│   │       ├── MainActivity.kt
│   │       ├── DashboardScreen.kt
│   │       ├── BlockActivity.kt                  <-- Floating pop-up modal
│   │       ├── FocusDmScreen.kt
│   │       ├── HelpScreen.kt
│   │       └── theme/
│   └── res/
│       ├── values/
│       │   ├── themes.xml                        <-- Translucent dialog window theme
│       │   └── strings.xml
│       └── xml/accessibility_service_config.xml
```

---

## 🚀 How to Run & Test

1. Open this project in **Android Studio**.
2. Run on your Android phone or emulator (`Shift + F10`).
3. In ScrollControl, turn on **ScrollControl Reels Blocker** under **Accessibility Settings**.
4. Test the pop-up screens directly via the **Test Reels Tab** and **Test DM Scroll** buttons on the dashboard.
5. In Instagram, open a Reel from a DM and swipe up: as soon as the **"Send to chat"** button appears, the scrolling lock activates!
