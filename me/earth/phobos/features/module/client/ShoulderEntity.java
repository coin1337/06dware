package me.earth.phobos.features.modules.client;

import me.earth.phobos.features.modules.Module;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ShoulderEntity extends Module {
   private static final ResourceLocation BLACK_OCELOT_TEXTURES = new ResourceLocation("textures/entity/cat/black.png");

   public ShoulderEntity() {
      super("ShoulderEntity", "Test", Module.Category.CLIENT, true, false, false);
   }

   public void onEnable() {
      mc.field_71441_e.func_73027_a(-101, new EntityOcelot(mc.field_71441_e));
      NBTTagCompound tag = new NBTTagCompound();
      tag.func_74782_a("id", new NBTTagInt(-101));
      mc.field_71439_g.func_192027_g(tag);
   }

   public void onDisable() {
      mc.field_71441_e.func_73028_b(-101);
   }

   @SubscribeEvent
   public void onRenderPlayer(Post event) {
   }

   public float interpolate(float yaw1, float yaw2, float percent) {
      float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0F;
      if (rotation < 0.0F) {
         rotation += 360.0F;
      }

      return rotation;
   }
}
