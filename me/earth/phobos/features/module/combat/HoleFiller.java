package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class HoleFiller extends Module {
   public Setting<HoleFiller.Mode> mode;
   public Setting<HoleFiller.PlaceMode> placeMode;
   public Setting<Bind> obbyBind;
   public Setting<Bind> webBind;
   private final Setting<Double> smartRange;
   private final Setting<Double> range;
   private final Setting<Integer> delay;
   private final Setting<Integer> blocksPerTick;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> raytrace;
   private final Setting<Boolean> disable;
   private final Setting<Integer> disableTime;
   private final Setting<Boolean> offhand;
   private final Setting<InventoryUtil.Switch> switchMode;
   private final Setting<Boolean> onlySafe;
   private final Setting<Boolean> webSelf;
   private final Setting<Boolean> highWeb;
   private final Setting<Boolean> freecam;
   private final Setting<Boolean> midSafeHoles;
   private final Setting<Boolean> packet;
   private static HoleFiller INSTANCE = new HoleFiller();
   public HoleFiller.Mode currentMode;
   private final Timer offTimer;
   private final Timer timer;
   private boolean accessedViaBind;
   private int targetSlot;
   private int blocksThisTick;
   private Offhand.Mode offhandMode;
   private Offhand.Mode2 offhandMode2;
   private final Map<BlockPos, Integer> retries;
   private final Timer retryTimer;
   private boolean isSneaking;
   private boolean hasOffhand;
   private boolean placeHighWeb;
   private int lastHotbarSlot;
   private boolean switchedItem;

   public HoleFiller() {
      super("HoleFiller", "Fills holes around you.", Module.Category.COMBAT, true, false, true);
      this.mode = this.register(new Setting("Mode", HoleFiller.Mode.OBSIDIAN));
      this.placeMode = this.register(new Setting("PlaceMode", HoleFiller.PlaceMode.ALL));
      this.obbyBind = this.register(new Setting("Obsidian", new Bind(-1)));
      this.webBind = this.register(new Setting("Webs", new Bind(-1)));
      this.smartRange = this.register(new Setting("SmartRange", 6.0D, 0.0D, 10.0D, (v) -> {
         return this.placeMode.getValue() == HoleFiller.PlaceMode.SMART;
      }));
      this.range = this.register(new Setting("PlaceRange", 6.0D, 0.0D, 10.0D));
      this.delay = this.register(new Setting("Delay/Place", 50, 0, 250));
      this.blocksPerTick = this.register(new Setting("Block/Place", 8, 1, 20));
      this.rotate = this.register(new Setting("Rotate", true));
      this.raytrace = this.register(new Setting("Raytrace", false));
      this.disable = this.register(new Setting("Disable", true));
      this.disableTime = this.register(new Setting("Ms/Disable", 200, 1, 250));
      this.offhand = this.register(new Setting("OffHand", true));
      this.switchMode = this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
      this.onlySafe = this.register(new Setting("OnlySafe", true, (v) -> {
         return (Boolean)this.offhand.getValue();
      }));
      this.webSelf = this.register(new Setting("SelfWeb", false));
      this.highWeb = this.register(new Setting("HighWeb", false));
      this.freecam = this.register(new Setting("Freecam", false));
      this.midSafeHoles = this.register(new Setting("MidSafe", false));
      this.packet = this.register(new Setting("Packet", false));
      this.currentMode = HoleFiller.Mode.OBSIDIAN;
      this.offTimer = new Timer();
      this.timer = new Timer();
      this.accessedViaBind = false;
      this.targetSlot = -1;
      this.blocksThisTick = 0;
      this.offhandMode = Offhand.Mode.CRYSTALS;
      this.offhandMode2 = Offhand.Mode2.CRYSTALS;
      this.retries = new HashMap();
      this.retryTimer = new Timer();
      this.hasOffhand = false;
      this.placeHighWeb = false;
      this.lastHotbarSlot = -1;
      this.switchedItem = false;
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static HoleFiller getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HoleFiller();
      }

      return INSTANCE;
   }

   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      }

      this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (!this.accessedViaBind) {
         this.currentMode = (HoleFiller.Mode)this.mode.getValue();
      }

      Offhand module = (Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class);
      this.offhandMode = module.mode;
      this.offhandMode2 = module.currentMode;
      if ((Boolean)this.offhand.getValue() && (EntityUtil.isSafe(mc.field_71439_g) || !(Boolean)this.onlySafe.getValue())) {
         if (module.type.getValue() == Offhand.Type.NEW) {
            if (this.currentMode == HoleFiller.Mode.WEBS) {
               module.setSwapToTotem(false);
               module.setMode(Offhand.Mode.WEBS);
            } else {
               module.setSwapToTotem(false);
               module.setMode(Offhand.Mode.OBSIDIAN);
            }
         } else {
            if (this.currentMode == HoleFiller.Mode.WEBS) {
               module.setMode(Offhand.Mode2.WEBS);
            } else {
               module.setMode(Offhand.Mode2.OBSIDIAN);
            }

            if (!module.didSwitchThisTick) {
               module.doOffhand();
            }
         }
      }

      Phobos.holeManager.update();
      this.offTimer.reset();
   }

   public void onTick() {
      if (this.isOn() && ((Integer)this.blocksPerTick.getValue() != 1 || !(Boolean)this.rotate.getValue())) {
         this.doHoleFill();
      }

   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (this.isOn() && event.getStage() == 0 && (Integer)this.blocksPerTick.getValue() == 1 && (Boolean)this.rotate.getValue()) {
         this.doHoleFill();
      }

   }

   public void onDisable() {
      if ((Boolean)this.offhand.getValue()) {
         ((Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode);
         ((Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode2);
      }

      this.switchItem(true);
      this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
      this.retries.clear();
      this.accessedViaBind = false;
      this.hasOffhand = false;
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onKeyInput(KeyInputEvent event) {
      if (Keyboard.getEventKeyState()) {
         if (((Bind)this.obbyBind.getValue()).getKey() == Keyboard.getEventKey()) {
            this.accessedViaBind = true;
            this.currentMode = HoleFiller.Mode.OBSIDIAN;
            this.toggle();
         }

         if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
            this.accessedViaBind = true;
            this.currentMode = HoleFiller.Mode.WEBS;
            this.toggle();
         }
      }

   }

   private void doHoleFill() {
      if (!this.check()) {
         if (this.placeHighWeb) {
            BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v);
            this.placeBlock(pos);
            this.placeHighWeb = false;
         }

         ArrayList targets;
         if ((Boolean)this.midSafeHoles.getValue()) {
            synchronized(Phobos.holeManager.getMidSafety()) {
               targets = new ArrayList(Phobos.holeManager.getMidSafety());
            }
         } else {
            synchronized(Phobos.holeManager.getHoles()) {
               targets = new ArrayList(Phobos.holeManager.getHoles());
            }
         }

         Iterator var2 = targets.iterator();

         while(true) {
            while(true) {
               BlockPos position;
               while(true) {
                  do {
                     do {
                        if (!var2.hasNext()) {
                           return;
                        }

                        position = (BlockPos)var2.next();
                     } while(mc.field_71439_g.func_174818_b(position) > MathUtil.square((Double)this.range.getValue()));
                  } while(this.placeMode.getValue() == HoleFiller.PlaceMode.SMART && !this.isPlayerInRange(position));

                  if (!position.equals(new BlockPos(mc.field_71439_g.func_174791_d()))) {
                     break;
                  }

                  if (this.currentMode == HoleFiller.Mode.WEBS && (Boolean)this.webSelf.getValue()) {
                     if ((Boolean)this.highWeb.getValue()) {
                        this.placeHighWeb = true;
                     }
                     break;
                  }
               }

               int placeability = BlockUtil.isPositionPlaceable(position, (Boolean)this.raytrace.getValue());
               if (placeability == 1 && (this.currentMode == HoleFiller.Mode.WEBS || this.switchMode.getValue() == InventoryUtil.Switch.SILENT || BlockTweaks.getINSTANCE().isOn() && (Boolean)BlockTweaks.getINSTANCE().noBlock.getValue()) && (this.currentMode == HoleFiller.Mode.WEBS || this.retries.get(position) == null || (Integer)this.retries.get(position) < 4)) {
                  this.placeBlock(position);
                  if (this.currentMode != HoleFiller.Mode.WEBS) {
                     this.retries.put(position, this.retries.get(position) == null ? 1 : (Integer)this.retries.get(position) + 1);
                  }
               } else if (placeability == 3) {
                  this.placeBlock(position);
               }
            }
         }
      }
   }

   private void placeBlock(BlockPos pos) {
      if (this.blocksThisTick < (Integer)this.blocksPerTick.getValue() && this.switchItem(false)) {
         boolean smartRotate = (Integer)this.blocksPerTick.getValue() == 1 && (Boolean)this.rotate.getValue();
         if (smartRotate) {
            this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, (Boolean)this.packet.getValue(), this.isSneaking);
         } else {
            this.isSneaking = BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), this.isSneaking);
         }

         this.timer.reset();
         ++this.blocksThisTick;
      }

   }

   private boolean isPlayerInRange(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

      EntityPlayer player;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         player = (EntityPlayer)var2.next();
      } while(EntityUtil.isntValid(player, (Double)this.smartRange.getValue()));

      return true;
   }

   private boolean check() {
      if (!fullNullCheck() && (!(Boolean)this.disable.getValue() || !this.offTimer.passedMs((long)(Integer)this.disableTime.getValue()))) {
         if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock(this.currentMode == HoleFiller.Mode.WEBS ? BlockWeb.class : BlockObsidian.class)) {
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         }

         this.switchItem(true);
         if (!(Boolean)this.freecam.getValue() && Phobos.moduleManager.isModuleEnabled(Freecam.class)) {
            return true;
         } else {
            this.blocksThisTick = 0;
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            if (this.retryTimer.passedMs(2000L)) {
               this.retries.clear();
               this.retryTimer.reset();
            }

            switch(this.currentMode) {
            case WEBS:
               this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockWeb.class);
               this.targetSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
               break;
            case OBSIDIAN:
               this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
               this.targetSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            }

            if ((Boolean)this.onlySafe.getValue() && !EntityUtil.isSafe(mc.field_71439_g)) {
               this.disable();
               return true;
            } else if (!this.hasOffhand && this.targetSlot == -1 && (!(Boolean)this.offhand.getValue() || !EntityUtil.isSafe(mc.field_71439_g) && (Boolean)this.onlySafe.getValue())) {
               return true;
            } else if ((Boolean)this.offhand.getValue() && !this.hasOffhand) {
               return true;
            } else {
               return !this.timer.passedMs((long)(Integer)this.delay.getValue());
            }
         }
      } else {
         this.disable();
         return true;
      }
   }

   private boolean switchItem(boolean back) {
      if ((Boolean)this.offhand.getValue()) {
         return true;
      } else {
         boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), this.currentMode == HoleFiller.Mode.WEBS ? BlockWeb.class : BlockObsidian.class);
         this.switchedItem = value[0];
         return value[1];
      }
   }

   public static enum PlaceMode {
      SMART,
      ALL;
   }

   public static enum Mode {
      WEBS,
      OBSIDIAN;
   }
}
