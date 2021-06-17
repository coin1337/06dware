package me.earth.phobos.features.modules.render;

import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class OffscreenESP extends Module {
   private final Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
   private final Setting<Boolean> invisibles = this.register(new Setting("Invisibles", false));
   private final Setting<Boolean> offscreenOnly = this.register(new Setting("Offscreen-Only", true));
   private final Setting<Boolean> outline = this.register(new Setting("Outline", true));
   private final Setting<Float> outlineWidth = this.register(new Setting("Outline-Width", 1.0F, 0.1F, 3.0F));
   private final Setting<Integer> fadeDistance = this.register(new Setting("Fade-Distance", 100, 10, 200));
   private final Setting<Integer> radius = this.register(new Setting("Radius", 45, 10, 200));
   private final Setting<Float> size = this.register(new Setting("Size", 10.0F, 5.0F, 25.0F));
   private final Setting<Integer> red = this.register(new Setting("Red", 255, 0, 255));
   private final Setting<Integer> green = this.register(new Setting("Green", 0, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
   private final OffscreenESP.EntityListener entityListener = new OffscreenESP.EntityListener();

   public OffscreenESP() {
      super("ArrowESP", "Shows the direction players are in with cool little triangles :3", Module.Category.RENDER, true, false, false);
   }

   public void onRender2D(Render2DEvent event) {
      this.entityListener.render();
      mc.field_71441_e.field_72996_f.forEach((o) -> {
         if (o instanceof EntityPlayer && this.isValid((EntityPlayer)o)) {
            EntityPlayer entity = (EntityPlayer)o;
            Vec3d pos = (Vec3d)this.entityListener.getEntityLowerBounds().get(entity);
            if (pos != null && !this.isOnScreen(pos) && (!RenderUtil.isInViewFrustrum((Entity)entity) || !(Boolean)this.offscreenOnly.getValue())) {
               Color color = (Boolean)this.colorSync.getValue() ? new Color(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), (int)MathHelper.func_76131_a(255.0F - 255.0F / (float)(Integer)this.fadeDistance.getValue() * mc.field_71439_g.func_70032_d(entity), 100.0F, 255.0F)) : EntityUtil.getColor(entity, (Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (int)MathHelper.func_76131_a(255.0F - 255.0F / (float)(Integer)this.fadeDistance.getValue() * mc.field_71439_g.func_70032_d(entity), 100.0F, 255.0F), true);
               int x = Display.getWidth() / 2 / (mc.field_71474_y.field_74335_Z == 0 ? 1 : mc.field_71474_y.field_74335_Z);
               int y = Display.getHeight() / 2 / (mc.field_71474_y.field_74335_Z == 0 ? 1 : mc.field_71474_y.field_74335_Z);
               float yaw = this.getRotations(entity) - mc.field_71439_g.field_70177_z;
               GL11.glTranslatef((float)x, (float)y, 0.0F);
               GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
               GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
               RenderUtil.drawTracerPointer((float)x, (float)(y - (Integer)this.radius.getValue()), (Float)this.size.getValue(), 2.0F, 1.0F, (Boolean)this.outline.getValue(), (Float)this.outlineWidth.getValue(), color.getRGB());
               GL11.glTranslatef((float)x, (float)y, 0.0F);
               GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
               GL11.glTranslatef((float)(-x), (float)(-y), 0.0F);
            }
         }

      });
   }

   private boolean isOnScreen(Vec3d pos) {
      if (pos.field_72450_a > -1.0D && pos.field_72448_b < 1.0D) {
         return pos.field_72450_a / (double)(mc.field_71474_y.field_74335_Z == 0 ? 1 : mc.field_71474_y.field_74335_Z) >= 0.0D && pos.field_72450_a / (double)(mc.field_71474_y.field_74335_Z == 0 ? 1 : mc.field_71474_y.field_74335_Z) <= (double)Display.getWidth() && pos.field_72448_b / (double)(mc.field_71474_y.field_74335_Z == 0 ? 1 : mc.field_71474_y.field_74335_Z) >= 0.0D && pos.field_72448_b / (double)(mc.field_71474_y.field_74335_Z == 0 ? 1 : mc.field_71474_y.field_74335_Z) <= (double)Display.getHeight();
      } else {
         return false;
      }
   }

   private boolean isValid(EntityPlayer entity) {
      return entity != mc.field_71439_g && (!entity.func_82150_aj() || (Boolean)this.invisibles.getValue()) && entity.func_70089_S();
   }

   private float getRotations(EntityLivingBase ent) {
      double x = ent.field_70165_t - mc.field_71439_g.field_70165_t;
      double z = ent.field_70161_v - mc.field_71439_g.field_70161_v;
      return (float)(-(Math.atan2(x, z) * 57.29577951308232D));
   }

   private static class EntityListener {
      private final Map<Entity, Vec3d> entityUpperBounds;
      private final Map<Entity, Vec3d> entityLowerBounds;

      private EntityListener() {
         this.entityUpperBounds = Maps.newHashMap();
         this.entityLowerBounds = Maps.newHashMap();
      }

      private void render() {
         if (!this.entityUpperBounds.isEmpty()) {
            this.entityUpperBounds.clear();
         }

         if (!this.entityLowerBounds.isEmpty()) {
            this.entityLowerBounds.clear();
         }

         Iterator var1 = Util.mc.field_71441_e.field_72996_f.iterator();

         while(var1.hasNext()) {
            Entity e = (Entity)var1.next();
            Vec3d bound = this.getEntityRenderPosition(e);
            bound.func_178787_e(new Vec3d(0.0D, (double)e.field_70131_O + 0.2D, 0.0D));
            Vec3d upperBounds = RenderUtil.to2D(bound.field_72450_a, bound.field_72448_b, bound.field_72449_c);
            Vec3d lowerBounds = RenderUtil.to2D(bound.field_72450_a, bound.field_72448_b - 2.0D, bound.field_72449_c);
            if (upperBounds != null && lowerBounds != null) {
               this.entityUpperBounds.put(e, upperBounds);
               this.entityLowerBounds.put(e, lowerBounds);
            }
         }

      }

      private Vec3d getEntityRenderPosition(Entity entity) {
         double partial = (double)Util.mc.field_71428_T.field_194147_b;
         double x = entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * partial - Util.mc.func_175598_ae().field_78730_l;
         double y = entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * partial - Util.mc.func_175598_ae().field_78731_m;
         double z = entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * partial - Util.mc.func_175598_ae().field_78728_n;
         return new Vec3d(x, y, z);
      }

      public Map<Entity, Vec3d> getEntityLowerBounds() {
         return this.entityLowerBounds;
      }

      // $FF: synthetic method
      EntityListener(Object x0) {
         this();
      }
   }
}
