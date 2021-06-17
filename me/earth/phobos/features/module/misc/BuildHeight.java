package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuildHeight extends Module {
   private Setting<Integer> height = this.register(new Setting("Height", 255, 0, 255));

   public BuildHeight() {
      super("BuildHeight", "Allows you to place at build height", Module.Category.MISC, true, false, false);
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
         CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
         if (packet.func_187023_a().func_177956_o() >= (Integer)this.height.getValue() && packet.func_187024_b() == EnumFacing.UP) {
            packet.field_149579_d = EnumFacing.DOWN;
         }
      }

   }
}
