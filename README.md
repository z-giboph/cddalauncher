# CDDA Launcher for Android

A custom launcher for **Cataclysm: Dark Days Ahead** on Android with a post-apocalyptic UI and mobile-friendly controls.

## Features
- 🎮 Mobile-optimized D-pad + action button layout
- 🖥️ Terminal-style dark UI (monospace, green accent)
- 📦 Mod manager screen (stub — ready to expand)
- ⚙️ Settings screen (stub — ready to expand)
- ⬇️ Direct link to official CDDA GitHub releases

## Project Structure
```
app/src/main/
├── java/com/cddalauncher/
│   ├── MainActivity.kt       ← Home screen + launch logic
│   ├── ControlsActivity.kt   ← On-screen D-pad & action buttons
│   └── StubActivities.kt     ← Settings & Mods placeholders
└── res/
    ├── layout/
    │   ├── activity_main.xml     ← Main launcher UI
    │   └── activity_controls.xml ← Controls overlay UI
    └── values/
        ├── colors.xml   ← Dark terminal palette
        └── styles.xml   ← Button & theme styles
```

## How to Build
1. Open project in **Android Studio Hedgehog** or newer
2. Sync Gradle
3. Build → Generate Signed APK or run on device/emulator

## Requirements
- Android 8.0+ (minSdk 26)
- CDDA official APK installed separately (from CleverRaven GitHub releases)
- Storage permission granted on first launch

## Controls Layout (Landscape)
```
[ D-PAD ]                          [ ACTION PAD ]
    ▲                               [✓] [✕]
  ◀   ▶                             [BAG] [GET]
    ▼                               [DROP] [WAIT]

[ CRAFT ] [ MAP ] [ SMASH ] [ FIRE ]   ← Bottom hotbar
```

## Next Steps / TODO
- [ ] Add overlay mode (floating buttons on top of CDDA)
- [ ] Swipe gesture support
- [ ] Auto-update checker via GitHub API
- [ ] Mod enable/disable with file management
- [ ] Custom keybinding editor
- [ ] Font size / tile vs ASCII toggle in Settings
