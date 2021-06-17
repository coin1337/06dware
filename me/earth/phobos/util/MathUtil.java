package me.earth.phobos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil implements Util {
   private static final Random random = new Random();

   public static int getRandom(int min, int max) {
      return min + random.nextInt(max - min + 1);
   }

   public static double getRandom(double min, double max) {
      return MathHelper.func_151237_a(min + random.nextDouble() * max, min, max);
   }

   public static float getRandom(float min, float max) {
      return MathHelper.func_76131_a(min + random.nextFloat() * max, min, max);
   }

   public static int clamp(int num, int min, int max) {
      return num < min ? min : Math.min(num, max);
   }

   public static float clamp(float num, float min, float max) {
      return num < min ? min : Math.min(num, max);
   }

   public static double clamp(double num, double min, double max) {
      return num < min ? min : Math.min(num, max);
   }

   public static float sin(float value) {
      return MathHelper.func_76126_a(value);
   }

   public static float cos(float value) {
      return MathHelper.func_76134_b(value);
   }

   public static float wrapDegrees(float value) {
      return MathHelper.func_76142_g(value);
   }

   public static double wrapDegrees(double value) {
      return MathHelper.func_76138_g(value);
   }

   public static Vec3d roundVec(Vec3d vec3d, int places) {
      return new Vec3d(round(vec3d.field_72450_a, places), round(vec3d.field_72448_b, places), round(vec3d.field_72449_c, places));
   }

   public static double angleBetweenVecs(Vec3d vec3d, Vec3d other) {
      double angle = Math.atan2(vec3d.field_72450_a - other.field_72450_a, vec3d.field_72449_c - other.field_72449_c);
      angle = -(angle / 3.141592653589793D) * 360.0D / 2.0D + 180.0D;
      return angle;
   }

   public static double lengthSQ(Vec3d vec3d) {
      return square(vec3d.field_72450_a) + square(vec3d.field_72448_b) + square(vec3d.field_72449_c);
   }

   public static double length(Vec3d vec3d) {
      return Math.sqrt(lengthSQ(vec3d));
   }

   public static double dot(Vec3d vec3d, Vec3d other) {
      return vec3d.field_72450_a * other.field_72450_a + vec3d.field_72448_b * other.field_72448_b + vec3d.field_72449_c * other.field_72449_c;
   }

   public static double square(double input) {
      return input * input;
   }

   public static double square(float input) {
      return (double)(input * input);
   }

   public static double round(double value, int places) {
      if (places < 0) {
         throw new IllegalArgumentException();
      } else {
         BigDecimal bd = BigDecimal.valueOf(value);
         bd = bd.setScale(places, RoundingMode.FLOOR);
         return bd.doubleValue();
      }
   }

   public static float wrap(float valI) {
      float val = valI % 360.0F;
      if (val >= 180.0F) {
         val -= 360.0F;
      }

      if (val < -180.0F) {
         val += 360.0F;
      }

      return val;
   }

   public static Vec3d direction(float yaw) {
      return new Vec3d(Math.cos(degToRad((double)(yaw + 90.0F))), 0.0D, Math.sin(degToRad((double)(yaw + 90.0F))));
   }

   public static float round(float value, int places) {
      if (places < 0) {
         throw new IllegalArgumentException();
      } else {
         BigDecimal bd = BigDecimal.valueOf((double)value);
         bd = bd.setScale(places, RoundingMode.FLOOR);
         return bd.floatValue();
      }
   }

   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean descending) {
      List<Entry<K, V>> list = new LinkedList(map.entrySet());
      if (descending) {
         list.sort(Entry.comparingByValue(Comparator.reverseOrder()));
      } else {
         list.sort(Entry.comparingByValue());
      }

      Map<K, V> result = new LinkedHashMap();
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         Entry<K, V> entry = (Entry)var4.next();
         result.put(entry.getKey(), entry.getValue());
      }

      return result;
   }

   public static String getTimeOfDay() {
      Calendar c = Calendar.getInstance();
      int timeOfDay = c.get(11);
      if (timeOfDay < 12) {
         return "Good Morning ";
      } else if (timeOfDay < 16) {
         return "Good Afternoon ";
      } else {
         return timeOfDay < 21 ? "Good Evening " : "Good Night ";
      }
   }

   public static double radToDeg(double rad) {
      return rad * 57.295780181884766D;
   }

   public static double degToRad(double deg) {
      return deg * 0.01745329238474369D;
   }

   public static double getIncremental(double val, double inc) {
      double one = 1.0D / inc;
      return (double)Math.round(val * one) / one;
   }

   public static double[] directionSpeed(double speed) {
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

   public static List<Vec3d> getBlockBlocks(Entity entity) {
      List<Vec3d> vec3ds = new ArrayList();
      AxisAlignedBB bb = entity.func_174813_aQ();
      double y = entity.field_70163_u;
      double minX = round(bb.field_72340_a, 0);
      double minZ = round(bb.field_72339_c, 0);
      double maxX = round(bb.field_72336_d, 0);
      double maxZ = round(bb.field_72334_f, 0);
      Vec3d vec3d1;
      Vec3d vec3d2;
      BlockPos pos1;
      BlockPos pos2;
      if (minX != maxX) {
         vec3d1 = new Vec3d(minX, y, minZ);
         vec3d2 = new Vec3d(maxX, y, minZ);
         pos1 = new BlockPos(vec3d1);
         pos2 = new BlockPos(vec3d2);
         if (BlockUtil.isBlockUnSolid(pos1) && BlockUtil.isBlockUnSolid(pos2)) {
            vec3ds.add(vec3d1);
            vec3ds.add(vec3d2);
         }

         if (minZ != maxZ) {
            Vec3d vec3d3 = new Vec3d(minX, y, maxZ);
            Vec3d vec3d4 = new Vec3d(maxX, y, maxZ);
            BlockPos pos3 = new BlockPos(vec3d1);
            BlockPos pos4 = new BlockPos(vec3d2);
            if (BlockUtil.isBlockUnSolid(pos3) && BlockUtil.isBlockUnSolid(pos4)) {
               vec3ds.add(vec3d3);
               vec3ds.add(vec3d4);
               return vec3ds;
            }
         }

         if (vec3ds.isEmpty()) {
            vec3ds.add(entity.func_174791_d());
         }

         return vec3ds;
      } else if (minZ != maxZ) {
         vec3d1 = new Vec3d(minX, y, minZ);
         vec3d2 = new Vec3d(minX, y, maxZ);
         pos1 = new BlockPos(vec3d1);
         pos2 = new BlockPos(vec3d2);
         if (BlockUtil.isBlockUnSolid(pos1) && BlockUtil.isBlockUnSolid(pos2)) {
            vec3ds.add(vec3d1);
            vec3ds.add(vec3d2);
         }

         if (vec3ds.isEmpty()) {
            vec3ds.add(entity.func_174791_d());
         }

         return vec3ds;
      } else {
         vec3ds.add(entity.func_174791_d());
         return vec3ds;
      }
   }

   public static boolean areVec3dsAligned(Vec3d vec3d1, Vec3d vec3d2) {
      return areVec3dsAlignedRetarded(vec3d1, vec3d2);
   }

   public static boolean areVec3dsAlignedRetarded(Vec3d vec3d1, Vec3d vec3d2) {
      BlockPos pos1 = new BlockPos(vec3d1);
      BlockPos pos2 = new BlockPos(vec3d2.field_72450_a, vec3d1.field_72448_b, vec3d2.field_72449_c);
      return pos1.equals(pos2);
   }

   public static float[] calcAngle(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difY = (to.field_72448_b - from.field_72448_b) * -1.0D;
      double difZ = to.field_72449_c - from.field_72449_c;
      double dist = (double)MathHelper.func_76133_a(difX * difX + difZ * difZ);
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difY, dist)))};
   }

   public static float[] calcAngleNoY(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difZ = to.field_72449_c - from.field_72449_c;
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D)};
   }

   public static Vec3d calculateLine(Vec3d x1, Vec3d x2, double distance) {
      double length = Math.sqrt(multiply(x2.field_72450_a - x1.field_72450_a) + multiply(x2.field_72448_b - x1.field_72448_b) + multiply(x2.field_72449_c - x1.field_72449_c));
      double unitSlopeX = (x2.field_72450_a - x1.field_72450_a) / length;
      double unitSlopeY = (x2.field_72448_b - x1.field_72448_b) / length;
      double unitSlopeZ = (x2.field_72449_c - x1.field_72449_c) / length;
      double x = x1.field_72450_a + unitSlopeX * distance;
      double y = x1.field_72448_b + unitSlopeY * distance;
      double z = x1.field_72449_c + unitSlopeZ * distance;
      return new Vec3d(x, y, z);
   }

   public static double multiply(double one) {
      return one * one;
   }

   public static Vec3d extrapolatePlayerPosition(EntityPlayer player, int ticks) {
      Vec3d lastPos = new Vec3d(player.field_70142_S, player.field_70137_T, player.field_70136_U);
      Vec3d currentPos = new Vec3d(player.field_70165_t, player.field_70163_u, player.field_70161_v);
      double distance = multiply(player.field_70159_w) + multiply(player.field_70181_x) + multiply(player.field_70179_y);
      Vec3d tempVec = calculateLine(lastPos, currentPos, distance * (double)ticks);
      return new Vec3d(tempVec.field_72450_a, player.field_70163_u, tempVec.field_72449_c);
   }
}
