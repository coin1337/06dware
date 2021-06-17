package me.earth.phobos.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Surround extends Module {
   private final Setting<Integer> delay = this.register(new Setting("Delay/Place", 50, 0, 250));
   private final Setting<Integer> blocksPerTick = this.register(new Setting("Block/Place", 8, 1, 20));
   private final Setting<Boolean> rotate = this.register(new Setting("Rotate", true));
   private final Setting<Boolean> raytrace = this.register(new Setting("Raytrace", false));
   private final Setting<InventoryUtil.Switch> switchMode;
   private final Setting<Boolean> center;
   private final Setting<Boolean> helpingBlocks;
   private final Setting<Boolean> intelligent;
   private final Setting<Boolean> antiPedo;
   private final Setting<Integer> extender;
   private final Setting<Boolean> extendMove;
   private final Setting<Surround.MovementMode> movementMode;
   private final Setting<Double> speed;
   private final Setting<Integer> eventMode;
   private final Setting<Boolean> floor;
   private final Setting<Boolean> echests;
   private final Setting<Boolean> noGhost;
   private final Setting<Boolean> info;
   private final Setting<Integer> retryer;
   private final Setting<Boolean> render;
   public final Setting<Boolean> colorSync;
   public final Setting<Boolean> box;
   public final Setting<Boolean> outline;
   private final Setting<Integer> red;
   private final Setting<Integer> green;
   private final Setting<Integer> blue;
   private final Setting<Integer> alpha;
   private final Setting<Integer> boxAlpha;
   private final Setting<Float> lineWidth;
   public final Setting<Boolean> customOutline;
   private final Setting<Integer> cRed;
   private final Setting<Integer> cGreen;
   private final Setting<Integer> cBlue;
   private final Setting<Integer> cAlpha;
   private final Timer timer;
   private final Timer retryTimer;
   private int isSafe;
   private BlockPos startPos;
   private boolean didPlace;
   private boolean switchedItem;
   private int lastHotbarSlot;
   private boolean isSneaking;
   private int placements;
   private final Set<Vec3d> extendingBlocks;
   private int extenders;
   public static boolean isPlacing = false;
   private int obbySlot;
   private boolean offHand;
   private final Map<BlockPos, Integer> retries;
   private List<BlockPos> placeVectors;

   public Surround() {
      super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
      this.switchMode = this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
      this.center = this.register(new Setting("Center", false));
      this.helpingBlocks = this.register(new Setting("HelpingBlocks", true));
      this.intelligent = this.register(new Setting("Intelligent", false, (v) -> {
         return (Boolean)this.helpingBlocks.getValue();
      }));
      this.antiPedo = this.register(new Setting("NoPedo", false));
      this.extender = this.register(new Setting("Extend", 1, 1, 4));
      this.extendMove = this.register(new Setting("MoveExtend", false, (v) -> {
         return (Integer)this.extender.getValue() > 1;
      }));
      this.movementMode = this.register(new Setting("Movement", Surround.MovementMode.STATIC));
      this.speed = this.register(new Setting("Speed", 10.0D, 0.0D, 30.0D, (v) -> {
         return this.movementMode.getValue() == Surround.MovementMode.LIMIT || this.movementMode.getValue() == Surround.MovementMode.OFF;
      }, "Maximum Movement Speed"));
      this.eventMode = this.register(new Setting("Updates", 3, 1, 3));
      this.floor = this.register(new Setting("Floor", false));
      this.echests = this.register(new Setting("Echests", false));
      this.noGhost = this.register(new Setting("Packet", false));
      this.info = this.register(new Setting("Info", false));
      this.retryer = this.register(new Setting("Retries", 4, 1, 15));
      this.render = this.register(new Setting("Render", true));
      this.colorSync = this.register(new Setting("Sync", false, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.box = this.register(new Setting("Box", false, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.outline = this.register(new Setting("Outline", true, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.red = this.register(new Setting("Red", 0, 0, 255, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.green = this.register(new Setting("Green", 255, 0, 255, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.blue = this.register(new Setting("Blue", 0, 0, 255, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.alpha = this.register(new Setting("Alpha", 255, 0, 255, (v) -> {
         return (Boolean)this.render.getValue();
      }));
      this.boxAlpha = this.register(new Setting("BoxAlpha", 125, 0, 255, (v) -> {
         return (Boolean)this.box.getValue() && (Boolean)this.render.getValue();
      }));
      this.lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F, (v) -> {
         return (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.customOutline = this.register(new Setting("CustomLine", false, (v) -> {
         return (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.cRed = this.register(new Setting("OL-Red", 255, 0, 255, (v) -> {
         return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.cGreen = this.register(new Setting("OL-Green", 255, 0, 255, (v) -> {
         return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.cBlue = this.register(new Setting("OL-Blue", 255, 0, 255, (v) -> {
         return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.cAlpha = this.register(new Setting("OL-Alpha", 255, 0, 255, (v) -> {
         return (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue() && (Boolean)this.render.getValue();
      }));
      this.timer = new Timer();
      this.retryTimer = new Timer();
      this.didPlace = false;
      this.placements = 0;
      this.extendingBlocks = new HashSet();
      this.extenders = 1;
      this.obbySlot = -1;
      this.offHand = false;
      this.retries = new HashMap();
      this.placeVectors = new ArrayList();
   }

   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      }

      this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      this.startPos = EntityUtil.getRoundedBlockPos(mc.field_71439_g);
      if ((Boolean)this.center.getValue() && !Phobos.moduleManager.isModuleEnabled("Freecam")) {
         if (mc.field_71441_e.func_180495_p(new BlockPos(mc.field_71439_g.func_174791_d())).func_177230_c() == Blocks.field_150321_G) {
            Phobos.positionManager.setPositionPacket(mc.field_71439_g.field_70165_t, (double)this.startPos.func_177956_o(), mc.field_71439_g.field_70161_v, true, true, true);
         } else {
            Phobos.positionManager.setPositionPacket((double)this.startPos.func_177958_n() + 0.5D, (double)this.startPos.func_177956_o(), (double)this.startPos.func_177952_p() + 0.5D, true, true, true);
         }
      }

      this.retries.clear();
      this.retryTimer.reset();
   }

   public void onTick() {
      if ((Integer)this.eventMode.getValue() == 3) {
         this.doFeetPlace();
      }

   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0 && (Integer)this.eventMode.getValue() == 2) {
         this.doFeetPlace();
      }

   }

   public void onUpdate() {
      if ((Integer)this.eventMode.getValue() == 1) {
         this.doFeetPlace();
      }

      if (this.isSafe == 2) {
         this.placeVectors = new ArrayList();
      }

   }

   public void onDisable() {
      if (!nullCheck()) {
         isPlacing = false;
         this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
         this.switchItem(true);
      }
   }

   public void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.getValue() && (this.isSafe == 0 || this.isSafe == 1)) {
         this.placeVectors = this.fuckYou3arthqu4keYourCodeIsGarbage();
         Iterator var2 = this.placeVectors.iterator();

         while(var2.hasNext()) {
            BlockPos pos = (BlockPos)var2.next();
            if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockAir) {
               RenderUtil.drawBoxESP(pos, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), (Boolean)this.customOutline.getValue(), new Color((Integer)this.cRed.getValue(), (Integer)this.cGreen.getValue(), (Integer)this.cBlue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
            }
         }
      }

   }

   public String getDisplayInfo() {
      if (!(Boolean)this.info.getValue()) {
         return null;
      } else {
         switch(this.isSafe) {
         case 0:
            return "§cUnsafe";
         case 1:
            return "§eSecure";
         default:
            return "§aSecure";
         }
      }
   }

   private void doFeetPlace() {
      if (!this.check()) {
         if (!EntityUtil.isSafe(mc.field_71439_g, 0, (Boolean)this.floor.getValue())) {
            this.isSafe = 0;
            this.placeBlocks(mc.field_71439_g.func_174791_d(), EntityUtil.getUnsafeBlockArray(mc.field_71439_g, 0, (Boolean)this.floor.getValue()), (Boolean)this.helpingBlocks.getValue(), false, false);
         } else if (!EntityUtil.isSafe(mc.field_71439_g, -1, false)) {
            this.isSafe = 1;
            if ((Boolean)this.antiPedo.getValue()) {
               this.placeBlocks(mc.field_71439_g.func_174791_d(), EntityUtil.getUnsafeBlockArray(mc.field_71439_g, -1, false), false, false, true);
            }
         } else {
            this.isSafe = 2;
         }

         this.processExtendingBlocks();
         if (this.didPlace) {
            this.timer.reset();
         }

      }
   }

   private void processExtendingBlocks() {
      if (this.extendingBlocks.size() == 2 && this.extenders < (Integer)this.extender.getValue()) {
         Vec3d[] array = new Vec3d[2];
         int i = 0;

         for(Iterator var3 = this.extendingBlocks.iterator(); var3.hasNext(); ++i) {
            Vec3d vec3d = (Vec3d)var3.next();
            array[i] = vec3d;
         }

         int placementsBefore = this.placements;
         if (this.areClose(array) != null) {
            this.placeBlocks(this.areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0, (Boolean)this.floor.getValue()), (Boolean)this.helpingBlocks.getValue(), false, true);
         }

         if (placementsBefore < this.placements) {
            this.extendingBlocks.clear();
         }
      } else if (this.extendingBlocks.size() > 2 || this.extenders >= (Integer)this.extender.getValue()) {
         this.extendingBlocks.clear();
      }

   }

   private Vec3d areClose(Vec3d[] vec3ds) {
      int matches = 0;
      Vec3d[] var3 = vec3ds;
      int var4 = vec3ds.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Vec3d vec3d = var3[var5];
         Vec3d[] var7 = EntityUtil.getUnsafeBlockArray(mc.field_71439_g, 0, (Boolean)this.floor.getValue());
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            Vec3d pos = var7[var9];
            if (vec3d.equals(pos)) {
               ++matches;
            }
         }
      }

      if (matches == 2) {
         return mc.field_71439_g.func_174791_d().func_178787_e(vec3ds[0].func_178787_e(vec3ds[1]));
      } else {
         return null;
      }
   }

   private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
      int helpings = 0;
      boolean gotHelp = true;
      Vec3d[] var8 = vec3ds;
      int var9 = vec3ds.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Vec3d vec3d = var8[var10];
         gotHelp = true;
         ++helpings;
         if (isHelping && !(Boolean)this.intelligent.getValue() && helpings > 1) {
            return false;
         }

         BlockPos position = (new BlockPos(pos)).func_177963_a(vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
         switch(BlockUtil.isPositionPlaceable(position, (Boolean)this.raytrace.getValue())) {
         case -1:
         case 0:
         default:
            break;
         case 1:
            if ((this.switchMode.getValue() == InventoryUtil.Switch.SILENT || BlockTweaks.getINSTANCE().isOn() && (Boolean)BlockTweaks.getINSTANCE().noBlock.getValue()) && (this.retries.get(position) == null || (Integer)this.retries.get(position) < (Integer)this.retryer.getValue())) {
               this.placeBlock(position);
               this.retries.put(position, this.retries.get(position) == null ? 1 : (Integer)this.retries.get(position) + 1);
               this.retryTimer.reset();
            } else if (((Boolean)this.extendMove.getValue() || Phobos.speedManager.getSpeedKpH() == 0.0D) && !isExtending && this.extenders < (Integer)this.extender.getValue()) {
               this.placeBlocks(mc.field_71439_g.func_174791_d().func_178787_e(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(mc.field_71439_g.func_174791_d().func_178787_e(vec3d), 0, (Boolean)this.floor.getValue()), hasHelpingBlocks, false, true);
               this.extendingBlocks.add(vec3d);
               ++this.extenders;
            }
            break;
         case 2:
            if (!hasHelpingBlocks) {
               break;
            }

            gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
         case 3:
            if (gotHelp) {
               this.placeBlock(position);
            }

            if (isHelping) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean check() {
      if (fullNullCheck()) {
         return true;
      } else {
         this.offHand = InventoryUtil.isBlock(mc.field_71439_g.func_184592_cb().func_77973_b(), BlockObsidian.class);
         isPlacing = false;
         this.didPlace = false;
         this.extenders = 1;
         this.placements = 0;
         this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
         int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
         if (this.isOff()) {
            return true;
         } else {
            if (this.retryTimer.passedMs(2500L)) {
               this.retries.clear();
               this.retryTimer.reset();
            }

            this.switchItem(true);
            if (this.obbySlot != -1 || this.offHand || (Boolean)this.echests.getValue() && echestSlot != -1) {
               this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
               if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != this.obbySlot && mc.field_71439_g.field_71071_by.field_70461_c != echestSlot) {
                  this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
               }

               switch((Surround.MovementMode)this.movementMode.getValue()) {
               case NONE:
               default:
                  break;
               case STATIC:
                  if (!this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.field_71439_g))) {
                     this.disable();
                     return true;
                  }
               case LIMIT:
                  if (Phobos.speedManager.getSpeedKpH() > (Double)this.speed.getValue()) {
                     return true;
                  }
                  break;
               case OFF:
                  if (Phobos.speedManager.getSpeedKpH() > (Double)this.speed.getValue()) {
                     this.disable();
                     return true;
                  }
               }

               return Phobos.moduleManager.isModuleEnabled("Freecam") || !this.timer.passedMs((long)(Integer)this.delay.getValue()) || this.switchMode.getValue() == InventoryUtil.Switch.NONE && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock(BlockObsidian.class);
            } else {
               if ((Boolean)this.info.getValue()) {
                  Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "You are out of Obsidian.");
               }

               this.disable();
               return true;
            }
         }
      }
   }

   private void placeBlock(BlockPos pos) {
      if (this.placements < (Integer)this.blocksPerTick.getValue() && this.switchItem(false)) {
         isPlacing = true;
         this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.noGhost.getValue(), this.isSneaking);
         this.didPlace = true;
         ++this.placements;
      }

   }

   private boolean switchItem(boolean back) {
      if (this.offHand) {
         return true;
      } else {
         boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), this.obbySlot == -1 ? BlockEnderChest.class : BlockObsidian.class);
         this.switchedItem = value[0];
         return value[1];
      }
   }

   private List<BlockPos> fuckYou3arthqu4keYourCodeIsGarbage() {
      return (Boolean)this.floor.getValue() ? Arrays.asList((new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(0, -1, 0), (new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(1, 0, 0), (new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(-1, 0, 0), (new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(0, 0, -1), (new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(0, 0, 1)) : Arrays.asList((new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(1, 0, 0), (new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(-1, 0, 0), (new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(0, 0, -1), (new BlockPos(mc.field_71439_g.func_174791_d())).func_177982_a(0, 0, 1));
   }

   public static enum MovementMode {
      NONE,
      STATIC,
      LIMIT,
      OFF;
   }
}
