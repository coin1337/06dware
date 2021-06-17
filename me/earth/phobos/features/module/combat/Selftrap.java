package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class Selftrap extends Module {
   public Setting<Selftrap.Mode> mode;
   public Setting<Bind> obbyBind;
   public Setting<Bind> webBind;
   private final Setting<Boolean> smart;
   private final Setting<Double> smartRange;
   private final Setting<Integer> delay;
   private final Setting<Integer> blocksPerTick;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> disable;
   private final Setting<Integer> disableTime;
   private final Setting<Boolean> offhand;
   private final Setting<InventoryUtil.Switch> switchMode;
   private final Setting<Boolean> onlySafe;
   private final Setting<Boolean> highWeb;
   private final Setting<Boolean> freecam;
   private final Setting<Boolean> packet;
   public Selftrap.Mode currentMode;
   private final Timer offTimer;
   private final Timer timer;
   private boolean accessedViaBind;
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

   public Selftrap() {
      super("Selftrap", "Lure your enemies in!", Module.Category.COMBAT, true, false, true);
      this.mode = this.register(new Setting("Mode", Selftrap.Mode.OBSIDIAN));
      this.obbyBind = this.register(new Setting("Obsidian", new Bind(-1)));
      this.webBind = this.register(new Setting("Webs", new Bind(-1)));
      this.smart = this.register(new Setting("Smart", false));
      this.smartRange = this.register(new Setting("SmartRange", 6.0D, 0.0D, 10.0D));
      this.delay = this.register(new Setting("Delay/Place", 50, 0, 250));
      this.blocksPerTick = this.register(new Setting("Block/Place", 8, 1, 20));
      this.rotate = this.register(new Setting("Rotate", true));
      this.disable = this.register(new Setting("Disable", true));
      this.disableTime = this.register(new Setting("Ms/Disable", 200, 1, 250));
      this.offhand = this.register(new Setting("OffHand", true));
      this.switchMode = this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
      this.onlySafe = this.register(new Setting("OnlySafe", true, (v) -> {
         return (Boolean)this.offhand.getValue();
      }));
      this.highWeb = this.register(new Setting("HighWeb", false));
      this.freecam = this.register(new Setting("Freecam", false));
      this.packet = this.register(new Setting("Packet", false));
      this.currentMode = Selftrap.Mode.OBSIDIAN;
      this.offTimer = new Timer();
      this.timer = new Timer();
      this.accessedViaBind = false;
      this.blocksThisTick = 0;
      this.offhandMode = Offhand.Mode.CRYSTALS;
      this.offhandMode2 = Offhand.Mode2.CRYSTALS;
      this.retries = new HashMap();
      this.retryTimer = new Timer();
      this.hasOffhand = false;
      this.placeHighWeb = false;
      this.lastHotbarSlot = -1;
      this.switchedItem = false;
   }

   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      }

      this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (!this.accessedViaBind) {
         this.currentMode = (Selftrap.Mode)this.mode.getValue();
      }

      Offhand module = (Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class);
      this.offhandMode = module.mode;
      this.offhandMode2 = module.currentMode;
      if ((Boolean)this.offhand.getValue() && (EntityUtil.isSafe(mc.field_71439_g) || !(Boolean)this.onlySafe.getValue())) {
         if (module.type.getValue() == Offhand.Type.OLD) {
            if (this.currentMode == Selftrap.Mode.WEBS) {
               module.setMode(Offhand.Mode2.WEBS);
            } else {
               module.setMode(Offhand.Mode2.OBSIDIAN);
            }
         } else if (this.currentMode == Selftrap.Mode.WEBS) {
            module.setSwapToTotem(false);
            module.setMode(Offhand.Mode.WEBS);
         } else {
            module.setSwapToTotem(false);
            module.setMode(Offhand.Mode.OBSIDIAN);
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
            this.currentMode = Selftrap.Mode.OBSIDIAN;
            this.toggle();
         }

         if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
            this.accessedViaBind = true;
            this.currentMode = Selftrap.Mode.WEBS;
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

         Iterator var4 = this.getPositions().iterator();

         while(true) {
            BlockPos position;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               position = (BlockPos)var4.next();
            } while((Boolean)this.smart.getValue() && !this.isPlayerInRange());

            int placeability = BlockUtil.isPositionPlaceable(position, false);
            if (placeability == 1) {
               switch(this.currentMode) {
               case WEBS:
                  this.placeBlock(position);
                  break;
               case OBSIDIAN:
                  if ((this.switchMode.getValue() == InventoryUtil.Switch.SILENT || BlockTweaks.getINSTANCE().isOn() && (Boolean)BlockTweaks.getINSTANCE().noBlock.getValue()) && (this.retries.get(position) == null || (Integer)this.retries.get(position) < 4)) {
                     this.placeBlock(position);
                     this.retries.put(position, this.retries.get(position) == null ? 1 : (Integer)this.retries.get(position) + 1);
                  }
               }
            }

            if (placeability == 3) {
               this.placeBlock(position);
            }
         }
      }
   }

   private boolean isPlayerInRange() {
      Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

      EntityPlayer player;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         player = (EntityPlayer)var1.next();
      } while(EntityUtil.isntValid(player, (Double)this.smartRange.getValue()));

      return true;
   }

   private List<BlockPos> getPositions() {
      List<BlockPos> positions = new ArrayList();
      switch(this.currentMode) {
      case WEBS:
         positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v));
         if ((Boolean)this.highWeb.getValue()) {
            positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v));
         }
         break;
      case OBSIDIAN:
         positions.add(new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v));
         int placeability = BlockUtil.isPositionPlaceable((BlockPos)positions.get(0), false);
         switch(placeability) {
         case 0:
            return new ArrayList();
         case 1:
            if (BlockUtil.isPositionPlaceable((BlockPos)positions.get(0), false, false) == 3) {
               return positions;
            }
         case 2:
            positions.add(new BlockPos(mc.field_71439_g.field_70165_t + 1.0D, mc.field_71439_g.field_70163_u + 1.0D, mc.field_71439_g.field_70161_v));
            positions.add(new BlockPos(mc.field_71439_g.field_70165_t + 1.0D, mc.field_71439_g.field_70163_u + 2.0D, mc.field_71439_g.field_70161_v));
            break;
         case 3:
            return positions;
         }
      }

      positions.sort(Comparator.comparingDouble(Vec3i::func_177956_o));
      return positions;
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

   private boolean check() {
      if (!fullNullCheck() && (!(Boolean)this.disable.getValue() || !this.offTimer.passedMs((long)(Integer)this.disableTime.getValue()))) {
         if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock(this.currentMode == Selftrap.Mode.WEBS ? BlockWeb.class : BlockObsidian.class)) {
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

            int targetSlot = -1;
            switch(this.currentMode) {
            case WEBS:
               this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockWeb.class);
               targetSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
               break;
            case OBSIDIAN:
               this.hasOffhand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
               targetSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            }

            if ((Boolean)this.onlySafe.getValue() && !EntityUtil.isSafe(mc.field_71439_g)) {
               this.disable();
               return true;
            } else if (!this.hasOffhand && targetSlot == -1 && (!(Boolean)this.offhand.getValue() || !EntityUtil.isSafe(mc.field_71439_g) && (Boolean)this.onlySafe.getValue())) {
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
         boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), this.currentMode == Selftrap.Mode.WEBS ? BlockWeb.class : BlockObsidian.class);
         this.switchedItem = value[0];
         return value[1];
      }
   }

   public static enum Mode {
      WEBS,
      OBSIDIAN;
   }
}
