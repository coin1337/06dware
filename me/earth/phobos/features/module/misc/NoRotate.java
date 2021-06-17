package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {
   private Setting<Integer> waitDelay = this.register(new Setting("Delay", 2500, 0, 10000));
   private Timer timer = new Timer();
   private boolean cancelPackets = true;
   private boolean timerReset = false;

   public NoRotate() {
      super("NoRotate", "Dangerous to use might desync you.", Module.Category.MISC, true, false, false);
   }

   public void onLogout() {
      this.cancelPackets = false;
   }

   public void onLogin() {
      this.timer.reset();
      this.timerReset = true;
   }

   public void onUpdate() {
      if (this.timerReset && !this.cancelPackets && this.timer.passedMs((long)(Integer)this.waitDelay.getValue())) {
         Command.sendMessage("<NoRotate> §cThis module might desync you!");
         this.cancelPackets = true;
         this.timerReset = false;
      }

   }

   public void onEnable() {
      Command.sendMessage("<NoRotate> §cThis module might desync you!");
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getStage() == 0 && this.cancelPackets && event.getPacket() instanceof SPacketPlayerPosLook) {
         SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
         packet.field_148936_d = mc.field_71439_g.field_70177_z;
         packet.field_148937_e = mc.field_71439_g.field_70125_A;
      }

   }
}
