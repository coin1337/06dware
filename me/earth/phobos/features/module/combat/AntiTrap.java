package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiTrap extends Module {
   public Setting<AntiTrap.Rotate> rotate;
   private final Setting<Integer> coolDown;
   private final Setting<InventoryUtil.Switch> switchMode;
   public Setting<Boolean> sortY;
   public static Set<BlockPos> placedPos = new HashSet();
   private final Vec3d[] surroundTargets;
   private int lastHotbarSlot;
   private boolean switchedItem;
   private boolean offhand;
   private final Timer timer;

   public AntiTrap() {
      super("AntiTrap", "Places a crystal to prevent you getting trapped.", Module.Category.COMBAT, true, false, false);
      this.rotate = this.register(new Setting("Rotate", AntiTrap.Rotate.NORMAL));
      this.coolDown = this.register(new Setting("CoolDown", 400, 0, 1000));
      this.switchMode = this.register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
      this.sortY = this.register(new Setting("SortY", true));
      this.surroundTargets = new Vec3d[]{new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, -1.0D), new Vec3d(-1.0D, 0.0D, 1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, -1.0D), new Vec3d(-1.0D, 1.0D, 1.0D)};
      this.lastHotbarSlot = -1;
      this.offhand = false;
      this.timer = new Timer();
   }

   public void onEnable() {
      if (!fullNullCheck() && this.timer.passedMs((long)(Integer)this.coolDown.getValue())) {
         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      } else {
         this.disable();
      }
   }

   public void onDisable() {
      if (!fullNullCheck()) {
         this.switchItem(true);
      }
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (!fullNullCheck() && event.getStage() == 0) {
         this.doAntiTrap();
      }

   }

   public void doAntiTrap() {
      this.offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
      if (!this.offhand && InventoryUtil.findHotbarBlock(ItemEndCrystal.class) == -1) {
         this.disable();
      } else {
         this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         List<Vec3d> targets = new ArrayList();
         Collections.addAll(targets, BlockUtil.convertVec3ds(mc.field_71439_g.func_174791_d(), this.surroundTargets));
         EntityPlayer closestPlayer = EntityUtil.getClosestEnemy(6.0D);
         if (closestPlayer != null) {
            targets.sort((vec3dx, vec3d2) -> {
               return Double.compare(closestPlayer.func_70092_e(vec3d2.field_72450_a, vec3d2.field_72448_b, vec3d2.field_72449_c), closestPlayer.func_70092_e(vec3dx.field_72450_a, vec3dx.field_72448_b, vec3dx.field_72449_c));
            });
            if ((Boolean)this.sortY.getValue()) {
               targets.sort(Comparator.comparingDouble((vec3dx) -> {
                  return vec3dx.field_72448_b;
               }));
            }
         }

         Iterator var3 = targets.iterator();

         while(var3.hasNext()) {
            Vec3d vec3d = (Vec3d)var3.next();
            BlockPos pos = new BlockPos(vec3d);
            if (BlockUtil.canPlaceCrystal(pos)) {
               this.placeCrystal(pos);
               this.disable();
               break;
            }
         }

      }
   }

   private void placeCrystal(BlockPos pos) {
      boolean mainhand = mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP;
      if (!mainhand && !this.offhand && !this.switchItem(false)) {
         this.disable();
      } else {
         RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() - 0.5D, (double)pos.func_177952_p() + 0.5D));
         EnumFacing facing = result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() - 0.5F), (double)((float)pos.func_177952_p() + 0.5F)));
         switch((AntiTrap.Rotate)this.rotate.getValue()) {
         case NONE:
         default:
            break;
         case NORMAL:
            Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
            break;
         case PACKET:
            mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(angle[0], (float)MathHelper.func_180184_b((int)angle[1], 360), mc.field_71439_g.field_70122_E));
         }

         placedPos.add(pos);
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         this.timer.reset();
      }
   }

   private boolean switchItem(boolean back) {
      if (this.offhand) {
         return true;
      } else {
         boolean[] value = InventoryUtil.switchItemToItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), Items.field_185158_cP);
         this.switchedItem = value[0];
         return value[1];
      }
   }

   public static enum Rotate {
      NONE,
      NORMAL,
      PACKET;
   }
}
