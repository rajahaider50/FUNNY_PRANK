# Funny Prank

A premium, 100% offline native Android application — **Funny Prank**, a funny audio / soundboard app.

This repository contains **Phase 1** of the application, built step-by-step as a real native Android project (Kotlin + Jetpack Compose). Phase 1 covers only the on-boarding experience:

1. **Splash Screen** — premium dark glassmorphism launch with animated loading progress
2. **Intro Screen 1** — Welcome · "Your Funny Soundboard"
3. **Intro Screen 2** — Your Library · "Import Your Audio"
4. **Intro Screen 3** — Instant Playback · "Play Anytime"
5. **Intro Screen 4** — Ready · "Built For Fun" (GET STARTED)
6. **Coming Soon** — temporary placeholder for the next feature phase (BACK TO INTRO)

> This is **NOT** a WebView wrapper or an HTML-to-APK conversion. It is a proper native Android project.

## Design System (LOCKED)

One permanent visual identity across all screens — **BLACK + RED + GREEN + GLASS**:

| Role | Color |
|------|-------|
| Primary background | `#050608` |
| Secondary background | `#080A0D` |
| Deep background | `#040507` |
| Primary red | `#FF3150` |
| Deep red | `#D7193B` |
| Primary green | `#18E58B` |
| Deep green | `#0CAF69` |
| Primary text | `#F8FAFC` |
| Secondary text | `#89939D` |
| Muted text | `#454D55` |
| Glass | `rgba(255,255,255,0.055)` |
| Glass border | `rgba(255,255,255,0.105)` |

- Style: **Premium Dark Glassmorphism**, modern, minimal, cinematic
- Typography: Inter-style modern sans-serif (native system stack)
- Icons: professional vector icons (Material Icons). **No emoji as UI icons.**
- Animations: smooth, natural, premium (no aggressive effects)

All color/typography/radius/glass tokens are centralized in `ui/theme/`.

## Current Scope (Phase 1)

Implemented:

- [x] Premium native Splash Screen (animated loading, ambient red/green glow, glass logo frame)
- [x] Four intro pages (swipe + Next + Skip + gradient page dots)
- [x] GET STARTED action on the final page
- [x] Temporary Coming Soon screen with BACK TO INTRO
- [x] Consistent locked design system
- [x] Screen-fitting / safe-area handling (status & navigation bars)
- [x] GitHub Actions workflow producing a debug + signed release APK

**Not yet implemented (future phases):** permission system, home dashboard, audio library, upload/ZIP/folder import, editing, settings, native audio/Bluetooth/microphone system, Firebase/Supabase/cloud.

## Tech Stack

- Kotlin 2.0.21, AGP 8.5.2, Gradle 8.7
- Jetpack Compose (BOM 2024.09.00) with Material 3
- Material Icons Extended (professional vector icons)
- Edge-to-edge / window insets for safe-area handling

## Project Layout

```
app/src/main/java/com/funnyprank/app/
├── MainActivity.kt          Root stage switch: Splash -> Intro -> Coming Soon
├── ui/
│   ├── screens/             SplashScreen, IntroScreen, ComingSoonScreen
│   ├── components/          PrankBackdrop (ambient background), PrankGlassCard
│   └── theme/               Color (locked tokens), Type, Theme
```

## Assets

Place these files in `app/src/main/res/drawable-nodpi/`:

- `logo.png`   — application logo (gradle-bundled, do not replace)
- `intro1.png` — intro page 1
- `intro2.png` — intro page 2
- `intro3.png` — intro page 3
- `intro4.png` — intro page 4

## Build

```bash
gradle assembleDebug          # debug APK
gradle assembleRelease        # signed release (needs KEYSTORE_FILE, KEYSTORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD env)
```

## CI

`.github/workflows/build-apk.yml` checks out the repo, sets up JDK 17, builds the project on every push / manual dispatch, and uploads debug + signed release APK artifacts (and publishes a GitHub Release).
