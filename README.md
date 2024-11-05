# @boengli/capacitor-fullscreen

This Capacitor plugin allows you to enable fullscreen mode in your Android app, hiding the system status and navigation bars for a more immersive experience.


## Details

The Fullscreen functionality utilizes `WindowInsetsControllerCompat` to manage immersive mode on Android.

### Backwards Compatibility
The immersive mode as implemented is best supported on Android 8.0 (API 26) and above. 


## Installation

1. Install `npm i @boengli/capacitor-fullscreen`
2. In `/android/variable.gradle` make sure to have at least `androidxCoreVersion` >=  1.9.0
3. `npx cap sync android`


## Example Usage

```
import { Fullscreen } from '@boengli/capacitor-fullscreen';

try {
  await Fullscreen.activateImmersiveMode();
  console.log('Fullscreen enabled');
} catch (error) {
  console.error('Error enabling fullscreen:', error);
}

```


## iOS

You don't need a plugin. Just add this to your `Info.plist`:

```xml
<key>UIStatusBarHidden</key>
<true/>
<key>UIViewControllerBasedStatusBarAppearance</key>
<false/>
```

<br><br>


## Function Descriptions

### activateImmersiveMode

```typescript
activateImmersiveMode() => Promise<void>
```

Activates immersive mode, hiding both the status and navigation bars.

| Param | Type   | Description |
|-------|--------|-------------|
| N/A   | N/A    | This function does not take any parameters. |

**Returns**: A Promise that resolves when immersive mode is successfully activated or rejects with an error if it fails.

---

### deactivateImmersiveMode

```typescript
deactivateImmersiveMode() => Promise<void>
```

Deactivates immersive mode, restoring visibility to the status and navigation bars.

| Param | Type   | Description |
|-------|--------|-------------|
| N/A   | N/A    | This function does not take any parameters. |

**Returns**: A Promise that resolves when immersive mode is successfully deactivated or rejects with an error if it fails.

---

### setLegacyFallbackEnabled

```typescript
setLegacyFallbackEnabled(useLegacyFallback: boolean) => Promise<void>
```

Enables or disables the legacy immersive mode fallback for Android devices running versions lower than Android 11. This allows the plugin to fall back to using `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` for consistent behavior on older devices.

| Param              | Type    | Description                                                                 |
|--------------------|---------|-----------------------------------------------------------------------------|
| `useLegacyFallback` | boolean | Set to `true` (default) to enable legacy fallback for older Android versions, or `false` to disable it. |

**Returns**: A Promise that resolves when the fallback setting is applied or rejects with an error if it fails.
