package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLog extends Module {
   private Setting<Float> health = this.register(new Setting("Health", 16.0F, 0.1F, 36.0F));
   private Setting<Boolean> bed = this.register(new Setting("Beds", true));
   private Setting<Float> range = this.register(new Setting("BedRange", 6.0F, 0.1F, 36.0F, (v) -> {
      return (Boolean)this.bed.getValue();
   }));
   private Setting<Boolean> logout = this.register(new Setting("LogoutOff", true));
   private static AutoLog INSTANCE = new AutoLog();

   public AutoLog() {
      super("AutoLog", "Logs when in danger.", Module.Category.MISC, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static AutoLog getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new AutoLog();
      }

      return INSTANCE;
   }

   public void onTick() {
      if (!nullCheck() && mc.field_71439_g.func_110143_aJ() <= (Float)this.health.getValue()) {
         Phobos.moduleManager.disableModule("AutoReconnect");
         mc.field_71439_g.field_71174_a.func_147297_a(new SPacketDisconnect(new TextComponentString("AutoLogged")));
         if ((Boolean)this.logout.getValue()) {
            this.disable();
         }
      }

   }

   @SubscribeEvent
   public void onReceivePacket(PacketEvent.Receive event) {
      if (event.getPacket() instanceof SPacketBlockChange && (Boolean)this.bed.getValue()) {
         SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
         if (packet.func_180728_a().func_177230_c() == Blocks.field_150324_C && mc.field_71439_g.func_174831_c(packet.func_179827_b()) <= MathUtil.square((Float)this.range.getValue())) {
            Phobos.moduleManager.disableModule("AutoReconnect");
            mc.field_71439_g.field_71174_a.func_147297_a(new SPacketDisconnect(new TextComponentString("AutoLogged")));
            if ((Boolean)this.logout.getValue()) {
               this.disable();
            }
         }
      }

   }
}
