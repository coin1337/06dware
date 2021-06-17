package me.earth.phobos.manager;

import me.earth.phobos.features.Feature;
import net.minecraft.network.play.client.CPacketPlayer.Position;

public class PositionManager extends Feature {
   private double x;
   private double y;
   private double z;
   private boolean onground;

   public void updatePosition() {
      this.x = mc.field_71439_g.field_70165_t;
      this.y = mc.field_71439_g.field_70163_u;
      this.z = mc.field_71439_g.field_70161_v;
      this.onground = mc.field_71439_g.field_70122_E;
   }

   public void restorePosition() {
      mc.field_71439_g.field_70165_t = this.x;
      mc.field_71439_g.field_70163_u = this.y;
      mc.field_71439_g.field_70161_v = this.z;
      mc.field_71439_g.field_70122_E = this.onground;
   }

   public void setPlayerPosition(double x, double y, double z) {
      mc.field_71439_g.field_70165_t = x;
      mc.field_71439_g.field_70163_u = y;
      mc.field_71439_g.field_70161_v = z;
   }

   public void setPlayerPosition(double x, double y, double z, boolean onground) {
      mc.field_71439_g.field_70165_t = x;
      mc.field_71439_g.field_70163_u = y;
      mc.field_71439_g.field_70161_v = z;
      mc.field_71439_g.field_70122_E = onground;
   }

   public void setPositionPacket(double x, double y, double z, boolean onGround, boolean setPos, boolean noLagBack) {
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(x, y, z, onGround));
      if (setPos) {
         mc.field_71439_g.func_70107_b(x, y, z);
         if (noLagBack) {
            this.updatePosition();
         }
      }

   }

   public double getX() {
      return this.x;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getY() {
      return this.y;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getZ() {
      return this.z;
   }

   public void setZ(double z) {
      this.z = z;
   }
}
