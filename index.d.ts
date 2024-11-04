// index.d.ts
declare module "@boengli/capacitor-fullscreen" {
  export interface FullscreenPlugin {
      // Method to activate immersive mode
      activateImmersiveMode(): Promise<void>;
      // Method to deactivate immersive mode, if needed
      deactivateImmersiveMode(): Promise<void>;
  }

  const Fullscreen: FullscreenPlugin;
  export { Fullscreen };
}