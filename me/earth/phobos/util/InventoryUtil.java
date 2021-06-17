package me.earth.phobos.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.phobos.Phobos;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil implements Util {
   public static void switchToHotbarSlot(int slot, boolean silent) {
      if (mc.field_71439_g.field_71071_by.field_70461_c != slot && slot >= 0) {
         if (silent) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            mc.field_71442_b.func_78765_e();
         } else {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71442_b.func_78765_e();
         }

      }
   }

   public static void switchToHotbarSlot(Class clazz, boolean silent) {
      int slot = findHotbarBlock(clazz);
      if (slot > -1) {
         switchToHotbarSlot(slot, silent);
      }

   }

   public static boolean isNull(ItemStack stack) {
      return stack == null || stack.func_77973_b() instanceof ItemAir;
   }

   public static int findHotbarBlock(Class clazz) {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a) {
            if (clazz.isInstance(stack.func_77973_b())) {
               return i;
            }

            if (stack.func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (clazz.isInstance(block)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   public static int findHotbarBlock(Block blockIn) {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block == blockIn) {
               return i;
            }
         }
      }

      return -1;
   }

   public static int getItemHotbar(Item input) {
      for(int i = 0; i < 9; ++i) {
         Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (Item.func_150891_b(item) == Item.func_150891_b(input)) {
            return i;
         }
      }

      return -1;
   }

   public static int findStackInventory(Item input) {
      return findStackInventory(input, false);
   }

   public static int findStackInventory(Item input, boolean withHotbar) {
      for(int i = withHotbar ? 0 : 9; i < 36; ++i) {
         Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (Item.func_150891_b(input) == Item.func_150891_b(item)) {
            return i + (i < 9 ? 36 : 0);
         }
      }

      return -1;
   }

   public static int findItemInventorySlot(Item item, boolean offHand) {
      AtomicInteger slot = new AtomicInteger();
      slot.set(-1);
      Iterator var3 = getInventoryAndHotbarSlots().entrySet().iterator();

      Entry entry;
      do {
         do {
            if (!var3.hasNext()) {
               return slot.get();
            }

            entry = (Entry)var3.next();
         } while(((ItemStack)entry.getValue()).func_77973_b() != item);
      } while((Integer)entry.getKey() == 45 && !offHand);

      slot.set((Integer)entry.getKey());
      return slot.get();
   }

   public static List<Integer> findEmptySlots(boolean withXCarry) {
      List<Integer> outPut = new ArrayList();
      Iterator var2 = getInventoryAndHotbarSlots().entrySet().iterator();

      while(true) {
         Entry entry;
         do {
            if (!var2.hasNext()) {
               if (withXCarry) {
                  for(int i = 1; i < 5; ++i) {
                     Slot craftingSlot = (Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
                     ItemStack craftingStack = craftingSlot.func_75211_c();
                     if (craftingStack.func_190926_b() || craftingStack.func_77973_b() == Items.field_190931_a) {
                        outPut.add(i);
                     }
                  }
               }

               return outPut;
            }

            entry = (Entry)var2.next();
         } while(!((ItemStack)entry.getValue()).field_190928_g && ((ItemStack)entry.getValue()).func_77973_b() != Items.field_190931_a);

         outPut.add(entry.getKey());
      }
   }

   public static int findInventoryBlock(Class clazz, boolean offHand) {
      AtomicInteger slot = new AtomicInteger();
      slot.set(-1);
      Iterator var3 = getInventoryAndHotbarSlots().entrySet().iterator();

      Entry entry;
      do {
         do {
            if (!var3.hasNext()) {
               return slot.get();
            }

            entry = (Entry)var3.next();
         } while(!isBlock(((ItemStack)entry.getValue()).func_77973_b(), clazz));
      } while((Integer)entry.getKey() == 45 && !offHand);

      slot.set((Integer)entry.getKey());
      return slot.get();
   }

   public static boolean isBlock(Item item, Class clazz) {
      if (item instanceof ItemBlock) {
         Block block = ((ItemBlock)item).func_179223_d();
         return clazz.isInstance(block);
      } else {
         return false;
      }
   }

   public static void confirmSlot(int slot) {
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
      mc.field_71439_g.field_71071_by.field_70461_c = slot;
      mc.field_71442_b.func_78765_e();
   }

   public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
      return getInventorySlots(9, 44);
   }

   private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
      int current = currentI;

      HashMap fullInventorySlots;
      for(fullInventorySlots = new HashMap(); current <= last; ++current) {
         fullInventorySlots.put(current, mc.field_71439_g.field_71069_bz.func_75138_a().get(current));
      }

      return fullInventorySlots;
   }

   public static boolean[] switchItem(boolean back, int lastHotbarSlot, boolean switchedItem, InventoryUtil.Switch mode, Class clazz) {
      boolean[] switchedItemSwitched = new boolean[]{switchedItem, false};
      switch(mode) {
      case NORMAL:
         if (!back && !switchedItem) {
            switchToHotbarSlot(findHotbarBlock(clazz), false);
            switchedItemSwitched[0] = true;
         } else if (back && switchedItem) {
            switchToHotbarSlot(lastHotbarSlot, false);
            switchedItemSwitched[0] = false;
         }

         switchedItemSwitched[1] = true;
         break;
      case SILENT:
         if (!back && !switchedItem) {
            switchToHotbarSlot(findHotbarBlock(clazz), true);
            switchedItemSwitched[0] = true;
         } else if (back && switchedItem) {
            switchedItemSwitched[0] = false;
            Phobos.inventoryManager.recoverSilent(lastHotbarSlot);
         }

         switchedItemSwitched[1] = true;
         break;
      case NONE:
         if (back) {
            switchedItemSwitched[1] = true;
         } else {
            switchedItemSwitched[1] = mc.field_71439_g.field_71071_by.field_70461_c == findHotbarBlock(clazz);
         }
      }

      return switchedItemSwitched;
   }

   public static boolean[] switchItemToItem(boolean back, int lastHotbarSlot, boolean switchedItem, InventoryUtil.Switch mode, Item item) {
      boolean[] switchedItemSwitched = new boolean[]{switchedItem, false};
      switch(mode) {
      case NORMAL:
         if (!back && !switchedItem) {
            switchToHotbarSlot(getItemHotbar(item), false);
            switchedItemSwitched[0] = true;
         } else if (back && switchedItem) {
            switchToHotbarSlot(lastHotbarSlot, false);
            switchedItemSwitched[0] = false;
         }

         switchedItemSwitched[1] = true;
         break;
      case SILENT:
         if (!back && !switchedItem) {
            switchToHotbarSlot(getItemHotbar(item), true);
            switchedItemSwitched[0] = true;
         } else if (back && switchedItem) {
            switchedItemSwitched[0] = false;
            Phobos.inventoryManager.recoverSilent(lastHotbarSlot);
         }

         switchedItemSwitched[1] = true;
         break;
      case NONE:
         if (back) {
            switchedItemSwitched[1] = true;
         } else {
            switchedItemSwitched[1] = mc.field_71439_g.field_71071_by.field_70461_c == getItemHotbar(item);
         }
      }

      return switchedItemSwitched;
   }

   public static boolean holdingItem(Class clazz) {
      boolean result = false;
      ItemStack stack = mc.field_71439_g.func_184614_ca();
      result = isInstanceOf(stack, clazz);
      if (!result) {
         ItemStack offhand = mc.field_71439_g.func_184592_cb();
         result = isInstanceOf(stack, clazz);
      }

      return result;
   }

   public static boolean isInstanceOf(ItemStack stack, Class clazz) {
      if (stack == null) {
         return false;
      } else {
         Item item = stack.func_77973_b();
         if (clazz.isInstance(item)) {
            return true;
         } else if (item instanceof ItemBlock) {
            Block block = Block.func_149634_a(item);
            return clazz.isInstance(block);
         } else {
            return false;
         }
      }
   }

   public static int getEmptyXCarry() {
      for(int i = 1; i < 5; ++i) {
         Slot craftingSlot = (Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
         ItemStack craftingStack = craftingSlot.func_75211_c();
         if (craftingStack.func_190926_b() || craftingStack.func_77973_b() == Items.field_190931_a) {
            return i;
         }
      }

      return -1;
   }

   public static boolean isSlotEmpty(int i) {
      Slot slot = (Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
      ItemStack stack = slot.func_75211_c();
      return stack.func_190926_b();
   }

   public static int convertHotbarToInv(int input) {
      return 36 + input;
   }

   public static boolean areStacksCompatible(ItemStack stack1, ItemStack stack2) {
      if (!stack1.func_77973_b().equals(stack2.func_77973_b())) {
         return false;
      } else {
         if (stack1.func_77973_b() instanceof ItemBlock && stack2.func_77973_b() instanceof ItemBlock) {
            Block block1 = ((ItemBlock)stack1.func_77973_b()).func_179223_d();
            Block block2 = ((ItemBlock)stack2.func_77973_b()).func_179223_d();
            if (!block1.field_149764_J.equals(block2.field_149764_J)) {
               return false;
            }
         }

         if (!stack1.func_82833_r().equals(stack2.func_82833_r())) {
            return false;
         } else {
            return stack1.func_77952_i() == stack2.func_77952_i();
         }
      }
   }

   public static EntityEquipmentSlot getEquipmentFromSlot(int slot) {
      if (slot == 5) {
         return EntityEquipmentSlot.HEAD;
      } else if (slot == 6) {
         return EntityEquipmentSlot.CHEST;
      } else {
         return slot == 7 ? EntityEquipmentSlot.LEGS : EntityEquipmentSlot.FEET;
      }
   }

   public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
      int slot = -1;
      float damage = 0.0F;

      for(int i = 9; i < 45; ++i) {
         ItemStack s = Minecraft.func_71410_x().field_71439_g.field_71069_bz.func_75139_a(i).func_75211_c();
         if (s.func_77973_b() != Items.field_190931_a && s.func_77973_b() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)s.func_77973_b();
            if (armor.field_77881_a == type) {
               float currentDamage = (float)(armor.field_77879_b + EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, s));
               boolean cursed = binding && EnchantmentHelper.func_190938_b(s);
               if (currentDamage > damage && !cursed) {
                  damage = currentDamage;
                  slot = i;
               }
            }
         }
      }

      return slot;
   }

   public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
      int slot = findArmorSlot(type, binding);
      if (slot == -1 && withXCarry) {
         float damage = 0.0F;

         for(int i = 1; i < 5; ++i) {
            Slot craftingSlot = (Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
            ItemStack craftingStack = craftingSlot.func_75211_c();
            if (craftingStack.func_77973_b() != Items.field_190931_a && craftingStack.func_77973_b() instanceof ItemArmor) {
               ItemArmor armor = (ItemArmor)craftingStack.func_77973_b();
               if (armor.field_77881_a == type) {
                  float currentDamage = (float)(armor.field_77879_b + EnchantmentHelper.func_77506_a(Enchantments.field_180310_c, craftingStack));
                  boolean cursed = binding && EnchantmentHelper.func_190938_b(craftingStack);
                  if (currentDamage > damage && !cursed) {
                     damage = currentDamage;
                     slot = i;
                  }
               }
            }
         }
      }

      return slot;
   }

   public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
      int slot = findItemInventorySlot(item, offHand);
      if (slot == -1 && withXCarry) {
         for(int i = 1; i < 5; ++i) {
            Slot craftingSlot = (Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
            ItemStack craftingStack = craftingSlot.func_75211_c();
            if (craftingStack.func_77973_b() != Items.field_190931_a) {
               Item craftingStackItem = craftingStack.func_77973_b();
               if (craftingStackItem == item) {
                  slot = i;
               }
            }
         }
      }

      return slot;
   }

   public static int findBlockSlotInventory(Class clazz, boolean offHand, boolean withXCarry) {
      int slot = findInventoryBlock(clazz, offHand);
      if (slot == -1 && withXCarry) {
         for(int i = 1; i < 5; ++i) {
            Slot craftingSlot = (Slot)mc.field_71439_g.field_71069_bz.field_75151_b.get(i);
            ItemStack craftingStack = craftingSlot.func_75211_c();
            if (craftingStack.func_77973_b() != Items.field_190931_a) {
               Item craftingStackItem = craftingStack.func_77973_b();
               if (clazz.isInstance(craftingStackItem)) {
                  slot = i;
               } else if (craftingStackItem instanceof ItemBlock) {
                  Block block = ((ItemBlock)craftingStackItem).func_179223_d();
                  if (clazz.isInstance(block)) {
                     slot = i;
                  }
               }
            }
         }
      }

      return slot;
   }

   public static class Task {
      private final int slot;
      private final boolean update;
      private final boolean quickClick;

      public Task() {
         this.update = true;
         this.slot = -1;
         this.quickClick = false;
      }

      public Task(int slot) {
         this.slot = slot;
         this.quickClick = false;
         this.update = false;
      }

      public Task(int slot, boolean quickClick) {
         this.slot = slot;
         this.quickClick = quickClick;
         this.update = false;
      }

      public void run() {
         if (this.update) {
            Util.mc.field_71442_b.func_78765_e();
         }

         if (this.slot != -1) {
            Util.mc.field_71442_b.func_187098_a(Util.mc.field_71439_g.field_71069_bz.field_75152_c, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, Util.mc.field_71439_g);
         }

      }

      public boolean isSwitching() {
         return !this.update;
      }
   }

   public static enum Switch {
      NORMAL,
      SILENT,
      NONE;
   }
}
