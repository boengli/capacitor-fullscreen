declare module "@boengli/capacitor-fullscreen" {
    export class Fullscreen {
      // Method to activate immersive mode
      static activateImmersiveMode(): Promise<void>;
      // Method to deactivate immersive mode, if needed
      static deactivateImmersiveMode(): Promise<void>;
    }
  }