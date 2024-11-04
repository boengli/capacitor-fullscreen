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


Backwards Compatibility
The immersive mode as implemented is best supported on Android 8.0 (API level 26) and above. While the base WindowInsetsControllerCompat can work on devices running Android 5.0 (API level 21), users may not experience the same level of functionality and behavior.


## Example Usage

```import { Component } from '@angular/core';
import { Fullscreen } from '@boengli/capacitor-fullscreen';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {

  constructor() {
    this.enterFullscreen();
  }

  async enterFullscreen() {
    try {
      await Fullscreen.activateImmersiveMode();
      console.log('Fullscreen enabled');
    } catch (error) {
      console.error('Error enabling fullscreen:', error);
    }
  }
}
```


## Android

1. Install the plugin :: npm i @boengli/capacitor-fullscreen
2. In `/android/variable.gradle` make sure to have at least `androidxCoreVersion` >= 1.5.0


## iOS

You don't need a plugin. Just add this to your `Info.plist`:

```xml
<key>UIStatusBarHidden</key>
<true/>
<key>UIViewControllerBasedStatusBarAppearance</key>
<false/>
```
