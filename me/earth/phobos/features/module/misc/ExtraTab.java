package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class ExtraTab extends Module {
   public Setting<Integer> size = this.register(new Setting("Size", 250, 1, 1000));
   private static ExtraTab INSTANCE = new ExtraTab();

   public ExtraTab() {
      super("ExtraTab", "Extends Tab.", Module.Category.MISC, false, false, false);
      this.setInstance();
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
      String name = networkPlayerInfoIn.func_178854_k() != null ? networkPlayerInfoIn.func_178854_k().func_150254_d() : ScorePlayerTeam.func_96667_a(networkPlayerInfoIn.func_178850_i(), networkPlayerInfoIn.func_178845_a().getName());
      return Phobos.friendManager.isFriend(name) ? "§b" + name : name;
   }

   public static ExtraTab getINSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new ExtraTab();
      }

      return INSTANCE;
   }
}
