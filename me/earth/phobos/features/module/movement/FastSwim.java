package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastSwim extends Module {
   public Setting<Double> waterHorizontal = this.register(new Setting("WaterHorizontal", 3.0D, 1.0D, 10.0D));
   public Setting<Double> waterVertical = this.register(new Setting("WaterVertical", 3.0D, 1.0D, 10.0D));
   public Setting<Double> lavaHorizontal = this.register(new Setting("LavaHorizontal", 4.0D, 1.0D, 10.0D));
   public Setting<Double> lavaVertical = this.register(new Setting("LavaVertical", 4.0D, 1.0D, 10.0D));

   public FastSwim() {
      super("FastSwim", "Swim fast", Module.Category.MOVEMENT, true, false, false);
   }

   @SubscribeEvent
   public void onMove(MoveEvent event) {
      if (mc.field_71439_g.func_180799_ab() && !mc.field_71439_g.field_70122_E) {
         event.setX(event.getX() * (Double)this.lavaHorizontal.getValue());
         event.setZ(event.getZ() * (Double)this.lavaHorizontal.getValue());
         event.setY(event.getY() * (Double)this.lavaVertical.getValue());
      } else if (mc.field_71439_g.func_70090_H() && !mc.field_71439_g.field_70122_E) {
         event.setX(event.getX() * (Double)this.waterHorizontal.getValue());
         event.setZ(event.getZ() * (Double)this.waterHorizontal.getValue());
         event.setY(event.getY() * (Double)this.waterVertical.getValue());
      }

   }
}
