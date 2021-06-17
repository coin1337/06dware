package me.earth.phobos.features.modules.render;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XRay extends Module {
   public Setting<String> newBlock = this.register(new Setting("NewBlock", "Add Block..."));
   public Setting<Boolean> showBlocks = this.register(new Setting("ShowBlocks", false));
   private static XRay INSTANCE = new XRay();

   public XRay() {
      super("XRay", "Lets you look through walls.", Module.Category.RENDER, false, false, true);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static XRay getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new XRay();
      }

      return INSTANCE;
   }

   public void onEnable() {
      mc.field_71438_f.func_72712_a();
   }

   public void onDisable() {
      mc.field_71438_f.func_72712_a();
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (!Phobos.configManager.loadingConfig && !Phobos.configManager.savingConfig) {
         if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.newBlock) && !this.shouldRender((String)this.newBlock.getPlannedValue())) {
               this.register(new Setting((String)this.newBlock.getPlannedValue(), true, (v) -> {
                  return (Boolean)this.showBlocks.getValue();
               }));
               Command.sendMessage("<Xray> Added new Block: " + (String)this.newBlock.getPlannedValue());
               if (this.isOn()) {
                  mc.field_71438_f.func_72712_a();
               }

               event.setCanceled(true);
            } else {
               Setting setting = event.getSetting();
               if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newBlock) || setting.equals(this.showBlocks)) {
                  return;
               }

               if (setting.getValue() instanceof Boolean && !(Boolean)setting.getPlannedValue()) {
                  this.unregister(setting);
                  if (this.isOn()) {
                     mc.field_71438_f.func_72712_a();
                  }

                  event.setCanceled(true);
               }
            }
         }

      }
   }

   public boolean shouldRender(Block block) {
      return this.shouldRender(block.func_149732_F());
   }

   public boolean shouldRender(String name) {
      Iterator var2 = this.getSettings().iterator();

      Setting setting;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         setting = (Setting)var2.next();
      } while(!name.equalsIgnoreCase(setting.getName()));

      return true;
   }
}
