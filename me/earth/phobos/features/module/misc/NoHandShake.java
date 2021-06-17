package me.earth.phobos.features.modules.misc;

import io.netty.buffer.Unpooled;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class NoHandShake extends Module {
   public NoHandShake() {
      super("NoHandshake", "Doesnt send your modlist to the server.", Module.Category.MISC, true, false, false);
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof FMLProxyPacket && !mc.func_71356_B()) {
         event.setCanceled(true);
      }

      if (event.getPacket() instanceof CPacketCustomPayload) {
         CPacketCustomPayload packet = (CPacketCustomPayload)event.getPacket();
         if (packet.func_149559_c().equals("MC|Brand")) {
            packet.field_149561_c = (new PacketBuffer(Unpooled.buffer())).func_180714_a("vanilla");
         }
      }

   }
}
