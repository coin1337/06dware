package me.earth.phobos.features.modules.misc;

import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.PlayerUtil;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiVanish extends Module {
   private final Queue<UUID> toLookUp = new ConcurrentLinkedQueue();

   public AntiVanish() {
      super("AntiVanish", "Notifies you when players vanish", Module.Category.MISC, true, false, false);
   }

   @SubscribeEvent
   public void onPacketReceive(PacketEvent.Receive event) {
      if (event.getPacket() instanceof SPacketPlayerListItem) {
         SPacketPlayerListItem sPacketPlayerListItem = (SPacketPlayerListItem)event.getPacket();
         if (sPacketPlayerListItem.func_179768_b() == Action.UPDATE_LATENCY) {
            Iterator var3 = sPacketPlayerListItem.func_179767_a().iterator();

            while(var3.hasNext()) {
               AddPlayerData addPlayerData = (AddPlayerData)var3.next();

               try {
                  if (mc.func_147114_u().func_175102_a(addPlayerData.func_179962_a().getId()) == null) {
                     this.toLookUp.add(addPlayerData.func_179962_a().getId());
                  }
               } catch (Exception var6) {
                  var6.printStackTrace();
                  return;
               }
            }
         }
      }

   }

   public void onUpdate() {
      if (PlayerUtil.timer.passedS(5.0D)) {
         UUID lookUp = (UUID)this.toLookUp.poll();
         if (lookUp != null) {
            try {
               String name = PlayerUtil.getNameFromUUID(lookUp);
               if (name != null) {
                  Command.sendMessage("§c" + name + " has gone into vanish.");
               }
            } catch (Exception var3) {
            }

            PlayerUtil.timer.reset();
         }
      }

   }

   public void onLogout() {
      this.toLookUp.clear();
   }
}
