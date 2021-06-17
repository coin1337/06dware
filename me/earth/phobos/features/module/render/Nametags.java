package me.earth.phobos.features.modules.render;

import java.util.Iterator;
import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.RotationUtil;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class Nametags extends Module {
   private final Setting<Boolean> health = this.register(new Setting("Health", true));
   private final Setting<Boolean> armor = this.register(new Setting("Armor", true));
   private final Setting<Float> scaling = this.register(new Setting("Size", 0.3F, 0.1F, 20.0F));
   private final Setting<Boolean> invisibles = this.register(new Setting("Invisibles", false));
   private final Setting<Boolean> ping = this.register(new Setting("Ping", true));
   private final Setting<Boolean> totemPops = this.register(new Setting("TotemPops", true));
   private final Setting<Boolean> gamemode = this.register(new Setting("Gamemode", false));
   private final Setting<Boolean> entityID = this.register(new Setting("ID", false));
   private final Setting<Boolean> rect = this.register(new Setting("Rectangle", true));
   private final Setting<Boolean> sneak = this.register(new Setting("SneakColor", false));
   private final Setting<Boolean> heldStackName = this.register(new Setting("StackName", false));
   private final Setting<Boolean> whiter = this.register(new Setting("White", false));
   private final Setting<Boolean> onlyFov = this.register(new Setting("OnlyFov", false));
   private final Setting<Boolean> scaleing = this.register(new Setting("Scale", false));
   private final Setting<Float> factor = this.register(new Setting("Factor", 0.3F, 0.1F, 1.0F, (v) -> {
      return (Boolean)this.scaleing.getValue();
   }));
   private final Setting<Boolean> smartScale = this.register(new Setting("SmartScale", false, (v) -> {
      return (Boolean)this.scaleing.getValue();
   }));
   private static Nametags INSTANCE = new Nametags();

   public Nametags() {
      super("Nametags", "Better Nametags", Module.Category.RENDER, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static Nametags getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new Nametags();
      }

      return INSTANCE;
   }

   public void onRender3D(Render3DEvent event) {
      if (!fullNullCheck()) {
         Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

         while(true) {
            EntityPlayer player;
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var2.hasNext()) {
                              return;
                           }

                           player = (EntityPlayer)var2.next();
                        } while(player == null);
                     } while(player.equals(mc.field_71439_g));
                  } while(!player.func_70089_S());
               } while(player.func_82150_aj() && !(Boolean)this.invisibles.getValue());
            } while((Boolean)this.onlyFov.getValue() && !RotationUtil.isInFov((Entity)player));

            double x = this.interpolate(player.field_70142_S, player.field_70165_t, event.getPartialTicks()) - mc.func_175598_ae().field_78725_b;
            double y = this.interpolate(player.field_70137_T, player.field_70163_u, event.getPartialTicks()) - mc.func_175598_ae().field_78726_c;
            double z = this.interpolate(player.field_70136_U, player.field_70161_v, event.getPartialTicks()) - mc.func_175598_ae().field_78723_d;
            this.renderNameTag(player, x, y, z, event.getPartialTicks());
         }
      }
   }

   private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
      double tempY = y + (player.func_70093_af() ? 0.5D : 0.7D);
      Entity camera = mc.func_175606_aa();

      assert camera != null;

      double originalPositionX = camera.field_70165_t;
      double originalPositionY = camera.field_70163_u;
      double originalPositionZ = camera.field_70161_v;
      camera.field_70165_t = this.interpolate(camera.field_70169_q, camera.field_70165_t, delta);
      camera.field_70163_u = this.interpolate(camera.field_70167_r, camera.field_70163_u, delta);
      camera.field_70161_v = this.interpolate(camera.field_70166_s, camera.field_70161_v, delta);
      String displayTag = this.getDisplayTag(player);
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
      GlStateManager.func_179109_b((float)x, (float)tempY + 1.4F, (float)z);
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
      ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
      if (renderMainHand.func_77962_s() && (renderMainHand.func_77973_b() instanceof ItemTool || renderMainHand.func_77973_b() instanceof ItemArmor)) {
         renderMainHand.field_77994_a = 1;
      }

      if ((Boolean)this.heldStackName.getValue() && !renderMainHand.field_190928_g && renderMainHand.func_77973_b() != Items.field_190931_a) {
         String stackName = renderMainHand.func_82833_r();
         int stackNameWidth = this.renderer.getStringWidth(stackName) / 2;
         GL11.glPushMatrix();
         GL11.glScalef(0.75F, 0.75F, 0.0F);
         this.renderer.drawStringWithShadow(stackName, (float)(-stackNameWidth), -(this.getBiggestArmorTag(player) + 20.0F), -1);
         GL11.glScalef(1.5F, 1.5F, 1.0F);
         GL11.glPopMatrix();
      }

      if ((Boolean)this.armor.getValue()) {
         GlStateManager.func_179094_E();
         int xOffset = -8;
         Iterator var31 = player.field_71071_by.field_70460_b.iterator();

         while(var31.hasNext()) {
            ItemStack stack = (ItemStack)var31.next();
            if (stack != null) {
               xOffset -= 8;
            }
         }

         xOffset -= 8;
         ItemStack renderOffhand = player.func_184592_cb().func_77946_l();
         if (renderOffhand.func_77962_s() && (renderOffhand.func_77973_b() instanceof ItemTool || renderOffhand.func_77973_b() instanceof ItemArmor)) {
            renderOffhand.field_77994_a = 1;
         }

         this.renderItemStack(renderOffhand, xOffset, -26);
         xOffset += 16;
         Iterator var33 = player.field_71071_by.field_70460_b.iterator();

         label74:
         while(true) {
            ItemStack stack;
            do {
               if (!var33.hasNext()) {
                  this.renderItemStack(renderMainHand, xOffset, -26);
                  GlStateManager.func_179121_F();
                  break label74;
               }

               stack = (ItemStack)var33.next();
            } while(stack == null);

            ItemStack armourStack = stack.func_77946_l();
            if (armourStack.func_77962_s() && (armourStack.func_77973_b() instanceof ItemTool || armourStack.func_77973_b() instanceof ItemArmor)) {
               armourStack.field_77994_a = 1;
            }

            this.renderItemStack(armourStack, xOffset, -26);
            xOffset += 16;
         }
      }

      this.renderer.drawStringWithShadow(displayTag, (float)(-width), (float)(-(this.renderer.getFontHeight() - 1)), this.getDisplayColour(player));
      camera.field_70165_t = originalPositionX;
      camera.field_70163_u = originalPositionY;
      camera.field_70161_v = originalPositionZ;
      GlStateManager.func_179126_j();
      GlStateManager.func_179084_k();
      GlStateManager.func_179113_r();
      GlStateManager.func_179136_a(1.0F, 1500000.0F);
      GlStateManager.func_179121_F();
   }

   private void renderItemStack(ItemStack stack, int x, int y) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179086_m(256);
      RenderHelper.func_74519_b();
      mc.func_175599_af().field_77023_b = -150.0F;
      GlStateManager.func_179118_c();
      GlStateManager.func_179126_j();
      GlStateManager.func_179129_p();
      mc.func_175599_af().func_180450_b(stack, x, y);
      mc.func_175599_af().func_175030_a(mc.field_71466_p, stack, x, y);
      mc.func_175599_af().field_77023_b = 0.0F;
      RenderHelper.func_74518_a();
      GlStateManager.func_179089_o();
      GlStateManager.func_179141_d();
      GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
      GlStateManager.func_179097_i();
      this.renderEnchantmentText(stack, x, y);
      GlStateManager.func_179126_j();
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      GlStateManager.func_179121_F();
   }

   private void renderEnchantmentText(ItemStack stack, int x, int y) {
      int enchantmentY = y - 8;
      if (stack.func_77973_b() == Items.field_151153_ao && stack.func_77962_s()) {
         this.renderer.drawStringWithShadow("god", (float)(x * 2), (float)enchantmentY, -3977919);
         enchantmentY -= 8;
      }

      NBTTagList enchants = stack.func_77986_q();

      int percent;
      for(percent = 0; percent < enchants.func_74745_c(); ++percent) {
         short id = enchants.func_150305_b(percent).func_74765_d("id");
         short level = enchants.func_150305_b(percent).func_74765_d("lvl");
         Enchantment enc = Enchantment.func_185262_c(id);
         if (enc != null) {
            String encName = enc.func_190936_d() ? TextFormatting.RED + enc.func_77316_c(level).substring(11).substring(0, 1).toLowerCase() : enc.func_77316_c(level).substring(0, 1).toLowerCase();
            encName = encName + level;
            this.renderer.drawStringWithShadow(encName, (float)(x * 2), (float)enchantmentY, -1);
            enchantmentY -= 8;
         }
      }

      if (DamageUtil.hasDurability(stack)) {
         percent = DamageUtil.getRoundedDamage(stack);
         String color;
         if (percent >= 60) {
            color = "§a";
         } else if (percent >= 25) {
            color = "§e";
         } else {
            color = "§c";
         }

         this.renderer.drawStringWithShadow(color + percent + "%", (float)(x * 2), (float)enchantmentY, -1);
      }

   }

   private float getBiggestArmorTag(EntityPlayer player) {
      float enchantmentY = 0.0F;
      boolean arm = false;
      Iterator var4 = player.field_71071_by.field_70460_b.iterator();

      ItemStack renderOffHand;
      float encY;
      NBTTagList enchants;
      int index;
      short id;
      Enchantment enc;
      while(var4.hasNext()) {
         renderOffHand = (ItemStack)var4.next();
         encY = 0.0F;
         if (renderOffHand != null) {
            enchants = renderOffHand.func_77986_q();

            for(index = 0; index < enchants.func_74745_c(); ++index) {
               id = enchants.func_150305_b(index).func_74765_d("id");
               enc = Enchantment.func_185262_c(id);
               if (enc != null) {
                  encY += 8.0F;
                  arm = true;
               }
            }
         }

         if (encY > enchantmentY) {
            enchantmentY = encY;
         }
      }

      ItemStack renderMainHand = player.func_184614_ca().func_77946_l();
      if (renderMainHand.func_77962_s()) {
         float encY = 0.0F;
         NBTTagList enchants = renderMainHand.func_77986_q();

         for(int index = 0; index < enchants.func_74745_c(); ++index) {
            short id = enchants.func_150305_b(index).func_74765_d("id");
            Enchantment enc = Enchantment.func_185262_c(id);
            if (enc != null) {
               encY += 8.0F;
               arm = true;
            }
         }

         if (encY > enchantmentY) {
            enchantmentY = encY;
         }
      }

      renderOffHand = player.func_184592_cb().func_77946_l();
      if (renderOffHand.func_77962_s()) {
         encY = 0.0F;
         enchants = renderOffHand.func_77986_q();

         for(index = 0; index < enchants.func_74745_c(); ++index) {
            id = enchants.func_150305_b(index).func_74765_d("id");
            enc = Enchantment.func_185262_c(id);
            if (enc != null) {
               encY += 8.0F;
               arm = true;
            }
         }

         if (encY > enchantmentY) {
            enchantmentY = encY;
         }
      }

      return (float)(arm ? 0 : 20) + enchantmentY;
   }

   private String getDisplayTag(EntityPlayer player) {
      String name = player.func_145748_c_().func_150254_d();
      if (name.contains(mc.func_110432_I().func_111285_a())) {
         name = "You";
      }

      if (!(Boolean)this.health.getValue()) {
         return name;
      } else {
         float health = EntityUtil.getHealth(player);
         String color;
         if (health > 18.0F) {
            color = "§a";
         } else if (health > 16.0F) {
            color = "§2";
         } else if (health > 12.0F) {
            color = "§e";
         } else if (health > 8.0F) {
            color = "§6";
         } else if (health > 5.0F) {
            color = "§c";
         } else {
            color = "§4";
         }

         String pingStr = "";
         if ((Boolean)this.ping.getValue()) {
            try {
               int responseTime = ((NetHandlerPlayClient)Objects.requireNonNull(mc.func_147114_u())).func_175102_a(player.func_110124_au()).func_178853_c();
               pingStr = pingStr + responseTime + "ms ";
            } catch (Exception var9) {
            }
         }

         String popStr = " ";
         if ((Boolean)this.totemPops.getValue()) {
            popStr = popStr + Phobos.totemPopManager.getTotemPopString(player);
         }

         String idString = "";
         if ((Boolean)this.entityID.getValue()) {
            idString = idString + "ID: " + player.func_145782_y() + " ";
         }

         String gameModeStr = "";
         if ((Boolean)this.gamemode.getValue()) {
            if (player.func_184812_l_()) {
               gameModeStr = gameModeStr + "[C] ";
            } else if (!player.func_175149_v() && !player.func_82150_aj()) {
               gameModeStr = gameModeStr + "[S] ";
            } else {
               gameModeStr = gameModeStr + "[I] ";
            }
         }

         if (Math.floor((double)health) == (double)health) {
            name = name + color + " " + (health > 0.0F ? (int)Math.floor((double)health) : "dead");
         } else {
            name = name + color + " " + (health > 0.0F ? (int)health : "dead");
         }

         return pingStr + idString + gameModeStr + name + popStr;
      }
   }

   private int getDisplayColour(EntityPlayer player) {
      int colour = -5592406;
      if ((Boolean)this.whiter.getValue()) {
         colour = -1;
      }

      if (Phobos.friendManager.isFriend(player)) {
         return -11157267;
      } else {
         if (player.func_82150_aj()) {
            colour = -1113785;
         } else if (player.func_70093_af() && (Boolean)this.sneak.getValue()) {
            colour = -6481515;
         }

         return colour;
      }
   }

   private double interpolate(double previous, double current, float delta) {
      return previous + (current - previous) * (double)delta;
   }
}
