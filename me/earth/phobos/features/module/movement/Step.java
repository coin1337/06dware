package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Step extends Module {
   public Setting<Boolean> vanilla = this.register(new Setting("Vanilla", false));
   public Setting<Float> stepHeightVanilla = this.register(new Setting("VHeight", 2.0F, 0.0F, 4.0F, (v) -> {
      return (Boolean)this.vanilla.getValue();
   }));
   public Setting<Integer> stepHeight = this.register(new Setting("Height", 2, 1, 4, (v) -> {
      return !(Boolean)this.vanilla.getValue();
   }));
   public Setting<Boolean> spoof = this.register(new Setting("Spoof", true, (v) -> {
      return !(Boolean)this.vanilla.getValue();
   }));
   public Setting<Integer> ticks = this.register(new Setting("Delay", 3, 0, 25, (v) -> {
      return (Boolean)this.spoof.getValue() && !(Boolean)this.vanilla.getValue();
   }));
   public Setting<Boolean> turnOff = this.register(new Setting("Disable", false, (v) -> {
      return !(Boolean)this.vanilla.getValue();
   }));
   public Setting<Boolean> check = this.register(new Setting("Check", true, (v) -> {
      return !(Boolean)this.vanilla.getValue();
   }));
   public Setting<Boolean> small = this.register(new Setting("Offset", false, (v) -> {
      return (Integer)this.stepHeight.getValue() > 1 && !(Boolean)this.vanilla.getValue();
   }));
   private final double[] oneblockPositions = new double[]{0.42D, 0.75D};
   private final double[] twoblockPositions = new double[]{0.4D, 0.75D, 0.5D, 0.41D, 0.83D, 1.16D, 1.41D, 1.57D, 1.58D, 1.42D};
   private final double[] futurePositions = new double[]{0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D};
   final double[] twoFiveOffset = new double[]{0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D};
   private final double[] threeBlockPositions = new double[]{0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 1.51D, 1.9D, 2.21D, 2.45D, 2.43D};
   private final double[] fourBlockPositions = new double[]{0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 1.51D, 1.9D, 2.21D, 2.45D, 2.43D, 2.78D, 2.63D, 2.51D, 2.9D, 3.21D, 3.45D, 3.43D};
   private double[] selectedPositions = new double[0];
   private int packets;
   private static Step instance;

   public Step() {
      super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
      instance = this;
   }

   public static Step getInstance() {
      if (instance == null) {
         instance = new Step();
      }

      return instance;
   }

   public void onToggle() {
      mc.field_71439_g.field_70138_W = 0.6F;
   }

   public void onUpdate() {
      if ((Boolean)this.vanilla.getValue()) {
         mc.field_71439_g.field_70138_W = (Float)this.stepHeightVanilla.getValue();
      } else {
         switch((Integer)this.stepHeight.getValue()) {
         case 1:
            this.selectedPositions = this.oneblockPositions;
            break;
         case 2:
            this.selectedPositions = (Boolean)this.small.getValue() ? this.twoblockPositions : this.futurePositions;
            break;
         case 3:
            this.selectedPositions = this.twoFiveOffset;
         case 4:
            this.selectedPositions = this.fourBlockPositions;
         }

         if (mc.field_71439_g.field_70123_F && mc.field_71439_g.field_70122_E) {
            ++this.packets;
         }

         AxisAlignedBB bb = mc.field_71439_g.func_174813_aQ();
         int z;
         if ((Boolean)this.check.getValue()) {
            for(int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d + 1.0D); ++x) {
               for(z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f + 1.0D); ++z) {
                  Block block = mc.field_71441_e.func_180495_p(new BlockPos((double)x, bb.field_72337_e + 1.0D, (double)z)).func_177230_c();
                  if (!(block instanceof BlockAir)) {
                     return;
                  }
               }
            }
         }

         if (mc.field_71439_g.field_70122_E && !mc.field_71439_g.func_70055_a(Material.field_151586_h) && !mc.field_71439_g.func_70055_a(Material.field_151587_i) && mc.field_71439_g.field_70124_G && mc.field_71439_g.field_70143_R == 0.0F && !mc.field_71474_y.field_74314_A.field_74513_e && mc.field_71439_g.field_70123_F && !mc.field_71439_g.func_70617_f_() && (this.packets > this.selectedPositions.length - 2 || (Boolean)this.spoof.getValue() && this.packets > (Integer)this.ticks.getValue())) {
            double[] var7 = this.selectedPositions;
            z = var7.length;

            for(int var8 = 0; var8 < z; ++var8) {
               double position = var7[var8];
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + position, mc.field_71439_g.field_70161_v, true));
            }

            mc.field_71439_g.func_70107_b(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + this.selectedPositions[this.selectedPositions.length - 1], mc.field_71439_g.field_70161_v);
            this.packets = 0;
            if ((Boolean)this.turnOff.getValue()) {
               this.disable();
            }
         }

      }
   }
}
