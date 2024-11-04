# @boengli/capacitor-fullscreen

This Capacitor plugin allows you to enable fullscreen mode in your Android application, effectively hiding the system status and navigation bars for an immersive experience.

**Important Note:**
This plugin is currently under development and has not been fully tested yet.


## Install

```bash
npm install @boengli/capacitor-fullscreen
npx cap sync
```


## Details

The Fullscreen functionality utilizes the WindowInsetsControllerCompat class to manage immersive mode in Android.

### Backwards Compatibility
The immersive mode as implemented is best supported on Android 8.0 (API level 26) and above. While the base WindowInsetsControllerCompat can work on devices running Android 5.0 (API level 21), users may not experience the same level of functionality and behavior.


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

## Android

1. Install the plugin :: `npm i @boengli/capacitor-fullscreen`
2. In `/android/variable.gradle` make sure to have at least `androidxCoreVersion` >=  1.9.0
3. `npx cap sync android`


## iOS

You don't need a plugin. Just add this to your `Info.plist`:

```xml
<key>UIStatusBarHidden</key>
<true/>
<key>UIViewControllerBasedStatusBarAppearance</key>
<false/>
```
