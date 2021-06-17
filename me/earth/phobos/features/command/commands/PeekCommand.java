package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import java.util.Map.Entry;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.misc.ToolTips;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;

public class PeekCommand extends Command {
   public PeekCommand() {
      super("peek", new String[]{"<player>"});
   }

   public void execute(String[] commands) {
      if (commands.length == 1) {
         ItemStack stack = mc.field_71439_g.func_184614_ca();
         if (stack == null || !(stack.func_77973_b() instanceof ItemShulkerBox)) {
            Command.sendMessage("§cYou need to hold a Shulker in your mainhand.");
            return;
         }

         ToolTips.displayInv(stack, (String)null);
      }

      if (commands.length > 1) {
         if (ToolTips.getInstance().isOn() && (Boolean)ToolTips.getInstance().shulkerSpy.getValue()) {
            Iterator var5 = ToolTips.getInstance().spiedPlayers.entrySet().iterator();

            while(var5.hasNext()) {
               Entry<EntityPlayer, ItemStack> entry = (Entry)var5.next();
               if (((EntityPlayer)entry.getKey()).func_70005_c_().equalsIgnoreCase(commands[0])) {
                  ItemStack stack = (ItemStack)entry.getValue();
                  ToolTips.displayInv(stack, ((EntityPlayer)entry.getKey()).func_70005_c_());
                  break;
               }
            }
         } else {
            Command.sendMessage("§cYou need to turn on Tooltips - ShulkerSpy");
         }
      }

   }
}
