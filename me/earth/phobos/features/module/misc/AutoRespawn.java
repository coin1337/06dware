package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoRespawn extends Module {
   public Setting<Boolean> antiDeathScreen = this.register(new Setting("AntiDeathScreen", true));
   public Setting<Boolean> deathCoords = this.register(new Setting("DeathCoords", false));
   public Setting<Boolean> respawn = this.register(new Setting("Respawn", true));

   public AutoRespawn() {
      super("AutoRespawn", "Respawns you when you die.", Module.Category.MISC, true, false, false);
   }

   @SubscribeEvent
   public void onDisplayDeathScreen(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiGameOver) {
         if ((Boolean)this.deathCoords.getValue() && event.getGui() instanceof GuiGameOver) {
            Command.sendMessage(String.format("You died at x %d y %d z %d", (int)mc.field_71439_g.field_70165_t, (int)mc.field_71439_g.field_70163_u, (int)mc.field_71439_g.field_70161_v));
         }

         if ((Boolean)this.respawn.getValue() && mc.field_71439_g.func_110143_aJ() <= 0.0F || (Boolean)this.antiDeathScreen.getValue() && mc.field_71439_g.func_110143_aJ() > 0.0F) {
            event.setCanceled(true);
            mc.field_71439_g.func_71004_bE();
         }
      }

   }
}
