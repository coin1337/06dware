package me.earth.phobos.features.command.commands;

import me.earth.phobos.features.command.Command;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand extends Command {
   public ReloadSoundCommand() {
      super("sound", new String[0]);
   }

   public void execute(String[] commands) {
      try {
         SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, mc.func_147118_V(), new String[]{"sndManager", "field_147694_f"});
         sndManager.func_148596_a();
         sendMessage("§aReloaded Sound System.");
      } catch (Exception var3) {
         System.out.println("Could not restart sound manager: " + var3.toString());
         var3.printStackTrace();
         sendMessage("§cCouldnt Reload Sound System!");
      }

   }
}
