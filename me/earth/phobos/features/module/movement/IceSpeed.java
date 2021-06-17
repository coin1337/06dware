package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.init.Blocks;

public class IceSpeed extends Module {
   private Setting<Float> speed = this.register(new Setting("Speed", 0.4F, 0.2F, 1.5F));
   private static IceSpeed INSTANCE = new IceSpeed();

   public IceSpeed() {
      super("IceSpeed", "Speeds you up on ice.", Module.Category.MOVEMENT, false, false, false);
      INSTANCE = this;
   }

   public static IceSpeed getINSTANCE() {
      if (INSTANCE == null) {
         INSTANCE = new IceSpeed();
      }

      return INSTANCE;
   }

   public void onUpdate() {
      Blocks.field_150432_aD.field_149765_K = (Float)this.speed.getValue();
      Blocks.field_150403_cj.field_149765_K = (Float)this.speed.getValue();
      Blocks.field_185778_de.field_149765_K = (Float)this.speed.getValue();
   }

   public void onDisable() {
      Blocks.field_150432_aD.field_149765_K = 0.98F;
      Blocks.field_150403_cj.field_149765_K = 0.98F;
      Blocks.field_185778_de.field_149765_K = 0.98F;
   }
}
