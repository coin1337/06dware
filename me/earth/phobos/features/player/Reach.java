package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class Reach extends Module {
   public Setting<Boolean> override = this.register(new Setting("Override", false));
   public Setting<Float> add = this.register(new Setting("Add", 3.0F, (v) -> {
      return !(Boolean)this.override.getValue();
   }));
   public Setting<Float> reach = this.register(new Setting("Reach", 6.0F, (v) -> {
      return (Boolean)this.override.getValue();
   }));
   private static Reach INSTANCE = new Reach();

   public Reach() {
      super("Reach", "Extends your block reach", Module.Category.PLAYER, true, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Reach getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Reach();
      }

      return INSTANCE;
   }

   public String getDisplayInfo() {
      return (Boolean)this.override.getValue() ? ((Float)this.reach.getValue()).toString() : ((Float)this.add.getValue()).toString();
   }
}
