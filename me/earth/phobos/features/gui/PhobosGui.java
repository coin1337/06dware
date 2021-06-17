package me.earth.phobos.features.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.components.Component;
import me.earth.phobos.features.gui.components.items.Item;
import me.earth.phobos.features.gui.components.items.buttons.ModuleButton;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class PhobosGui extends GuiScreen {
   private static PhobosGui phobosGui;
   private final ArrayList<Component> components = new ArrayList();
   private static PhobosGui INSTANCE = new PhobosGui();

   public PhobosGui() {
      this.setInstance();
      this.load();
   }

   public static PhobosGui getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new PhobosGui();
      }

      return INSTANCE;
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static PhobosGui getClickGui() {
      return getInstance();
   }

   private void load() {
      int x = -84;
      Iterator var2 = Phobos.moduleManager.getCategories().iterator();

      while(var2.hasNext()) {
         final Module.Category category = (Module.Category)var2.next();
         ArrayList var10000 = this.components;
         String var10004 = category.getName();
         x += 90;
         var10000.add(new Component(var10004, x, 4, true) {
            public void setupItems() {
               Phobos.moduleManager.getModulesByCategory(category).forEach((module) -> {
                  if (!module.hidden) {
                     this.addButton(new ModuleButton(module));
                  }

               });
            }
         });
      }

      this.components.forEach((components) -> {
         components.getItems().sort((item1, item2) -> {
            return item1.getName().compareTo(item2.getName());
         });
      });
   }

   public void updateModule(Module module) {
      Iterator var2 = this.components.iterator();

      while(true) {
         while(var2.hasNext()) {
            Component component = (Component)var2.next();
            Iterator var4 = component.getItems().iterator();

            while(var4.hasNext()) {
               Item item = (Item)var4.next();
               if (item instanceof ModuleButton) {
                  ModuleButton button = (ModuleButton)item;
                  Module mod = button.getModule();
                  if (module != null && module.equals(mod)) {
                     button.initSettings();
                     break;
                  }
               }
            }
         }

         return;
      }
   }

   public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
      this.checkMouseWheel();
      this.func_146276_q_();
      this.components.forEach((components) -> {
         components.drawScreen(mouseX, mouseY, partialTicks);
      });
   }

   public void func_73864_a(int mouseX, int mouseY, int clickedButton) {
      this.components.forEach((components) -> {
         components.mouseClicked(mouseX, mouseY, clickedButton);
      });
   }

   public void func_146286_b(int mouseX, int mouseY, int releaseButton) {
      this.components.forEach((components) -> {
         components.mouseReleased(mouseX, mouseY, releaseButton);
      });
   }

   public boolean func_73868_f() {
      return false;
   }

   public final ArrayList<Component> getComponents() {
      return this.components;
   }

   public void checkMouseWheel() {
      int dWheel = Mouse.getDWheel();
      if (dWheel < 0) {
         this.components.forEach((component) -> {
            component.setY(component.getY() - 10);
         });
      } else if (dWheel > 0) {
         this.components.forEach((component) -> {
            component.setY(component.getY() + 10);
         });
      }

   }

   public int getTextOffset() {
      return -6;
   }

   public Component getComponentByName(String name) {
      Iterator var2 = this.components.iterator();

      Component component;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         component = (Component)var2.next();
      } while(!component.getName().equalsIgnoreCase(name));

      return component;
   }

   public void func_73869_a(char typedChar, int keyCode) throws IOException {
      super.func_73869_a(typedChar, keyCode);
      this.components.forEach((component) -> {
         component.onKeyTyped(typedChar, keyCode);
      });
   }
}
