package me.earth.phobos.features.modules.misc;

import java.util.Random;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

public class NoAFK extends Module {
   private final Setting<Boolean> swing = this.register(new Setting("Swing", true));
   private final Setting<Boolean> turn = this.register(new Setting("Turn", true));
   private final Random random = new Random();

   public NoAFK() {
      super("NoAFK", "Prevents you from getting kicked for afk.", Module.Category.MISC, false, false, false);
   }

   public void onUpdate() {
      if (!mc.field_71442_b.func_181040_m()) {
         if (mc.field_71439_g.field_70173_aa % 40 == 0 && (Boolean)this.swing.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.MAIN_HAND));
         }

         if (mc.field_71439_g.field_70173_aa % 15 == 0 && (Boolean)this.turn.getValue()) {
            mc.field_71439_g.field_70177_z = (float)(this.random.nextInt(360) - 180);
         }

         if (!(Boolean)this.swing.getValue() && !(Boolean)this.turn.getValue() && mc.field_71439_g.field_70173_aa % 80 == 0) {
            mc.field_71439_g.func_70664_aZ();
         }

      }
   }
}
