package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class CameraClip extends Module {
   public Setting<Boolean> extend = this.register(new Setting("Extend", false));
   public Setting<Double> distance = this.register(new Setting("Distance", 10.0D, 0.0D, 50.0D, (v) -> {
      return (Boolean)this.extend.getValue();
   }, "By how much you want to extend the distance."));
   private static CameraClip INSTANCE = new CameraClip();

   public CameraClip() {
      super("CameraClip", "Makes your Camera clip.", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static CameraClip getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new CameraClip();
      }

      return INSTANCE;
   }
}
