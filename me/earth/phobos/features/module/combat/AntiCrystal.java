package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiCrystal extends Module {
   public Setting<Float> range = this.register(new Setting("Range", 6.0F, 0.0F, 10.0F));
   public Setting<Float> wallsRange = this.register(new Setting("WallsRange", 3.5F, 0.0F, 10.0F));
   public Setting<Float> minDmg = this.register(new Setting("MinDmg", 6.0F, 0.0F, 100.0F));
   public Setting<Float> selfDmg = this.register(new Setting("SelfDmg", 2.0F, 0.0F, 10.0F));
   public Setting<Integer> placeDelay = this.register(new Setting("PlaceDelay", 0, 0, 500));
   public Setting<Integer> breakDelay = this.register(new Setting("BreakDelay", 0, 0, 500));
   public Setting<Integer> checkDelay = this.register(new Setting("CheckDelay", 0, 0, 500));
   public Setting<Integer> wasteAmount = this.register(new Setting("WasteAmount", 1, 1, 5));
   public Setting<Boolean> switcher = this.register(new Setting("Switch", true));
   public Setting<Boolean> rotate = this.register(new Setting("Rotate", true));
   public Setting<Boolean> packet = this.register(new Setting("Packet", true));
   public Setting<Integer> rotations = this.register(new Setting("Spoofs", 1, 1, 20));
   private float yaw = 0.0F;
   private float pitch = 0.0F;
   private boolean rotating = false;
   private int rotationPacketsSpoofed = 0;
   private final List<BlockPos> targets = new ArrayList();
   private Entity breakTarget;
   private final Timer timer = new Timer();
   private final Timer breakTimer = new Timer();
   private final Timer checkTimer = new Timer();

   public AntiCrystal() {
      super("AntiCrystal", "Hacker shit", Module.Category.COMBAT, true, false, false);
   }

   public void onToggle() {
      this.rotating = false;
   }

   private Entity getDeadlyCrystal() {
      Entity bestcrystal = null;
      float highestDamage = 0.0F;
      Iterator var3 = mc.field_71441_e.field_72996_f.iterator();

      while(var3.hasNext()) {
         Entity crystal = (Entity)var3.next();
         if (crystal instanceof EntityEnderCrystal && !(mc.field_71439_g.func_70068_e(crystal) > 169.0D)) {
            float damage = DamageUtil.calculateDamage((Entity)crystal, mc.field_71439_g);
            if (!(damage < (Float)this.minDmg.getValue())) {
               if (bestcrystal == null) {
                  bestcrystal = crystal;
                  highestDamage = damage;
               } else if (damage > highestDamage) {
                  bestcrystal = crystal;
                  highestDamage = damage;
               }
            }
         }
      }

      return bestcrystal;
   }

   private int getSafetyCrystals(Entity deadlyCrystal) {
      int count = 0;
      Iterator var3 = mc.field_71441_e.field_72996_f.iterator();

      while(var3.hasNext()) {
         Entity entity = (Entity)var3.next();
         if (!(entity instanceof EntityEnderCrystal)) {
            float damage = DamageUtil.calculateDamage((Entity)entity, mc.field_71439_g);
            if (!(damage > 2.0F) && !(deadlyCrystal.func_70068_e(entity) > 144.0D)) {
               ++count;
            }
         }
      }

      return count;
   }

   private BlockPos getPlaceTarget(Entity deadlyCrystal) {
      BlockPos closestPos = null;
      float smallestDamage = 10.0F;
      Iterator var4 = BlockUtil.possiblePlacePositions((Float)this.range.getValue()).iterator();

      while(true) {
         while(true) {
            BlockPos pos;
            float damage;
            do {
               do {
                  do {
                     if (!var4.hasNext()) {
                        return closestPos;
                     }

                     pos = (BlockPos)var4.next();
                     damage = DamageUtil.calculateDamage((BlockPos)pos, mc.field_71439_g);
                  } while(damage > 2.0F);
               } while(deadlyCrystal.func_174818_b(pos) > 144.0D);
            } while(mc.field_71439_g.func_174818_b(pos) >= MathUtil.square((Float)this.wallsRange.getValue()) && BlockUtil.rayTracePlaceCheck(pos, true, 1.0F));

            if (closestPos == null) {
               smallestDamage = damage;
               closestPos = pos;
            } else if (damage < smallestDamage || damage == smallestDamage && mc.field_71439_g.func_174818_b(pos) < mc.field_71439_g.func_174818_b(closestPos)) {
               smallestDamage = damage;
               closestPos = pos;
            }
         }
      }
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && (Boolean)this.rotate.getValue() && this.rotating) {
         if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer)event.getPacket();
            packet.field_149476_e = this.yaw;
            packet.field_149473_f = this.pitch;
         }

         ++this.rotationPacketsSpoofed;
         if (this.rotationPacketsSpoofed >= (Integer)this.rotations.getValue()) {
            this.rotating = false;
            this.rotationPacketsSpoofed = 0;
         }
      }

   }

   public void onTick() {
      if (!fullNullCheck() && this.checkTimer.passedMs((long)(Integer)this.checkDelay.getValue())) {
         Entity deadlyCrystal = this.getDeadlyCrystal();
         if (deadlyCrystal != null) {
            BlockPos placeTarget = this.getPlaceTarget(deadlyCrystal);
            if (placeTarget != null) {
               this.targets.add(placeTarget);
            }

            this.placeCrystal(deadlyCrystal);
            this.breakTarget = this.getBreakTarget(deadlyCrystal);
            this.breakCrystal();
         }

         this.checkTimer.reset();
      }

   }

   public Entity getBreakTarget(Entity deadlyCrystal) {
      Entity smallestCrystal = null;
      float smallestDamage = 10.0F;
      Iterator var4 = mc.field_71441_e.field_72996_f.iterator();

      while(true) {
         while(true) {
            Entity entity;
            float damage;
            do {
               do {
                  do {
                     do {
                        if (!var4.hasNext()) {
                           return smallestCrystal;
                        }

                        entity = (Entity)var4.next();
                     } while(!(entity instanceof EntityEnderCrystal));

                     damage = DamageUtil.calculateDamage((Entity)entity, mc.field_71439_g);
                  } while(damage > (Float)this.selfDmg.getValue());
               } while(entity.func_70068_e(deadlyCrystal) > 144.0D);
            } while(mc.field_71439_g.func_70068_e(entity) > MathUtil.square((Float)this.wallsRange.getValue()) && EntityUtil.rayTraceHitCheck(entity, true));

            if (smallestCrystal == null) {
               smallestCrystal = entity;
               smallestDamage = damage;
            } else if (damage < smallestDamage || smallestDamage == damage && mc.field_71439_g.func_70068_e(entity) < mc.field_71439_g.func_70068_e(smallestCrystal)) {
               smallestCrystal = entity;
               smallestDamage = damage;
            }
         }
      }
   }

   private void placeCrystal(Entity deadlyCrystal) {
      boolean offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
      if (this.timer.passedMs((long)(Integer)this.placeDelay.getValue()) && ((Boolean)this.switcher.getValue() || mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP || offhand) && !this.targets.isEmpty() && this.getSafetyCrystals(deadlyCrystal) <= (Integer)this.wasteAmount.getValue()) {
         if ((Boolean)this.switcher.getValue() && mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_185158_cP && !offhand) {
            this.doSwitch();
         }

         this.rotateToPos((BlockPos)this.targets.get(this.targets.size() - 1));
         BlockUtil.placeCrystalOnBlock((BlockPos)this.targets.get(this.targets.size() - 1), offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, true);
         this.timer.reset();
      }

   }

   private void doSwitch() {
      int crystalSlot = mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP ? mc.field_71439_g.field_71071_by.field_70461_c : -1;
      if (crystalSlot == -1) {
         for(int l = 0; l < 9; ++l) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(l).func_77973_b() == Items.field_185158_cP) {
               crystalSlot = l;
               break;
            }
         }
      }

      if (crystalSlot != -1) {
         mc.field_71439_g.field_71071_by.field_70461_c = crystalSlot;
      }

   }

   private void breakCrystal() {
      if (this.breakTimer.passedMs((long)(Integer)this.breakDelay.getValue()) && this.breakTarget != null && DamageUtil.canBreakWeakness(mc.field_71439_g)) {
         this.rotateTo(this.breakTarget);
         EntityUtil.attackEntity(this.breakTarget, (Boolean)this.packet.getValue(), true);
         this.breakTimer.reset();
         this.targets.clear();
      }

   }

   private void rotateTo(Entity entity) {
      if ((Boolean)this.rotate.getValue()) {
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), entity.func_174791_d());
         this.yaw = angle[0];
         this.pitch = angle[1];
         this.rotating = true;
      }

   }

   private void rotateToPos(BlockPos pos) {
      if ((Boolean)this.rotate.getValue()) {
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() - 0.5F), (double)((float)pos.func_177952_p() + 0.5F)));
         this.yaw = angle[0];
         this.pitch = angle[1];
         this.rotating = true;
      }

   }
}
