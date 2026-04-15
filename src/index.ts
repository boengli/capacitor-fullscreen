import { registerPlugin } from '@capacitor/core';

export interface FullscreenConfig {
  /**
   * Whether to activate immersive mode automatically when the plugin loads.
   * Set to false if you want to control fullscreen manually via activateImmersiveMode().
   * @default true
   */
  activateOnLoad?: boolean;
}

export interface FullscreenPlugin {
  /**
   * Activates immersive (fullscreen) mode, hiding the system status bar and navigation bar.
   */
  activateImmersiveMode(): Promise<void>;

  /**
   * Deactivates immersive mode, restoring the system status bar and navigation bar.
   */
  deactivateImmersiveMode(): Promise<void>;
}

const Fullscreen = registerPlugin<FullscreenPlugin>('Fullscreen');

export { Fullscreen };
