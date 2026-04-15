import { registerPlugin } from '@capacitor/core';

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
