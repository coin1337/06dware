package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoHunger extends Module {
   public Setting<Boolean> cancelSprint = this.register(new Setting("CancelSprint", true));

   public NoHunger() {
      super("NoHunger", "Prevents you from getting Hungry", Module.Category.PLAYER, true, false, false);
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof CPacketPlayer) {
         CPacketPlayer packet = (CPacketPlayer)event.getPacket();
         packet.field_149474_g = mc.field_71439_g.field_70143_R >= 0.0F || mc.field_71442_b.field_78778_j;
      }

      if ((Boolean)this.cancelSprint.getValue() && event.getPacket() instanceof CPacketEntityAction) {
         CPacketEntityAction packet = (CPacketEntityAction)event.getPacket();
         if (packet.func_180764_b() == Action.START_SPRINTING || packet.func_180764_b() == Action.STOP_SPRINTING) {
            event.setCanceled(true);
         }
      }

   }
}
