package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;

public class EchestBP extends Module {
   private GuiScreen echestScreen = null;

   public EchestBP() {
      super("EchestBP", "Allows to open your echest later.", Module.Category.PLAYER, false, false, false);
   }

   public void onUpdate() {
      if (mc.field_71462_r instanceof GuiContainer) {
         Container container = ((GuiContainer)mc.field_71462_r).field_147002_h;
         if (container instanceof ContainerChest && ((ContainerChest)container).func_85151_d() instanceof InventoryBasic) {
            InventoryBasic basic = (InventoryBasic)((ContainerChest)container).func_85151_d();
            if (basic.func_70005_c_().equalsIgnoreCase("Ender Chest")) {
               this.echestScreen = mc.field_71462_r;
               mc.field_71462_r = null;
            }
         }
      }

   }

   public void onDisable() {
      if (!fullNullCheck() && this.echestScreen != null) {
         mc.func_147108_a(this.echestScreen);
      }

      this.echestScreen = null;
   }
}
