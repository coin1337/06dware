package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.TextUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;

public class KitDelete extends Module {
   private Setting<Bind> deleteKey = this.register(new Setting("Key", new Bind(-1)));
   private boolean keyDown;

   public KitDelete() {
      super("KitDelete", "Automates /deleteukit", Module.Category.MISC, false, false, false);
   }

   public void onTick() {
      if (((Bind)this.deleteKey.getValue()).getKey() != -1) {
         if (mc.field_71462_r instanceof GuiContainer && Keyboard.isKeyDown(((Bind)this.deleteKey.getValue()).getKey())) {
            Slot slot = ((GuiContainer)mc.field_71462_r).getSlotUnderMouse();
            if (slot != null && !this.keyDown) {
               mc.field_71439_g.func_71165_d("/deleteukit " + TextUtil.stripColor(slot.func_75211_c().func_82833_r()));
               this.keyDown = true;
            }
         } else if (this.keyDown) {
            this.keyDown = false;
         }
      }

   }
}
