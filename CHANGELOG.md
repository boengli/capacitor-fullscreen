# Changelog

## [0.0.20-beta.3] - 2026-05-26

- Fixed: `statusBarColor`/`navigationBarColor` now only set on API < 35 (ignored by Android 15+ edge-to-edge enforcement)
- Added dual CJS + ESM build output (`dist/esm/index.js`)
- Added `"module"` field to `package.json` for bundlers that support tree-shaking (Vite, Rollup, webpack 5)

## [0.0.20-beta.2]

- Fixed: `activateOnLoad: true` (the default) now correctly hides the system bars on plugin load
- Internal: removed unused `isImmersiveModeSupported()` and `preloadImmersiveModeResources()` functions
- Internal: suppressed deprecation warnings on `statusBarColor`/`navigationBarColor` (still needed for transparent transient bars on API 24–34)
- Internal: fixed `tsconfig.json` lib target alignment

## [0.0.20-beta.1]

- Fixed: fullscreen activates on plugin load even when not called (fixes [#1](https://github.com/boengli/capacitor-fullscreen/issues/1))
- Added `activateOnLoad` config option (default `true`) — set to `false` in `capacitor.config.ts` to control fullscreen manually; default remains `true` for backwards compatibility

## [0.0.20-beta.0] - Capacitor 8 migration

- Migrated to Capacitor 8 (`@capacitor/core >= 8.0.0`)
- Updated Android Gradle Plugin to 8.13.0, Kotlin to 2.2.20
- Updated `compileSdk` / `targetSdk` to 36, `minSdk` to 24 (Android 7.0+)
- Updated `androidx.core:core-ktx` to 1.17.0
- Updated TypeScript to 5.x, compiler target to ES2020
- Requires Java 21
- **Breaking:** minimum Capacitor version is now 8.0.0
- **Breaking:** minimum Android version is API 24 (Android 7.0)

## [0.0.19] - 2025-03-21

- Updated for Capacitor 7
- Requires Android 11.0 (API 30) and above, Java 21

## [0.0.18] - 2025-03-20

- **Breaking:** dropped legacy Android support; streamlined for Android 11.0 (API 30) and above

## [0.0.17] - 2024-12-02

- Added 3-second timeout and improved focus handling for immersive mode
- Upgraded Kotlin to 1.9.10, Android Gradle Plugin to 8.2.1

## [0.0.16] - 2024-11-08

- Fixed threading issues by ensuring all UI operations run on the main thread

## [0.0.15] - 2024-11-06

- Added backward compatibility for pre-API 30 devices using `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` flags
- Replaced deprecated `setOnSystemUiVisibilityChangeListener` with API 30+ alternative while retaining fallback
- Added `useLegacyFallback` option to enable/disable legacy immersive mode on Android 10 and below (enabled by default)

## [0.0.14] - 2024-11-05

- Fixed immersive mode persistence: added focus change listener to re-apply immersive mode when the app window regains focus
- Removed redundant null checks
- Improved thread safety and memory leak prevention
