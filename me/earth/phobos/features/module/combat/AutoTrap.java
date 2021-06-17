package me.earth.phobos.features.modules.combat;

import java.awt.Color;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTrap extends Module {
   private final Setting<Integer> delay = this.register(new Setting("Delay/Place", 50, 0, 250));
   private final Setting<Integer> blocksPerPlace = this.register(new Setting("Block/Place", 8, 1, 30));
   private final Setting<Double> targetRange = this.register(new Setting("TargetRange", 10.0D, 0.0D, 20.0D));
   private final Setting<Double> range = this.register(new Setting("PlaceRange", 6.0D, 0.0D, 10.0D));
   private final Setting<AutoTrap.TargetMode> targetMode;
   private final Setting<InventoryUtil.Switch> switchMode;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> raytrace;
   private final Setting<AutoTrap.Pattern> pattern;
   private final Setting<Integer> extend;
   private final Setting<Boolean> antiScaffold;
   private final Setting<Boolean> antiStep;
   private final Setting<Boolean> legs;
   private final Setting<Boolean> platform;
   private final Setting<Boolean> antiDrop;
   private final Setting<Double> speed;
   private final Setting<Boolean> antiSelf;
   private final Setting<Integer> eventMode;
   private final Setting<Boolean> freecam;
   private final Setting<Boolean> info;
   private final Setting<Boolean> entityCheck;
   private final Setting<Boolean> noScaffoldExtend;
   private final Setting<Boolean> disable;
   private final Setting<Boolean> packet;
   private final Setting<Boolean> airPacket;
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
   private boolean didPlace;
   private boolean switchedItem;
   public EntityPlayer target;
   private boolean isSneaking;
   private int lastHotbarSlot;
   private int placements;
   public static boolean isPlacing = false;
   private boolean smartRotate;
   private final Map<BlockPos, Integer> retries;
   private final Timer retryTimer;
   private BlockPos startPos;
   private final Map<BlockPos, IBlockState> toAir;
   private List<Vec3d> currentPlaceList;

   public AutoTrap() {
      super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
      this.targetMode = this.register(new Setting("Target", AutoTrap.TargetMode.CLOSEST));
      this.switchMode = this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
      this.rotate = this.register(new Setting("Rotate", true));
      this.raytrace = this.register(new Setting("Raytrace", false));
      this.pattern = this.register(new Setting("Pattern", AutoTrap.Pattern.STATIC));
      this.extend = this.register(new Setting("Extend", 4, 1, 4, (v) -> {
         return this.pattern.getValue() != AutoTrap.Pattern.STATIC;
      }, "Extending the Trap."));
      this.antiScaffold = this.register(new Setting("AntiScaffold", false));
      this.antiStep = this.register(new Setting("AntiStep", false));
      this.legs = this.register(new Setting("Legs", false, (v) -> {
         return this.pattern.getValue() != AutoTrap.Pattern.OPEN;
      }));
      this.platform = this.register(new Setting("Platform", false, (v) -> {
         return this.pattern.getValue() != AutoTrap.Pattern.OPEN;
      }));
      this.antiDrop = this.register(new Setting("AntiDrop", false));
      this.speed = this.register(new Setting("Speed", 10.0D, 0.0D, 30.0D));
      this.antiSelf = this.register(new Setting("AntiSelf", false));
      this.eventMode = this.register(new Setting("Updates", 3, 1, 3));
      this.freecam = this.register(new Setting("Freecam", false));
      this.info = this.register(new Setting("Info", false));
      this.entityCheck = this.register(new Setting("NoBlock", true));
      this.noScaffoldExtend = this.register(new Setting("NoScaffoldExtend", false));
      this.disable = this.register(new Setting("TSelfMove", false));
      this.packet = this.register(new Setting("Packet", false));
      this.airPacket = this.register(new Setting("AirPacket", false, (v) -> {
         return (Boolean)this.packet.getValue();
      }));
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
      this.didPlace = false;
      this.placements = 0;
      this.smartRotate = false;
      this.retries = new HashMap();
      this.retryTimer = new Timer();
      this.startPos = null;
      this.toAir = new HashMap();
   }

   public void onEnable() {
      if (fullNullCheck()) {
         this.disable();
      } else {
         this.toAir.clear();
         this.startPos = EntityUtil.getRoundedBlockPos(mc.field_71439_g);
         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         this.retries.clear();
      }
   }

   public void onLogout() {
      this.disable();
   }

   public void onTick() {
      if ((Integer)this.eventMode.getValue() == 3) {
         this.smartRotate = false;
         this.doTrap();
      }

   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0 && (Integer)this.eventMode.getValue() == 2) {
         this.smartRotate = (Boolean)this.rotate.getValue() && (Integer)this.blocksPerPlace.getValue() == 1;
         this.doTrap();
      }

   }

   public void onUpdate() {
      if ((Integer)this.eventMode.getValue() == 1) {
         this.smartRotate = false;
         this.doTrap();
      }

   }

   public String getDisplayInfo() {
      return (Boolean)this.info.getValue() && this.target != null ? this.target.func_70005_c_() : null;
   }

   public void onDisable() {
      if (!fullNullCheck()) {
         isPlacing = false;
         this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
         this.switchItem(true);
      }
   }

   public void onRender3D(Render3DEvent event) {
      if ((Boolean)this.render.getValue()) {
         Iterator var2 = this.currentPlaceList.iterator();

         while(var2.hasNext()) {
            Vec3d vec = (Vec3d)var2.next();
            BlockPos pos = new BlockPos(vec);
            if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockAir) {
               RenderUtil.drawBoxESP(pos, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), (Boolean)this.customOutline.getValue(), new Color((Integer)this.cRed.getValue(), (Integer)this.cGreen.getValue(), (Integer)this.cBlue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
            }
         }
      }

   }

   private void doTrap() {
      if (!this.check()) {
         switch((AutoTrap.Pattern)this.pattern.getValue()) {
         case STATIC:
            this.doStaticTrap();
            break;
         case SMART:
         case OPEN:
            this.doSmartTrap();
         }

         if ((Boolean)this.packet.getValue() && (Boolean)this.airPacket.getValue()) {
            Iterator var1 = this.toAir.entrySet().iterator();

            while(var1.hasNext()) {
               Entry<BlockPos, IBlockState> entry = (Entry)var1.next();
               mc.field_71441_e.func_175656_a((BlockPos)entry.getKey(), (IBlockState)entry.getValue());
            }

            this.toAir.clear();
         }

         if (this.didPlace) {
            this.timer.reset();
         }

      }
   }

   private void doSmartTrap() {
      List<Vec3d> placeTargets = EntityUtil.getUntrappedBlocksExtended((Integer)this.extend.getValue(), this.target, (Boolean)this.antiScaffold.getValue(), (Boolean)this.antiStep.getValue(), (Boolean)this.legs.getValue(), (Boolean)this.platform.getValue(), (Boolean)this.antiDrop.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.noScaffoldExtend.getValue());
      this.placeList(placeTargets);
      this.currentPlaceList = placeTargets;
   }

   private void doStaticTrap() {
      List<Vec3d> placeTargets = EntityUtil.targets(this.target.func_174791_d(), (Boolean)this.antiScaffold.getValue(), (Boolean)this.antiStep.getValue(), (Boolean)this.legs.getValue(), (Boolean)this.platform.getValue(), (Boolean)this.antiDrop.getValue(), (Boolean)this.raytrace.getValue());
      this.placeList(placeTargets);
      this.currentPlaceList = placeTargets;
   }

   private void placeList(List<Vec3d> list) {
      list.sort((vec3dx, vec3d2) -> {
         return Double.compare(mc.field_71439_g.func_70092_e(vec3d2.field_72450_a, vec3d2.field_72448_b, vec3d2.field_72449_c), mc.field_71439_g.func_70092_e(vec3dx.field_72450_a, vec3dx.field_72448_b, vec3dx.field_72449_c));
      });
      list.sort(Comparator.comparingDouble((vec3dx) -> {
         return vec3dx.field_72448_b;
      }));
      Iterator var2 = list.iterator();

      while(true) {
         while(var2.hasNext()) {
            Vec3d vec3d = (Vec3d)var2.next();
            BlockPos position = new BlockPos(vec3d);
            int placeability = BlockUtil.isPositionPlaceable(position, (Boolean)this.raytrace.getValue());
            if ((Boolean)this.entityCheck.getValue() && placeability == 1 && (this.switchMode.getValue() == InventoryUtil.Switch.SILENT || BlockTweaks.getINSTANCE().isOn() && (Boolean)BlockTweaks.getINSTANCE().noBlock.getValue()) && (this.retries.get(position) == null || (Integer)this.retries.get(position) < (Integer)this.retryer.getValue())) {
               this.placeBlock(position);
               this.retries.put(position, this.retries.get(position) == null ? 1 : (Integer)this.retries.get(position) + 1);
               this.retryTimer.reset();
            } else if (placeability == 3 && (!(Boolean)this.antiSelf.getValue() || !MathUtil.areVec3dsAligned(mc.field_71439_g.func_174791_d(), vec3d))) {
               this.placeBlock(position);
            }
         }

         return;
      }
   }

   private boolean check() {
      isPlacing = false;
      this.didPlace = false;
      this.placements = 0;
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      if (this.isOff()) {
         return true;
      } else if ((Boolean)this.disable.getValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.field_71439_g))) {
         this.disable();
         return true;
      } else {
         if (this.retryTimer.passedMs(2000L)) {
            this.retries.clear();
            this.retryTimer.reset();
         }

         if (obbySlot == -1) {
            if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
               if ((Boolean)this.info.getValue()) {
                  Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "You are out of Obsidian.");
               }

               this.disable();
            }

            return true;
         } else {
            if (mc.field_71439_g.field_71071_by.field_70461_c != this.lastHotbarSlot && mc.field_71439_g.field_71071_by.field_70461_c != obbySlot) {
               this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            }

            this.switchItem(true);
            this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
            this.target = this.getTarget((Double)this.targetRange.getValue(), this.targetMode.getValue() == AutoTrap.TargetMode.UNTRAPPED);
            return this.target == null || Phobos.moduleManager.isModuleEnabled("Freecam") && !(Boolean)this.freecam.getValue() || !this.timer.passedMs((long)(Integer)this.delay.getValue()) || this.switchMode.getValue() == InventoryUtil.Switch.NONE && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock(BlockObsidian.class);
         }
      }
   }

   private EntityPlayer getTarget(double range, boolean trapped) {
      EntityPlayer target = null;
      double distance = Math.pow(range, 2.0D) + 1.0D;
      Iterator var7 = mc.field_71441_e.field_73010_i.iterator();

      while(true) {
         EntityPlayer player;
         do {
            do {
               do {
                  do {
                     if (!var7.hasNext()) {
                        return target;
                     }

                     player = (EntityPlayer)var7.next();
                  } while(EntityUtil.isntValid(player, range));
               } while(this.pattern.getValue() == AutoTrap.Pattern.STATIC && trapped && EntityUtil.isTrapped(player, (Boolean)this.antiScaffold.getValue(), (Boolean)this.antiStep.getValue(), (Boolean)this.legs.getValue(), (Boolean)this.platform.getValue(), (Boolean)this.antiDrop.getValue()));
            } while(this.pattern.getValue() != AutoTrap.Pattern.STATIC && trapped && EntityUtil.isTrappedExtended((Integer)this.extend.getValue(), player, (Boolean)this.antiScaffold.getValue(), (Boolean)this.antiStep.getValue(), (Boolean)this.legs.getValue(), (Boolean)this.platform.getValue(), (Boolean)this.antiDrop.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.noScaffoldExtend.getValue()));
         } while(EntityUtil.getRoundedBlockPos(mc.field_71439_g).equals(EntityUtil.getRoundedBlockPos(player)) && (Boolean)this.antiSelf.getValue());

         if (!(Phobos.speedManager.getPlayerSpeed(player) > (Double)this.speed.getValue())) {
            if (target == null) {
               target = player;
               distance = mc.field_71439_g.func_70068_e(player);
            } else if (mc.field_71439_g.func_70068_e(player) < distance) {
               target = player;
               distance = mc.field_71439_g.func_70068_e(player);
            }
         }
      }
   }

   private void placeBlock(BlockPos pos) {
      if (this.placements < (Integer)this.blocksPerPlace.getValue() && mc.field_71439_g.func_174818_b(pos) <= MathUtil.square((Double)this.range.getValue()) && this.switchItem(false)) {
         isPlacing = true;
         if ((Boolean)this.airPacket.getValue() && (Boolean)this.packet.getValue()) {
            this.toAir.put(pos, mc.field_71441_e.func_180495_p(pos));
         }

         if (this.smartRotate) {
            this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, !(Boolean)this.airPacket.getValue() && (Boolean)this.packet.getValue(), this.isSneaking);
         } else {
            this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), !(Boolean)this.airPacket.getValue() && (Boolean)this.packet.getValue(), this.isSneaking);
         }

         this.didPlace = true;
         ++this.placements;
      }

   }

   private boolean switchItem(boolean back) {
      boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), BlockObsidian.class);
      this.switchedItem = value[0];
      return value[1];
   }

   public static enum TargetMode {
      CLOSEST,
      UNTRAPPED;
   }

   public static enum Pattern {
      STATIC,
      SMART,
      OPEN;
   }
}
