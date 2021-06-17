package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;

public class StairSpeed extends Module {
   public StairSpeed() {
      super("StairSpeed", "Great module", Module.Category.MOVEMENT, true, false, false);
   }

   public void onUpdate() {
      if (mc.field_71439_g.field_70122_E && mc.field_71439_g.field_70163_u - Math.floor(mc.field_71439_g.field_70163_u) > 0.0D && mc.field_71439_g.field_191988_bg != 0.0F) {
         mc.field_71439_g.func_70664_aZ();
      }

   }
}
