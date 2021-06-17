package me.earth.phobos.features.command.commands;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.render.XRay;
import me.earth.phobos.features.setting.Setting;

public class XrayCommand extends Command {
   public XrayCommand() {
      super("xray", new String[]{"<add/del>", "<block>"});
   }

   public void execute(String[] commands) {
      XRay module = (XRay)Phobos.moduleManager.getModuleByClass(XRay.class);
      if (module != null) {
         Setting setting;
         if (commands.length == 1) {
            StringBuilder blocks = new StringBuilder();
            Iterator var7 = module.getSettings().iterator();

            while(var7.hasNext()) {
               setting = (Setting)var7.next();
               if (!setting.equals(module.enabled) && !setting.equals(module.drawn) && !setting.equals(module.bind) && !setting.equals(module.newBlock) && !setting.equals(module.showBlocks)) {
                  blocks.append(setting.getName()).append(", ");
               }
            }

            Command.sendMessage(blocks.toString());
            return;
         }

         if (commands.length == 2) {
            sendMessage("Please specify a block.");
            return;
         }

         String addRemove = commands[0];
         String blockName = commands[1];
         if (!addRemove.equalsIgnoreCase("del") && !addRemove.equalsIgnoreCase("remove")) {
            if (addRemove.equalsIgnoreCase("add")) {
               if (!module.shouldRender(blockName)) {
                  module.register(new Setting(blockName, true, (v) -> {
                     return (Boolean)module.showBlocks.getValue();
                  }));
                  sendMessage("<Xray> Added new Block: " + blockName);
               }
            } else {
               sendMessage("§cAn error occured, block either exists or wrong use of command: .xray <add/del(remove)> <block>");
            }
         } else {
            setting = module.getSettingByName(blockName);
            if (setting != null) {
               if (setting.equals(module.enabled) || setting.equals(module.drawn) || setting.equals(module.bind) || setting.equals(module.newBlock) || setting.equals(module.showBlocks)) {
                  return;
               }

               module.unregister(setting);
            }

            sendMessage("<XRay>§c Removed: " + blockName);
         }
      }

   }
}
