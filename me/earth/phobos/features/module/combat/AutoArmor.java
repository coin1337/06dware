package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.XCarry;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class AutoArmor extends Module {
   private final Setting<Integer> delay = this.register(new Setting("Delay", 50, 0, 500));
   private final Setting<Boolean> mendingTakeOff = this.register(new Setting("AutoMend", false));
   private final Setting<Integer> closestEnemy = this.register(new Setting("Enemy", 8, 1, 20, (v) -> {
      return (Boolean)this.mendingTakeOff.getValue();
   }));
   private final Setting<Integer> helmetThreshold = this.register(new Setting("Helmet%", 80, 1, 100, (v) -> {
      return (Boolean)this.mendingTakeOff.getValue();
   }));
   private final Setting<Integer> chestThreshold = this.register(new Setting("Chest%", 80, 1, 100, (v) -> {
      return (Boolean)this.mendingTakeOff.getValue();
   }));
   private final Setting<Integer> legThreshold = this.register(new Setting("Legs%", 80, 1, 100, (v) -> {
      return (Boolean)this.mendingTakeOff.getValue();
   }));
   private final Setting<Integer> bootsThreshold = this.register(new Setting("Boots%", 80, 1, 100, (v) -> {
      return (Boolean)this.mendingTakeOff.getValue();
   }));
   private final Setting<Boolean> curse = this.register(new Setting("CurseOfBinding", false));
   private final Setting<Integer> actions = this.register(new Setting("Actions", 3, 1, 12));
   private final Setting<Bind> elytraBind = this.register(new Setting("Elytra", new Bind(-1)));
   private final Setting<Boolean> tps = this.register(new Setting("TpsSync", true));
   private final Setting<Boolean> updateController = this.register(new Setting("Update", true));
   private final Setting<Boolean> shiftClick = this.register(new Setting("ShiftClick", false));
   private final Timer timer = new Timer();
   private final Timer elytraTimer = new Timer();
   private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue();
   private final List<Integer> doneSlots = new ArrayList();
   private boolean elytraOn = false;

   public AutoArmor() {
      super("AutoArmor", "Puts Armor on for you.", Module.Category.COMBAT, true, false, false);
   }

   @SubscribeEvent
   public void onKeyInput(KeyInputEvent event) {
      if (Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof PhobosGui) && ((Bind)this.elytraBind.getValue()).getKey() == Keyboard.getEventKey()) {
         this.elytraOn = !this.elytraOn;
      }

   }

   public void onLogin() {
      this.timer.reset();
      this.elytraTimer.reset();
   }

   public void onDisable() {
      this.taskList.clear();
      this.doneSlots.clear();
      this.elytraOn = false;
   }

   public void onLogout() {
      this.taskList.clear();
      this.doneSlots.clear();
   }

   public void onTick() {
      if (!fullNullCheck() && (!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory)) {
         if (this.taskList.isEmpty()) {
            ItemStack helm;
            int slot;
            int slot;
            int slot;
            int slot;
            ItemStack chest;
            ItemStack feet;
            ItemStack legging;
            if ((Boolean)this.mendingTakeOff.getValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && mc.field_71474_y.field_74313_G.func_151470_d() && (this.isSafe() || EntityUtil.isSafe(mc.field_71439_g, 1, false))) {
               helm = mc.field_71439_g.field_71069_bz.func_75139_a(5).func_75211_c();
               if (!helm.field_190928_g) {
                  slot = DamageUtil.getRoundedDamage(helm);
                  if (slot >= (Integer)this.helmetThreshold.getValue()) {
                     this.takeOffSlot(5);
                  }
               }

               chest = mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c();
               if (!chest.field_190928_g) {
                  slot = DamageUtil.getRoundedDamage(chest);
                  if (slot >= (Integer)this.chestThreshold.getValue()) {
                     this.takeOffSlot(6);
                  }
               }

               legging = mc.field_71439_g.field_71069_bz.func_75139_a(7).func_75211_c();
               if (!legging.field_190928_g) {
                  slot = DamageUtil.getRoundedDamage(legging);
                  if (slot >= (Integer)this.legThreshold.getValue()) {
                     this.takeOffSlot(7);
                  }
               }

               feet = mc.field_71439_g.field_71069_bz.func_75139_a(8).func_75211_c();
               if (!feet.field_190928_g) {
                  slot = DamageUtil.getRoundedDamage(feet);
                  if (slot >= (Integer)this.bootsThreshold.getValue()) {
                     this.takeOffSlot(8);
                  }
               }

               return;
            }

            helm = mc.field_71439_g.field_71069_bz.func_75139_a(5).func_75211_c();
            if (helm.func_77973_b() == Items.field_190931_a) {
               slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, (Boolean)this.curse.getValue(), XCarry.getInstance().isOn());
               if (slot != -1) {
                  this.getSlotOn(5, slot);
               }
            }

            chest = mc.field_71439_g.field_71069_bz.func_75139_a(6).func_75211_c();
            if (chest.func_77973_b() == Items.field_190931_a) {
               if (this.taskList.isEmpty()) {
                  if (this.elytraOn && this.elytraTimer.passedMs(500L)) {
                     slot = InventoryUtil.findItemInventorySlot(Items.field_185160_cR, false, XCarry.getInstance().isOn());
                     if (slot != -1) {
                        if ((slot >= 5 || slot <= 1) && (Boolean)this.shiftClick.getValue()) {
                           this.taskList.add(new InventoryUtil.Task(slot, true));
                        } else {
                           this.taskList.add(new InventoryUtil.Task(slot));
                           this.taskList.add(new InventoryUtil.Task(6));
                        }

                        if ((Boolean)this.updateController.getValue()) {
                           this.taskList.add(new InventoryUtil.Task());
                        }

                        this.elytraTimer.reset();
                     }
                  } else if (!this.elytraOn) {
                     slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, (Boolean)this.curse.getValue(), XCarry.getInstance().isOn());
                     if (slot != -1) {
                        this.getSlotOn(6, slot);
                     }
                  }
               }
            } else if (this.elytraOn && chest.func_77973_b() != Items.field_185160_cR && this.elytraTimer.passedMs(500L)) {
               if (this.taskList.isEmpty()) {
                  slot = InventoryUtil.findItemInventorySlot(Items.field_185160_cR, false, XCarry.getInstance().isOn());
                  if (slot != -1) {
                     this.taskList.add(new InventoryUtil.Task(slot));
                     this.taskList.add(new InventoryUtil.Task(6));
                     this.taskList.add(new InventoryUtil.Task(slot));
                     if ((Boolean)this.updateController.getValue()) {
                        this.taskList.add(new InventoryUtil.Task());
                     }
                  }

                  this.elytraTimer.reset();
               }
            } else if (!this.elytraOn && chest.func_77973_b() == Items.field_185160_cR && this.elytraTimer.passedMs(500L) && this.taskList.isEmpty()) {
               slot = InventoryUtil.findItemInventorySlot(Items.field_151163_ad, false, XCarry.getInstance().isOn());
               if (slot == -1) {
                  slot = InventoryUtil.findItemInventorySlot(Items.field_151030_Z, false, XCarry.getInstance().isOn());
                  if (slot == -1) {
                     slot = InventoryUtil.findItemInventorySlot(Items.field_151171_ah, false, XCarry.getInstance().isOn());
                     if (slot == -1) {
                        slot = InventoryUtil.findItemInventorySlot(Items.field_151023_V, false, XCarry.getInstance().isOn());
                        if (slot == -1) {
                           slot = InventoryUtil.findItemInventorySlot(Items.field_151027_R, false, XCarry.getInstance().isOn());
                        }
                     }
                  }
               }

               if (slot != -1) {
                  this.taskList.add(new InventoryUtil.Task(slot));
                  this.taskList.add(new InventoryUtil.Task(6));
                  this.taskList.add(new InventoryUtil.Task(slot));
                  if ((Boolean)this.updateController.getValue()) {
                     this.taskList.add(new InventoryUtil.Task());
                  }
               }

               this.elytraTimer.reset();
            }

            legging = mc.field_71439_g.field_71069_bz.func_75139_a(7).func_75211_c();
            if (legging.func_77973_b() == Items.field_190931_a) {
               slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, (Boolean)this.curse.getValue(), XCarry.getInstance().isOn());
               if (slot != -1) {
                  this.getSlotOn(7, slot);
               }
            }

            feet = mc.field_71439_g.field_71069_bz.func_75139_a(8).func_75211_c();
            if (feet.func_77973_b() == Items.field_190931_a) {
               slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, (Boolean)this.curse.getValue(), XCarry.getInstance().isOn());
               if (slot != -1) {
                  this.getSlotOn(8, slot);
               }
            }
         }

         if (this.timer.passedMs((long)((int)((float)(Integer)this.delay.getValue() * ((Boolean)this.tps.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0F))))) {
            if (!this.taskList.isEmpty()) {
               for(int i = 0; i < (Integer)this.actions.getValue(); ++i) {
                  InventoryUtil.Task task = (InventoryUtil.Task)this.taskList.poll();
                  if (task != null) {
                     task.run();
                  }
               }
            }

            this.timer.reset();
         }

      }
   }

   public String getDisplayInfo() {
      return this.elytraOn ? "Elytra" : null;
   }

   private void takeOffSlot(int slot) {
      if (this.taskList.isEmpty()) {
         int target = -1;
         Iterator var3 = InventoryUtil.findEmptySlots(XCarry.getInstance().isOn()).iterator();

         while(var3.hasNext()) {
            int i = (Integer)var3.next();
            if (!this.doneSlots.contains(target)) {
               target = i;
               this.doneSlots.add(i);
            }
         }

         if (target != -1) {
            if ((target >= 5 || target <= 0) && (Boolean)this.shiftClick.getValue()) {
               this.taskList.add(new InventoryUtil.Task(slot, true));
            } else {
               this.taskList.add(new InventoryUtil.Task(slot));
               this.taskList.add(new InventoryUtil.Task(target));
            }

            if ((Boolean)this.updateController.getValue()) {
               this.taskList.add(new InventoryUtil.Task());
            }
         }
      }

   }

   private void getSlotOn(int slot, int target) {
      if (this.taskList.isEmpty()) {
         this.doneSlots.remove(target);
         if ((target >= 5 || target <= 0) && (Boolean)this.shiftClick.getValue()) {
            this.taskList.add(new InventoryUtil.Task(target, true));
         } else {
            this.taskList.add(new InventoryUtil.Task(target));
            this.taskList.add(new InventoryUtil.Task(slot));
         }

         if ((Boolean)this.updateController.getValue()) {
            this.taskList.add(new InventoryUtil.Task());
         }
      }

   }

   private boolean isSafe() {
      EntityPlayer closest = EntityUtil.getClosestEnemy((double)(Integer)this.closestEnemy.getValue());
      if (closest == null) {
         return true;
      } else {
         return mc.field_71439_g.func_70068_e(closest) >= MathUtil.square((float)(Integer)this.closestEnemy.getValue());
      }
   }
}
