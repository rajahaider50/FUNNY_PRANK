# Funny Prank 🤣

A premium, 100% offline Android sound-prank app built with **Kotlin** + **Jetpack Compose**. Import your own funny sounds (MP3 / WAV / OGG / M4A / AAC), pin favorites, route audio to the right output, and launch a professional floating glass soundboard over any other app — all legally and locally (no hacking, no root, no mic hijacking).

> **How it works (100% legal):** the app simply plays your chosen sound through the active output (speaker / wired / Bluetooth). Whatever app is in the foreground may naturally pick that up in its microphone — the same acoustic behavior as any media player. No injection, no accessibility tricks, no root.

## Features

- ⚡ **Lightning launch** — big round glowing button opens the floating soundboard
- 🧊 **Premium glassmorphism** — luxury navy gradient, neon orbs, frosted glass surfaces
- 📁 **Flexible import** — single audio file, whole folder, or ZIP (auto-extract) via SAF/storage
- ⭐ **Favorites pinned** — marked sounds stay on top of the grid
- 🔊 **Full audio routing** — Auto / Speaker / Wired / Bluetooth (via AudioManager + Media3/ExoPlayer)
- 🗂️ **Local storage only** — Room DB metadata + app-private audio files, no accounts, no cloud
- 🪟 **Floating ounce** — draggable glass bubble + professional sound panel over other apps
- 📳 **Background playback** — foreground service keeps the board alive

## Tech Stack

- Kotlin 2.0.21, AGP 8.5.2, Gradle 8.7
- Jetpack Compose (BOM 2024.09.00) with Material 3
- Navigation Compose, ViewModel + StateFlow
- Room (KSP) for metadata
- Media3 / ExoPlayer + low-level AudioTrack PCM engine
- AudioManager routing (speaker / wired / Bluetooth, SCO for BT)
- SAF + DocumentFile for import

## Build

```bash
gradle assembleDebug        # debug
gradle assembleRelease       # signed release (needs KEYSTORE_* env)
```

## Project Layout

```
app/src/main/java/com/funnyprank/app/
├── audio/        AudioRouting (AudioRouter), ExoPlayer (MediaPlaybackManager),
│                 low-level PCM engine (PcmAudioEngine) + PcmMixer
├── data/         Room entities, DAO, database, repository
├── import/       Single / folder / ZIP importer (SAF + local copy)
├── floating/     FloatingOverlayService (glass bubble + sound panel)
├── ui/           Theme (glass), ViewModel, navigation + screens
└── MainActivity  Root (permission gate -> dashboard)
```

## CI

`.github/workflows/build-apk.yml` builds debug + signed-release APKs on every push and auto-publishes an installable GitHub Release.
