package me.earth.phobos.util;

import java.awt.Color;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.features.modules.combat.Killaura;
import me.earth.phobos.features.modules.player.Blink;
import me.earth.phobos.features.modules.player.FakePlayer;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.mixin.mixins.accessors.IEntityLivingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class EntityUtil implements Util {
   public static final Vec3d[] antiDropOffsetList = new Vec3d[]{new Vec3d(0.0D, -2.0D, 0.0D)};
   public static final Vec3d[] platformOffsetList = new Vec3d[]{new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D)};
   public static final Vec3d[] legOffsetList = new Vec3d[]{new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D)};
   public static final Vec3d[] OffsetList = new Vec3d[]{new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D)};
   public static final Vec3d[] headpiece = new Vec3d[]{new Vec3d(0.0D, 2.0D, 0.0D)};
   public static final Vec3d[] offsetsNoHead = new Vec3d[]{new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D)};
   public static final Vec3d[] antiStepOffsetList = new Vec3d[]{new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D)};
   public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0D, 3.0D, 0.0D)};

   public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
      if (packet) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(entity));
      } else {
         mc.field_71442_b.func_78764_a(mc.field_71439_g, entity);
      }

      if (swingArm) {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   public static Vec3d interpolateEntity(Entity entity, float time) {
      return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)time);
   }

   public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
      return (new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U)).func_178787_e(getInterpolatedAmount(entity, partialTicks));
   }

   public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
      return getInterpolatedPos(entity, partialTicks).func_178786_a(mc.func_175598_ae().field_78725_b, mc.func_175598_ae().field_78726_c, mc.func_175598_ae().field_78723_d);
   }

   public static Vec3d getInterpolatedRenderPos(Vec3d vec) {
      return (new Vec3d(vec.field_72450_a, vec.field_72448_b, vec.field_72449_c)).func_178786_a(mc.func_175598_ae().field_78725_b, mc.func_175598_ae().field_78726_c, mc.func_175598_ae().field_78723_d);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
      return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, Vec3d vec) {
      return getInterpolatedAmount(entity, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
   }

   public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
      return getInterpolatedAmount(entity, (double)partialTicks, (double)partialTicks, (double)partialTicks);
   }

   public static boolean isPassive(Entity entity) {
      if (entity instanceof EntityWolf && ((EntityWolf)entity).func_70919_bu()) {
         return false;
      } else if (!(entity instanceof EntityAgeable) && !(entity instanceof EntityAmbientCreature) && !(entity instanceof EntitySquid)) {
         return entity instanceof EntityIronGolem && ((EntityIronGolem)entity).func_70643_av() == null;
      } else {
         return true;
      }
   }

   public static boolean isSafe(Entity entity, int height, boolean floor) {
      return getUnsafeBlocks(entity, height, floor).size() == 0;
   }

   public static boolean stopSneaking(boolean isSneaking) {
      if (isSneaking && mc.field_71439_g != null) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
      }

      return false;
   }

   public static boolean isSafe(Entity entity) {
      return isSafe(entity, 0, false);
   }

   public static BlockPos getPlayerPos(EntityPlayer player) {
      return new BlockPos(Math.floor(player.field_70165_t), Math.floor(player.field_70163_u), Math.floor(player.field_70161_v));
   }

   public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
      return getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
   }

   public static boolean isMobAggressive(Entity entity) {
      if (entity instanceof EntityPigZombie) {
         if (((EntityPigZombie)entity).func_184734_db() || ((EntityPigZombie)entity).func_175457_ck()) {
            return true;
         }
      } else {
         if (entity instanceof EntityWolf) {
            return ((EntityWolf)entity).func_70919_bu() && !mc.field_71439_g.equals(((EntityWolf)entity).func_70902_q());
         }

         if (entity instanceof EntityEnderman) {
            return ((EntityEnderman)entity).func_70823_r();
         }
      }

      return isHostileMob(entity);
   }

   public static boolean isNeutralMob(Entity entity) {
      return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
   }

   public static boolean isProjectile(Entity entity) {
      return entity instanceof EntityShulkerBullet || entity instanceof EntityFireball;
   }

   public static boolean isVehicle(Entity entity) {
      return entity instanceof EntityBoat || entity instanceof EntityMinecart;
   }

   public static boolean isFriendlyMob(Entity entity) {
      return entity.isCreatureType(EnumCreatureType.CREATURE, false) && !isNeutralMob(entity) || entity.isCreatureType(EnumCreatureType.AMBIENT, false) || entity instanceof EntityVillager || entity instanceof EntityIronGolem || isNeutralMob(entity) && !isMobAggressive(entity);
   }

   public static boolean isHostileMob(Entity entity) {
      return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isNeutralMob(entity);
   }

   public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
      List<Vec3d> vec3ds = new ArrayList();
      Vec3d[] var4 = getOffsets(height, floor);
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Vec3d vector = var4[var6];
         BlockPos targetPos = (new BlockPos(pos)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
         Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
         if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static boolean isInHole(Entity entity) {
      return isBlockValid(new BlockPos(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
   }

   public static boolean isBlockValid(BlockPos blockPos) {
      return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
   }

   public static boolean isObbyHole(BlockPos blockPos) {
      BlockPos[] touchingBlocks = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
      BlockPos[] var2 = touchingBlocks;
      int var3 = touchingBlocks.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BlockPos pos = var2[var4];
         IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
         if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150343_Z) {
            return false;
         }
      }

      return true;
   }

   public static boolean isBedrockHole(BlockPos blockPos) {
      BlockPos[] touchingBlocks = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
      BlockPos[] var2 = touchingBlocks;
      int var3 = touchingBlocks.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BlockPos pos = var2[var4];
         IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
         if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h) {
            return false;
         }
      }

      return true;
   }

   public static boolean isBothHole(BlockPos blockPos) {
      BlockPos[] touchingBlocks = new BlockPos[]{blockPos.func_177978_c(), blockPos.func_177968_d(), blockPos.func_177974_f(), blockPos.func_177976_e(), blockPos.func_177977_b()};
      BlockPos[] var2 = touchingBlocks;
      int var3 = touchingBlocks.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         BlockPos pos = var2[var4];
         IBlockState touchingState = mc.field_71441_e.func_180495_p(pos);
         if (touchingState.func_177230_c() == Blocks.field_150350_a || touchingState.func_177230_c() != Blocks.field_150357_h && touchingState.func_177230_c() != Blocks.field_150343_Z) {
            return false;
         }
      }

      return true;
   }

   public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
      List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
      Vec3d[] array = new Vec3d[list.size()];
      return (Vec3d[])list.toArray(array);
   }

   public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height, boolean floor) {
      List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height, floor);
      Vec3d[] array = new Vec3d[list.size()];
      return (Vec3d[])list.toArray(array);
   }

   public static double getDst(Vec3d vec) {
      return mc.field_71439_g.func_174791_d().func_72438_d(vec);
   }

   public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
   }

   public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean noScaffoldExtend) {
      return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace, noScaffoldExtend).size() == 0;
   }

   public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> vec3ds = new ArrayList();
      if (!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
         vec3ds.addAll(getUnsafeBlocks(player, 2, false));
      }

      for(int i = 0; i < getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop).length; ++i) {
         Vec3d vector = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop)[i];
         BlockPos targetPos = (new BlockPos(player.func_174791_d())).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
         Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
         if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static boolean isInWater(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         double y = entity.field_70163_u + 0.01D;

         for(int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); ++x) {
            for(int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ++z) {
               BlockPos pos = new BlockPos(x, (int)y, z);
               if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isDrivenByPlayer(Entity entityIn) {
      return mc.field_71439_g != null && entityIn != null && entityIn.equals(mc.field_71439_g.func_184187_bx());
   }

   public static boolean isPlayer(Entity entity) {
      return entity instanceof EntityPlayer;
   }

   public static boolean isAboveWater(Entity entity) {
      return isAboveWater(entity, false);
   }

   public static boolean isAboveWater(Entity entity, boolean packet) {
      if (entity == null) {
         return false;
      } else {
         double y = entity.field_70163_u - (packet ? 0.03D : (isPlayer(entity) ? 0.2D : 0.5D));

         for(int x = MathHelper.func_76128_c(entity.field_70165_t); x < MathHelper.func_76143_f(entity.field_70165_t); ++x) {
            for(int z = MathHelper.func_76128_c(entity.field_70161_v); z < MathHelper.func_76143_f(entity.field_70161_v); ++z) {
               BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
               if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace, boolean noScaffoldExtend) {
      List<Vec3d> placeTargets = new ArrayList();
      Iterator var11;
      Vec3d vec3d;
      if (extension == 1) {
         placeTargets.addAll(targets(player.func_174791_d(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
      } else {
         int extend = 1;

         for(var11 = MathUtil.getBlockBlocks(player).iterator(); var11.hasNext(); ++extend) {
            vec3d = (Vec3d)var11.next();
            if (extend > extension) {
               break;
            }

            placeTargets.addAll(targets(vec3d, !noScaffoldExtend, antiStep, legs, platform, antiDrop, raytrace));
         }
      }

      List<Vec3d> removeList = new ArrayList();
      var11 = placeTargets.iterator();

      while(var11.hasNext()) {
         vec3d = (Vec3d)var11.next();
         BlockPos pos = new BlockPos(vec3d);
         if (BlockUtil.isPositionPlaceable(pos, raytrace) == -1) {
            removeList.add(vec3d);
         }
      }

      var11 = removeList.iterator();

      while(var11.hasNext()) {
         vec3d = (Vec3d)var11.next();
         placeTargets.remove(vec3d);
      }

      return placeTargets;
   }

   public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      List<Vec3d> placeTargets = new ArrayList();
      if (antiDrop) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiDropOffsetList));
      }

      if (platform) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
      }

      if (legs) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
      }

      Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, OffsetList));
      if (antiStep) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiStepOffsetList));
      } else {
         List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
         if (vec3ds.size() == 4) {
            Iterator var9 = vec3ds.iterator();

            label35:
            while(var9.hasNext()) {
               Vec3d vector = (Vec3d)var9.next();
               BlockPos position = (new BlockPos(vec3d)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
               switch(BlockUtil.isPositionPlaceable(position, raytrace)) {
               case -1:
               case 1:
               case 2:
                  break;
               case 0:
               default:
                  break label35;
               case 3:
                  placeTargets.add(vec3d.func_178787_e(vector));
                  break label35;
               }
            }
         }
      }

      if (antiScaffold) {
         Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
      }

      return placeTargets;
   }

   public static List<Vec3d> getOffsetList(int y, boolean floor) {
      List<Vec3d> offsets = new ArrayList();
      offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
      offsets.add(new Vec3d(1.0D, (double)y, 0.0D));
      offsets.add(new Vec3d(0.0D, (double)y, -1.0D));
      offsets.add(new Vec3d(0.0D, (double)y, 1.0D));
      if (floor) {
         offsets.add(new Vec3d(0.0D, (double)(y - 1), 0.0D));
      }

      return offsets;
   }

   public static Vec3d[] getOffsets(int y, boolean floor) {
      List<Vec3d> offsets = getOffsetList(y, floor);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> offsets = new ArrayList(getOffsetList(1, false));
      offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
      if (antiScaffold) {
         offsets.add(new Vec3d(0.0D, 3.0D, 0.0D));
      }

      if (antiStep) {
         offsets.addAll(getOffsetList(2, false));
      }

      if (legs) {
         offsets.addAll(getOffsetList(0, false));
      }

      if (platform) {
         offsets.addAll(getOffsetList(-1, false));
         offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
      }

      if (antiDrop) {
         offsets.add(new Vec3d(0.0D, -2.0D, 0.0D));
      }

      return offsets;
   }

   public static Vec3d[] getHeightOffsets(int min, int max) {
      List<Vec3d> offsets = new ArrayList();

      for(int i = min; i <= max; ++i) {
         offsets.add(new Vec3d(0.0D, (double)i, 0.0D));
      }

      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static BlockPos getRoundedBlockPos(Entity entity) {
      return new BlockPos(MathUtil.roundVec(entity.func_174791_d(), 0));
   }

   public static boolean isLiving(Entity entity) {
      return entity instanceof EntityLivingBase;
   }

   public static boolean isAlive(Entity entity) {
      return isLiving(entity) && !entity.field_70128_L && ((EntityLivingBase)((EntityLivingBase)entity)).func_110143_aJ() > 0.0F;
   }

   public static boolean isDead(Entity entity) {
      return !isAlive(entity);
   }

   public static float getHealth(Entity entity) {
      if (isLiving(entity)) {
         EntityLivingBase livingBase = (EntityLivingBase)entity;
         return livingBase.func_110143_aJ() + livingBase.func_110139_bj();
      } else {
         return 0.0F;
      }
   }

   public static float getHealth(Entity entity, boolean absorption) {
      if (isLiving(entity)) {
         EntityLivingBase livingBase = (EntityLivingBase)entity;
         return livingBase.func_110143_aJ() + (absorption ? livingBase.func_110139_bj() : 0.0F);
      } else {
         return 0.0F;
      }
   }

   public static boolean canEntityFeetBeSeen(Entity entityIn) {
      return mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70165_t + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d(entityIn.field_70165_t, entityIn.field_70163_u, entityIn.field_70161_v), false, true, false) == null;
   }

   public static boolean isntValid(Entity entity, double range) {
      return entity == null || isDead(entity) || entity.equals(mc.field_71439_g) || entity instanceof EntityPlayer && Phobos.friendManager.isFriend(entity.func_70005_c_()) || mc.field_71439_g.func_70068_e(entity) > MathUtil.square(range);
   }

   public static boolean isValid(Entity entity, double range) {
      return !isntValid(entity, range);
   }

   public static boolean holdingWeapon(EntityPlayer player) {
      return player.func_184614_ca().func_77973_b() instanceof ItemSword || player.func_184614_ca().func_77973_b() instanceof ItemAxe;
   }

   public static double getMaxSpeed() {
      double maxModifier = 0.2873D;
      if (mc.field_71439_g.func_70644_a((Potion)Objects.requireNonNull(Potion.func_188412_a(1)))) {
         maxModifier *= 1.0D + 0.2D * (double)(((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b((Potion)Objects.requireNonNull(Potion.func_188412_a(1))))).func_76458_c() + 1);
      }

      return maxModifier;
   }

   public static void mutliplyEntitySpeed(Entity entity, double multiplier) {
      if (entity != null) {
         entity.field_70159_w *= multiplier;
         entity.field_70179_y *= multiplier;
      }

   }

   public static boolean isEntityMoving(Entity entity) {
      if (entity == null) {
         return false;
      } else if (entity instanceof EntityPlayer) {
         return mc.field_71474_y.field_74351_w.func_151470_d() || mc.field_71474_y.field_74368_y.func_151470_d() || mc.field_71474_y.field_74370_x.func_151470_d() || mc.field_71474_y.field_74366_z.func_151470_d();
      } else {
         return entity.field_70159_w != 0.0D || entity.field_70181_x != 0.0D || entity.field_70179_y != 0.0D;
      }
   }

   public static boolean movementKey() {
      return mc.field_71439_g.field_71158_b.field_187255_c || mc.field_71439_g.field_71158_b.field_187258_f || mc.field_71439_g.field_71158_b.field_187257_e || mc.field_71439_g.field_71158_b.field_187256_d || mc.field_71439_g.field_71158_b.field_78901_c || mc.field_71439_g.field_71158_b.field_78899_d;
   }

   public static double getEntitySpeed(Entity entity) {
      if (entity != null) {
         double distTraveledLastTickX = entity.field_70165_t - entity.field_70169_q;
         double distTraveledLastTickZ = entity.field_70161_v - entity.field_70166_s;
         double speed = (double)MathHelper.func_76133_a(distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ);
         return speed * 20.0D;
      } else {
         return 0.0D;
      }
   }

   public static boolean holding32k(EntityPlayer player) {
      return is32k(player.func_184614_ca());
   }

   public static boolean is32k(ItemStack stack) {
      if (stack == null) {
         return false;
      } else if (stack.func_77978_p() == null) {
         return false;
      } else {
         NBTTagList enchants = (NBTTagList)stack.func_77978_p().func_74781_a("ench");
         if (enchants == null) {
            return false;
         } else {
            for(int i = 0; i < enchants.func_74745_c(); ++i) {
               NBTTagCompound enchant = enchants.func_150305_b(i);
               if (enchant.func_74762_e("id") == 16) {
                  int lvl = enchant.func_74762_e("lvl");
                  if (lvl >= 42) {
                     return true;
                  }
                  break;
               }
            }

            return false;
         }
      }
   }

   public static boolean simpleIs32k(ItemStack stack) {
      return EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, stack) >= 1000;
   }

   public static void moveEntityStrafe(double speed, Entity entity) {
      if (entity != null) {
         MovementInput movementInput = mc.field_71439_g.field_71158_b;
         double forward = (double)movementInput.field_192832_b;
         double strafe = (double)movementInput.field_78902_a;
         float yaw = mc.field_71439_g.field_70177_z;
         if (forward == 0.0D && strafe == 0.0D) {
            entity.field_70159_w = 0.0D;
            entity.field_70179_y = 0.0D;
         } else {
            if (forward != 0.0D) {
               if (strafe > 0.0D) {
                  yaw += (float)(forward > 0.0D ? -45 : 45);
               } else if (strafe < 0.0D) {
                  yaw += (float)(forward > 0.0D ? 45 : -45);
               }

               strafe = 0.0D;
               if (forward > 0.0D) {
                  forward = 1.0D;
               } else if (forward < 0.0D) {
                  forward = -1.0D;
               }
            }

            entity.field_70159_w = forward * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F))) + strafe * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F)));
            entity.field_70179_y = forward * speed * Math.sin(Math.toRadians((double)(yaw + 90.0F))) - strafe * speed * Math.cos(Math.toRadians((double)(yaw + 90.0F)));
         }
      }

   }

   public static boolean rayTraceHitCheck(Entity entity, boolean shouldCheck) {
      return !shouldCheck || mc.field_71439_g.func_70685_l(entity);
   }

   public static Color getColor(Entity entity, int red, int green, int blue, int alpha, boolean colorFriends) {
      Color color = new Color((float)red / 255.0F, (float)green / 255.0F, (float)blue / 255.0F, (float)alpha / 255.0F);
      if (entity instanceof EntityPlayer) {
         if (colorFriends && Phobos.friendManager.isFriend((EntityPlayer)entity)) {
            color = new Color(0.33333334F, 1.0F, 1.0F, (float)alpha / 255.0F);
         }

         Killaura killaura = (Killaura)Phobos.moduleManager.getModuleByClass(Killaura.class);
         if ((Boolean)killaura.info.getValue() && Killaura.target != null && Killaura.target.equals(entity)) {
            color = new Color(1.0F, 0.0F, 0.0F, (float)alpha / 255.0F);
         }
      }

      return color;
   }

   public static boolean isFakePlayer(EntityPlayer player) {
      Freecam freecam = Freecam.getInstance();
      FakePlayer fakePlayer = FakePlayer.getInstance();
      Blink blink = Blink.getInstance();
      int playerID = player.func_145782_y();
      if (freecam.isOn() && playerID == 69420) {
         return true;
      } else {
         if (fakePlayer.isOn()) {
            Iterator var5 = fakePlayer.fakePlayerIdList.iterator();

            while(var5.hasNext()) {
               int id = (Integer)var5.next();
               if (id == playerID) {
                  return true;
               }
            }
         }

         if (blink.isOn()) {
            return playerID == 6942069;
         } else {
            return false;
         }
      }
   }

   public static boolean isMoving() {
      return (double)mc.field_71439_g.field_191988_bg != 0.0D || (double)mc.field_71439_g.field_70702_br != 0.0D;
   }

   public static EntityPlayer getClosestEnemy(double distance) {
      EntityPlayer closest = null;
      Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

      while(var3.hasNext()) {
         EntityPlayer player = (EntityPlayer)var3.next();
         if (!isntValid(player, distance)) {
            if (closest == null) {
               closest = player;
            } else if (mc.field_71439_g.func_70068_e(player) < mc.field_71439_g.func_70068_e(closest)) {
               closest = player;
            }
         }
      }

      return closest;
   }

   public static boolean checkCollide() {
      if (mc.field_71439_g.func_70093_af()) {
         return false;
      } else if (mc.field_71439_g.func_184187_bx() != null && mc.field_71439_g.func_184187_bx().field_70143_R >= 3.0F) {
         return false;
      } else {
         return !(mc.field_71439_g.field_70143_R >= 3.0F);
      }
   }

   public static boolean isInLiquid() {
      if (mc.field_71439_g.field_70143_R >= 3.0F) {
         return false;
      } else {
         boolean inLiquid = false;
         AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ() : mc.field_71439_g.func_174813_aQ();
         int y = (int)bb.field_72338_b;

         for(int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
            for(int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
               Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
               if (!(block instanceof BlockAir)) {
                  if (!(block instanceof BlockLiquid)) {
                     return false;
                  }

                  inLiquid = true;
               }
            }
         }

         return inLiquid;
      }
   }

   public static boolean isOnLiquid(double offset) {
      if (mc.field_71439_g.field_70143_R >= 3.0F) {
         return false;
      } else {
         AxisAlignedBB bb = mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(0.0D, -offset, 0.0D) : mc.field_71439_g.func_174813_aQ().func_191195_a(0.0D, 0.0D, 0.0D).func_72317_d(0.0D, -offset, 0.0D);
         boolean onLiquid = false;
         int y = (int)bb.field_72338_b;

         for(int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d + 1.0D); ++x) {
            for(int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f + 1.0D); ++z) {
               Block block = mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
               if (block != Blocks.field_150350_a) {
                  if (!(block instanceof BlockLiquid)) {
                     return false;
                  }

                  onLiquid = true;
               }
            }
         }

         return onLiquid;
      }
   }

   public static boolean isAboveLiquid(Entity entity) {
      if (entity == null) {
         return false;
      } else {
         double n = entity.field_70163_u + 0.01D;

         for(int i = MathHelper.func_76128_c(entity.field_70165_t); i < MathHelper.func_76143_f(entity.field_70165_t); ++i) {
            for(int j = MathHelper.func_76128_c(entity.field_70161_v); j < MathHelper.func_76143_f(entity.field_70161_v); ++j) {
               if (mc.field_71441_e.func_180495_p(new BlockPos(i, (int)n, j)).func_177230_c() instanceof BlockLiquid) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static BlockPos getPlayerPosWithEntity() {
      return new BlockPos(mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().field_70165_t : mc.field_71439_g.field_70165_t, mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().field_70163_u : mc.field_71439_g.field_70163_u, mc.field_71439_g.func_184187_bx() != null ? mc.field_71439_g.func_184187_bx().field_70161_v : mc.field_71439_g.field_70161_v);
   }

   public static boolean checkForLiquid(Entity entity, boolean b) {
      if (entity == null) {
         return false;
      } else {
         double posY = entity.field_70163_u;
         double n;
         if (b) {
            n = 0.03D;
         } else if (entity instanceof EntityPlayer) {
            n = 0.2D;
         } else {
            n = 0.5D;
         }

         double n2 = posY - n;

         for(int i = MathHelper.func_76128_c(entity.field_70165_t); i < MathHelper.func_76143_f(entity.field_70165_t); ++i) {
            for(int j = MathHelper.func_76128_c(entity.field_70161_v); j < MathHelper.func_76143_f(entity.field_70161_v); ++j) {
               if (mc.field_71441_e.func_180495_p(new BlockPos(i, MathHelper.func_76128_c(n2), j)).func_177230_c() instanceof BlockLiquid) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isOnLiquid() {
      double y = mc.field_71439_g.field_70163_u - 0.03D;

      for(int x = MathHelper.func_76128_c(mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f(mc.field_71439_g.field_70165_t); ++x) {
         for(int z = MathHelper.func_76128_c(mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f(mc.field_71439_g.field_70161_v); ++z) {
            BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
            if (mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }

   public static double[] forward(double speed) {
      float forward = mc.field_71439_g.field_71158_b.field_192832_b;
      float side = mc.field_71439_g.field_71158_b.field_78902_a;
      float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
      if (forward != 0.0F) {
         if (side > 0.0F) {
            yaw += (float)(forward > 0.0F ? -45 : 45);
         } else if (side < 0.0F) {
            yaw += (float)(forward > 0.0F ? 45 : -45);
         }

         side = 0.0F;
         if (forward > 0.0F) {
            forward = 1.0F;
         } else if (forward < 0.0F) {
            forward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double posX = (double)forward * speed * cos + (double)side * speed * sin;
      double posZ = (double)forward * speed * sin - (double)side * speed * cos;
      return new double[]{posX, posZ};
   }

   public static Map<String, Integer> getTextRadarPlayers() {
      Map<String, Integer> output = new HashMap();
      DecimalFormat dfHealth = new DecimalFormat("#.#");
      dfHealth.setRoundingMode(RoundingMode.CEILING);
      DecimalFormat dfDistance = new DecimalFormat("#.#");
      dfDistance.setRoundingMode(RoundingMode.CEILING);
      StringBuilder healthSB = new StringBuilder();
      StringBuilder distanceSB = new StringBuilder();
      Iterator var5 = mc.field_71441_e.field_73010_i.iterator();

      while(true) {
         EntityPlayer player;
         do {
            if (!var5.hasNext()) {
               if (!((Map)output).isEmpty()) {
                  output = MathUtil.sortByValue((Map)output, false);
               }

               return (Map)output;
            }

            player = (EntityPlayer)var5.next();
         } while(player.func_82150_aj() && !(Boolean)Managers.getInstance().tRadarInv.getValue());

         if (!player.func_70005_c_().equals(mc.field_71439_g.func_70005_c_())) {
            int hpRaw = (int)getHealth(player);
            String hp = dfHealth.format((long)hpRaw);
            healthSB.append("§");
            if (hpRaw >= 20) {
               healthSB.append("a");
            } else if (hpRaw >= 10) {
               healthSB.append("e");
            } else if (hpRaw >= 5) {
               healthSB.append("6");
            } else {
               healthSB.append("c");
            }

            healthSB.append(hp);
            int distanceInt = (int)mc.field_71439_g.func_70032_d(player);
            String distance = dfDistance.format((long)distanceInt);
            distanceSB.append("§");
            if (distanceInt >= 25) {
               distanceSB.append("a");
            } else if (distanceInt > 10) {
               distanceSB.append("6");
            } else if (distanceInt >= 50) {
               distanceSB.append("7");
            } else {
               distanceSB.append("c");
            }

            distanceSB.append(distance);
            ((Map)output).put(healthSB.toString() + " " + (Phobos.friendManager.isFriend(player) ? "§b" : "§r") + player.func_70005_c_() + " " + distanceSB.toString() + " " + "§f" + Phobos.totemPopManager.getTotemPopString(player) + Phobos.potionManager.getTextRadarPotion(player), (int)mc.field_71439_g.func_70032_d(player));
            healthSB.setLength(0);
            distanceSB.setLength(0);
         }
      }
   }

   public static void swingArmNoPacket(EnumHand hand, EntityLivingBase entity) {
      ItemStack stack = entity.func_184586_b(hand);
      if (stack.func_190926_b() || !stack.func_77973_b().onEntitySwing(entity, stack)) {
         if (!entity.field_82175_bq || entity.field_110158_av >= ((IEntityLivingBase)entity).getArmSwingAnimationEnd() / 2 || entity.field_110158_av < 0) {
            entity.field_110158_av = -1;
            entity.field_82175_bq = true;
            entity.field_184622_au = hand;
         }

      }
   }

   public static boolean isAboveBlock(Entity entity, BlockPos blockPos) {
      return entity.field_70163_u >= (double)blockPos.func_177956_o();
   }
}
