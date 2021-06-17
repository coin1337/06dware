package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

public class Auto32k extends Module {
   public Setting<Auto32k.Mode> mode;
   private final Setting<Integer> delay;
   private final Setting<Integer> delayDispenser;
   private final Setting<Integer> blocksPerPlace;
   private final Setting<Float> range;
   private final Setting<Boolean> raytrace;
   private final Setting<Boolean> rotate;
   public Setting<Boolean> autoSwitch;
   public Setting<Boolean> withBind;
   public Setting<Bind> switchBind;
   private final Setting<Double> targetRange;
   private final Setting<Boolean> extra;
   private final Setting<Auto32k.PlaceType> placeType;
   private final Setting<Boolean> freecam;
   private final Setting<Boolean> onOtherHoppers;
   private final Setting<Boolean> preferObby;
   private final Setting<Boolean> checkForShulker;
   private final Setting<Integer> checkDelay;
   private final Setting<Boolean> drop;
   private final Setting<Boolean> mine;
   private final Setting<Boolean> checkStatus;
   private final Setting<Boolean> packet;
   private final Setting<Boolean> superPacket;
   private final Setting<Boolean> secretClose;
   private final Setting<Boolean> closeGui;
   private final Setting<Boolean> repeatSwitch;
   private final Setting<Boolean> simulate;
   private final Setting<Float> hopperDistance;
   private final Setting<Integer> trashSlot;
   private final Setting<Boolean> messages;
   private final Setting<Boolean> antiHopper;
   private float yaw;
   private float pitch;
   private boolean spoof;
   public boolean switching;
   private int lastHotbarSlot;
   private int shulkerSlot;
   private int hopperSlot;
   private BlockPos hopperPos;
   private EntityPlayer target;
   public Auto32k.Step currentStep;
   private final Timer placeTimer;
   private static Auto32k instance;
   private int obbySlot;
   private int dispenserSlot;
   private int redstoneSlot;
   private Auto32k.DispenserData finalDispenserData;
   private int actionsThisTick;
   private boolean checkedThisTick;
   private boolean authSneakPacket;
   private Timer disableTimer;
   private boolean shouldDisable;
   private boolean rotationprepared;

   public Auto32k() {
      super("Auto32k", "Auto32ks", Module.Category.COMBAT, true, false, false);
      this.mode = this.register(new Setting("Mode", Auto32k.Mode.NORMAL));
      this.delay = this.register(new Setting("Delay/Place", 25, 0, 250));
      this.delayDispenser = this.register(new Setting("Blocks/Place", 1, 1, 8, (v) -> {
         return this.mode.getValue() != Auto32k.Mode.NORMAL;
      }));
      this.blocksPerPlace = this.register(new Setting("Actions/Place", 1, 1, 3, (v) -> {
         return this.mode.getValue() == Auto32k.Mode.NORMAL;
      }));
      this.range = this.register(new Setting("PlaceRange", 4.5F, 0.0F, 6.0F));
      this.raytrace = this.register(new Setting("Raytrace", false));
      this.rotate = this.register(new Setting("Rotate", false));
      this.autoSwitch = this.register(new Setting("AutoSwitch", false, (v) -> {
         return this.mode.getValue() == Auto32k.Mode.NORMAL;
      }));
      this.withBind = this.register(new Setting("WithBind", false, (v) -> {
         return this.mode.getValue() == Auto32k.Mode.NORMAL && (Boolean)this.autoSwitch.getValue();
      }));
      this.switchBind = this.register(new Setting("SwitchBind", new Bind(-1), (v) -> {
         return (Boolean)this.autoSwitch.getValue() && this.mode.getValue() == Auto32k.Mode.NORMAL && (Boolean)this.withBind.getValue();
      }));
      this.targetRange = this.register(new Setting("TargetRange", 6.0D, 0.0D, 20.0D));
      this.extra = this.register(new Setting("ExtraRotation", false));
      this.placeType = this.register(new Setting("Place", Auto32k.PlaceType.CLOSE));
      this.freecam = this.register(new Setting("Freecam", false));
      this.onOtherHoppers = this.register(new Setting("UseHoppers", false));
      this.preferObby = this.register(new Setting("UseObby", false, (v) -> {
         return this.mode.getValue() != Auto32k.Mode.NORMAL;
      }));
      this.checkForShulker = this.register(new Setting("CheckShulker", true));
      this.checkDelay = this.register(new Setting("CheckDelay", 500, 0, 500, (v) -> {
         return (Boolean)this.checkForShulker.getValue();
      }));
      this.drop = this.register(new Setting("Drop", false));
      this.mine = this.register(new Setting("Mine", false, (v) -> {
         return (Boolean)this.drop.getValue();
      }));
      this.checkStatus = this.register(new Setting("CheckState", true));
      this.packet = this.register(new Setting("Packet", false));
      this.superPacket = this.register(new Setting("DispExtra", false));
      this.secretClose = this.register(new Setting("SecretClose", false));
      this.closeGui = this.register(new Setting("CloseGui", false, (v) -> {
         return (Boolean)this.secretClose.getValue();
      }));
      this.repeatSwitch = this.register(new Setting("SwitchOnFail", true));
      this.simulate = this.register(new Setting("Simulate", true, (v) -> {
         return this.mode.getValue() != Auto32k.Mode.NORMAL;
      }));
      this.hopperDistance = this.register(new Setting("HopperRange", 8.0F, 0.0F, 20.0F));
      this.trashSlot = this.register(new Setting("32kSlot", 0, 0, 9));
      this.messages = this.register(new Setting("Messages", false));
      this.antiHopper = this.register(new Setting("AntiHopper", false));
      this.lastHotbarSlot = -1;
      this.shulkerSlot = -1;
      this.hopperSlot = -1;
      this.currentStep = Auto32k.Step.PRE;
      this.placeTimer = new Timer();
      this.obbySlot = -1;
      this.dispenserSlot = -1;
      this.redstoneSlot = -1;
      this.actionsThisTick = 0;
      this.checkedThisTick = false;
      this.authSneakPacket = false;
      this.disableTimer = new Timer();
      this.rotationprepared = false;
      instance = this;
   }

   public static Auto32k getInstance() {
      if (instance == null) {
         instance = new Auto32k();
      }

      return instance;
   }

   public void onEnable() {
      this.checkedThisTick = false;
      this.resetFields();
      if (mc.field_71462_r instanceof GuiHopper) {
         this.currentStep = Auto32k.Step.HOPPERGUI;
      }

      if (this.mode.getValue() == Auto32k.Mode.NORMAL && (Boolean)this.autoSwitch.getValue() && !(Boolean)this.withBind.getValue()) {
         this.switching = true;
      }

   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 0) {
         if (this.shouldDisable && this.disableTimer.passedMs(1000L)) {
            this.shouldDisable = false;
            this.disable();
         } else {
            this.checkedThisTick = false;
            this.actionsThisTick = 0;
            if (!this.isOff() && (this.mode.getValue() != Auto32k.Mode.NORMAL || !(Boolean)this.autoSwitch.getValue() || this.switching)) {
               if (this.mode.getValue() == Auto32k.Mode.NORMAL) {
                  this.normal32k();
               } else {
                  this.processDispenser32k();
               }

            }
         }
      }
   }

   @SubscribeEvent
   public void onGui(GuiOpenEvent event) {
      if (!fullNullCheck() && !this.isOff()) {
         if (!(Boolean)this.secretClose.getValue() && mc.field_71462_r instanceof GuiHopper) {
            if ((Boolean)this.drop.getValue() && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151048_u && this.hopperPos != null) {
               mc.field_71439_g.func_71040_bB(true);
               if ((Boolean)this.mine.getValue() && this.hopperPos != null) {
                  int pickaxeSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
                  if (pickaxeSlot != -1) {
                     InventoryUtil.switchToHotbarSlot(pickaxeSlot, false);
                     if ((Boolean)this.rotate.getValue()) {
                        this.rotateToPos(this.hopperPos.func_177984_a(), (Vec3d)null);
                     }

                     mc.field_71442_b.func_180512_c(this.hopperPos.func_177984_a(), mc.field_71439_g.func_174811_aO());
                     mc.field_71442_b.func_180512_c(this.hopperPos.func_177984_a(), mc.field_71439_g.func_174811_aO());
                     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  }
               }
            }

            this.resetFields();
            if (this.mode.getValue() != Auto32k.Mode.NORMAL) {
               this.disable();
               return;
            }

            if ((Boolean)this.autoSwitch.getValue() && this.mode.getValue() != Auto32k.Mode.DISPENSER) {
               if (!(Boolean)this.withBind.getValue()) {
                  this.disable();
               }
            } else {
               this.disable();
            }
         } else if (event.getGui() instanceof GuiHopper) {
            this.currentStep = Auto32k.Step.HOPPERGUI;
         }

      }
   }

   public String getDisplayInfo() {
      return this.switching ? "§aSwitch" : null;
   }

   @SubscribeEvent
   public void onKeyInput(KeyInputEvent event) {
      if (!this.isOff()) {
         if (Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof PhobosGui) && ((Bind)this.switchBind.getValue()).getKey() == Keyboard.getEventKey() && (Boolean)this.withBind.getValue()) {
            if (this.switching) {
               this.resetFields();
               this.switching = true;
            }

            this.switching = !this.switching;
         }

      }
   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2) {
         Setting setting = event.getSetting();
         if (setting != null && setting.getFeature().equals(this) && setting.equals(this.mode)) {
            this.resetFields();
         }
      }

   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (!fullNullCheck() && !this.isOff()) {
         if (event.getPacket() instanceof CPacketPlayer) {
            if (this.spoof) {
               CPacketPlayer packet = (CPacketPlayer)event.getPacket();
               packet.field_149476_e = this.yaw;
               packet.field_149473_f = this.pitch;
               this.spoof = false;
            }
         } else if (event.getPacket() instanceof CPacketCloseWindow) {
            if (!(Boolean)this.secretClose.getValue() && mc.field_71462_r instanceof GuiHopper && this.hopperPos != null) {
               if ((Boolean)this.drop.getValue() && mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151048_u) {
                  mc.field_71439_g.func_71040_bB(true);
                  if ((Boolean)this.mine.getValue()) {
                     int pickaxeSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
                     if (pickaxeSlot != -1) {
                        InventoryUtil.switchToHotbarSlot(pickaxeSlot, false);
                        if ((Boolean)this.rotate.getValue()) {
                           this.rotateToPos(this.hopperPos.func_177984_a(), (Vec3d)null);
                        }

                        mc.field_71442_b.func_180512_c(this.hopperPos.func_177984_a(), mc.field_71439_g.func_174811_aO());
                        mc.field_71442_b.func_180512_c(this.hopperPos.func_177984_a(), mc.field_71439_g.func_174811_aO());
                        mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                     }
                  }
               }

               this.resetFields();
               if ((Boolean)this.autoSwitch.getValue() && this.mode.getValue() != Auto32k.Mode.DISPENSER) {
                  if (!(Boolean)this.withBind.getValue()) {
                     this.disable();
                  }
               } else {
                  this.disable();
               }
            } else if ((Boolean)this.secretClose.getValue() && (!(Boolean)this.autoSwitch.getValue() || this.switching || this.mode.getValue() == Auto32k.Mode.DISPENSER) && this.currentStep == Auto32k.Step.HOPPERGUI) {
               event.setCanceled(true);
            }
         }

      }
   }

   private void normal32k() {
      if ((Boolean)this.autoSwitch.getValue()) {
         if (this.switching) {
            this.processNormal32k();
         } else {
            this.resetFields();
         }
      } else {
         this.processNormal32k();
      }

   }

   private void processNormal32k() {
      if (!this.isOff()) {
         if (this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
            this.check();
            switch(this.currentStep) {
            case PRE:
               this.runPreStep();
               if (this.currentStep == Auto32k.Step.PRE) {
                  break;
               }
            case HOPPER:
               if (this.currentStep == Auto32k.Step.HOPPER) {
                  this.checkState();
                  if (this.currentStep == Auto32k.Step.PRE) {
                     if (this.checkedThisTick) {
                        this.processNormal32k();
                     }

                     return;
                  }

                  this.runHopperStep();
                  if (this.actionsThisTick >= (Integer)this.blocksPerPlace.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
                     break;
                  }
               }
            case SHULKER:
               this.checkState();
               if (this.currentStep == Auto32k.Step.PRE) {
                  if (this.checkedThisTick) {
                     this.processNormal32k();
                  }

                  return;
               }

               this.runShulkerStep();
               if (this.actionsThisTick >= (Integer)this.blocksPerPlace.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
                  break;
               }
            case CLICKHOPPER:
               this.checkState();
               if (this.currentStep == Auto32k.Step.PRE) {
                  if (this.checkedThisTick) {
                     this.processNormal32k();
                  }

                  return;
               }

               this.runClickHopper();
            case HOPPERGUI:
               this.runHopperGuiStep();
               break;
            default:
               Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
               Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
               Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
               Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
               Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
               this.currentStep = Auto32k.Step.PRE;
            }
         }

      }
   }

   private void runPreStep() {
      if (!this.isOff()) {
         Auto32k.PlaceType type = (Auto32k.PlaceType)this.placeType.getValue();
         if (Freecam.getInstance().isOn() && !(Boolean)this.freecam.getValue()) {
            if ((Boolean)this.messages.getValue()) {
               Command.sendMessage("§c<Auto32k> Disable Freecam.");
            }

            if ((Boolean)this.autoSwitch.getValue()) {
               this.resetFields();
               if (!(Boolean)this.withBind.getValue()) {
                  this.disable();
               }
            } else {
               this.disable();
            }

         } else {
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
            this.shulkerSlot = InventoryUtil.findHotbarBlock(BlockShulkerBox.class);
            if (mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d();
               if (block instanceof BlockShulkerBox) {
                  this.shulkerSlot = -2;
               } else if (block instanceof BlockHopper) {
                  this.hopperSlot = -2;
               }
            }

            if (this.shulkerSlot != -1 && this.hopperSlot != -1) {
               this.target = EntityUtil.getClosestEnemy((Double)this.targetRange.getValue());
               if (this.target == null) {
                  if ((Boolean)this.autoSwitch.getValue()) {
                     if (this.switching) {
                        this.resetFields();
                        this.switching = true;
                     } else {
                        this.resetFields();
                     }

                     return;
                  }

                  type = this.placeType.getValue() == Auto32k.PlaceType.MOUSE ? Auto32k.PlaceType.MOUSE : Auto32k.PlaceType.CLOSE;
               }

               this.hopperPos = this.findBestPos(type, this.target);
               if (this.hopperPos != null) {
                  if (mc.field_71441_e.func_180495_p(this.hopperPos).func_177230_c() instanceof BlockHopper) {
                     this.currentStep = Auto32k.Step.SHULKER;
                  } else {
                     this.currentStep = Auto32k.Step.HOPPER;
                  }
               } else {
                  if ((Boolean)this.messages.getValue()) {
                     Command.sendMessage("§c<Auto32k> Block not found.");
                  }

                  if ((Boolean)this.autoSwitch.getValue()) {
                     this.resetFields();
                     if (!(Boolean)this.withBind.getValue()) {
                        this.disable();
                     }
                  } else {
                     this.disable();
                  }
               }

            } else {
               if ((Boolean)this.messages.getValue()) {
                  Command.sendMessage("§c<Auto32k> Materials not found.");
               }

               if ((Boolean)this.autoSwitch.getValue()) {
                  this.resetFields();
                  if (!(Boolean)this.withBind.getValue()) {
                     this.disable();
                  }
               } else {
                  this.disable();
               }

            }
         }
      }
   }

   private void runHopperStep() {
      if (!this.isOff()) {
         if (this.currentStep == Auto32k.Step.HOPPER) {
            this.runPlaceStep(this.hopperPos, this.hopperSlot);
            this.currentStep = Auto32k.Step.SHULKER;
         }

      }
   }

   private void runShulkerStep() {
      if (!this.isOff()) {
         if (this.currentStep == Auto32k.Step.SHULKER) {
            this.runPlaceStep(this.hopperPos.func_177984_a(), this.shulkerSlot);
            this.currentStep = Auto32k.Step.CLICKHOPPER;
         }

      }
   }

   private void runClickHopper() {
      if (!this.isOff()) {
         if (this.currentStep == Auto32k.Step.CLICKHOPPER) {
            if (this.mode.getValue() == Auto32k.Mode.NORMAL && !(mc.field_71441_e.func_180495_p(this.hopperPos.func_177984_a()).func_177230_c() instanceof BlockShulkerBox) && (Boolean)this.checkForShulker.getValue()) {
               if (this.placeTimer.passedMs((long)(Integer)this.checkDelay.getValue())) {
                  this.currentStep = Auto32k.Step.SHULKER;
               }

            } else {
               this.clickBlock(this.hopperPos);
               this.currentStep = Auto32k.Step.HOPPERGUI;
            }
         }
      }
   }

   private void runHopperGuiStep() {
      if (!this.isOff()) {
         if (this.currentStep == Auto32k.Step.HOPPERGUI) {
            if (mc.field_71439_g.field_71070_bA instanceof ContainerHopper) {
               if (!EntityUtil.holding32k(mc.field_71439_g)) {
                  int swordIndex = -1;

                  for(int i = 0; i < 5; ++i) {
                     if (EntityUtil.is32k(((Slot)mc.field_71439_g.field_71070_bA.field_75151_b.get(0)).field_75224_c.func_70301_a(i))) {
                        swordIndex = i;
                        break;
                     }
                  }

                  if (swordIndex == -1) {
                     return;
                  }

                  if ((Integer)this.trashSlot.getValue() != 0) {
                     InventoryUtil.switchToHotbarSlot((Integer)this.trashSlot.getValue() - 1, false);
                  } else if (this.mode.getValue() != Auto32k.Mode.NORMAL && this.shulkerSlot > 35 && this.shulkerSlot != 45) {
                     InventoryUtil.switchToHotbarSlot(44 - this.shulkerSlot, false);
                  }

                  mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71070_bA.field_75152_c, swordIndex, (Integer)this.trashSlot.getValue() == 0 ? mc.field_71439_g.field_71071_by.field_70461_c : (Integer)this.trashSlot.getValue() - 1, ClickType.SWAP, mc.field_71439_g);
               } else if ((Boolean)this.closeGui.getValue() && (Boolean)this.secretClose.getValue()) {
                  mc.field_71439_g.func_71053_j();
               }
            } else if (EntityUtil.holding32k(mc.field_71439_g)) {
               if ((Boolean)this.autoSwitch.getValue() && this.mode.getValue() == Auto32k.Mode.NORMAL) {
                  this.switching = false;
               } else if (!(Boolean)this.autoSwitch.getValue() || this.mode.getValue() == Auto32k.Mode.DISPENSER) {
                  this.shouldDisable = true;
                  this.disableTimer.reset();
               }
            }

         }
      }
   }

   private void runPlaceStep(BlockPos pos, int slot) {
      if (!this.isOff()) {
         EnumFacing side = EnumFacing.UP;
         if ((Boolean)this.antiHopper.getValue() && this.currentStep == Auto32k.Step.HOPPER) {
            boolean foundfacing = false;
            EnumFacing[] var5 = EnumFacing.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               EnumFacing facing = var5[var7];
               if (mc.field_71441_e.func_180495_p(pos.func_177972_a(facing)).func_177230_c() != Blocks.field_150438_bZ && !mc.field_71441_e.func_180495_p(pos.func_177972_a(facing)).func_185904_a().func_76222_j()) {
                  foundfacing = true;
                  side = facing;
                  break;
               }
            }

            if (!foundfacing) {
               this.resetFields();
               return;
            }
         } else {
            side = BlockUtil.getFirstFacing(pos);
            if (side == null) {
               this.resetFields();
               return;
            }
         }

         BlockPos neighbour = pos.func_177972_a(side);
         EnumFacing opposite = side.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
         this.authSneakPacket = true;
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
         this.authSneakPacket = false;
         if ((Boolean)this.rotate.getValue()) {
            if ((Integer)this.blocksPerPlace.getValue() > 1) {
               float[] angle = RotationUtil.getLegitRotations(hitVec);
               if ((Boolean)this.extra.getValue()) {
                  RotationUtil.faceYawAndPitch(angle[0], angle[1]);
               }
            } else {
               this.rotateToPos((BlockPos)null, hitVec);
            }
         }

         InventoryUtil.switchToHotbarSlot(slot, false);
         BlockUtil.rightClickBlock(neighbour, hitVec, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, (Boolean)this.packet.getValue());
         this.authSneakPacket = true;
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         this.authSneakPacket = false;
         this.placeTimer.reset();
         ++this.actionsThisTick;
      }
   }

   private BlockPos findBestPos(Auto32k.PlaceType type, EntityPlayer target) {
      BlockPos pos = null;
      NonNullList<BlockPos> positions = NonNullList.func_191196_a();
      positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos(mc.field_71439_g), (Float)this.range.getValue(), ((Float)this.range.getValue()).intValue(), false, true, 0).stream().filter(this::canPlace).collect(Collectors.toList()));
      if (positions.isEmpty()) {
         return null;
      } else {
         switch(type) {
         case MOUSE:
            if (mc.field_71476_x != null && mc.field_71476_x.field_72313_a == Type.BLOCK) {
               BlockPos mousePos = mc.field_71476_x.func_178782_a();
               if (mousePos != null && !this.canPlace(mousePos)) {
                  BlockPos mousePosUp = mousePos.func_177984_a();
                  if (this.canPlace(mousePosUp)) {
                     pos = mousePosUp;
                  }
               } else {
                  pos = mousePos;
               }
            }

            if (pos != null) {
               break;
            }
         case CLOSE:
            positions.sort(Comparator.comparingDouble((pos2) -> {
               return mc.field_71439_g.func_174818_b(pos2);
            }));
            pos = (BlockPos)positions.get(0);
            break;
         case ENEMY:
            target.getClass();
            positions.sort(Comparator.comparingDouble(target::func_174818_b));
            pos = (BlockPos)positions.get(0);
            break;
         case MIDDLE:
            List<BlockPos> toRemove = new ArrayList();
            NonNullList<BlockPos> copy = NonNullList.func_191196_a();
            copy.addAll(positions);
            Iterator var7 = copy.iterator();

            while(true) {
               BlockPos position;
               double difference;
               do {
                  if (!var7.hasNext()) {
                     copy.removeAll(toRemove);
                     if (copy.isEmpty()) {
                        copy.addAll(positions);
                     }

                     copy.sort(Comparator.comparingDouble((pos2) -> {
                        return mc.field_71439_g.func_174818_b(pos2);
                     }));
                     pos = (BlockPos)copy.get(0);
                     return pos;
                  }

                  position = (BlockPos)var7.next();
                  difference = mc.field_71439_g.func_174818_b(position) - target.func_174818_b(position);
               } while(!(difference > 1.0D) && !(difference < -1.0D));

               toRemove.add(position);
            }
         case FAR:
            positions.sort(Comparator.comparingDouble((pos2) -> {
               return -target.func_174818_b(pos2);
            }));
            pos = (BlockPos)positions.get(0);
            break;
         case SAFE:
            positions.sort(Comparator.comparingInt((pos2) -> {
               return -this.safetyFactor(pos2);
            }));
            pos = (BlockPos)positions.get(0);
         }

         return pos;
      }
   }

   private boolean canPlace(BlockPos pos) {
      if (pos == null) {
         return false;
      } else {
         BlockPos boost = pos.func_177984_a();
         if (this.isGoodMaterial(mc.field_71441_e.func_180495_p(pos).func_177230_c(), (Boolean)this.onOtherHoppers.getValue()) && this.isGoodMaterial(mc.field_71441_e.func_180495_p(boost).func_177230_c(), false)) {
            if ((Boolean)this.raytrace.getValue() && (!BlockUtil.rayTracePlaceCheck(pos, (Boolean)this.raytrace.getValue()) || !BlockUtil.rayTracePlaceCheck(pos, (Boolean)this.raytrace.getValue()))) {
               return false;
            } else if (!this.badEntities(pos) && !this.badEntities(boost)) {
               return (Boolean)this.onOtherHoppers.getValue() && mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockHopper ? true : this.findFacing(pos);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private void check() {
      if (this.currentStep != Auto32k.Step.PRE && this.currentStep != Auto32k.Step.HOPPER && this.hopperPos != null && !(mc.field_71462_r instanceof GuiHopper) && !EntityUtil.holding32k(mc.field_71439_g) && (mc.field_71439_g.func_174818_b(this.hopperPos) > MathUtil.square((Float)this.hopperDistance.getValue()) || mc.field_71441_e.func_180495_p(this.hopperPos).func_177230_c() != Blocks.field_150438_bZ)) {
         this.resetFields();
         if (!(Boolean)this.autoSwitch.getValue() || !(Boolean)this.withBind.getValue() || this.mode.getValue() != Auto32k.Mode.NORMAL) {
            this.disable();
         }
      }

   }

   private void checkState() {
      if ((Boolean)this.checkStatus.getValue() && !this.checkedThisTick && (this.currentStep == Auto32k.Step.HOPPER || this.currentStep == Auto32k.Step.SHULKER || this.currentStep == Auto32k.Step.CLICKHOPPER)) {
         if (this.hopperPos == null || !this.isGoodMaterial(mc.field_71441_e.func_180495_p(this.hopperPos).func_177230_c(), true) || !this.isGoodMaterial(mc.field_71441_e.func_180495_p(this.hopperPos.func_177984_a()).func_177230_c(), false) && !(mc.field_71441_e.func_180495_p(this.hopperPos.func_177984_a()).func_177230_c() instanceof BlockShulkerBox) || this.badEntities(this.hopperPos) || this.badEntities(this.hopperPos.func_177984_a())) {
            if ((Boolean)this.autoSwitch.getValue() && this.mode.getValue() == Auto32k.Mode.NORMAL) {
               if (this.switching) {
                  this.resetFields();
                  if ((Boolean)this.repeatSwitch.getValue()) {
                     this.switching = true;
                  }
               } else {
                  this.resetFields();
               }

               if (!(Boolean)this.withBind.getValue()) {
                  this.disable();
               }
            } else {
               this.disable();
            }

            this.checkedThisTick = true;
         }

      } else {
         this.checkedThisTick = false;
      }
   }

   private void processDispenser32k() {
      if (!this.isOff()) {
         if (this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
            this.check();
            switch(this.currentStep) {
            case PRE:
               this.runDispenserPreStep();
               if (this.currentStep == Auto32k.Step.PRE) {
                  break;
               }
            case HOPPER:
               this.runHopperStep();
               this.currentStep = Auto32k.Step.DISPENSER;
               if (this.actionsThisTick >= (Integer)this.delayDispenser.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
                  break;
               }
            case DISPENSER:
               this.runDispenserStep();
               boolean quickCheck = !mc.field_71441_e.func_180495_p(this.finalDispenserData.getHelpingPos()).func_185904_a().func_76222_j();
               if (this.actionsThisTick >= (Integer)this.delayDispenser.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue()) || this.currentStep != Auto32k.Step.DISPENSER_HELPING && this.currentStep != Auto32k.Step.CLICK_DISPENSER || (Boolean)this.rotate.getValue() && quickCheck) {
                  break;
               }
            case DISPENSER_HELPING:
               this.runDispenserStep();
               if (this.actionsThisTick >= (Integer)this.delayDispenser.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue()) || this.currentStep != Auto32k.Step.CLICK_DISPENSER && this.currentStep != Auto32k.Step.DISPENSER_HELPING || (Boolean)this.rotate.getValue()) {
                  break;
               }
            case CLICK_DISPENSER:
               this.clickDispenser();
               if (this.actionsThisTick >= (Integer)this.delayDispenser.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
                  break;
               }
            case DISPENSER_GUI:
               this.dispenserGui();
               if (this.currentStep == Auto32k.Step.DISPENSER_GUI) {
                  break;
               }
            case REDSTONE:
               this.placeRedstone();
               if (this.actionsThisTick >= (Integer)this.delayDispenser.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
                  break;
               }
            case CLICKHOPPER:
               this.runClickHopper();
               if (this.actionsThisTick >= (Integer)this.delayDispenser.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
                  break;
               }
            case HOPPERGUI:
               this.runHopperGuiStep();
               if (this.actionsThisTick >= (Integer)this.delayDispenser.getValue() && !this.placeTimer.passedMs((long)(Integer)this.delay.getValue())) {
               }
            case SHULKER:
            }
         }

      }
   }

   private void placeRedstone() {
      if (!this.isOff()) {
         if (!this.badEntities(this.hopperPos.func_177984_a()) || mc.field_71441_e.func_180495_p(this.hopperPos.func_177984_a()).func_177230_c() instanceof BlockShulkerBox) {
            this.runPlaceStep(this.finalDispenserData.getRedStonePos(), this.redstoneSlot);
            this.currentStep = Auto32k.Step.CLICKHOPPER;
         }
      }
   }

   private void clickDispenser() {
      if (!this.isOff()) {
         this.clickBlock(this.finalDispenserData.getDispenserPos());
         this.currentStep = Auto32k.Step.DISPENSER_GUI;
      }
   }

   private void dispenserGui() {
      if (!this.isOff()) {
         if (mc.field_71462_r instanceof GuiDispenser) {
            mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71070_bA.field_75152_c, this.shulkerSlot, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
            mc.field_71439_g.func_71053_j();
            this.currentStep = Auto32k.Step.REDSTONE;
         }
      }
   }

   private void clickBlock(BlockPos pos) {
      if (!this.isOff() && pos != null) {
         this.authSneakPacket = true;
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         this.authSneakPacket = false;
         Vec3d hitVec = (new Vec3d(pos)).func_72441_c(0.5D, -0.5D, 0.5D);
         if ((Boolean)this.rotate.getValue()) {
            this.rotateToPos((BlockPos)null, hitVec);
         }

         EnumFacing facing = EnumFacing.UP;
         if (this.finalDispenserData != null && this.finalDispenserData.getDispenserPos() != null && this.finalDispenserData.getDispenserPos().equals(pos) && pos.func_177956_o() > (new BlockPos(mc.field_71439_g.func_174791_d())).func_177984_a().func_177956_o()) {
            facing = EnumFacing.DOWN;
         }

         BlockUtil.rightClickBlock(pos, hitVec, this.shulkerSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, facing, (Boolean)this.packet.getValue());
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         mc.field_71467_ac = 4;
         ++this.actionsThisTick;
      }
   }

   private void runDispenserStep() {
      if (!this.isOff()) {
         if (this.finalDispenserData != null && this.finalDispenserData.getDispenserPos() != null && this.finalDispenserData.getHelpingPos() != null) {
            if (this.currentStep == Auto32k.Step.DISPENSER || this.currentStep == Auto32k.Step.DISPENSER_HELPING) {
               BlockPos dispenserPos = this.finalDispenserData.getDispenserPos();
               BlockPos helpingPos = this.finalDispenserData.getHelpingPos();
               if (!mc.field_71441_e.func_180495_p(helpingPos).func_185904_a().func_76222_j()) {
                  this.placeDispenserAgainstBlock(dispenserPos, helpingPos);
                  ++this.actionsThisTick;
                  this.currentStep = Auto32k.Step.CLICK_DISPENSER;
               } else {
                  this.currentStep = Auto32k.Step.DISPENSER_HELPING;
                  EnumFacing facing = EnumFacing.DOWN;
                  boolean foundHelpingPos = false;
                  EnumFacing[] var5 = EnumFacing.values();
                  int var6 = var5.length;

                  for(int var7 = 0; var7 < var6; ++var7) {
                     EnumFacing enumFacing = var5[var7];
                     BlockPos position = helpingPos.func_177972_a(enumFacing);
                     if (!position.equals(this.hopperPos) && !position.equals(this.hopperPos.func_177984_a()) && !position.equals(dispenserPos) && !position.equals(this.finalDispenserData.getRedStonePos()) && mc.field_71439_g.func_174818_b(position) <= MathUtil.square((Float)this.range.getValue()) && (!(Boolean)this.raytrace.getValue() || BlockUtil.rayTracePlaceCheck(position, (Boolean)this.raytrace.getValue())) && !mc.field_71441_e.func_180495_p(position).func_185904_a().func_76222_j()) {
                        foundHelpingPos = true;
                        facing = enumFacing;
                        break;
                     }
                  }

                  if (!foundHelpingPos) {
                     this.disable();
                  } else {
                     BlockPos neighbour = helpingPos.func_177972_a(facing);
                     EnumFacing opposite = facing.func_176734_d();
                     Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                     Block neighbourBlock = mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
                     this.authSneakPacket = true;
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                     this.authSneakPacket = false;
                     if ((Boolean)this.rotate.getValue()) {
                        if ((Integer)this.blocksPerPlace.getValue() > 1) {
                           float[] angle = RotationUtil.getLegitRotations(hitVec);
                           if ((Boolean)this.extra.getValue()) {
                              RotationUtil.faceYawAndPitch(angle[0], angle[1]);
                           }
                        } else {
                           this.rotateToPos((BlockPos)null, hitVec);
                        }
                     }

                     int slot = (Boolean)this.preferObby.getValue() && this.obbySlot != -1 ? this.obbySlot : this.dispenserSlot;
                     InventoryUtil.switchToHotbarSlot(slot, false);
                     BlockUtil.rightClickBlock(neighbour, hitVec, slot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, (Boolean)this.packet.getValue());
                     this.authSneakPacket = true;
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
                     this.authSneakPacket = false;
                     this.placeTimer.reset();
                     ++this.actionsThisTick;
                  }
               }
            }
         } else {
            this.resetFields();
         }
      }
   }

   private void placeDispenserAgainstBlock(BlockPos dispenserPos, BlockPos helpingPos) {
      if (!this.isOff()) {
         EnumFacing facing = EnumFacing.DOWN;
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         EnumFacing enumFacing;
         for(int var6 = 0; var6 < var5; ++var6) {
            enumFacing = var4[var6];
            BlockPos position = dispenserPos.func_177972_a(enumFacing);
            if (position.equals(helpingPos)) {
               facing = enumFacing;
               break;
            }
         }

         EnumFacing opposite = facing.func_176734_d();
         Vec3d hitVec = (new Vec3d(helpingPos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         Block neighbourBlock = mc.field_71441_e.func_180495_p(helpingPos).func_177230_c();
         this.authSneakPacket = true;
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
         this.authSneakPacket = false;
         enumFacing = null;
         EnumFacing facings = EnumFacing.UP;
         float[] rotations;
         Vec3d rotationVec;
         if ((Boolean)this.rotate.getValue()) {
            if ((Integer)this.blocksPerPlace.getValue() > 1) {
               rotations = RotationUtil.getLegitRotations(hitVec);
               if ((Boolean)this.extra.getValue()) {
                  RotationUtil.faceYawAndPitch(rotations[0], rotations[1]);
               }
            } else {
               this.rotateToPos((BlockPos)null, hitVec);
            }

            rotationVec = (new Vec3d(helpingPos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         } else if (dispenserPos.func_177956_o() <= (new BlockPos(mc.field_71439_g.func_174791_d())).func_177984_a().func_177956_o()) {
            EnumFacing[] var19 = EnumFacing.values();
            int var10 = var19.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               EnumFacing enumFacing = var19[var11];
               BlockPos position = this.hopperPos.func_177984_a().func_177972_a(enumFacing);
               if (position.equals(dispenserPos)) {
                  facings = enumFacing;
                  break;
               }
            }

            rotations = RotationUtil.simpleFacing(facings);
            this.yaw = rotations[0];
            this.pitch = rotations[1];
            this.spoof = true;
         } else {
            rotations = RotationUtil.simpleFacing(facings);
            this.yaw = rotations[0];
            this.pitch = rotations[1];
            this.spoof = true;
         }

         rotationVec = (new Vec3d(helpingPos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         rotations = RotationUtil.simpleFacing(facings);
         float[] angle = RotationUtil.getLegitRotations(hitVec);
         if ((Boolean)this.superPacket.getValue()) {
            RotationUtil.faceYawAndPitch(!(Boolean)this.rotate.getValue() ? rotations[0] : angle[0], !(Boolean)this.rotate.getValue() ? rotations[1] : angle[1]);
         }

         InventoryUtil.switchToHotbarSlot(this.dispenserSlot, false);
         BlockUtil.rightClickBlock(helpingPos, rotationVec, this.dispenserSlot == -2 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, (Boolean)this.packet.getValue());
         this.authSneakPacket = true;
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         this.authSneakPacket = false;
         this.placeTimer.reset();
         ++this.actionsThisTick;
         this.currentStep = Auto32k.Step.CLICK_DISPENSER;
      }
   }

   private void runDispenserPreStep() {
      if (!this.isOff()) {
         if (Freecam.getInstance().isOn() && !(Boolean)this.freecam.getValue()) {
            if ((Boolean)this.messages.getValue()) {
               Command.sendMessage("§c<Auto32k> Disable Freecam.");
            }

            this.disable();
         } else {
            this.lastHotbarSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
            this.shulkerSlot = InventoryUtil.findBlockSlotInventory(BlockShulkerBox.class, false, false);
            this.dispenserSlot = InventoryUtil.findHotbarBlock(BlockDispenser.class);
            this.redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.field_150451_bX);
            this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
            if (mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d();
               if (block instanceof BlockHopper) {
                  this.hopperSlot = -2;
               } else if (block instanceof BlockDispenser) {
                  this.dispenserSlot = -2;
               } else if (block == Blocks.field_150451_bX) {
                  this.redstoneSlot = -2;
               } else if (block instanceof BlockObsidian) {
                  this.obbySlot = -2;
               }
            }

            if (this.shulkerSlot != -1 && this.hopperSlot != -1 && this.dispenserSlot != -1 && this.redstoneSlot != -1) {
               this.finalDispenserData = this.findBestPos();
               if (this.finalDispenserData.isPlaceable()) {
                  this.hopperPos = this.finalDispenserData.getHopperPos();
                  if (mc.field_71441_e.func_180495_p(this.hopperPos).func_177230_c() instanceof BlockHopper) {
                     this.currentStep = Auto32k.Step.DISPENSER;
                  } else {
                     this.currentStep = Auto32k.Step.HOPPER;
                  }
               } else {
                  if ((Boolean)this.messages.getValue()) {
                     Command.sendMessage("§c<Auto32k> Block not found.");
                  }

                  this.disable();
               }

            } else {
               if ((Boolean)this.messages.getValue()) {
                  Command.sendMessage("§c<Auto32k> Materials not found.");
               }

               this.disable();
            }
         }
      }
   }

   private Auto32k.DispenserData findBestPos() {
      Auto32k.PlaceType type = (Auto32k.PlaceType)this.placeType.getValue();
      this.target = EntityUtil.getClosestEnemy((Double)this.targetRange.getValue());
      if (this.target == null) {
         type = this.placeType.getValue() == Auto32k.PlaceType.MOUSE ? Auto32k.PlaceType.MOUSE : Auto32k.PlaceType.CLOSE;
      }

      NonNullList positions;
      Auto32k.DispenserData data;
      positions = NonNullList.func_191196_a();
      positions.addAll(BlockUtil.getSphere(EntityUtil.getPlayerPos(mc.field_71439_g), (Float)this.range.getValue(), ((Float)this.range.getValue()).intValue(), false, true, 0));
      data = new Auto32k.DispenserData();
      label54:
      switch(type) {
      case MOUSE:
         if (mc.field_71476_x != null && mc.field_71476_x.field_72313_a == Type.BLOCK) {
            BlockPos mousePos = mc.field_71476_x.func_178782_a();
            if (mousePos != null) {
               data = this.analyzePos(mousePos);
               if (!data.isPlaceable()) {
                  data = this.analyzePos(mousePos.func_177984_a());
               }
            }
         }

         if (data.isPlaceable()) {
            return data;
         }
      case CLOSE:
         positions.sort(Comparator.comparingDouble((pos2) -> {
            return mc.field_71439_g.func_174818_b(pos2);
         }));
         break;
      case ENEMY:
         EntityPlayer var10001 = this.target;
         var10001.getClass();
         positions.sort(Comparator.comparingDouble(var10001::func_174818_b));
         break;
      case MIDDLE:
         List<BlockPos> toRemove = new ArrayList();
         NonNullList<BlockPos> copy = NonNullList.func_191196_a();
         copy.addAll(positions);
         Iterator var6 = copy.iterator();

         while(true) {
            BlockPos position;
            double difference;
            do {
               if (!var6.hasNext()) {
                  copy.removeAll(toRemove);
                  if (copy.isEmpty()) {
                     copy.addAll(positions);
                  }

                  copy.sort(Comparator.comparingDouble((pos2) -> {
                     return mc.field_71439_g.func_174818_b(pos2);
                  }));
                  break label54;
               }

               position = (BlockPos)var6.next();
               difference = mc.field_71439_g.func_174818_b(position) - this.target.func_174818_b(position);
            } while(!(difference > 1.0D) && !(difference < -1.0D));

            toRemove.add(position);
         }
      case FAR:
         positions.sort(Comparator.comparingDouble((pos2) -> {
            return -this.target.func_174818_b(pos2);
         }));
         break;
      case SAFE:
         positions.sort(Comparator.comparingInt((pos2) -> {
            return -this.safetyFactor(pos2);
         }));
      }

      data = this.findData(positions);
      return data;
   }

   private Auto32k.DispenserData findData(NonNullList<BlockPos> positions) {
      Iterator var2 = positions.iterator();

      Auto32k.DispenserData data;
      do {
         if (!var2.hasNext()) {
            return new Auto32k.DispenserData();
         }

         BlockPos position = (BlockPos)var2.next();
         data = this.analyzePos(position);
      } while(!data.isPlaceable());

      return data;
   }

   private Auto32k.DispenserData analyzePos(BlockPos pos) {
      Auto32k.DispenserData data = new Auto32k.DispenserData(pos);
      if (pos == null) {
         return data;
      } else if (this.isGoodMaterial(mc.field_71441_e.func_180495_p(pos).func_177230_c(), (Boolean)this.onOtherHoppers.getValue()) && this.isGoodMaterial(mc.field_71441_e.func_180495_p(pos.func_177984_a()).func_177230_c(), false)) {
         if ((Boolean)this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(pos, (Boolean)this.raytrace.getValue())) {
            return data;
         } else if (!this.badEntities(pos) && !this.badEntities(pos.func_177984_a())) {
            if (this.hasAdjancedRedstone(pos)) {
               return data;
            } else if (!this.findFacing(pos)) {
               return data;
            } else {
               BlockPos[] otherPositions = this.checkForDispenserPos(pos);
               if (otherPositions[0] != null && otherPositions[1] != null && otherPositions[2] != null) {
                  data.setDispenserPos(otherPositions[0]);
                  data.setRedStonePos(otherPositions[1]);
                  data.setHelpingPos(otherPositions[2]);
                  data.setPlaceable(true);
                  return data;
               } else {
                  return data;
               }
            }
         } else {
            return data;
         }
      } else {
         return data;
      }
   }

   private boolean findFacing(BlockPos pos) {
      boolean foundFacing = false;
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing facing = var3[var5];
         if (facing != EnumFacing.UP) {
            if (facing == EnumFacing.DOWN && (Boolean)this.antiHopper.getValue() && mc.field_71441_e.func_180495_p(pos.func_177972_a(facing)).func_177230_c() == Blocks.field_150438_bZ) {
               foundFacing = false;
               break;
            }

            if (!mc.field_71441_e.func_180495_p(pos.func_177972_a(facing)).func_185904_a().func_76222_j() && (!(Boolean)this.antiHopper.getValue() || mc.field_71441_e.func_180495_p(pos.func_177972_a(facing)).func_177230_c() != Blocks.field_150438_bZ)) {
               foundFacing = true;
            }
         }
      }

      return foundFacing;
   }

   private BlockPos[] checkForDispenserPos(BlockPos posIn) {
      BlockPos[] pos = new BlockPos[3];
      BlockPos playerPos = new BlockPos(mc.field_71439_g.func_174791_d());
      if (posIn.func_177956_o() < playerPos.func_177977_b().func_177956_o()) {
         return pos;
      } else {
         List<BlockPos> possiblePositions = this.getDispenserPositions(posIn);
         if (posIn.func_177956_o() < playerPos.func_177956_o()) {
            possiblePositions.remove(posIn.func_177984_a().func_177984_a());
         } else if (posIn.func_177956_o() > playerPos.func_177956_o()) {
            possiblePositions.remove(posIn.func_177976_e().func_177984_a());
            possiblePositions.remove(posIn.func_177978_c().func_177984_a());
            possiblePositions.remove(posIn.func_177968_d().func_177984_a());
            possiblePositions.remove(posIn.func_177974_f().func_177984_a());
         }

         if (!(Boolean)this.rotate.getValue() && !(Boolean)this.simulate.getValue()) {
            possiblePositions.removeIf((positionx) -> {
               return mc.field_71439_g.func_174818_b(positionx) > MathUtil.square((Float)this.range.getValue());
            });
            possiblePositions.removeIf((positionx) -> {
               return !this.isGoodMaterial(mc.field_71441_e.func_180495_p(positionx).func_177230_c(), false);
            });
            possiblePositions.removeIf((positionx) -> {
               return (Boolean)this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(positionx, (Boolean)this.raytrace.getValue());
            });
            possiblePositions.removeIf(this::badEntities);
            possiblePositions.removeIf(this::hasAdjancedRedstone);
            Iterator var9 = possiblePositions.iterator();

            while(var9.hasNext()) {
               BlockPos position = (BlockPos)var9.next();
               List<BlockPos> possibleRedStonePositions = this.checkRedStone(position, posIn);
               if (!possiblePositions.isEmpty()) {
                  BlockPos[] helpingStuff = this.getHelpingPos(position, posIn, possibleRedStonePositions);
                  if (helpingStuff != null && helpingStuff[0] != null && helpingStuff[1] != null) {
                     pos[0] = position;
                     pos[1] = helpingStuff[1];
                     pos[2] = helpingStuff[0];
                     break;
                  }
               }
            }
         } else {
            possiblePositions.sort(Comparator.comparingDouble((pos2) -> {
               return -mc.field_71439_g.func_174818_b(pos2);
            }));
            BlockPos posToCheck = (BlockPos)possiblePositions.get(0);
            if (!this.isGoodMaterial(mc.field_71441_e.func_180495_p(posToCheck).func_177230_c(), false)) {
               return pos;
            }

            if (mc.field_71439_g.func_174818_b(posToCheck) > MathUtil.square((Float)this.range.getValue())) {
               return pos;
            }

            if ((Boolean)this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(posToCheck, (Boolean)this.raytrace.getValue())) {
               return pos;
            }

            if (this.badEntities(posToCheck)) {
               return pos;
            }

            if (this.hasAdjancedRedstone(posToCheck)) {
               return pos;
            }

            List<BlockPos> possibleRedStonePositions = this.checkRedStone(posToCheck, posIn);
            if (possiblePositions.isEmpty()) {
               return pos;
            }

            BlockPos[] helpingStuff = this.getHelpingPos(posToCheck, posIn, possibleRedStonePositions);
            if (helpingStuff != null && helpingStuff[0] != null && helpingStuff[1] != null) {
               pos[0] = posToCheck;
               pos[1] = helpingStuff[1];
               pos[2] = helpingStuff[0];
            }
         }

         return pos;
      }
   }

   private List<BlockPos> checkRedStone(BlockPos pos, BlockPos hopperPos) {
      List<BlockPos> toCheck = new ArrayList();
      EnumFacing[] var4 = EnumFacing.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumFacing facing = var4[var6];
         toCheck.add(pos.func_177972_a(facing));
      }

      toCheck.removeIf((position) -> {
         return position.equals(hopperPos.func_177984_a());
      });
      toCheck.removeIf((position) -> {
         return mc.field_71439_g.func_174818_b(position) > MathUtil.square((Float)this.range.getValue());
      });
      toCheck.removeIf((position) -> {
         return !this.isGoodMaterial(mc.field_71441_e.func_180495_p(position).func_177230_c(), false);
      });
      toCheck.removeIf((position) -> {
         return (Boolean)this.raytrace.getValue() && !BlockUtil.rayTracePlaceCheck(position, (Boolean)this.raytrace.getValue());
      });
      toCheck.removeIf(this::badEntities);
      toCheck.sort(Comparator.comparingDouble((pos2) -> {
         return mc.field_71439_g.func_174818_b(pos2);
      }));
      return toCheck;
   }

   private boolean hasAdjancedRedstone(BlockPos pos) {
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing facing = var2[var4];
         BlockPos position = pos.func_177972_a(facing);
         if (mc.field_71441_e.func_180495_p(position).func_177230_c() == Blocks.field_150451_bX || mc.field_71441_e.func_180495_p(position).func_177230_c() == Blocks.field_150429_aA) {
            return true;
         }
      }

      return false;
   }

   private List<BlockPos> getDispenserPositions(BlockPos pos) {
      List<BlockPos> list = new ArrayList();
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing facing = var3[var5];
         if (facing != EnumFacing.DOWN) {
            list.add(pos.func_177972_a(facing).func_177984_a());
         }
      }

      return list;
   }

   private BlockPos[] getHelpingPos(BlockPos pos, BlockPos hopperPos, List<BlockPos> redStonePositions) {
      BlockPos[] result = new BlockPos[2];
      List<BlockPos> possiblePositions = new ArrayList();
      if (redStonePositions.isEmpty()) {
         return null;
      } else {
         EnumFacing[] var6 = EnumFacing.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            EnumFacing facing = var6[var8];
            BlockPos facingPos = pos.func_177972_a(facing);
            if (!facingPos.equals(hopperPos) && !facingPos.equals(hopperPos.func_177984_a())) {
               if (!mc.field_71441_e.func_180495_p(facingPos).func_185904_a().func_76222_j()) {
                  if (!redStonePositions.contains(facingPos)) {
                     result[0] = facingPos;
                     result[1] = (BlockPos)redStonePositions.get(0);
                     return result;
                  }

                  redStonePositions.remove(facingPos);
                  if (!redStonePositions.isEmpty()) {
                     result[0] = facingPos;
                     result[1] = (BlockPos)redStonePositions.get(0);
                     return result;
                  }

                  redStonePositions.add(facingPos);
               } else {
                  EnumFacing[] var11 = EnumFacing.values();
                  int var12 = var11.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     EnumFacing facing1 = var11[var13];
                     BlockPos facingPos1 = facingPos.func_177972_a(facing1);
                     if (!facingPos1.equals(hopperPos) && !facingPos1.equals(hopperPos.func_177984_a()) && !facingPos1.equals(pos) && !mc.field_71441_e.func_180495_p(facingPos1).func_185904_a().func_76222_j()) {
                        if (redStonePositions.contains(facingPos)) {
                           redStonePositions.remove(facingPos);
                           if (redStonePositions.isEmpty()) {
                              redStonePositions.add(facingPos);
                           } else {
                              possiblePositions.add(facingPos);
                           }
                        } else {
                           possiblePositions.add(facingPos);
                        }
                     }
                  }
               }
            }
         }

         possiblePositions.removeIf((position) -> {
            return mc.field_71439_g.func_174818_b(position) > MathUtil.square((Float)this.range.getValue());
         });
         possiblePositions.sort(Comparator.comparingDouble((position) -> {
            return mc.field_71439_g.func_174818_b(position);
         }));
         if (!possiblePositions.isEmpty()) {
            redStonePositions.remove(possiblePositions.get(0));
            if (!redStonePositions.isEmpty()) {
               result[0] = (BlockPos)possiblePositions.get(0);
               result[1] = (BlockPos)redStonePositions.get(0);
            }

            return result;
         } else {
            return null;
         }
      }
   }

   private void rotateToPos(BlockPos pos, Vec3d vec3d) {
      float[] angle;
      if (vec3d == null) {
         angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() - 0.5F), (double)((float)pos.func_177952_p() + 0.5F)));
      } else {
         angle = RotationUtil.getLegitRotations(vec3d);
      }

      this.yaw = angle[0];
      this.pitch = angle[1];
      this.spoof = true;
   }

   private boolean isGoodMaterial(Block block, boolean allowHopper) {
      return block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow || allowHopper && block instanceof BlockHopper;
   }

   private void resetFields() {
      this.shouldDisable = false;
      this.spoof = false;
      this.switching = false;
      this.lastHotbarSlot = -1;
      this.shulkerSlot = -1;
      this.hopperSlot = -1;
      this.hopperPos = null;
      this.target = null;
      this.currentStep = Auto32k.Step.PRE;
      this.obbySlot = -1;
      this.dispenserSlot = -1;
      this.redstoneSlot = -1;
      this.finalDispenserData = null;
      this.actionsThisTick = 0;
      this.rotationprepared = false;
   }

   private boolean badEntities(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity instanceof EntityExpBottle || entity instanceof EntityItem || entity instanceof EntityXPOrb);

      return true;
   }

   private int safetyFactor(BlockPos pos) {
      return this.safety(pos) + this.safety(pos.func_177984_a());
   }

   private int safety(BlockPos pos) {
      int safety = 0;
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing facing = var3[var5];
         if (!mc.field_71441_e.func_180495_p(pos.func_177972_a(facing)).func_185904_a().func_76222_j()) {
            ++safety;
         }
      }

      return safety;
   }

   public static enum Step {
      PRE,
      HOPPER,
      SHULKER,
      CLICKHOPPER,
      HOPPERGUI,
      DISPENSER_HELPING,
      DISPENSER_GUI,
      DISPENSER,
      CLICK_DISPENSER,
      REDSTONE;
   }

   public static enum Mode {
      NORMAL,
      DISPENSER;
   }

   public static enum PlaceType {
      MOUSE,
      CLOSE,
      ENEMY,
      MIDDLE,
      FAR,
      SAFE;
   }

   public static class DispenserData {
      private BlockPos dispenserPos;
      private BlockPos redStonePos;
      private BlockPos hopperPos;
      private BlockPos helpingPos;
      private boolean isPlaceable = false;

      public DispenserData() {
      }

      public DispenserData(BlockPos pos) {
         this.hopperPos = pos;
      }

      public void setPlaceable(boolean placeable) {
         this.isPlaceable = placeable;
      }

      public boolean isPlaceable() {
         return this.dispenserPos != null && this.hopperPos != null && this.redStonePos != null && this.helpingPos != null;
      }

      public BlockPos getDispenserPos() {
         return this.dispenserPos;
      }

      public void setDispenserPos(BlockPos dispenserPos) {
         this.dispenserPos = dispenserPos;
      }

      public BlockPos getRedStonePos() {
         return this.redStonePos;
      }

      public void setRedStonePos(BlockPos redStonePos) {
         this.redStonePos = redStonePos;
      }

      public BlockPos getHopperPos() {
         return this.hopperPos;
      }

      public void setHopperPos(BlockPos hopperPos) {
         this.hopperPos = hopperPos;
      }

      public BlockPos getHelpingPos() {
         return this.helpingPos;
      }

      public void setHelpingPos(BlockPos helpingPos) {
         this.helpingPos = helpingPos;
      }
   }
}
