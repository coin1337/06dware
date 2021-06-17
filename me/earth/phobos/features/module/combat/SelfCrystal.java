package me.earth.phobos.features.modules.combat;

import me.earth.phobos.features.modules.Module;

public class SelfCrystal extends Module {
   public SelfCrystal() {
      super("SelfCrystal", "Best module", Module.Category.COMBAT, true, false, false);
   }

   public void onTick() {
      if (AutoCrystal.getInstance().isEnabled()) {
         AutoCrystal.target = mc.field_71439_g;
      }

   }
}
