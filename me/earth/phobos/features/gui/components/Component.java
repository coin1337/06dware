package me.earth.phobos.features.gui.components;

import java.util.ArrayList;
import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.gui.components.items.Item;
import me.earth.phobos.features.gui.components.items.buttons.Button;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Component extends Feature {
   private int x;
   private int y;
   private int x2;
   private int y2;
   private int width;
   private int height;
   private boolean open;
   public boolean drag;
   private final ArrayList<Item> items = new ArrayList();
   private boolean hidden = false;

   public Component(String name, int x, int y, boolean open) {
      super(name);
      this.x = x;
      this.y = y;
      this.width = 88;
      this.height = 18;
      this.open = open;
      this.setupItems();
   }

   public void setupItems() {
   }

   private void drag(int mouseX, int mouseY) {
      if (this.drag) {
         this.x = this.x2 + mouseX;
         this.y = this.y2 + mouseY;
      }
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drag(mouseX, mouseY);
      float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0F : 0.0F;
      int color = -7829368;
      if ((Boolean)ClickGui.getInstance().devSettings.getValue()) {
         color = (Boolean)ClickGui.getInstance().colorSync.getValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toARGB((Integer)ClickGui.getInstance().topRed.getValue(), (Integer)ClickGui.getInstance().topGreen.getValue(), (Integer)ClickGui.getInstance().topBlue.getValue(), (Integer)ClickGui.getInstance().topAlpha.getValue());
      }

      if ((Boolean)ClickGui.getInstance().rainbowRolling.getValue() && (Boolean)ClickGui.getInstance().colorSync.getValue() && (Boolean)Colors.INSTANCE.rainbow.getValue()) {
         RenderUtil.drawGradientRect((float)this.x, (float)this.y - 1.5F, (float)this.width, (float)(this.height - 4), (Integer)HUD.getInstance().colorMap.get(MathUtil.clamp(this.y, 0, this.renderer.scaledHeight)), (Integer)HUD.getInstance().colorMap.get(MathUtil.clamp(this.y + this.height - 4, 0, this.renderer.scaledHeight)));
      } else {
         RenderUtil.drawRect((float)this.x, (float)this.y - 1.5F, (float)(this.x + this.width), (float)(this.y + this.height - 6), color);
      }

      if (this.open) {
         RenderUtil.drawRect((float)this.x, (float)this.y + 12.5F, (float)(this.x + this.width), (float)(this.y + this.height) + totalItemHeight, 1996488704);
      }

      Phobos.textManager.drawStringWithShadow(this.getName(), (float)this.x + 3.0F, (float)this.y - 4.0F - (float)PhobosGui.getClickGui().getTextOffset(), -1);
      if (this.open) {
         float y = (float)(this.getY() + this.getHeight()) - 3.0F;
         Iterator var7 = this.getItems().iterator();

         while(var7.hasNext()) {
            Item item = (Item)var7.next();
            if (!item.isHidden()) {
               item.setLocation((float)this.x + 2.0F, y);
               item.setWidth(this.getWidth() - 4);
               item.drawScreen(mouseX, mouseY, partialTicks);
               y += (float)item.getHeight() + 1.5F;
            }
         }
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
         this.x2 = this.x - mouseX;
         this.y2 = this.y - mouseY;
         PhobosGui.getClickGui().getComponents().forEach((component) -> {
            if (component.drag) {
               component.drag = false;
            }

         });
         this.drag = true;
      } else if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
         this.open = !this.open;
         mc.func_147118_V().func_147682_a(PositionedSoundRecord.func_184371_a(SoundEvents.field_187909_gi, 1.0F));
      } else if (this.open) {
         this.getItems().forEach((item) -> {
            item.mouseClicked(mouseX, mouseY, mouseButton);
         });
      }
   }

   public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
      if (releaseButton == 0) {
         this.drag = false;
      }

      if (this.open) {
         this.getItems().forEach((item) -> {
            item.mouseReleased(mouseX, mouseY, releaseButton);
         });
      }
   }

   public void onKeyTyped(char typedChar, int keyCode) {
      if (this.open) {
         this.getItems().forEach((item) -> {
            item.onKeyTyped(typedChar, keyCode);
         });
      }
   }

   public void addButton(Button button) {
      this.items.add(button);
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public boolean isOpen() {
      return this.open;
   }

   public final ArrayList<Item> getItems() {
      return this.items;
   }

   private boolean isHovering(int mouseX, int mouseY) {
      return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
   }

   private float getTotalItemHeight() {
      float height = 0.0F;

      Item item;
      for(Iterator var2 = this.getItems().iterator(); var2.hasNext(); height += (float)item.getHeight() + 1.5F) {
         item = (Item)var2.next();
      }

      return height;
   }
}
