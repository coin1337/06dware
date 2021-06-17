package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Webaura extends Module {
   private final Setting<Integer> delay = this.register(new Setting("Delay/Place", 50, 0, 250));
   private final Setting<Integer> blocksPerPlace = this.register(new Setting("Block/Place", 8, 1, 30));
   private final Setting<Double> targetRange = this.register(new Setting("TargetRange", 10.0D, 0.0D, 20.0D));
   private final Setting<Double> range = this.register(new Setting("PlaceRange", 6.0D, 0.0D, 10.0D));
   private final Setting<Webaura.TargetMode> targetMode;
   private final Setting<InventoryUtil.Switch> switchMode;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> raytrace;
   private final Setting<Double> speed;
   private final Setting<Boolean> upperBody;
   private final Setting<Boolean> lowerbody;
   private final Setting<Boolean> ylower;
   private final Setting<Boolean> antiSelf;
   private final Setting<Integer> eventMode;
   private final Setting<Boolean> freecam;
   private final Setting<Boolean> info;
   private final Setting<Boolean> disable;
   private final Setting<Boolean> packet;
   private final Timer timer;
   private boolean didPlace;
   private boolean switchedItem;
   public EntityPlayer target;
   private boolean isSneaking;
   private int lastHotbarSlot;
   private int placements;
   public static boolean isPlacing = false;
   private boolean smartRotate;
   private BlockPos startPos;

   public Webaura() {
      super("Webaura", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
      this.targetMode = this.register(new Setting("Target", Webaura.TargetMode.CLOSEST));
      this.switchMode = this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
      this.rotate = this.register(new Setting("Rotate", true));
      this.raytrace = this.register(new Setting("Raytrace", false));
      this.speed = this.register(new Setting("Speed", 30.0D, 0.0D, 30.0D));
      this.upperBody = this.register(new Setting("Upper", false));
      this.lowerbody = this.register(new Setting("Lower", true));
      this.ylower = this.register(new Setting("Y-1", false));
      this.antiSelf = this.register(new Setting("AntiSelf", false));
      this.eventMode = this.register(new Setting("Updates", 3, 1, 3));
      this.freecam = this.register(new Setting("Freecam", false));
      this.info = this.register(new Setting("Info", false));
      this.disable = this.register(new Setting("TSelfMove", false));
      this.packet = this.register(new Setting("Packet", false));
      this.timer = new Timer();
      this.didPlace = false;
      this.placements = 0;
      this.smartRotate = false;
      this.startPos = null;
   }

   public void onEnable() {
      if (!fullNullCheck()) {
         this.startPos = EntityUtil.getRoundedBlockPos(mc.field_71439_g);
         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      }
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
      isPlacing = false;
      this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
      this.switchItem(true);
   }

   private void doTrap() {
      if (!this.check()) {
         this.doWebTrap();
         if (this.didPlace) {
            this.timer.reset();
         }

      }
   }

   private void doWebTrap() {
      List<Vec3d> placeTargets = this.getPlacements();
      this.placeList(placeTargets);
   }

   private List<Vec3d> getPlacements() {
      List<Vec3d> list = new ArrayList();
      Vec3d baseVec = this.target.func_174791_d();
      if ((Boolean)this.ylower.getValue()) {
         list.add(baseVec.func_72441_c(0.0D, -1.0D, 0.0D));
      }

      if ((Boolean)this.lowerbody.getValue()) {
         list.add(baseVec);
      }

      if ((Boolean)this.upperBody.getValue()) {
         list.add(baseVec.func_72441_c(0.0D, 1.0D, 0.0D));
      }

      return list;
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
         Vec3d vec3d;
         BlockPos position;
         int placeability;
         do {
            do {
               if (!var2.hasNext()) {
                  return;
               }

               vec3d = (Vec3d)var2.next();
               position = new BlockPos(vec3d);
               placeability = BlockUtil.isPositionPlaceable(position, (Boolean)this.raytrace.getValue());
            } while(placeability != 3 && placeability != 1);
         } while((Boolean)this.antiSelf.getValue() && MathUtil.areVec3dsAligned(mc.field_71439_g.func_174791_d(), vec3d));

         this.placeBlock(position);
      }
   }

   private boolean check() {
      isPlacing = false;
      this.didPlace = false;
      this.placements = 0;
      int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
      if (this.isOff()) {
         return true;
      } else if ((Boolean)this.disable.getValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos(mc.field_71439_g))) {
         this.disable();
         return true;
      } else if (obbySlot == -1) {
         if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
            if ((Boolean)this.info.getValue()) {
               Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "You are out of Webs.");
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
         this.target = this.getTarget((Double)this.targetRange.getValue(), this.targetMode.getValue() == Webaura.TargetMode.UNTRAPPED);
         return this.target == null || Phobos.moduleManager.isModuleEnabled("Freecam") && !(Boolean)this.freecam.getValue() || !this.timer.passedMs((long)(Integer)this.delay.getValue()) || this.switchMode.getValue() == InventoryUtil.Switch.NONE && mc.field_71439_g.field_71071_by.field_70461_c != InventoryUtil.findHotbarBlock(BlockWeb.class);
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
                  if (!var7.hasNext()) {
                     return target;
                  }

                  player = (EntityPlayer)var7.next();
               } while(EntityUtil.isntValid(player, range));
            } while(trapped && player.field_70134_J);
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
         if (this.smartRotate) {
            this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, (Boolean)this.packet.getValue(), this.isSneaking);
         } else {
            this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), this.isSneaking);
         }

         this.didPlace = true;
         ++this.placements;
      }

   }

   private boolean switchItem(boolean back) {
      boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), BlockWeb.class);
      this.switchedItem = value[0];
      return value[1];
   }

   public static enum TargetMode {
      CLOSEST,
      UNTRAPPED;
   }
}
