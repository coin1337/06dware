package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class SmallShield extends Module {
   public Setting<Boolean> normalOffset = this.register(new Setting("OffNormal", false));
   public Setting<Float> offset = this.register(new Setting("Offset", 0.7F, 0.0F, 1.0F, (v) -> {
      return (Boolean)this.normalOffset.getValue();
   }));
   public Setting<Float> offX = this.register(new Setting("OffX", 0.0F, -1.0F, 1.0F, (v) -> {
      return !(Boolean)this.normalOffset.getValue();
   }));
   public Setting<Float> offY = this.register(new Setting("OffY", 0.0F, -1.0F, 1.0F, (v) -> {
      return !(Boolean)this.normalOffset.getValue();
   }));
   public Setting<Float> mainX = this.register(new Setting("MainX", 0.0F, -1.0F, 1.0F));
   public Setting<Float> mainY = this.register(new Setting("MainY", 0.0F, -1.0F, 1.0F));
   private static SmallShield INSTANCE = new SmallShield();

   public SmallShield() {
      super("SmallShield", "Makes you offhand lower.", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public void onUpdate() {
      if ((Boolean)this.normalOffset.getValue()) {
         mc.field_71460_t.field_78516_c.field_187471_h = (Float)this.offset.getValue();
      }

   }

   public static SmallShield getINSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new SmallShield();
      }

      return INSTANCE;
   }
}
