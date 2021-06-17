package me.earth.phobos.features.modules.combat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorMessage extends Module {
   private final Setting<Integer> armorThreshhold = this.register(new Setting("Armor%", 20, 1, 100));
   private final Setting<Boolean> notifySelf = this.register(new Setting("NotifySelf", true));
   private final Setting<Boolean> notification = this.register(new Setting("Notification", true));
   private final Map<EntityPlayer, Integer> entityArmorArraylist = new HashMap();
   private final Timer timer = new Timer();

   public ArmorMessage() {
      super("ArmorMessage", "Message friends when their armor is low", Module.Category.COMBAT, true, false, false);
   }

   @SubscribeEvent
   public void onUpdate(UpdateWalkingPlayerEvent event) {
      Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

      label65:
      while(true) {
         EntityPlayer player;
         do {
            do {
               if (!var2.hasNext()) {
                  return;
               }

               player = (EntityPlayer)var2.next();
            } while(player.field_70128_L);
         } while(!Phobos.friendManager.isFriend(player.func_70005_c_()));

         Iterator var4 = player.field_71071_by.field_70460_b.iterator();

         while(true) {
            ItemStack stack;
            do {
               if (!var4.hasNext()) {
                  if (this.entityArmorArraylist.containsKey(player) && player.field_71071_by.field_70460_b.get((Integer)this.entityArmorArraylist.get(player)) == ItemStack.field_190927_a) {
                     this.entityArmorArraylist.remove(player);
                  }
                  continue label65;
               }

               stack = (ItemStack)var4.next();
            } while(stack == ItemStack.field_190927_a);

            int percent = DamageUtil.getRoundedDamage(stack);
            if (percent <= (Integer)this.armorThreshhold.getValue() && !this.entityArmorArraylist.containsKey(player)) {
               if (player == mc.field_71439_g && (Boolean)this.notifySelf.getValue()) {
                  Command.sendMessage(player.func_70005_c_() + " watchout your " + this.getArmorPieceName(stack) + " low dura!", (Boolean)this.notification.getValue());
               } else {
                  mc.field_71439_g.func_71165_d("/msg " + player.func_70005_c_() + " " + player.func_70005_c_() + " watchout your " + this.getArmorPieceName(stack) + " low dura!");
               }

               this.entityArmorArraylist.put(player, player.field_71071_by.field_70460_b.indexOf(stack));
            }

            if (this.entityArmorArraylist.containsKey(player) && (Integer)this.entityArmorArraylist.get(player) == player.field_71071_by.field_70460_b.indexOf(stack) && percent > (Integer)this.armorThreshhold.getValue()) {
               this.entityArmorArraylist.remove(player);
            }
         }
      }
   }

   private String getArmorPieceName(ItemStack stack) {
      if (stack.func_77973_b() != Items.field_151161_ac && stack.func_77973_b() != Items.field_151169_ag && stack.func_77973_b() != Items.field_151028_Y && stack.func_77973_b() != Items.field_151020_U && stack.func_77973_b() != Items.field_151024_Q) {
         if (stack.func_77973_b() != Items.field_151163_ad && stack.func_77973_b() != Items.field_151171_ah && stack.func_77973_b() != Items.field_151030_Z && stack.func_77973_b() != Items.field_151023_V && stack.func_77973_b() != Items.field_151027_R) {
            return stack.func_77973_b() != Items.field_151173_ae && stack.func_77973_b() != Items.field_151149_ai && stack.func_77973_b() != Items.field_151165_aa && stack.func_77973_b() != Items.field_151022_W && stack.func_77973_b() != Items.field_151026_S ? "boots are" : "leggings are";
         } else {
            return "chestplate is";
         }
      } else {
         return "helmet is";
      }
   }
}
