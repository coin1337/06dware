package me.earth.phobos.features.modules.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Trails extends Module {
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.5F, 0.1F, 5.0F));
   private final Setting<Integer> red = this.register(new Setting("Red", 0, 0, 255));
   private final Setting<Integer> green = this.register(new Setting("Green", 255, 0, 255));
   private final Setting<Integer> blue = this.register(new Setting("Blue", 0, 0, 255));
   private final Setting<Integer> alpha = this.register(new Setting("Alpha", 255, 0, 255));
   private Map<Entity, List<Vec3d>> renderMap = new HashMap();

   public Trails() {
      super("Trails", "Draws trails on projectiles", Module.Category.RENDER, true, false, false);
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      while(true) {
         Entity entity;
         do {
            if (!var2.hasNext()) {
               return;
            }

            entity = (Entity)var2.next();
         } while(!(entity instanceof EntityThrowable) && !(entity instanceof EntityArrow));

         Object vectors;
         if (this.renderMap.get(entity) != null) {
            vectors = (List)this.renderMap.get(entity);
         } else {
            vectors = new ArrayList();
         }

         ((List)vectors).add(new Vec3d(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v));
         this.renderMap.put(entity, vectors);
      }
   }

   public void onRender3D(Render3DEvent event) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      while(true) {
         Entity entity;
         do {
            if (!var2.hasNext()) {
               return;
            }

            entity = (Entity)var2.next();
         } while(!this.renderMap.containsKey(entity));

         GlStateManager.func_179094_E();
         RenderUtil.GLPre((Float)this.lineWidth.getValue());
         GlStateManager.func_179147_l();
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179097_i();
         GlStateManager.func_187428_a(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GL11.glColor4f((float)(Integer)this.red.getValue() / 255.0F, (float)(Integer)this.green.getValue() / 255.0F, (float)(Integer)this.blue.getValue() / 255.0F, (float)(Integer)this.alpha.getValue() / 255.0F);
         GL11.glLineWidth((Float)this.lineWidth.getValue());
         GL11.glBegin(1);

         for(int i = 0; i < ((List)this.renderMap.get(entity)).size() - 1; ++i) {
            GL11.glVertex3d(((Vec3d)((List)this.renderMap.get(entity)).get(i)).field_72450_a, ((Vec3d)((List)this.renderMap.get(entity)).get(i)).field_72448_b, ((Vec3d)((List)this.renderMap.get(entity)).get(i)).field_72449_c);
            GL11.glVertex3d(((Vec3d)((List)this.renderMap.get(entity)).get(i + 1)).field_72450_a, ((Vec3d)((List)this.renderMap.get(entity)).get(i + 1)).field_72448_b, ((Vec3d)((List)this.renderMap.get(entity)).get(i + 1)).field_72449_c);
         }

         GL11.glEnd();
         GlStateManager.func_179117_G();
         GlStateManager.func_179126_j();
         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
         RenderUtil.GlPost();
         GlStateManager.func_179121_F();
      }
   }
}
