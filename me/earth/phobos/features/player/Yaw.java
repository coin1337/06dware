package me.earth.phobos.features.modules.player;

import java.util.Objects;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Yaw extends Module {
   public Setting<Boolean> lockYaw = this.register(new Setting("LockYaw", false));
   public Setting<Boolean> byDirection = this.register(new Setting("ByDirection", false));
   public Setting<Yaw.Direction> direction;
   public Setting<Integer> yaw;
   public Setting<Boolean> lockPitch;
   public Setting<Integer> pitch;

   public Yaw() {
      super("Yaw", "Locks your yaw", Module.Category.PLAYER, true, false, false);
      this.direction = this.register(new Setting("Direction", Yaw.Direction.NORTH, (v) -> {
         return (Boolean)this.byDirection.getValue();
      }));
      this.yaw = this.register(new Setting("Yaw", 0, -180, 180, (v) -> {
         return !(Boolean)this.byDirection.getValue();
      }));
      this.lockPitch = this.register(new Setting("LockPitch", false));
      this.pitch = this.register(new Setting("Pitch", 0, -180, 180));
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if ((Boolean)this.lockYaw.getValue()) {
         if ((Boolean)this.byDirection.getValue()) {
            switch((Yaw.Direction)this.direction.getValue()) {
            case NORTH:
               this.setYaw(180);
               break;
            case NE:
               this.setYaw(225);
               break;
            case EAST:
               this.setYaw(270);
               break;
            case SE:
               this.setYaw(315);
               break;
            case SOUTH:
               this.setYaw(0);
               break;
            case SW:
               this.setYaw(45);
               break;
            case WEST:
               this.setYaw(90);
               break;
            case NW:
               this.setYaw(135);
            }
         } else {
            this.setYaw((Integer)this.yaw.getValue());
         }
      }

      if ((Boolean)this.lockPitch.getValue()) {
         if (mc.field_71439_g.func_184218_aH()) {
            ((Entity)Objects.requireNonNull(mc.field_71439_g.func_184187_bx())).field_70125_A = (float)(Integer)this.pitch.getValue();
         }

         mc.field_71439_g.field_70125_A = (float)(Integer)this.pitch.getValue();
      }

   }

   private void setYaw(int yaw) {
      if (mc.field_71439_g.func_184218_aH()) {
         ((Entity)Objects.requireNonNull(mc.field_71439_g.func_184187_bx())).field_70177_z = (float)yaw;
      }

      mc.field_71439_g.field_70177_z = (float)yaw;
   }

   public static enum Direction {
      NORTH,
      NE,
      EAST,
      SE,
      SOUTH,
      SW,
      WEST,
      NW;
   }
}
