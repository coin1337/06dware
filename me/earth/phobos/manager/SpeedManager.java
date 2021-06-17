package me.earth.phobos.manager;

import java.util.HashMap;
import java.util.Iterator;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.Managers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class SpeedManager extends Feature {
   public double firstJumpSpeed = 0.0D;
   public double lastJumpSpeed = 0.0D;
   public double percentJumpSpeedChanged = 0.0D;
   public double jumpSpeedChanged = 0.0D;
   public static boolean didJumpThisTick = false;
   public static boolean isJumping = false;
   public boolean didJumpLastTick = false;
   public long jumpInfoStartTime = 0L;
   public boolean wasFirstJump = true;
   public static final double LAST_JUMP_INFO_DURATION_DEFAULT = 3.0D;
   public double speedometerCurrentSpeed = 0.0D;
   public HashMap<EntityPlayer, Double> playerSpeeds = new HashMap();
   private int distancer = 20;

   public static void setDidJumpThisTick(boolean val) {
      didJumpThisTick = val;
   }

   public static void setIsJumping(boolean val) {
      isJumping = val;
   }

   public float lastJumpInfoTimeRemaining() {
      return (float)(Minecraft.func_71386_F() - this.jumpInfoStartTime) / 1000.0F;
   }

   public void updateValues() {
      double distTraveledLastTickX = mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q;
      double distTraveledLastTickZ = mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s;
      this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
      if (didJumpThisTick && (!mc.field_71439_g.field_70122_E || isJumping)) {
         if (didJumpThisTick && !this.didJumpLastTick) {
            this.wasFirstJump = this.lastJumpSpeed == 0.0D;
            this.percentJumpSpeedChanged = this.speedometerCurrentSpeed != 0.0D ? this.speedometerCurrentSpeed / this.lastJumpSpeed - 1.0D : -1.0D;
            this.jumpSpeedChanged = this.speedometerCurrentSpeed - this.lastJumpSpeed;
            this.jumpInfoStartTime = Minecraft.func_71386_F();
            this.lastJumpSpeed = this.speedometerCurrentSpeed;
            this.firstJumpSpeed = this.wasFirstJump ? this.lastJumpSpeed : 0.0D;
         }

         this.didJumpLastTick = didJumpThisTick;
      } else {
         this.didJumpLastTick = false;
         this.lastJumpSpeed = 0.0D;
      }

      if ((Boolean)Managers.getInstance().speed.getValue()) {
         this.updatePlayers();
      }

   }

   public void updatePlayers() {
      Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

      while(var1.hasNext()) {
         EntityPlayer player = (EntityPlayer)var1.next();
         if (mc.field_71439_g.func_70068_e(player) < (double)(this.distancer * this.distancer)) {
            double distTraveledLastTickX = player.field_70165_t - player.field_70169_q;
            double distTraveledLastTickZ = player.field_70161_v - player.field_70166_s;
            double playerSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
            this.playerSpeeds.put(player, playerSpeed);
         }
      }

   }

   public double getPlayerSpeed(EntityPlayer player) {
      return this.playerSpeeds.get(player) == null ? 0.0D : this.turnIntoKpH((Double)this.playerSpeeds.get(player));
   }

   public double turnIntoKpH(double input) {
      return (double)MathHelper.func_76133_a(input) * 71.2729367892D;
   }

   public double getSpeedKpH() {
      double speedometerkphdouble = this.turnIntoKpH(this.speedometerCurrentSpeed);
      speedometerkphdouble = (double)Math.round(10.0D * speedometerkphdouble) / 10.0D;
      return speedometerkphdouble;
   }

   public double getSpeedMpS() {
      double speedometerMpsdouble = this.turnIntoKpH(this.speedometerCurrentSpeed) / 3.6D;
      speedometerMpsdouble = (double)Math.round(10.0D * speedometerMpsdouble) / 10.0D;
      return speedometerMpsdouble;
   }
}
