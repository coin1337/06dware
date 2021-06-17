package me.earth.phobos.features.modules.combat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.ProcessRightClickBlockEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.EnumConverter;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {
   public Setting<Offhand.Type> type;
   public Setting<Boolean> cycle;
   public Setting<Bind> cycleKey;
   public Setting<Bind> offHandGapple;
   public Setting<Float> gappleHealth;
   public Setting<Float> gappleHoleHealth;
   public Setting<Bind> offHandCrystal;
   public Setting<Float> crystalHealth;
   public Setting<Float> crystalHoleHealth;
   public Setting<Float> cTargetDistance;
   public Setting<Bind> obsidian;
   public Setting<Float> obsidianHealth;
   public Setting<Float> obsidianHoleHealth;
   public Setting<Bind> webBind;
   public Setting<Float> webHealth;
   public Setting<Float> webHoleHealth;
   public Setting<Boolean> holeCheck;
   public Setting<Boolean> crystalCheck;
   public Setting<Boolean> gapSwap;
   public Setting<Integer> updates;
   public Setting<Boolean> cycleObby;
   public Setting<Boolean> cycleWebs;
   public Setting<Boolean> crystalToTotem;
   public Setting<Boolean> absorption;
   public Setting<Boolean> autoGapple;
   public Setting<Boolean> onlyWTotem;
   public Setting<Boolean> unDrawTotem;
   public Setting<Boolean> noOffhandGC;
   public Setting<Boolean> retardOGC;
   public Setting<Boolean> returnToCrystal;
   public Setting<Integer> timeout;
   public Setting<Integer> timeout2;
   public Setting<Integer> actions;
   public Setting<Offhand.NameMode> displayNameChange;
   public Setting<Boolean> guis;
   public Offhand.Mode mode;
   public Offhand.Mode oldMode;
   private int oldSlot;
   private boolean swapToTotem;
   private boolean eatingApple;
   private boolean oldSwapToTotem;
   public Offhand.Mode2 currentMode;
   public int totems;
   public int crystals;
   public int gapples;
   public int obby;
   public int webs;
   public int lastTotemSlot;
   public int lastGappleSlot;
   public int lastCrystalSlot;
   public int lastObbySlot;
   public int lastWebSlot;
   public boolean holdingCrystal;
   public boolean holdingTotem;
   public boolean holdingGapple;
   public boolean holdingObby;
   public boolean holdingWeb;
   public boolean didSwitchThisTick;
   private final Queue<InventoryUtil.Task> taskList;
   private boolean autoGappleSwitch;
   private static Offhand instance;
   private Timer timer;
   private Timer secondTimer;
   private boolean second;
   private boolean switchedForHealthReason;

   public Offhand() {
      super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
      this.type = this.register(new Setting("Mode", Offhand.Type.NEW));
      this.cycle = this.register(new Setting("Cycle", false, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.cycleKey = this.register(new Setting("Key", new Bind(-1), (v) -> {
         return (Boolean)this.cycle.getValue() && this.type.getValue() == Offhand.Type.OLD;
      }));
      this.offHandGapple = this.register(new Setting("Gapple", new Bind(-1)));
      this.gappleHealth = this.register(new Setting("G-Health", 13.0F, 0.1F, 36.0F));
      this.gappleHoleHealth = this.register(new Setting("G-H-Health", 3.5F, 0.1F, 36.0F));
      this.offHandCrystal = this.register(new Setting("Crystal", new Bind(-1)));
      this.crystalHealth = this.register(new Setting("C-Health", 13.0F, 0.1F, 36.0F));
      this.crystalHoleHealth = this.register(new Setting("C-H-Health", 3.5F, 0.1F, 36.0F));
      this.cTargetDistance = this.register(new Setting("C-Distance", 10.0F, 1.0F, 20.0F));
      this.obsidian = this.register(new Setting("Obsidian", new Bind(-1)));
      this.obsidianHealth = this.register(new Setting("O-Health", 13.0F, 0.1F, 36.0F));
      this.obsidianHoleHealth = this.register(new Setting("O-H-Health", 8.0F, 0.1F, 36.0F));
      this.webBind = this.register(new Setting("Webs", new Bind(-1)));
      this.webHealth = this.register(new Setting("W-Health", 13.0F, 0.1F, 36.0F));
      this.webHoleHealth = this.register(new Setting("W-H-Health", 8.0F, 0.1F, 36.0F));
      this.holeCheck = this.register(new Setting("Hole-Check", true));
      this.crystalCheck = this.register(new Setting("Crystal-Check", false));
      this.gapSwap = this.register(new Setting("Gap-Swap", true));
      this.updates = this.register(new Setting("Updates", 1, 1, 2));
      this.cycleObby = this.register(new Setting("CycleObby", false, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.cycleWebs = this.register(new Setting("CycleWebs", false, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.crystalToTotem = this.register(new Setting("Crystal-Totem", true, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.absorption = this.register(new Setting("Absorption", false, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.autoGapple = this.register(new Setting("AutoGapple", false, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.onlyWTotem = this.register(new Setting("OnlyWTotem", true, (v) -> {
         return (Boolean)this.autoGapple.getValue() && this.type.getValue() == Offhand.Type.OLD;
      }));
      this.unDrawTotem = this.register(new Setting("DrawTotems", true, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.noOffhandGC = this.register(new Setting("NoOGC", false));
      this.retardOGC = this.register(new Setting("RetardOGC", false));
      this.returnToCrystal = this.register(new Setting("RecoverySwitch", false));
      this.timeout = this.register(new Setting("Timeout", 50, 0, 500));
      this.timeout2 = this.register(new Setting("Timeout2", 50, 0, 500));
      this.actions = this.register(new Setting("Actions", 4, 1, 4, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.displayNameChange = this.register(new Setting("Name", Offhand.NameMode.TOTEM, (v) -> {
         return this.type.getValue() == Offhand.Type.OLD;
      }));
      this.guis = this.register(new Setting("Guis", false));
      this.mode = Offhand.Mode.CRYSTALS;
      this.oldMode = Offhand.Mode.CRYSTALS;
      this.oldSlot = -1;
      this.swapToTotem = false;
      this.eatingApple = false;
      this.oldSwapToTotem = false;
      this.currentMode = Offhand.Mode2.TOTEMS;
      this.totems = 0;
      this.crystals = 0;
      this.gapples = 0;
      this.obby = 0;
      this.webs = 0;
      this.lastTotemSlot = -1;
      this.lastGappleSlot = -1;
      this.lastCrystalSlot = -1;
      this.lastObbySlot = -1;
      this.lastWebSlot = -1;
      this.holdingCrystal = false;
      this.holdingTotem = false;
      this.holdingGapple = false;
      this.holdingObby = false;
      this.holdingWeb = false;
      this.didSwitchThisTick = false;
      this.taskList = new ConcurrentLinkedQueue();
      this.autoGappleSwitch = false;
      this.timer = new Timer();
      this.secondTimer = new Timer();
      this.second = false;
      this.switchedForHealthReason = false;
      instance = this;
   }

   public static Offhand getInstance() {
      if (instance == null) {
         instance = new Offhand();
      }

      return instance;
   }

   public void onItemFinish(ItemStack stack, EntityLivingBase base) {
      if ((Boolean)this.noOffhandGC.getValue() && base.equals(mc.field_71439_g) && stack.func_77973_b() == mc.field_71439_g.func_184592_cb().func_77973_b()) {
         this.secondTimer.reset();
         this.second = true;
      }

   }

   public void onTick() {
      if (!nullCheck() && (Integer)this.updates.getValue() != 1) {
         this.doOffhand();
      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
      if ((Boolean)this.noOffhandGC.getValue() && event.hand == EnumHand.MAIN_HAND && event.stack.func_77973_b() == Items.field_185158_cP && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71476_x != null && event.pos == mc.field_71476_x.func_178782_a()) {
         event.setCanceled(true);
         mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
         mc.field_71442_b.func_187101_a(mc.field_71439_g, mc.field_71441_e, EnumHand.OFF_HAND);
      }

   }

   public void onUpdate() {
      if ((Boolean)this.noOffhandGC.getValue() && (Boolean)this.retardOGC.getValue()) {
         if (this.timer.passedMs((long)(Integer)this.timeout.getValue())) {
            if (mc.field_71439_g != null && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && Mouse.isButtonDown(1)) {
               mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
               mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown(1);
            }
         } else if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
            mc.field_71474_y.field_74313_G.field_74513_e = false;
         }
      }

      if (!nullCheck() && (Integer)this.updates.getValue() != 2) {
         this.doOffhand();
         if (this.secondTimer.passedMs((long)(Integer)this.timeout2.getValue()) && this.second) {
            this.second = false;
            this.timer.reset();
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onKeyInput(KeyInputEvent event) {
      if (Keyboard.getEventKeyState()) {
         if (this.type.getValue() == Offhand.Type.NEW) {
            if (((Bind)this.offHandCrystal.getValue()).getKey() == Keyboard.getEventKey()) {
               if (this.mode == Offhand.Mode.CRYSTALS) {
                  this.setSwapToTotem(!this.isSwapToTotem());
               } else {
                  this.setSwapToTotem(false);
               }

               this.setMode(Offhand.Mode.CRYSTALS);
            }

            if (((Bind)this.offHandGapple.getValue()).getKey() == Keyboard.getEventKey()) {
               if (this.mode == Offhand.Mode.GAPPLES) {
                  this.setSwapToTotem(!this.isSwapToTotem());
               } else {
                  this.setSwapToTotem(false);
               }

               this.setMode(Offhand.Mode.GAPPLES);
            }

            if (((Bind)this.obsidian.getValue()).getKey() == Keyboard.getEventKey()) {
               if (this.mode == Offhand.Mode.OBSIDIAN) {
                  this.setSwapToTotem(!this.isSwapToTotem());
               } else {
                  this.setSwapToTotem(false);
               }

               this.setMode(Offhand.Mode.OBSIDIAN);
            }

            if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
               if (this.mode == Offhand.Mode.WEBS) {
                  this.setSwapToTotem(!this.isSwapToTotem());
               } else {
                  this.setSwapToTotem(false);
               }

               this.setMode(Offhand.Mode.WEBS);
            }
         } else if ((Boolean)this.cycle.getValue()) {
            if (((Bind)this.cycleKey.getValue()).getKey() == Keyboard.getEventKey()) {
               Offhand.Mode2 newMode = (Offhand.Mode2)EnumConverter.increaseEnum(this.currentMode);
               if (newMode == Offhand.Mode2.OBSIDIAN && !(Boolean)this.cycleObby.getValue() || newMode == Offhand.Mode2.WEBS && !(Boolean)this.cycleWebs.getValue()) {
                  newMode = Offhand.Mode2.TOTEMS;
               }

               this.setMode(newMode);
            }
         } else {
            if (((Bind)this.offHandCrystal.getValue()).getKey() == Keyboard.getEventKey()) {
               this.setMode(Offhand.Mode2.CRYSTALS);
            }

            if (((Bind)this.offHandGapple.getValue()).getKey() == Keyboard.getEventKey()) {
               this.setMode(Offhand.Mode2.GAPPLES);
            }

            if (((Bind)this.obsidian.getValue()).getKey() == Keyboard.getEventKey()) {
               this.setMode(Offhand.Mode2.OBSIDIAN);
            }

            if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
               this.setMode(Offhand.Mode2.WEBS);
            }
         }
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if ((Boolean)this.noOffhandGC.getValue() && !fullNullCheck() && mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP && mc.field_71474_y.field_74313_G.func_151470_d()) {
         if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (packet.func_187022_c() == EnumHand.MAIN_HAND && !AutoCrystal.placedPos.contains(packet.func_187023_a())) {
               if (this.timer.passedMs((long)(Integer)this.timeout.getValue())) {
                  mc.field_71439_g.func_184598_c(EnumHand.OFF_HAND);
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
               }

               event.setCanceled(true);
            }
         } else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
            CPacketPlayerTryUseItem packet = (CPacketPlayerTryUseItem)event.getPacket();
            if (packet.func_187028_a() == EnumHand.OFF_HAND && !this.timer.passedMs((long)(Integer)this.timeout.getValue())) {
               event.setCanceled(true);
            }
         }
      }

   }

   public String getDisplayInfo() {
      if (this.type.getValue() == Offhand.Type.NEW) {
         return String.valueOf(this.getStackSize());
      } else {
         switch((Offhand.NameMode)this.displayNameChange.getValue()) {
         case MODE:
            return EnumConverter.getProperName(this.currentMode);
         case TOTEM:
            if (this.currentMode == Offhand.Mode2.TOTEMS) {
               return this.totems + "";
            }

            return EnumConverter.getProperName(this.currentMode);
         default:
            switch(this.currentMode) {
            case TOTEMS:
               return this.totems + "";
            case GAPPLES:
               return this.gapples + "";
            default:
               return this.crystals + "";
            }
         }
      }
   }

   public String getDisplayName() {
      if (this.type.getValue() == Offhand.Type.NEW) {
         if (!this.shouldTotem()) {
            switch(this.mode) {
            case GAPPLES:
               return "OffhandGapple";
            case WEBS:
               return "OffhandWebs";
            case OBSIDIAN:
               return "OffhandObby";
            default:
               return "OffhandCrystal";
            }
         } else {
            return "AutoTotem" + (!this.isSwapToTotem() ? "-" + this.getModeStr() : "");
         }
      } else {
         switch((Offhand.NameMode)this.displayNameChange.getValue()) {
         case MODE:
            return (String)this.displayName.getValue();
         case TOTEM:
            if (this.currentMode == Offhand.Mode2.TOTEMS) {
               return "AutoTotem";
            }

            return (String)this.displayName.getValue();
         default:
            switch(this.currentMode) {
            case TOTEMS:
               return "AutoTotem";
            case GAPPLES:
               return "OffhandGapple";
            case WEBS:
               return "OffhandWebs";
            case OBSIDIAN:
               return "OffhandObby";
            default:
               return "OffhandCrystal";
            }
         }
      }
   }

   public void doOffhand() {
      if (this.type.getValue() == Offhand.Type.NEW) {
         if (mc.field_71462_r instanceof GuiContainer && !(Boolean)this.guis.getValue() && !(mc.field_71462_r instanceof GuiInventory)) {
            return;
         }

         if ((Boolean)this.gapSwap.getValue()) {
            if ((this.getSlot(Offhand.Mode.GAPPLES) != -1 || mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao) && mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_151153_ao && mc.field_71474_y.field_74313_G.func_151470_d()) {
               this.setMode(Offhand.Mode.GAPPLES);
               this.eatingApple = true;
               this.swapToTotem = false;
            } else if (this.eatingApple) {
               this.setMode(this.oldMode);
               this.swapToTotem = this.oldSwapToTotem;
               this.eatingApple = false;
            } else {
               this.oldMode = this.mode;
               this.oldSwapToTotem = this.swapToTotem;
            }
         }

         int slot;
         if (!this.shouldTotem()) {
            if (mc.field_71439_g.func_184592_cb() == ItemStack.field_190927_a || !this.isItemInOffhand()) {
               slot = this.getSlot(this.mode) < 9 ? this.getSlot(this.mode) + 36 : this.getSlot(this.mode);
               if (this.getSlot(this.mode) != -1) {
                  if (this.oldSlot != -1) {
                     mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, mc.field_71439_g);
                     mc.field_71442_b.func_187098_a(0, this.oldSlot, 0, ClickType.PICKUP, mc.field_71439_g);
                  }

                  this.oldSlot = slot;
                  mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, mc.field_71439_g);
                  mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, mc.field_71439_g);
                  mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, mc.field_71439_g);
               }
            }
         } else if (!this.eatingApple && (mc.field_71439_g.func_184592_cb() == ItemStack.field_190927_a || mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_190929_cY)) {
            slot = this.getTotemSlot() < 9 ? this.getTotemSlot() + 36 : this.getTotemSlot();
            if (this.getTotemSlot() != -1) {
               mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.PICKUP, mc.field_71439_g);
               mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, mc.field_71439_g);
               mc.field_71442_b.func_187098_a(0, this.oldSlot, 0, ClickType.PICKUP, mc.field_71439_g);
               this.oldSlot = -1;
            }
         }
      } else {
         if (!(Boolean)this.unDrawTotem.getValue()) {
            this.manageDrawn();
         }

         this.didSwitchThisTick = false;
         this.holdingCrystal = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
         this.holdingTotem = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY;
         this.holdingGapple = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao;
         this.holdingObby = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
         this.holdingWeb = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockWeb.class);
         this.totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_190929_cY;
         }).mapToInt(ItemStack::func_190916_E).sum();
         if (this.holdingTotem) {
            this.totems += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
               return itemStack.func_77973_b() == Items.field_190929_cY;
            }).mapToInt(ItemStack::func_190916_E).sum();
         }

         this.crystals = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_185158_cP;
         }).mapToInt(ItemStack::func_190916_E).sum();
         if (this.holdingCrystal) {
            this.crystals += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
               return itemStack.func_77973_b() == Items.field_185158_cP;
            }).mapToInt(ItemStack::func_190916_E).sum();
         }

         this.gapples = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
            return itemStack.func_77973_b() == Items.field_151153_ao;
         }).mapToInt(ItemStack::func_190916_E).sum();
         if (this.holdingGapple) {
            this.gapples += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
               return itemStack.func_77973_b() == Items.field_151153_ao;
            }).mapToInt(ItemStack::func_190916_E).sum();
         }

         if (this.currentMode == Offhand.Mode2.WEBS || this.currentMode == Offhand.Mode2.OBSIDIAN) {
            this.obby = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
               return InventoryUtil.isBlock(itemStack.func_77973_b(), BlockObsidian.class);
            }).mapToInt(ItemStack::func_190916_E).sum();
            if (this.holdingObby) {
               this.obby += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
                  return InventoryUtil.isBlock(itemStack.func_77973_b(), BlockObsidian.class);
               }).mapToInt(ItemStack::func_190916_E).sum();
            }

            this.webs = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
               return InventoryUtil.isBlock(itemStack.func_77973_b(), BlockWeb.class);
            }).mapToInt(ItemStack::func_190916_E).sum();
            if (this.holdingWeb) {
               this.webs += mc.field_71439_g.field_71071_by.field_184439_c.stream().filter((itemStack) -> {
                  return InventoryUtil.isBlock(itemStack.func_77973_b(), BlockWeb.class);
               }).mapToInt(ItemStack::func_190916_E).sum();
            }
         }

         this.doSwitch();
      }

   }

   private void manageDrawn() {
      if (this.currentMode == Offhand.Mode2.TOTEMS && (Boolean)this.drawn.getValue()) {
         this.drawn.setValue(false);
      }

      if (this.currentMode != Offhand.Mode2.TOTEMS && !(Boolean)this.drawn.getValue()) {
         this.drawn.setValue(true);
      }

   }

   public void doSwitch() {
      if ((Boolean)this.autoGapple.getValue()) {
         if (mc.field_71474_y.field_74313_G.func_151470_d()) {
            if (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword && (!(Boolean)this.onlyWTotem.getValue() || mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY)) {
               this.setMode(Offhand.Mode.GAPPLES);
               this.autoGappleSwitch = true;
            }
         } else if (this.autoGappleSwitch) {
            this.setMode(Offhand.Mode2.TOTEMS);
            this.autoGappleSwitch = false;
         }
      }

      if (this.currentMode == Offhand.Mode2.GAPPLES && (!EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.gappleHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.gappleHoleHealth.getValue()) || this.currentMode == Offhand.Mode2.CRYSTALS && (!EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.crystalHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.crystalHoleHealth.getValue()) || this.currentMode == Offhand.Mode2.OBSIDIAN && (!EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.obsidianHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.obsidianHoleHealth.getValue()) || this.currentMode == Offhand.Mode2.WEBS && (!EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.webHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) <= (Float)this.webHoleHealth.getValue())) {
         if ((Boolean)this.returnToCrystal.getValue() && this.currentMode == Offhand.Mode2.CRYSTALS) {
            this.switchedForHealthReason = true;
         }

         this.setMode(Offhand.Mode2.TOTEMS);
      }

      if (this.switchedForHealthReason && (EntityUtil.isSafe(mc.field_71439_g) && EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) > (Float)this.crystalHoleHealth.getValue() || EntityUtil.getHealth(mc.field_71439_g, (Boolean)this.absorption.getValue()) > (Float)this.crystalHealth.getValue())) {
         this.setMode(Offhand.Mode2.CRYSTALS);
         this.switchedForHealthReason = false;
      }

      if (!(mc.field_71462_r instanceof GuiContainer) || (Boolean)this.guis.getValue() || mc.field_71462_r instanceof GuiInventory) {
         Item currentOffhandItem = mc.field_71439_g.func_184592_cb().func_77973_b();
         int lastSlot;
         switch(this.currentMode) {
         case TOTEMS:
            if (this.totems > 0 && !this.holdingTotem) {
               this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.field_190929_cY, false);
               lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
               this.putItemInOffhand(this.lastTotemSlot, lastSlot);
            }
            break;
         case GAPPLES:
            if (this.gapples > 0 && !this.holdingGapple) {
               this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.field_151153_ao, false);
               lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
               this.putItemInOffhand(this.lastGappleSlot, lastSlot);
            }
            break;
         case WEBS:
            if (this.webs > 0 && !this.holdingWeb) {
               this.lastWebSlot = InventoryUtil.findInventoryBlock(BlockWeb.class, false);
               lastSlot = this.getLastSlot(currentOffhandItem, this.lastWebSlot);
               this.putItemInOffhand(this.lastWebSlot, lastSlot);
            }
            break;
         case OBSIDIAN:
            if (this.obby > 0 && !this.holdingObby) {
               this.lastObbySlot = InventoryUtil.findInventoryBlock(BlockObsidian.class, false);
               lastSlot = this.getLastSlot(currentOffhandItem, this.lastObbySlot);
               this.putItemInOffhand(this.lastObbySlot, lastSlot);
            }
            break;
         default:
            if (this.crystals > 0 && !this.holdingCrystal) {
               this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.field_185158_cP, false);
               lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
               this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
            }
         }

         for(int i = 0; i < (Integer)this.actions.getValue(); ++i) {
            InventoryUtil.Task task = (InventoryUtil.Task)this.taskList.poll();
            if (task != null) {
               task.run();
               if (task.isSwitching()) {
                  this.didSwitchThisTick = true;
               }
            }
         }

      }
   }

   private int getLastSlot(Item item, int slotIn) {
      if (item == Items.field_185158_cP) {
         return this.lastCrystalSlot;
      } else if (item == Items.field_151153_ao) {
         return this.lastGappleSlot;
      } else if (item == Items.field_190929_cY) {
         return this.lastTotemSlot;
      } else if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
         return this.lastObbySlot;
      } else if (InventoryUtil.isBlock(item, BlockWeb.class)) {
         return this.lastWebSlot;
      } else {
         return item == Items.field_190931_a ? -1 : slotIn;
      }
   }

   private void putItemInOffhand(int slotIn, int slotOut) {
      if (slotIn != -1 && this.taskList.isEmpty()) {
         this.taskList.add(new InventoryUtil.Task(slotIn));
         this.taskList.add(new InventoryUtil.Task(45));
         this.taskList.add(new InventoryUtil.Task(slotOut));
         this.taskList.add(new InventoryUtil.Task());
      }

   }

   private boolean noNearbyPlayers() {
      return this.mode == Offhand.Mode.CRYSTALS && mc.field_71441_e.field_73010_i.stream().noneMatch((e) -> {
         return e != mc.field_71439_g && !Phobos.friendManager.isFriend(e) && mc.field_71439_g.func_70032_d(e) <= (Float)this.cTargetDistance.getValue();
      });
   }

   private boolean isItemInOffhand() {
      switch(this.mode) {
      case GAPPLES:
         return mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151153_ao;
      case WEBS:
         return mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184592_cb().func_77973_b()).field_150939_a == Blocks.field_150321_G;
      case OBSIDIAN:
         return mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184592_cb().func_77973_b()).field_150939_a == Blocks.field_150343_Z;
      case CRYSTALS:
         return mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
      default:
         return false;
      }
   }

   private boolean isHeldInMainHand() {
      switch(this.mode) {
      case GAPPLES:
         return mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151153_ao;
      case WEBS:
         return mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184614_ca().func_77973_b()).field_150939_a == Blocks.field_150321_G;
      case OBSIDIAN:
         return mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.func_184614_ca().func_77973_b()).field_150939_a == Blocks.field_150343_Z;
      case CRYSTALS:
         return mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP;
      default:
         return false;
      }
   }

   private boolean shouldTotem() {
      if (!this.isHeldInMainHand() && !this.isSwapToTotem()) {
         if ((Boolean)this.holeCheck.getValue() && EntityUtil.isInHole(mc.field_71439_g)) {
            return mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj() <= this.getHoleHealth() || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_185160_cR || mc.field_71439_g.field_70143_R >= 3.0F || this.noNearbyPlayers() || (Boolean)this.crystalCheck.getValue() && this.isCrystalsAABBEmpty();
         } else {
            return mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj() <= this.getHealth() || mc.field_71439_g.func_184582_a(EntityEquipmentSlot.CHEST).func_77973_b() == Items.field_185160_cR || mc.field_71439_g.field_70143_R >= 3.0F || this.noNearbyPlayers() || (Boolean)this.crystalCheck.getValue() && this.isCrystalsAABBEmpty();
         }
      } else {
         return true;
      }
   }

   private boolean isNotEmpty(BlockPos pos) {
      return mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(pos)).stream().anyMatch((e) -> {
         return e instanceof EntityEnderCrystal;
      });
   }

   private float getHealth() {
      switch(this.mode) {
      case GAPPLES:
         return (Float)this.gappleHealth.getValue();
      case WEBS:
      default:
         return (Float)this.webHealth.getValue();
      case OBSIDIAN:
         return (Float)this.obsidianHealth.getValue();
      case CRYSTALS:
         return (Float)this.crystalHealth.getValue();
      }
   }

   private float getHoleHealth() {
      switch(this.mode) {
      case GAPPLES:
         return (Float)this.gappleHoleHealth.getValue();
      case WEBS:
      default:
         return (Float)this.webHoleHealth.getValue();
      case OBSIDIAN:
         return (Float)this.obsidianHoleHealth.getValue();
      case CRYSTALS:
         return (Float)this.crystalHoleHealth.getValue();
      }
   }

   private boolean isCrystalsAABBEmpty() {
      return this.isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(1, 0, 0)) || this.isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(-1, 0, 0)) || this.isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(0, 0, 1)) || this.isNotEmpty(mc.field_71439_g.func_180425_c().func_177982_a(0, 0, -1)) || this.isNotEmpty(mc.field_71439_g.func_180425_c());
   }

   int getStackSize() {
      int size = 0;
      int i;
      if (this.shouldTotem()) {
         for(i = 45; i > 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190929_cY) {
               size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E();
            }
         }
      } else if (this.mode == Offhand.Mode.OBSIDIAN) {
         for(i = 45; i > 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a == Blocks.field_150343_Z) {
               size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E();
            }
         }
      } else if (this.mode == Offhand.Mode.WEBS) {
         for(i = 45; i > 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a == Blocks.field_150321_G) {
               size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E();
            }
         }
      } else {
         for(i = 45; i > 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == (this.mode == Offhand.Mode.CRYSTALS ? Items.field_185158_cP : Items.field_151153_ao)) {
               size += mc.field_71439_g.field_71071_by.func_70301_a(i).func_190916_E();
            }
         }
      }

      return size;
   }

   int getSlot(Offhand.Mode m) {
      int slot = -1;
      int i;
      if (m == Offhand.Mode.OBSIDIAN) {
         for(i = 45; i > 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a == Blocks.field_150343_Z) {
               slot = i;
               break;
            }
         }
      } else if (m == Offhand.Mode.WEBS) {
         for(i = 45; i > 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).field_150939_a == Blocks.field_150321_G) {
               slot = i;
               break;
            }
         }
      } else {
         for(i = 45; i > 0; --i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == (m == Offhand.Mode.CRYSTALS ? Items.field_185158_cP : Items.field_151153_ao)) {
               slot = i;
               break;
            }
         }
      }

      return slot;
   }

   int getTotemSlot() {
      int totemSlot = -1;

      for(int i = 45; i > 0; --i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190929_cY) {
            totemSlot = i;
            break;
         }
      }

      return totemSlot;
   }

   private String getModeStr() {
      switch(this.mode) {
      case GAPPLES:
         return "G";
      case WEBS:
         return "W";
      case OBSIDIAN:
         return "O";
      default:
         return "C";
      }
   }

   public void setMode(Offhand.Mode mode) {
      this.mode = mode;
   }

   public void setMode(Offhand.Mode2 mode) {
      if (this.currentMode == mode) {
         this.currentMode = Offhand.Mode2.TOTEMS;
      } else if (!(Boolean)this.cycle.getValue() && (Boolean)this.crystalToTotem.getValue() && (this.currentMode == Offhand.Mode2.CRYSTALS || this.currentMode == Offhand.Mode2.OBSIDIAN || this.currentMode == Offhand.Mode2.WEBS) && mode == Offhand.Mode2.GAPPLES) {
         this.currentMode = Offhand.Mode2.TOTEMS;
      } else {
         this.currentMode = mode;
      }

   }

   public boolean isSwapToTotem() {
      return this.swapToTotem;
   }

   public void setSwapToTotem(boolean swapToTotem) {
      this.swapToTotem = swapToTotem;
   }

   public static enum NameMode {
      MODE,
      TOTEM,
      AMOUNT;
   }

   public static enum Mode2 {
      TOTEMS,
      GAPPLES,
      CRYSTALS,
      OBSIDIAN,
      WEBS;
   }

   public static enum Type {
      OLD,
      NEW;
   }

   public static enum Mode {
      CRYSTALS,
      GAPPLES,
      OBSIDIAN,
      WEBS;
   }
}
