package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.math.BlockPos;

public class StorageESP extends Module {
   private final Setting<Float> range = this.register(new Setting("Range", 50.0F, 1.0F, 300.0F));
   private final Setting<Boolean> colorSync = this.register(new Setting("Sync", false));
   private final Setting<Boolean> chest = this.register(new Setting("Chest", true));
   private final Setting<Boolean> dispenser = this.register(new Setting("Dispenser", false));
   private final Setting<Boolean> shulker = this.register(new Setting("Shulker", true));
   private final Setting<Boolean> echest = this.register(new Setting("Ender Chest", true));
   private final Setting<Boolean> furnace = this.register(new Setting("Furnace", false));
   private final Setting<Boolean> hopper = this.register(new Setting("Hopper", false));
   private final Setting<Boolean> cart = this.register(new Setting("Minecart", false));
   private final Setting<Boolean> frame = this.register(new Setting("Item Frame", false));
   private final Setting<Boolean> box = this.register(new Setting("Box", false));
   private final Setting<Integer> boxAlpha = this.register(new Setting("BoxAlpha", 125, 0, 255, (v) -> {
      return (Boolean)this.box.getValue();
   }));
   private final Setting<Boolean> outline = this.register(new Setting("Outline", true));
   private final Setting<Float> lineWidth = this.register(new Setting("LineWidth", 1.0F, 0.1F, 5.0F, (v) -> {
      return (Boolean)this.outline.getValue();
   }));

   public StorageESP() {
      super("StorageESP", "Highlights Containers.", Module.Category.RENDER, false, false, false);
   }

   public void onRender3D(Render3DEvent event) {
      Map<BlockPos, Integer> positions = new HashMap();
      Iterator var3 = mc.field_71441_e.field_147482_g.iterator();

      while(true) {
         TileEntity tileEntity;
         BlockPos pos;
         int color;
         do {
            if (!var3.hasNext()) {
               var3 = mc.field_71441_e.field_72996_f.iterator();

               while(true) {
                  Entity entity;
                  do {
                     if (!var3.hasNext()) {
                        var3 = positions.entrySet().iterator();

                        while(var3.hasNext()) {
                           Entry<BlockPos, Integer> entry = (Entry)var3.next();
                           pos = (BlockPos)entry.getKey();
                           color = (Integer)entry.getValue();
                           RenderUtil.drawBoxESP(pos, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color(color), false, new Color(color), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
                        }

                        return;
                     }

                     entity = (Entity)var3.next();
                  } while((!(entity instanceof EntityItemFrame) || !(Boolean)this.frame.getValue()) && (!(entity instanceof EntityMinecartChest) || !(Boolean)this.cart.getValue()));

                  pos = entity.func_180425_c();
                  if (mc.field_71439_g.func_174818_b(pos) <= MathUtil.square((Float)this.range.getValue())) {
                     color = this.getEntityColor(entity);
                     if (color != -1) {
                        positions.put(pos, color);
                     }
                  }
               }
            }

            tileEntity = (TileEntity)var3.next();
         } while((!(tileEntity instanceof TileEntityChest) || !(Boolean)this.chest.getValue()) && (!(tileEntity instanceof TileEntityDispenser) || !(Boolean)this.dispenser.getValue()) && (!(tileEntity instanceof TileEntityShulkerBox) || !(Boolean)this.shulker.getValue()) && (!(tileEntity instanceof TileEntityEnderChest) || !(Boolean)this.echest.getValue()) && (!(tileEntity instanceof TileEntityFurnace) || !(Boolean)this.furnace.getValue()) && (!(tileEntity instanceof TileEntityHopper) || !(Boolean)this.hopper.getValue()));

         pos = tileEntity.func_174877_v();
         if (mc.field_71439_g.func_174818_b(pos) <= MathUtil.square((Float)this.range.getValue())) {
            color = this.getTileEntityColor(tileEntity);
            if (color != -1) {
               positions.put(pos, color);
            }
         }
      }
   }

   private int getTileEntityColor(TileEntity tileEntity) {
      if (tileEntity instanceof TileEntityChest) {
         return ColorUtil.Colors.BLUE;
      } else if (tileEntity instanceof TileEntityShulkerBox) {
         return ColorUtil.Colors.RED;
      } else if (tileEntity instanceof TileEntityEnderChest) {
         return ColorUtil.Colors.PURPLE;
      } else if (tileEntity instanceof TileEntityFurnace) {
         return ColorUtil.Colors.GRAY;
      } else if (tileEntity instanceof TileEntityHopper) {
         return ColorUtil.Colors.DARK_RED;
      } else {
         return tileEntity instanceof TileEntityDispenser ? ColorUtil.Colors.ORANGE : -1;
      }
   }

   private int getEntityColor(Entity entity) {
      if (entity instanceof EntityMinecartChest) {
         return ColorUtil.Colors.ORANGE;
      } else if (entity instanceof EntityItemFrame && ((EntityItemFrame)entity).func_82335_i().func_77973_b() instanceof ItemShulkerBox) {
         return ColorUtil.Colors.YELLOW;
      } else {
         return entity instanceof EntityItemFrame && !(((EntityItemFrame)entity).func_82335_i().func_77973_b() instanceof ItemShulkerBox) ? ColorUtil.Colors.ORANGE : -1;
      }
   }
}
