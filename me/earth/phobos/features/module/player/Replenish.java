package me.earth.phobos.features.modules.player;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.Auto32k;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Replenish extends Module {
   private final Setting<Integer> threshold = this.register(new Setting("Threshold", 0, 0, 63));
   private final Setting<Integer> replenishments = this.register(new Setting("RUpdates", 0, 0, 1000));
   private final Setting<Integer> updates = this.register(new Setting("HBUpdates", 100, 0, 1000));
   private final Setting<Integer> actions = this.register(new Setting("Actions", 2, 1, 30));
   private final Setting<Boolean> pauseInv = this.register(new Setting("PauseInv", true));
   private final Setting<Boolean> putBack = this.register(new Setting("PutBack", true));
   private final Timer timer = new Timer();
   private final Timer replenishTimer = new Timer();
   private Map<Integer, ItemStack> hotbar = new ConcurrentHashMap();
   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue();

   public Replenish() {
      super("Replenish", "Replenishes your hotbar", Module.Category.PLAYER, false, false, false);
   }

   public void onUpdate() {
      if (!Auto32k.getInstance().isOn() || (Boolean)Auto32k.getInstance().autoSwitch.getValue() && !Auto32k.getInstance().switching) {
         if (!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory && !(Boolean)this.pauseInv.getValue()) {
            if (this.timer.passedMs((long)(Integer)this.updates.getValue())) {
               this.mapHotbar();
            }

            if (this.replenishTimer.passedMs((long)(Integer)this.replenishments.getValue())) {
               for(int i = 0; i < (Integer)this.actions.getValue(); ++i) {
                  InventoryUtil.Task task = (InventoryUtil.Task)this.taskList.poll();
                  if (task != null) {
                     task.run();
                  }
               }

               this.replenishTimer.reset();
            }

         }
      }
   }

   public void onDisable() {
      this.hotbar.clear();
   }

   public void onLogout() {
      this.onDisable();
   }

   private void mapHotbar() {
      Map<Integer, ItemStack> map = new ConcurrentHashMap();

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         map.put(i, stack);
      }

      if (this.hotbar.isEmpty()) {
         this.hotbar = map;
      } else {
         Map<Integer, Integer> fromTo = new ConcurrentHashMap();
         Iterator var10 = map.entrySet().iterator();

         while(true) {
            Entry hotbarItem;
            ItemStack stack;
            Integer slotKey;
            do {
               do {
                  do {
                     if (!var10.hasNext()) {
                        if (!fromTo.isEmpty()) {
                           var10 = fromTo.entrySet().iterator();

                           while(var10.hasNext()) {
                              hotbarItem = (Entry)var10.next();
                              this.taskList.add(new InventoryUtil.Task((Integer)hotbarItem.getKey()));
                              this.taskList.add(new InventoryUtil.Task((Integer)hotbarItem.getValue()));
                              this.taskList.add(new InventoryUtil.Task((Integer)hotbarItem.getKey()));
                              this.taskList.add(new InventoryUtil.Task());
                           }
                        }

                        this.hotbar = map;
                        return;
                     }

                     hotbarItem = (Entry)var10.next();
                     stack = (ItemStack)hotbarItem.getValue();
                     slotKey = (Integer)hotbarItem.getKey();
                  } while(slotKey == null);
               } while(stack == null);
            } while(!stack.field_190928_g && stack.func_77973_b() != Items.field_190931_a && (stack.field_77994_a > (Integer)this.threshold.getValue() || stack.field_77994_a >= stack.func_77976_d()));

            ItemStack previousStack = (ItemStack)hotbarItem.getValue();
            if (stack.field_190928_g || stack.func_77973_b() != Items.field_190931_a) {
               previousStack = (ItemStack)this.hotbar.get(slotKey);
            }

            if (previousStack != null && !previousStack.field_190928_g && previousStack.func_77973_b() != Items.field_190931_a) {
               int replenishSlot = this.getReplenishSlot(previousStack);
               if (replenishSlot != -1) {
                  fromTo.put(replenishSlot, InventoryUtil.convertHotbarToInv(slotKey));
               }
            }
         }
      }
   }

   private int getReplenishSlot(ItemStack stack) {
      AtomicInteger slot = new AtomicInteger();
      slot.set(-1);
      Iterator var3 = InventoryUtil.getInventoryAndHotbarSlots().entrySet().iterator();

      Entry entry;
      do {
         if (!var3.hasNext()) {
            return slot.get();
         }

         entry = (Entry)var3.next();
      } while((Integer)entry.getKey() >= 36 || !InventoryUtil.areStacksCompatible(stack, (ItemStack)entry.getValue()));

      slot.set((Integer)entry.getKey());
      return slot.get();
   }
}
