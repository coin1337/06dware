package me.earth.phobos;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.phobos.features.modules.misc.RPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

public class DiscordPresence {
   public static DiscordRichPresence presence;
   private static final DiscordRPC rpc;
   private static Thread thread;

   public static void start() {
      DiscordEventHandlers handlers = new DiscordEventHandlers();
      rpc.Discord_Initialize("737779695134834695", handlers, true, "");
      presence.startTimestamp = System.currentTimeMillis() / 1000L;
      presence.details = Minecraft.func_71410_x().field_71462_r instanceof GuiMainMenu ? "In the main menu." : "Playing " + (Minecraft.func_71410_x().field_71422_O != null ? ((Boolean)RPC.INSTANCE.showIP.getValue() ? "on " + Minecraft.func_71410_x().field_71422_O.field_78845_b + "." : " multiplayer.") : " singleplayer.");
      presence.state = (String)RPC.INSTANCE.state.getValue();
      presence.largeImageKey = "phobos";
      presence.largeImageText = "3arthh4ck 1.7.2";
      rpc.Discord_UpdatePresence(presence);
      thread = new Thread(() -> {
         while(!Thread.currentThread().isInterrupted()) {
            rpc.Discord_RunCallbacks();
            presence.details = "Playing " + (Minecraft.func_71410_x().field_71422_O != null ? ((Boolean)RPC.INSTANCE.showIP.getValue() ? "on " + Minecraft.func_71410_x().field_71422_O.field_78845_b + "." : " multiplayer.") : " singleplayer.");
            presence.state = (String)RPC.INSTANCE.state.getValue();
            rpc.Discord_UpdatePresence(presence);

            try {
               Thread.sleep(2000L);
            } catch (InterruptedException var1) {
            }
         }

      }, "RPC-Callback-Handler");
      thread.start();
   }

   public static void stop() {
      if (thread != null && !thread.isInterrupted()) {
         thread.interrupt();
      }

      rpc.Discord_Shutdown();
   }

   static {
      rpc = DiscordRPC.INSTANCE;
      presence = new DiscordRichPresence();
   }
}
