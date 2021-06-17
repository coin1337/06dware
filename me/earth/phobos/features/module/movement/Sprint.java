package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
   public Setting<Sprint.Mode> mode;
   private static Sprint INSTANCE = new Sprint();

   public Sprint() {
      super("Sprint", "Modifies sprinting", Module.Category.MOVEMENT, false, false, false);
      this.mode = this.register(new Setting("Mode", Sprint.Mode.LEGIT));
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Sprint getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Sprint();
      }

      return INSTANCE;
   }

   @SubscribeEvent
   public void onSprint(MoveEvent event) {
      if (event.getStage() == 1 && this.mode.getValue() == Sprint.Mode.RAGE && (mc.field_71439_g.field_71158_b.field_192832_b != 0.0F || mc.field_71439_g.field_71158_b.field_78902_a != 0.0F)) {
         event.setCanceled(true);
      }

   }

   public void onUpdate() {
      switch((Sprint.Mode)this.mode.getValue()) {
      case RAGE:
         if ((mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d()) && !mc.field_71439_g.func_70093_af() && !mc.field_71439_g.field_70123_F && !((float)mc.field_71439_g.func_71024_bL().func_75116_a() <= 6.0F)) {
            mc.field_71439_g.func_70031_b(true);
         }
         break;
      case LEGIT:
         if (mc.field_71474_y.field_74351_w.func_151470_d() && !mc.field_71439_g.func_70093_af() && !mc.field_71439_g.func_184587_cr() && !mc.field_71439_g.field_70123_F && !((float)mc.field_71439_g.func_71024_bL().func_75116_a() <= 6.0F) && mc.field_71462_r == null) {
            mc.field_71439_g.func_70031_b(true);
         }
      }

   }

   public void onDisable() {
      if (!nullCheck()) {
         mc.field_71439_g.func_70031_b(false);
      }

   }

   public String getDisplayInfo() {
      return this.mode.currentEnumName();
   }

   public static enum Mode {
      LEGIT,
      RAGE;
   }
}
