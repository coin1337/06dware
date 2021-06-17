package me.earth.phobos.features.modules.misc;

import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Godmode extends Module {
   public Minecraft mc = Minecraft.func_71410_x();
   public Entity entity;
   private final Setting<Boolean> remount = this.register(new Setting("Remount", false));

   public Godmode() {
      super("Godmode", "Hi there :D", Module.Category.MISC, true, false, false);
   }

   public void onEnable() {
      super.onEnable();
      if (this.mc.field_71441_e != null && this.mc.field_71439_g.func_184187_bx() != null) {
         this.entity = this.mc.field_71439_g.func_184187_bx();
         this.mc.field_71438_f.func_72712_a();
         this.hideEntity();
         this.mc.field_71439_g.func_70107_b((double)Minecraft.func_71410_x().field_71439_g.func_180425_c().func_177958_n(), (double)(Minecraft.func_71410_x().field_71439_g.func_180425_c().func_177956_o() - 1), (double)Minecraft.func_71410_x().field_71439_g.func_180425_c().func_177952_p());
      }

      if (this.mc.field_71441_e != null && (Boolean)this.remount.getValue()) {
         this.remount.setValue(false);
      }

   }

   public void onDisable() {
      super.onDisable();
      if ((Boolean)this.remount.getValue()) {
         this.remount.setValue(false);
      }

      this.mc.field_71439_g.func_184210_p();
      this.mc.func_147114_u().func_147297_a(new CPacketEntityAction(this.mc.field_71439_g, Action.START_SNEAKING));
      this.mc.func_147114_u().func_147297_a(new CPacketEntityAction(this.mc.field_71439_g, Action.STOP_SNEAKING));
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getPacket() instanceof Position || event.getPacket() instanceof PositionRotation) {
         event.setCanceled(true);
      }

   }

   private void hideEntity() {
      if (this.mc.field_71439_g.func_184187_bx() != null) {
         this.mc.field_71439_g.func_184210_p();
         this.mc.field_71441_e.func_72900_e(this.entity);
      }

   }

   private void showEntity(Entity entity2) {
      entity2.field_70128_L = false;
      this.mc.field_71441_e.field_72996_f.add(entity2);
      this.mc.field_71439_g.func_184205_a(entity2, true);
   }

   @SubscribeEvent
   public void onPlayerWalkingUpdate(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0) {
         if ((Boolean)this.remount.getValue() && ((Godmode)Objects.requireNonNull(Phobos.moduleManager.getModuleByClass(Godmode.class))).isEnabled()) {
            this.showEntity(this.entity);
         }

         this.entity.func_70080_a(Minecraft.func_71410_x().field_71439_g.field_70165_t, Minecraft.func_71410_x().field_71439_g.field_70163_u, Minecraft.func_71410_x().field_71439_g.field_70161_v, Minecraft.func_71410_x().field_71439_g.field_70177_z, Minecraft.func_71410_x().field_71439_g.field_70125_A);
         this.mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(this.mc.field_71439_g.field_70177_z, this.mc.field_71439_g.field_70125_A, true));
         this.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketInput(this.mc.field_71439_g.field_71158_b.field_192832_b, this.mc.field_71439_g.field_71158_b.field_78902_a, false, false));
         this.mc.field_71439_g.field_71174_a.func_147297_a(new CPacketVehicleMove(this.entity));
      }

   }
}
