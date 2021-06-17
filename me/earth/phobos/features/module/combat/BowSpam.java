package me.earth.phobos.features.modules.combat;

import java.util.Iterator;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class BowSpam extends Module {
   public Setting<BowSpam.Mode> mode;
   public Setting<Boolean> bowbomb;
   public Setting<Boolean> allowOffhand;
   public Setting<Integer> ticks;
   public Setting<Integer> delay;
   public Setting<Boolean> tpsSync;
   public Setting<Boolean> autoSwitch;
   public Setting<Boolean> onlyWhenSave;
   public Setting<BowSpam.Target> targetMode;
   public Setting<Float> range;
   public Setting<Float> health;
   public Setting<Float> ownHealth;
   private final Timer timer;
   private boolean offhand;
   private boolean switched;
   private int lastHotbarSlot;

   public BowSpam() {
      super("BowSpam", "Spams your bow", Module.Category.COMBAT, true, false, false);
      this.mode = this.register(new Setting("Mode", BowSpam.Mode.FAST));
      this.bowbomb = this.register(new Setting("BowBomb", false, (v) -> {
         return this.mode.getValue() != BowSpam.Mode.BOWBOMB;
      }));
      this.allowOffhand = this.register(new Setting("Offhand", true, (v) -> {
         return this.mode.getValue() != BowSpam.Mode.AUTORELEASE;
      }));
      this.ticks = this.register(new Setting("Ticks", 3, 0, 20, (v) -> {
         return this.mode.getValue() == BowSpam.Mode.BOWBOMB || this.mode.getValue() == BowSpam.Mode.FAST;
      }, "Speed"));
      this.delay = this.register(new Setting("Delay", 50, 0, 500, (v) -> {
         return this.mode.getValue() == BowSpam.Mode.AUTORELEASE;
      }, "Speed"));
      this.tpsSync = this.register(new Setting("TpsSync", true));
      this.autoSwitch = this.register(new Setting("AutoSwitch", false));
      this.onlyWhenSave = this.register(new Setting("OnlyWhenSave", true, (v) -> {
         return (Boolean)this.autoSwitch.getValue();
      }));
      this.targetMode = this.register(new Setting("Target", BowSpam.Target.LOWEST, (v) -> {
         return (Boolean)this.autoSwitch.getValue();
      }));
      this.range = this.register(new Setting("Range", 3.0F, 0.0F, 6.0F, (v) -> {
         return (Boolean)this.autoSwitch.getValue();
      }, "Range of the target"));
      this.health = this.register(new Setting("Lethal", 6.0F, 0.1F, 36.0F, (v) -> {
         return (Boolean)this.autoSwitch.getValue();
      }, "When should it switch?"));
      this.ownHealth = this.register(new Setting("OwnHealth", 20.0F, 0.1F, 36.0F, (v) -> {
         return (Boolean)this.autoSwitch.getValue();
      }, "Own Health."));
      this.timer = new Timer();
      this.offhand = false;
      this.switched = false;
      this.lastHotbarSlot = -1;
   }

   public void onEnable() {
      this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0) {
         if ((Boolean)this.autoSwitch.getValue() && InventoryUtil.findHotbarBlock(ItemBow.class) != -1 && (Float)this.ownHealth.getValue() <= EntityUtil.getHealth(mc.field_71439_g) && (!(Boolean)this.onlyWhenSave.getValue() || EntityUtil.isSafe(mc.field_71439_g))) {
            EntityPlayer target = this.getTarget();
            if (target != null) {
               AutoCrystal crystal = (AutoCrystal)Phobos.moduleManager.getModuleByClass(AutoCrystal.class);
               if (!crystal.isOn() || !InventoryUtil.holdingItem(ItemEndCrystal.class)) {
                  Vec3d pos = target.func_174791_d();
                  double xPos = pos.field_72450_a;
                  double yPos = pos.field_72448_b;
                  double zPos = pos.field_72449_c;
                  if (mc.field_71439_g.func_70685_l(target)) {
                     yPos += (double)target.eyeHeight;
                  } else {
                     if (!EntityUtil.canEntityFeetBeSeen(target)) {
                        return;
                     }

                     yPos += 0.1D;
                  }

                  if (!(mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow)) {
                     this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
                     InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
                     mc.field_71474_y.field_74313_G.field_74513_e = true;
                     this.switched = true;
                  }

                  Phobos.rotationManager.lookAtVec3d(xPos, yPos, zPos);
                  if (mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBow) {
                     this.switched = true;
                  }
               }
            }
         } else if (event.getStage() == 0 && this.switched && this.lastHotbarSlot != -1) {
            InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
            mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown(1);
            this.switched = false;
         } else {
            mc.field_71474_y.field_74313_G.field_74513_e = Mouse.isButtonDown(1);
         }

         if (this.mode.getValue() == BowSpam.Mode.FAST && (this.offhand || mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) && mc.field_71439_g.func_184587_cr() && (float)mc.field_71439_g.func_184612_cw() >= (float)(Integer)this.ticks.getValue() * ((Boolean)this.tpsSync.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0F)) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, mc.field_71439_g.func_174811_aO()));
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            mc.field_71439_g.func_184597_cx();
         }

      }
   }

   public void onUpdate() {
      this.offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151031_f && (Boolean)this.allowOffhand.getValue();
      switch((BowSpam.Mode)this.mode.getValue()) {
      case AUTORELEASE:
         if ((this.offhand || mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) && this.timer.passedMs((long)((int)((float)(Integer)this.delay.getValue() * ((Boolean)this.tpsSync.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0F))))) {
            mc.field_71442_b.func_78766_c(mc.field_71439_g);
            this.timer.reset();
         }
         break;
      case BOWBOMB:
         if ((this.offhand || mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) && mc.field_71439_g.func_184587_cr() && (float)mc.field_71439_g.func_184612_cw() >= (float)(Integer)this.ticks.getValue() * ((Boolean)this.tpsSync.getValue() ? Phobos.serverManager.getTpsFactor() : 1.0F)) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, mc.field_71439_g.func_174811_aO()));
            mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.0624D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, false));
            mc.field_71439_g.field_71174_a.func_147297_a(new PositionRotation(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 999.0D, mc.field_71439_g.field_70161_v, mc.field_71439_g.field_70177_z, mc.field_71439_g.field_70125_A, true));
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
            mc.field_71439_g.func_184597_cx();
         }
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && (Boolean)this.bowbomb.getValue() && this.mode.getValue() != BowSpam.Mode.BOWBOMB && event.getPacket() instanceof CPacketPlayerDigging) {
         CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
         if (packet.func_180762_c() == Action.RELEASE_USE_ITEM && (this.offhand || mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow) && mc.field_71439_g.func_184612_cw() >= 20 && !mc.field_71439_g.field_70122_E) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 0.10000000149011612D, mc.field_71439_g.field_70161_v, false));
            mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u - 10000.0D, mc.field_71439_g.field_70161_v, true));
         }
      }

   }

   private EntityPlayer getTarget() {
      double maxHealth = 36.0D;
      EntityPlayer target = null;
      Iterator var4 = mc.field_71441_e.field_73010_i.iterator();

      while(true) {
         EntityPlayer player;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              if (!var4.hasNext()) {
                                 return target;
                              }

                              player = (EntityPlayer)var4.next();
                           } while(player == null);
                        } while(EntityUtil.isDead(player));
                     } while(EntityUtil.getHealth(player) > (Float)this.health.getValue());
                  } while(player.equals(mc.field_71439_g));
               } while(Phobos.friendManager.isFriend(player));
            } while(mc.field_71439_g.func_70068_e(player) > MathUtil.square((Float)this.range.getValue()));
         } while(!mc.field_71439_g.func_70685_l(player) && !EntityUtil.canEntityFeetBeSeen(player));

         if (target == null) {
            target = player;
            maxHealth = (double)EntityUtil.getHealth(player);
         }

         if (this.targetMode.getValue() == BowSpam.Target.CLOSEST && mc.field_71439_g.func_70068_e(player) < mc.field_71439_g.func_70068_e(target)) {
            target = player;
            maxHealth = (double)EntityUtil.getHealth(player);
         }

         if (this.targetMode.getValue() == BowSpam.Target.LOWEST && (double)EntityUtil.getHealth(player) < maxHealth) {
            target = player;
            maxHealth = (double)EntityUtil.getHealth(player);
         }
      }
   }

   public static enum Mode {
      FAST,
      AUTORELEASE,
      BOWBOMB;
   }

   public static enum Target {
      CLOSEST,
      LOWEST;
   }
}
