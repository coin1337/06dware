package me.earth.phobos.features.modules.combat;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura extends Module {
   public Setting<Float> range = this.register(new Setting("Range", 6.0F, 0.1F, 7.0F));
   private Setting<Killaura.TargetMode> targetMode;
   public Setting<Float> health;
   public Setting<Boolean> delay;
   public Setting<Boolean> rotate;
   public Setting<Boolean> stay;
   public Setting<Boolean> armorBreak;
   public Setting<Boolean> eating;
   public Setting<Boolean> onlySharp;
   public Setting<Boolean> teleport;
   public Setting<Float> raytrace;
   public Setting<Float> teleportRange;
   public Setting<Boolean> lagBack;
   public Setting<Boolean> teekaydelay;
   public Setting<Integer> time32k;
   public Setting<Integer> multi;
   public Setting<Boolean> multi32k;
   public Setting<Boolean> players;
   public Setting<Boolean> mobs;
   public Setting<Boolean> animals;
   public Setting<Boolean> vehicles;
   public Setting<Boolean> projectiles;
   public Setting<Boolean> tps;
   public Setting<Boolean> packet;
   public Setting<Boolean> swing;
   public Setting<Boolean> sneak;
   public Setting<Boolean> info;
   private final Timer timer;
   public static Entity target;

   public Killaura() {
      super("Killaura", "Kills aura.", Module.Category.COMBAT, true, false, false);
      this.targetMode = this.register(new Setting("Target", Killaura.TargetMode.CLOSEST));
      this.health = this.register(new Setting("Health", 6.0F, 0.1F, 36.0F, (v) -> {
         return this.targetMode.getValue() == Killaura.TargetMode.SMART;
      }));
      this.delay = this.register(new Setting("Delay", true));
      this.rotate = this.register(new Setting("Rotate", true));
      this.stay = this.register(new Setting("Stay", true, (v) -> {
         return (Boolean)this.rotate.getValue();
      }));
      this.armorBreak = this.register(new Setting("ArmorBreak", false));
      this.eating = this.register(new Setting("Eating", true));
      this.onlySharp = this.register(new Setting("Axe/Sword", true));
      this.teleport = this.register(new Setting("Teleport", false));
      this.raytrace = this.register(new Setting("Raytrace", 6.0F, 0.1F, 7.0F, (v) -> {
         return !(Boolean)this.teleport.getValue();
      }, "Wall Range."));
      this.teleportRange = this.register(new Setting("TpRange", 15.0F, 0.1F, 50.0F, (v) -> {
         return (Boolean)this.teleport.getValue();
      }, "Teleport Range."));
      this.lagBack = this.register(new Setting("LagBack", true, (v) -> {
         return (Boolean)this.teleport.getValue();
      }));
      this.teekaydelay = this.register(new Setting("32kDelay", false));
      this.time32k = this.register(new Setting("32kTime", 5, 1, 50));
      this.multi = this.register(new Setting("32kPackets", 2, (v) -> {
         return !(Boolean)this.teekaydelay.getValue();
      }));
      this.multi32k = this.register(new Setting("Multi32k", false));
      this.players = this.register(new Setting("Players", true));
      this.mobs = this.register(new Setting("Mobs", false));
      this.animals = this.register(new Setting("Animals", false));
      this.vehicles = this.register(new Setting("Entities", false));
      this.projectiles = this.register(new Setting("Projectiles", false));
      this.tps = this.register(new Setting("TpsSync", true));
      this.packet = this.register(new Setting("Packet", false));
      this.swing = this.register(new Setting("Swing", true));
      this.sneak = this.register(new Setting("State", false));
      this.info = this.register(new Setting("Info", true));
      this.timer = new Timer();
   }

   public void onTick() {
      if (!(Boolean)this.rotate.getValue()) {
         this.doKillaura();
      }

   }

   @SubscribeEvent
   public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0 && (Boolean)this.rotate.getValue()) {
         if ((Boolean)this.stay.getValue() && target != null) {
            Phobos.rotationManager.lookAtEntity(target);
         }

         this.doKillaura();
      }

   }

   private void doKillaura() {
      if ((Boolean)this.onlySharp.getValue() && !EntityUtil.holdingWeapon(mc.field_71439_g)) {
         target = null;
      } else {
         int wait = !(Boolean)this.delay.getValue() || EntityUtil.holding32k(mc.field_71439_g) && !(Boolean)this.teekaydelay.getValue() ? 0 : (int)((float)DamageUtil.getCooldownByWeapon(mc.field_71439_g) * ((Boolean)this.tps.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0F));
         if (this.timer.passedMs((long)wait) && ((Boolean)this.eating.getValue() || !mc.field_71439_g.func_184587_cr() || mc.field_71439_g.func_184592_cb().func_77973_b().equals(Items.field_185159_cQ) && mc.field_71439_g.func_184600_cs() == EnumHand.OFF_HAND)) {
            if (this.targetMode.getValue() != Killaura.TargetMode.FOCUS || target == null || !(mc.field_71439_g.func_70068_e(target) < MathUtil.square((Float)this.range.getValue())) && (!(Boolean)this.teleport.getValue() || !(mc.field_71439_g.func_70068_e(target) < MathUtil.square((Float)this.teleportRange.getValue()))) || !mc.field_71439_g.func_70685_l(target) && !EntityUtil.canEntityFeetBeSeen(target) && !(mc.field_71439_g.func_70068_e(target) < MathUtil.square((Float)this.raytrace.getValue())) && !(Boolean)this.teleport.getValue()) {
               target = this.getTarget();
            }

            if (target != null) {
               if ((Boolean)this.rotate.getValue()) {
                  Phobos.rotationManager.lookAtEntity(target);
               }

               if ((Boolean)this.teleport.getValue()) {
                  Phobos.positionManager.setPositionPacket(target.field_70165_t, EntityUtil.canEntityFeetBeSeen(target) ? target.field_70163_u : target.field_70163_u + (double)target.func_70047_e(), target.field_70161_v, true, true, !(Boolean)this.lagBack.getValue());
               }

               if (EntityUtil.holding32k(mc.field_71439_g) && !(Boolean)this.teekaydelay.getValue()) {
                  if ((Boolean)this.multi32k.getValue()) {
                     Iterator var4 = mc.field_71441_e.field_73010_i.iterator();

                     while(var4.hasNext()) {
                        EntityPlayer player = (EntityPlayer)var4.next();
                        if (EntityUtil.isValid(player, (double)(Float)this.range.getValue())) {
                           this.teekayAttack(player);
                        }
                     }
                  } else {
                     this.teekayAttack(target);
                  }

                  this.timer.reset();
               } else {
                  if ((Boolean)this.armorBreak.getValue()) {
                     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 9, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, mc.field_71439_g);
                     EntityUtil.attackEntity(target, (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
                     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, 9, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, mc.field_71439_g);
                     EntityUtil.attackEntity(target, (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
                  } else {
                     boolean sneaking = mc.field_71439_g.func_70093_af();
                     boolean sprint = mc.field_71439_g.func_70051_ag();
                     if ((Boolean)this.sneak.getValue()) {
                        if (sneaking) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
                        }

                        if (sprint) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SPRINTING));
                        }
                     }

                     EntityUtil.attackEntity(target, (Boolean)this.packet.getValue(), (Boolean)this.swing.getValue());
                     if ((Boolean)this.sneak.getValue()) {
                        if (sprint) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SPRINTING));
                        }

                        if (sneaking) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                        }
                     }
                  }

                  this.timer.reset();
               }
            }
         }
      }
   }

   private void teekayAttack(Entity entity) {
      for(int i = 0; i < (Integer)this.multi.getValue(); ++i) {
         this.startEntityAttackThread(entity, i * (Integer)this.time32k.getValue());
      }

   }

   private void startEntityAttackThread(Entity entity, int time) {
      (new Thread(() -> {
         Timer timer = new Timer();
         timer.reset();

         try {
            Thread.sleep((long)time);
         } catch (InterruptedException var5) {
            Thread.currentThread().interrupt();
         }

         EntityUtil.attackEntity(entity, true, (Boolean)this.swing.getValue());
      })).start();
   }

   private Entity getTarget() {
      Entity target = null;
      double distance = (Boolean)this.teleport.getValue() ? (double)(Float)this.teleportRange.getValue() : (double)(Float)this.range.getValue();
      double maxHealth = 36.0D;
      Iterator var6 = mc.field_71441_e.field_72996_f.iterator();

      while(var6.hasNext()) {
         Entity entity = (Entity)var6.next();
         if (((Boolean)this.players.getValue() && entity instanceof EntityPlayer || (Boolean)this.animals.getValue() && EntityUtil.isPassive(entity) || (Boolean)this.mobs.getValue() && EntityUtil.isMobAggressive(entity) || (Boolean)this.vehicles.getValue() && EntityUtil.isVehicle(entity) || (Boolean)this.projectiles.getValue() && EntityUtil.isProjectile(entity)) && (!(entity instanceof EntityLivingBase) || !EntityUtil.isntValid(entity, distance)) && ((Boolean)this.teleport.getValue() || mc.field_71439_g.func_70685_l(entity) || EntityUtil.canEntityFeetBeSeen(entity) || !(mc.field_71439_g.func_70068_e(entity) > MathUtil.square((Float)this.raytrace.getValue())))) {
            if (target == null) {
               target = entity;
               distance = mc.field_71439_g.func_70068_e(entity);
               maxHealth = (double)EntityUtil.getHealth(entity);
            } else {
               if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer)entity, 18)) {
                  target = entity;
                  break;
               }

               if (this.targetMode.getValue() == Killaura.TargetMode.SMART && EntityUtil.getHealth(entity) < (Float)this.health.getValue()) {
                  target = entity;
                  break;
               }

               if (this.targetMode.getValue() != Killaura.TargetMode.HEALTH && mc.field_71439_g.func_70068_e(entity) < distance) {
                  target = entity;
                  distance = mc.field_71439_g.func_70068_e(entity);
                  maxHealth = (double)EntityUtil.getHealth(entity);
               }

               if (this.targetMode.getValue() == Killaura.TargetMode.HEALTH && (double)EntityUtil.getHealth(entity) < maxHealth) {
                  target = entity;
                  distance = mc.field_71439_g.func_70068_e(entity);
                  maxHealth = (double)EntityUtil.getHealth(entity);
               }
            }
         }
      }

      return target;
   }

   public String getDisplayInfo() {
      return (Boolean)this.info.getValue() && target instanceof EntityPlayer ? target.func_70005_c_() : null;
   }

   public static enum TargetMode {
      FOCUS,
      CLOSEST,
      HEALTH,
      SMART;
   }
}
