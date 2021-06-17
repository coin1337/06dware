package me.earth.phobos.features.modules.client;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class Media extends Module {
   public final Setting<Boolean> changeOwn = this.register(new Setting("MyName", true));
   public final Setting<String> ownName = this.register(new Setting("Name", "Name here...", (v) -> {
      return (Boolean)this.changeOwn.getValue();
   }));
   private static Media instance;

   public Media() {
      super("Media", "Helps with creating Media", Module.Category.CLIENT, false, false, false);
      instance = this;
   }

   public static Media getInstance() {
      if (instance == null) {
         instance = new Media();
      }

      return instance;
   }

   public static String getPlayerName() {
      if (!fullNullCheck() && ServerModule.getInstance().isConnected()) {
         String name = ServerModule.getInstance().getPlayerName();
         return name != null && !name.isEmpty() ? name : mc.func_110432_I().func_111285_a();
      } else {
         return mc.func_110432_I().func_111285_a();
      }
   }
}
