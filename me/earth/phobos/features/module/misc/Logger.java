package me.earth.phobos.features.modules.misc;

import java.lang.reflect.Field;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.network.Packet;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Logger extends Module {
   public Setting<Logger.Packets> packets;
   public Setting<Boolean> chat;
   public Setting<Boolean> fullInfo;
   public Setting<Boolean> noPing;

   public Logger() {
      super("Logger", "Logs stuff", Module.Category.MISC, true, false, false);
      this.packets = this.register(new Setting("Packets", Logger.Packets.OUTGOING));
      this.chat = this.register(new Setting("Chat", false));
      this.fullInfo = this.register(new Setting("FullInfo", false));
      this.noPing = this.register(new Setting("NoPing", false));
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onPacketSend(PacketEvent.Send event) {
      if (!(Boolean)this.noPing.getValue() || !(mc.field_71462_r instanceof GuiMultiplayer)) {
         if (this.packets.getValue() == Logger.Packets.OUTGOING || this.packets.getValue() == Logger.Packets.ALL) {
            if ((Boolean)this.chat.getValue()) {
               Command.sendMessage(event.getPacket().toString());
            } else {
               this.writePacketOnConsole(event.getPacket(), false);
            }
         }

      }
   }

   @SubscribeEvent(
      receiveCanceled = true
   )
   public void onPacketReceive(PacketEvent.Receive event) {
      if (!(Boolean)this.noPing.getValue() || !(mc.field_71462_r instanceof GuiMultiplayer)) {
         if (this.packets.getValue() == Logger.Packets.INCOMING || this.packets.getValue() == Logger.Packets.ALL) {
            if ((Boolean)this.chat.getValue()) {
               Command.sendMessage(event.getPacket().toString());
            } else {
               this.writePacketOnConsole(event.getPacket(), true);
            }
         }

      }
   }

   private void writePacketOnConsole(Packet<?> packet, boolean in) {
      if ((Boolean)this.fullInfo.getValue()) {
         System.out.println((in ? "In: " : "Send: ") + packet.getClass().getSimpleName() + " {");

         try {
            for(Class clazz = packet.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
               Field[] var4 = clazz.getDeclaredFields();
               int var5 = var4.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Field field = var4[var6];
                  if (field != null) {
                     if (!field.isAccessible()) {
                        field.setAccessible(true);
                     }

                     System.out.println(StringUtils.func_76338_a("      " + field.getType().getSimpleName() + " " + field.getName() + " : " + field.get(packet)));
                  }
               }
            }
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         System.out.println("}");
      } else {
         System.out.println(packet.toString());
      }

   }

   public static enum Packets {
      NONE,
      INCOMING,
      OUTGOING,
      ALL;
   }
}
