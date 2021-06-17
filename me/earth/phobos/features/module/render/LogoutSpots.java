package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import me.earth.phobos.event.events.ConnectionEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LogoutSpots extends Module {
   public Setting<Float> range = this.register(new Setting("Range", 300.0F, 50.0F, 500.0F));
   private final Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
   private final Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
   private final Setting<Integer> green = this.register(new Setting("Green", 0, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255));
   private final Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
   private final Setting<Boolean> scaleing = this.register(new Setting("Scale", false));
   private final Setting<Float> scaling = this.register(new Setting("Size", 4.0F, 0.1F, 20.0F));
   private final Setting<Float> factor = this.register(new Setting("Factor", 0.3F, 0.1F, 1.0F, (v) -> {
      return (Boolean)this.scaleing.getValue();
   }));
   private final Setting<Boolean> smartScale = this.register(new Setting("SmartScale", false, (v) -> {
      return (Boolean)this.scaleing.getValue();
   }));
   private final Setting<Boolean> rect = this.register(new Setting("Rectangle", true));
   private final Setting<Boolean> coords = this.register(new Setting("Coords", true));
   private final Setting<Boolean> notification = this.register(new Setting("Notification", true));
   public Setting<Boolean> message = this.register(new Setting("Message", false));
   private final List<LogoutSpots.LogoutPos> spots = new CopyOnWriteArrayList();

   public LogoutSpots() {
      super("LogoutSpots", "Renders LogoutSpots", Module.Category.RENDER, true, false, false);
   }

   public void onLogout() {
      this.spots.clear();
   }

   public void onDisable() {
      this.spots.clear();
   }

   public void onRender3D(Render3DEvent event) {
      if (!this.spots.isEmpty()) {
         synchronized(this.spots) {
            this.spots.forEach((spot) -> {
               if (spot.getEntity() != null) {
                  AxisAlignedBB bb = RenderUtil.interpolateAxis(spot.getEntity().func_174813_aQ());
                  RenderUtil.drawBlockOutline(bb, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), 1.0F);
                  double x = this.interpolate(spot.getEntity().field_70142_S, spot.getEntity().field_70165_t, event.getPartialTicks()) - mc.func_175598_ae().field_78725_b;
                  double y = this.interpolate(spot.getEntity().field_70137_T, spot.getEntity().field_70163_u, event.getPartialTicks()) - mc.func_175598_ae().field_78726_c;
                  double z = this.interpolate(spot.getEntity().field_70136_U, spot.getEntity().field_70161_v, event.getPartialTicks()) - mc.func_175598_ae().field_78723_d;
                  this.renderNameTag(spot.getName(), x, y, z, event.getPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
               }

            });
         }
      }

   }

   public void onUpdate() {
      if (!fullNullCheck()) {
         this.spots.removeIf((spot) -> {
            return mc.field_71439_g.func_70068_e(spot.getEntity()) >= MathUtil.square((Float)this.range.getValue());
         });
      }

   }

   @SubscribeEvent
   public void onConnection(ConnectionEvent event) {
      if (event.getStage() == 0) {
         UUID uuid = event.getUuid();
         EntityPlayer entity = mc.field_71441_e.func_152378_a(uuid);
         if (entity != null && (Boolean)this.message.getValue()) {
            Command.sendMessage("§a" + entity.func_70005_c_() + " just logged in" + ((Boolean)this.coords.getValue() ? " at (" + (int)entity.field_70165_t + ", " + (int)entity.field_70163_u + ", " + (int)entity.field_70161_v + ")!" : "!"), (Boolean)this.notification.getValue());
         }

         this.spots.removeIf((pos) -> {
            return pos.getName().equalsIgnoreCase(event.getName());
         });
      } else if (event.getStage() == 1) {
         EntityPlayer entity = event.getEntity();
         UUID uuid = event.getUuid();
         String name = event.getName();
         if ((Boolean)this.message.getValue()) {
            Command.sendMessage("§c" + event.getName() + " just logged out" + ((Boolean)this.coords.getValue() ? " at (" + (int)entity.field_70165_t + ", " + (int)entity.field_70163_u + ", " + (int)entity.field_70161_v + ")!" : "!"), (Boolean)this.notification.getValue());
         }

         if (name != null && entity != null && uuid != null) {
            this.spots.add(new LogoutSpots.LogoutPos(name, uuid, entity));
         }
      }

   }

   private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
      double y = yi + 0.7D;
      Entity camera = mc.func_175606_aa();

      assert camera != null;

      double originalPositionX = camera.field_70165_t;
      double originalPositionY = camera.field_70163_u;
      double originalPositionZ = camera.field_70161_v;
      camera.field_70165_t = this.interpolate(camera.field_70169_q, camera.field_70165_t, delta);
      camera.field_70163_u = this.interpolate(camera.field_70167_r, camera.field_70163_u, delta);
      camera.field_70161_v = this.interpolate(camera.field_70166_s, camera.field_70161_v, delta);
      String displayTag = name + " XYZ: " + (int)xPos + ", " + (int)yPos + ", " + (int)zPos;
      double distance = camera.func_70011_f(x + mc.func_175598_ae().field_78730_l, y + mc.func_175598_ae().field_78731_m, z + mc.func_175598_ae().field_78728_n);
      int width = this.renderer.getStringWidth(displayTag) / 2;
      double scale = (0.0018D + (double)(Float)this.scaling.getValue() * distance * (double)(Float)this.factor.getValue()) / 1000.0D;
      if (distance <= 8.0D && (Boolean)this.smartScale.getValue()) {
         scale = 0.0245D;
      }

      if (!(Boolean)this.scaleing.getValue()) {
         scale = (double)(Float)this.scaling.getValue() / 100.0D;
      }

      GlStateManager.func_179094_E();
      RenderHelper.func_74519_b();
      GlStateManager.func_179088_q();
      GlStateManager.func_179136_a(1.0F, -1500000.0F);
      GlStateManager.func_179140_f();
      GlStateManager.func_179109_b((float)x, (float)y + 1.4F, (float)z);
      GlStateManager.func_179114_b(-mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
      float var10001 = mc.field_71474_y.field_74320_O == 2 ? -1.0F : 1.0F;
      GlStateManager.func_179114_b(mc.func_175598_ae().field_78732_j, var10001, 0.0F, 0.0F);
      GlStateManager.func_179139_a(-scale, -scale, scale);
      GlStateManager.func_179097_i();
      GlStateManager.func_179147_l();
      GlStateManager.func_179147_l();
      if ((Boolean)this.rect.getValue()) {
         RenderUtil.drawRect((float)(-width - 2), (float)(-(this.renderer.getFontHeight() + 1)), (float)width + 2.0F, 1.5F, 1426063360);
      }

      GlStateManager.func_179084_k();
      this.renderer.drawStringWithShadow(displayTag, (float)(-width), (float)(-(this.renderer.getFontHeight() - 1)), (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue())));
      camera.field_70165_t = originalPositionX;
      camera.field_70163_u = originalPositionY;
      camera.field_70161_v = originalPositionZ;
      GlStateManager.func_179126_j();
      GlStateManager.func_179084_k();
      GlStateManager.func_179113_r();
      GlStateManager.func_179136_a(1.0F, 1500000.0F);
      GlStateManager.func_179121_F();
   }

   private double interpolate(double previous, double current, float delta) {
      return previous + (current - previous) * (double)delta;
   }

   private static class LogoutPos {
      private final String name;
      private final UUID uuid;
      private final EntityPlayer entity;
      private final double x;
      private final double y;
      private final double z;

      public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
         this.name = name;
         this.uuid = uuid;
         this.entity = entity;
         this.x = entity.field_70165_t;
         this.y = entity.field_70163_u;
         this.z = entity.field_70161_v;
      }

      public String getName() {
         return this.name;
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public EntityPlayer getEntity() {
         return this.entity;
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public double getZ() {
         return this.z;
      }
   }
}
