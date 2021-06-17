package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fullbright extends Module {
   public Setting<Fullbright.Mode> mode;
   public Setting<Boolean> effects;
   private float previousSetting;

   public Fullbright() {
      super("Fullbright", "Makes your game brighter.", Module.Category.RENDER, true, false, false);
      this.mode = this.register(new Setting("Mode", Fullbright.Mode.GAMMA));
      this.effects = this.register(new Setting("Effects", false));
      this.previousSetting = 1.0F;
   }

   public void onEnable() {
      this.previousSetting = mc.field_71474_y.field_74333_Y;
   }

   public void onUpdate() {
      if (this.mode.getValue() == Fullbright.Mode.GAMMA) {
         mc.field_71474_y.field_74333_Y = 1000.0F;
      }

      if (this.mode.getValue() == Fullbright.Mode.POTION) {
         mc.field_71439_g.func_70690_d(new PotionEffect(MobEffects.field_76439_r, 5210));
      }

   }

   public void onDisable() {
      if (this.mode.getValue() == Fullbright.Mode.POTION) {
         mc.field_71439_g.func_184589_d(MobEffects.field_76439_r);
      }

      mc.field_71474_y.field_74333_Y = this.previousSetting;
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getStage() == 0 && event.getPacket() instanceof SPacketEntityEffect && (Boolean)this.effects.getValue()) {
         SPacketEntityEffect packet = (SPacketEntityEffect)event.getPacket();
         if (mc.field_71439_g != null && packet.func_149426_d() == mc.field_71439_g.func_145782_y() && (packet.func_149427_e() == 9 || packet.func_149427_e() == 15)) {
            event.setCanceled(true);
         }
      }

   }

   public static enum Mode {
      GAMMA,
      POTION;
   }
}
