package me.earth.phobos.features.modules.combat;

import com.mojang.authlib.GameProfile;
import io.netty.util.internal.ConcurrentSet;
import java.awt.Color;
import java.lang.Thread.State;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.misc.NoSoundLag;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AutoCrystal extends Module {
   private final Setting<AutoCrystal.Settings> setting;
   public Setting<AutoCrystal.Raytrace> raytrace;
   public Setting<Boolean> place;
   public Setting<Integer> placeDelay;
   public Setting<Float> placeRange;
   public Setting<Float> minDamage;
   public Setting<Float> maxSelfPlace;
   public Setting<Integer> wasteAmount;
   public Setting<Boolean> wasteMinDmgCount;
   public Setting<Float> facePlace;
   public Setting<Float> placetrace;
   public Setting<Boolean> antiSurround;
   public Setting<Boolean> limitFacePlace;
   public Setting<Boolean> oneDot15;
   public Setting<Boolean> doublePop;
   public Setting<Double> popHealth;
   public Setting<Float> popDamage;
   public Setting<Integer> popTime;
   public Setting<Boolean> explode;
   public Setting<AutoCrystal.Switch> switchMode;
   public Setting<Integer> breakDelay;
   public Setting<Float> breakRange;
   public Setting<Integer> packets;
   public Setting<Float> maxSelfBreak;
   public Setting<Float> breaktrace;
   public Setting<Boolean> manual;
   public Setting<Boolean> manualMinDmg;
   public Setting<Integer> manualBreak;
   public Setting<Boolean> sync;
   public Setting<Boolean> instant;
   public Setting<AutoCrystal.PredictTimer> instantTimer;
   public Setting<Boolean> resetBreakTimer;
   public Setting<Integer> predictDelay;
   public Setting<Boolean> predictCalc;
   public Setting<Boolean> superSafe;
   public Setting<Boolean> antiCommit;
   public Setting<Boolean> render;
   public Setting<Boolean> colorSync;
   public Setting<Boolean> box;
   public Setting<Boolean> outline;
   public Setting<Boolean> text;
   private final Setting<Integer> red;
   private final Setting<Integer> green;
   private final Setting<Integer> blue;
   private final Setting<Integer> alpha;
   private final Setting<Integer> boxAlpha;
   private final Setting<Float> lineWidth;
   public Setting<Boolean> customOutline;
   private final Setting<Integer> cRed;
   private final Setting<Integer> cGreen;
   private final Setting<Integer> cBlue;
   private final Setting<Integer> cAlpha;
   public Setting<Boolean> holdFacePlace;
   public Setting<Boolean> holdFaceBreak;
   public Setting<Boolean> slowFaceBreak;
   public Setting<Boolean> actualSlowBreak;
   public Setting<Integer> facePlaceSpeed;
   public Setting<Boolean> antiNaked;
   public Setting<Float> range;
   public Setting<AutoCrystal.Target> targetMode;
   public Setting<Integer> minArmor;
   private final Setting<Integer> switchCooldown;
   public Setting<AutoCrystal.AutoSwitch> autoSwitch;
   public Setting<Bind> switchBind;
   public Setting<Boolean> offhandSwitch;
   public Setting<Boolean> switchBack;
   public Setting<Boolean> lethalSwitch;
   public Setting<Boolean> mineSwitch;
   public Setting<AutoCrystal.Rotate> rotate;
   public Setting<Boolean> suicide;
   public Setting<Boolean> webAttack;
   public Setting<Boolean> fullCalc;
   public Setting<Boolean> sound;
   public Setting<Float> soundRange;
   public Setting<Float> soundPlayer;
   public Setting<Boolean> soundConfirm;
   public Setting<Boolean> extraSelfCalc;
   public Setting<AutoCrystal.AntiFriendPop> antiFriendPop;
   public Setting<Boolean> noCount;
   public Setting<Boolean> calcEvenIfNoDamage;
   public Setting<Boolean> predictFriendDmg;
   public Setting<Float> minMinDmg;
   public final Setting<Boolean> attackOppositeHand;
   public final Setting<Boolean> removeAfterAttack;
   public Setting<Boolean> breakSwing;
   public Setting<Boolean> placeSwing;
   public Setting<Boolean> exactHand;
   public Setting<Boolean> justRender;
   public Setting<Boolean> fakeSwing;
   public Setting<AutoCrystal.Logic> logic;
   public Setting<AutoCrystal.DamageSync> damageSync;
   public Setting<Integer> damageSyncTime;
   public Setting<Float> dropOff;
   public Setting<Integer> confirm;
   public Setting<Boolean> syncedFeetPlace;
   public Setting<Boolean> fullSync;
   public Setting<Boolean> syncCount;
   public Setting<Boolean> hyperSync;
   public Setting<Boolean> gigaSync;
   public Setting<Boolean> syncySync;
   public Setting<Boolean> enormousSync;
   public Setting<Boolean> holySync;
   private final Setting<Integer> eventMode;
   public Setting<Boolean> rotateFirst;
   public Setting<AutoCrystal.ThreadMode> threadMode;
   public Setting<Integer> threadDelay;
   public Setting<Boolean> syncThreadBool;
   public Setting<Integer> syncThreads;
   public Setting<Boolean> predictPos;
   public Setting<Boolean> renderExtrapolation;
   public Setting<Integer> predictTicks;
   public Setting<Integer> rotations;
   public Setting<Boolean> predictRotate;
   public Setting<Float> predictOffset;
   public Setting<Boolean> doublePopOnDamage;
   private Queue<Entity> attackList;
   private Map<Entity, Float> crystalMap;
   private final Timer switchTimer;
   private final Timer manualTimer;
   private final Timer breakTimer;
   private final Timer placeTimer;
   private final Timer syncTimer;
   private final Timer predictTimer;
   public static EntityPlayer target = null;
   private Entity efficientTarget;
   private double currentDamage;
   private double renderDamage;
   private double lastDamage;
   private boolean didRotation;
   private boolean switching;
   private BlockPos placePos;
   private BlockPos renderPos;
   private boolean mainHand;
   public boolean rotating;
   private boolean offHand;
   private int crystalCount;
   private int minDmgCount;
   private int lastSlot;
   private float yaw;
   private float pitch;
   private BlockPos webPos;
   private final Timer renderTimer;
   private BlockPos lastPos;
   public static Set<BlockPos> lowDmgPos = new ConcurrentSet();
   public static Set<BlockPos> placedPos = new HashSet();
   public static Set<BlockPos> brokenPos = new HashSet();
   private boolean posConfirmed;
   private boolean foundDoublePop;
   private int rotationPacketsSpoofed;
   private final AtomicBoolean shouldInterrupt;
   private ScheduledExecutorService executor;
   private final Timer syncroTimer;
   private Thread thread;
   private EntityPlayer currentSyncTarget;
   private BlockPos syncedPlayerPos;
   private BlockPos syncedCrystalPos;
   private static AutoCrystal instance;
   private final Map<EntityPlayer, Timer> totemPops;
   private final Queue<CPacketUseEntity> packetUseEntities;
   private AutoCrystal.PlaceInfo placeInfo;
   private final AtomicBoolean threadOngoing;
   public final Timer threadTimer;
   private boolean addTolowDmg;

   public AutoCrystal() {
      super("AutoCrystal", "Best CA on the market", Module.Category.COMBAT, true, false, false);
      this.setting = this.register(new Setting("Settings", AutoCrystal.Settings.PLACE));
      this.raytrace = this.register(new Setting("Raytrace", AutoCrystal.Raytrace.NONE, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.place = this.register(new Setting("Place", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE;
      }));
      this.placeDelay = this.register(new Setting("PlaceDelay", 25, 0, 500, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.placeRange = this.register(new Setting("PlaceRange", 6.0F, 0.0F, 10.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.minDamage = this.register(new Setting("MinDamage", 7.0F, 0.1F, 20.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.maxSelfPlace = this.register(new Setting("MaxSelfPlace", 10.0F, 0.1F, 36.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.wasteAmount = this.register(new Setting("WasteAmount", 2, 1, 5, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.wasteMinDmgCount = this.register(new Setting("CountMinDmg", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.facePlace = this.register(new Setting("FacePlace", 8.0F, 0.1F, 20.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.placetrace = this.register(new Setting("Placetrace", 4.5F, 0.0F, 10.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue() && this.raytrace.getValue() != AutoCrystal.Raytrace.NONE && this.raytrace.getValue() != AutoCrystal.Raytrace.BREAK;
      }));
      this.antiSurround = this.register(new Setting("AntiSurround", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.limitFacePlace = this.register(new Setting("LimitFacePlace", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.oneDot15 = this.register(new Setting("1.15", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.doublePop = this.register(new Setting("AntiTotem", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue();
      }));
      this.popHealth = this.register(new Setting("PopHealth", 1.0D, 0.0D, 3.0D, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue() && (Boolean)this.doublePop.getValue();
      }));
      this.popDamage = this.register(new Setting("PopDamage", 4.0F, 0.0F, 6.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue() && (Boolean)this.doublePop.getValue();
      }));
      this.popTime = this.register(new Setting("PopTime", 500, 0, 1000, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue() && (Boolean)this.doublePop.getValue();
      }));
      this.explode = this.register(new Setting("Break", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK;
      }));
      this.switchMode = this.register(new Setting("Attack", AutoCrystal.Switch.BREAKSLOT, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue();
      }));
      this.breakDelay = this.register(new Setting("BreakDelay", 50, 0, 500, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue();
      }));
      this.breakRange = this.register(new Setting("BreakRange", 6.0F, 0.0F, 10.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue();
      }));
      this.packets = this.register(new Setting("Packets", 1, 1, 6, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue();
      }));
      this.maxSelfBreak = this.register(new Setting("MaxSelfBreak", 10.0F, 0.1F, 36.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue();
      }));
      this.breaktrace = this.register(new Setting("Breaktrace", 4.5F, 0.0F, 10.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && this.raytrace.getValue() != AutoCrystal.Raytrace.NONE && this.raytrace.getValue() != AutoCrystal.Raytrace.PLACE;
      }));
      this.manual = this.register(new Setting("Manual", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK;
      }));
      this.manualMinDmg = this.register(new Setting("ManMinDmg", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.manual.getValue();
      }));
      this.manualBreak = this.register(new Setting("ManualDelay", 500, 0, 500, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.manual.getValue();
      }));
      this.sync = this.register(new Setting("Sync", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && ((Boolean)this.explode.getValue() || (Boolean)this.manual.getValue());
      }));
      this.instant = this.register(new Setting("Predict", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && (Boolean)this.place.getValue();
      }));
      this.instantTimer = this.register(new Setting("PredictTimer", AutoCrystal.PredictTimer.NONE, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && (Boolean)this.place.getValue() && (Boolean)this.instant.getValue();
      }));
      this.resetBreakTimer = this.register(new Setting("ResetBreakTimer", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && (Boolean)this.place.getValue() && (Boolean)this.instant.getValue();
      }));
      this.predictDelay = this.register(new Setting("PredictDelay", 12, 0, 500, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && (Boolean)this.place.getValue() && (Boolean)this.instant.getValue() && this.instantTimer.getValue() == AutoCrystal.PredictTimer.PREDICT;
      }));
      this.predictCalc = this.register(new Setting("PredictCalc", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && (Boolean)this.place.getValue() && (Boolean)this.instant.getValue();
      }));
      this.superSafe = this.register(new Setting("SuperSafe", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && (Boolean)this.place.getValue() && (Boolean)this.instant.getValue();
      }));
      this.antiCommit = this.register(new Setting("AntiOverCommit", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.BREAK && (Boolean)this.explode.getValue() && (Boolean)this.place.getValue() && (Boolean)this.instant.getValue();
      }));
      this.render = this.register(new Setting("Render", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER;
      }));
      this.colorSync = this.register(new Setting("CSync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER;
      }));
      this.box = this.register(new Setting("Box", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue();
      }));
      this.outline = this.register(new Setting("Outline", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue();
      }));
      this.text = this.register(new Setting("Text", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue();
      }));
      this.red = this.register(new Setting("Red", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue();
      }));
      this.green = this.register(new Setting("Green", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue();
      }));
      this.blue = this.register(new Setting("Blue", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue();
      }));
      this.alpha = this.register(new Setting("Alpha", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue();
      }));
      this.boxAlpha = this.register(new Setting("BoxAlpha", 125, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue() && (Boolean)this.box.getValue();
      }));
      this.lineWidth = this.register(new Setting("LineWidth", 1.5F, 0.1F, 5.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue() && (Boolean)this.outline.getValue();
      }));
      this.customOutline = this.register(new Setting("CustomLine", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue() && (Boolean)this.outline.getValue();
      }));
      this.cRed = this.register(new Setting("OL-Red", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue() && (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
      }));
      this.cGreen = this.register(new Setting("OL-Green", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue() && (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
      }));
      this.cBlue = this.register(new Setting("OL-Blue", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue() && (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
      }));
      this.cAlpha = this.register(new Setting("OL-Alpha", 255, 0, 255, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.RENDER && (Boolean)this.render.getValue() && (Boolean)this.customOutline.getValue() && (Boolean)this.outline.getValue();
      }));
      this.holdFacePlace = this.register(new Setting("HoldFacePlace", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.holdFaceBreak = this.register(new Setting("HoldSlowBreak", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && (Boolean)this.holdFacePlace.getValue();
      }));
      this.slowFaceBreak = this.register(new Setting("SlowFaceBreak", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.actualSlowBreak = this.register(new Setting("ActuallySlow", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.facePlaceSpeed = this.register(new Setting("FaceSpeed", 500, 0, 500, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.antiNaked = this.register(new Setting("AntiNaked", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.range = this.register(new Setting("Range", 12.0F, 0.1F, 20.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.targetMode = this.register(new Setting("Target", AutoCrystal.Target.CLOSEST, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.minArmor = this.register(new Setting("MinArmor", 5, 0, 125, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.switchCooldown = this.register(new Setting("Cooldown", 500, 0, 1000, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.autoSwitch = this.register(new Setting("Switch", AutoCrystal.AutoSwitch.TOGGLE, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.switchBind = this.register(new Setting("SwitchBind", new Bind(-1), (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() == AutoCrystal.AutoSwitch.TOGGLE;
      }));
      this.offhandSwitch = this.register(new Setting("Offhand", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE;
      }));
      this.switchBack = this.register(new Setting("Switchback", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE && (Boolean)this.offhandSwitch.getValue();
      }));
      this.lethalSwitch = this.register(new Setting("LethalSwitch", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE;
      }));
      this.mineSwitch = this.register(new Setting("MineSwitch", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE;
      }));
      this.rotate = this.register(new Setting("Rotate", AutoCrystal.Rotate.OFF, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.suicide = this.register(new Setting("Suicide", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.webAttack = this.register(new Setting("WebAttack", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && this.targetMode.getValue() != AutoCrystal.Target.DAMAGE;
      }));
      this.fullCalc = this.register(new Setting("ExtraCalc", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.sound = this.register(new Setting("Sound", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.soundRange = this.register(new Setting("SoundRange", 12.0F, 0.0F, 12.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.soundPlayer = this.register(new Setting("SoundPlayer", 6.0F, 0.0F, 12.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.soundConfirm = this.register(new Setting("SoundConfirm", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.extraSelfCalc = this.register(new Setting("MinSelfDmg", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.antiFriendPop = this.register(new Setting("FriendPop", AutoCrystal.AntiFriendPop.NONE, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC;
      }));
      this.noCount = this.register(new Setting("AntiCount", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK);
      }));
      this.calcEvenIfNoDamage = this.register(new Setting("BigFriendCalc", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK) && this.targetMode.getValue() != AutoCrystal.Target.DAMAGE;
      }));
      this.predictFriendDmg = this.register(new Setting("PredictFriend", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.MISC && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK) && (Boolean)this.instant.getValue();
      }));
      this.minMinDmg = this.register(new Setting("MinMinDmg", 0.0F, 0.0F, 3.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && (Boolean)this.place.getValue();
      }));
      this.attackOppositeHand = this.register(new Setting("OppositeHand", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.removeAfterAttack = this.register(new Setting("AttackRemove", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.breakSwing = this.register(new Setting("BreakSwing", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.placeSwing = this.register(new Setting("PlaceSwing", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.exactHand = this.register(new Setting("ExactHand", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && (Boolean)this.placeSwing.getValue();
      }));
      this.justRender = this.register(new Setting("JustRender", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.fakeSwing = this.register(new Setting("FakeSwing", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && (Boolean)this.justRender.getValue();
      }));
      this.logic = this.register(new Setting("Logic", AutoCrystal.Logic.BREAKPLACE, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.damageSync = this.register(new Setting("DamageSync", AutoCrystal.DamageSync.NONE, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.damageSyncTime = this.register(new Setting("SyncDelay", 500, 0, 500, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE;
      }));
      this.dropOff = this.register(new Setting("DropOff", 5.0F, 0.0F, 10.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK;
      }));
      this.confirm = this.register(new Setting("Confirm", 250, 0, 1000, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE;
      }));
      this.syncedFeetPlace = this.register(new Setting("FeetSync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE;
      }));
      this.fullSync = this.register(new Setting("FullSync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && (Boolean)this.syncedFeetPlace.getValue();
      }));
      this.syncCount = this.register(new Setting("SyncCount", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && (Boolean)this.syncedFeetPlace.getValue();
      }));
      this.hyperSync = this.register(new Setting("HyperSync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && (Boolean)this.syncedFeetPlace.getValue();
      }));
      this.gigaSync = this.register(new Setting("GigaSync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && (Boolean)this.syncedFeetPlace.getValue();
      }));
      this.syncySync = this.register(new Setting("SyncySync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && (Boolean)this.syncedFeetPlace.getValue();
      }));
      this.enormousSync = this.register(new Setting("EnormousSync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && (Boolean)this.syncedFeetPlace.getValue();
      }));
      this.holySync = this.register(new Setting("UnbelievableSync", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && (Boolean)this.syncedFeetPlace.getValue();
      }));
      this.eventMode = this.register(new Setting("Updates", 3, 1, 3, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.rotateFirst = this.register(new Setting("FirstRotation", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.rotate.getValue() != AutoCrystal.Rotate.OFF && (Integer)this.eventMode.getValue() == 2;
      }));
      this.threadMode = this.register(new Setting("Thread", AutoCrystal.ThreadMode.NONE, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.threadDelay = this.register(new Setting("ThreadDelay", 50, 1, 1000, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE;
      }));
      this.syncThreadBool = this.register(new Setting("ThreadSync", true, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE;
      }));
      this.syncThreads = this.register(new Setting("SyncThreads", 1000, 1, 10000, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE && (Boolean)this.syncThreadBool.getValue();
      }));
      this.predictPos = this.register(new Setting("PredictPos", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.renderExtrapolation = this.register(new Setting("RenderExtrapolation", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && (Boolean)this.predictPos.getValue();
      }));
      this.predictTicks = this.register(new Setting("ExtrapolationTicks", 2, 1, 20, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV && (Boolean)this.predictPos.getValue();
      }));
      this.rotations = this.register(new Setting("Spoofs", 1, 1, 20, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.predictRotate = this.register(new Setting("PredictRotate", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.predictOffset = this.register(new Setting("PredictOffset", 0.0F, 0.0F, 4.0F, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.DEV;
      }));
      this.doublePopOnDamage = this.register(new Setting("DamagePop", false, (v) -> {
         return this.setting.getValue() == AutoCrystal.Settings.PLACE && (Boolean)this.place.getValue() && (Boolean)this.doublePop.getValue() && this.targetMode.getValue() == AutoCrystal.Target.DAMAGE;
      }));
      this.attackList = new ConcurrentLinkedQueue();
      this.crystalMap = new HashMap();
      this.switchTimer = new Timer();
      this.manualTimer = new Timer();
      this.breakTimer = new Timer();
      this.placeTimer = new Timer();
      this.syncTimer = new Timer();
      this.predictTimer = new Timer();
      this.efficientTarget = null;
      this.currentDamage = 0.0D;
      this.renderDamage = 0.0D;
      this.lastDamage = 0.0D;
      this.didRotation = false;
      this.switching = false;
      this.placePos = null;
      this.renderPos = null;
      this.mainHand = false;
      this.rotating = false;
      this.offHand = false;
      this.crystalCount = 0;
      this.minDmgCount = 0;
      this.lastSlot = -1;
      this.yaw = 0.0F;
      this.pitch = 0.0F;
      this.webPos = null;
      this.renderTimer = new Timer();
      this.lastPos = null;
      this.posConfirmed = false;
      this.foundDoublePop = false;
      this.rotationPacketsSpoofed = 0;
      this.shouldInterrupt = new AtomicBoolean(false);
      this.syncroTimer = new Timer();
      this.totemPops = new ConcurrentHashMap();
      this.packetUseEntities = new LinkedList();
      this.threadOngoing = new AtomicBoolean(false);
      this.threadTimer = new Timer();
      instance = this;
   }

   public static AutoCrystal getInstance() {
      if (instance == null) {
         instance = new AutoCrystal();
      }

      return instance;
   }

   public void onTick() {
      if (this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && (Integer)this.eventMode.getValue() == 3) {
         this.doAutoCrystal();
      }

   }

   @SubscribeEvent
   public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
      if (event.getStage() == 1) {
         this.postProcessing();
      }

      if (event.getStage() == 0) {
         if (this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) {
            this.processMultiThreading();
         } else if ((Integer)this.eventMode.getValue() == 2) {
            this.doAutoCrystal();
         }

      }
   }

   public void onUpdate() {
      if (this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && (Integer)this.eventMode.getValue() == 1) {
         this.doAutoCrystal();
      }

   }

   public void onToggle() {
      brokenPos.clear();
      placedPos.clear();
      this.totemPops.clear();
      this.rotating = false;
   }

   public void onDisable() {
      if (this.thread != null) {
         this.shouldInterrupt.set(true);
      }

      if (this.executor != null) {
         this.executor.shutdown();
      }

   }

   public void onEnable() {
      if (this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) {
         this.processMultiThreading();
      }

   }

   public String getDisplayInfo() {
      if (this.switching) {
         return "§aSwitch";
      } else {
         return target != null ? target.func_70005_c_() : null;
      }
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      if (event.getStage() == 0 && this.rotate.getValue() != AutoCrystal.Rotate.OFF && this.rotating && (Integer)this.eventMode.getValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
         CPacketPlayer packet = (CPacketPlayer)event.getPacket();
         packet.field_149476_e = this.yaw;
         packet.field_149473_f = this.pitch;
         ++this.rotationPacketsSpoofed;
         if (this.rotationPacketsSpoofed >= (Integer)this.rotations.getValue()) {
            this.rotating = false;
            this.rotationPacketsSpoofed = 0;
         }
      }

      if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity) {
         CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
         if (packet.func_149565_c() == Action.ATTACK && packet.func_149564_a(mc.field_71441_e) instanceof EntityEnderCrystal) {
            if ((Boolean)this.attackOppositeHand.getValue()) {
               boolean offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
               packet.field_186995_d = offhand ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
            }

            if ((Boolean)this.removeAfterAttack.getValue()) {
               packet.func_149564_a(mc.field_71441_e).func_70106_y();
               mc.field_71441_e.func_73028_b(packet.field_149567_a);
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGH,
      receiveCanceled = true
   )
   public void onPacketReceive(PacketEvent.Receive event) {
      if (!fullNullCheck()) {
         BlockPos pos;
         if (!(Boolean)this.justRender.getValue() && (Boolean)this.explode.getValue() && (Boolean)this.instant.getValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !(Boolean)this.syncedFeetPlace.getValue() || this.damageSync.getValue() == AutoCrystal.DamageSync.NONE)) {
            SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
            if (packet.func_148993_l() == 51) {
               pos = new BlockPos(packet.func_186880_c(), packet.func_186882_d(), packet.func_186881_e());
               if (mc.field_71439_g.func_174818_b(pos) + (double)(Float)this.predictOffset.getValue() <= MathUtil.square((Float)this.breakRange.getValue()) && (this.instantTimer.getValue() == AutoCrystal.PredictTimer.NONE || this.instantTimer.getValue() == AutoCrystal.PredictTimer.BREAK && this.breakTimer.passedMs((long)(Integer)this.breakDelay.getValue()) || this.instantTimer.getValue() == AutoCrystal.PredictTimer.PREDICT && this.predictTimer.passedMs((long)(Integer)this.predictDelay.getValue()))) {
                  if (this.predictSlowBreak(pos.func_177977_b())) {
                     return;
                  }

                  if ((Boolean)this.predictFriendDmg.getValue() && (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL) && this.isRightThread()) {
                     Iterator var13 = mc.field_71441_e.field_73010_i.iterator();

                     while(var13.hasNext()) {
                        EntityPlayer friend = (EntityPlayer)var13.next();
                        if (friend != null && !mc.field_71439_g.equals(friend) && !(friend.func_174818_b(pos) > MathUtil.square((Float)this.range.getValue() + (Float)this.placeRange.getValue())) && Phobos.friendManager.isFriend(friend) && (double)DamageUtil.calculateDamage((BlockPos)pos, friend) > (double)EntityUtil.getHealth(friend) + 0.5D) {
                           return;
                        }
                     }
                  }

                  float selfDamage;
                  if (!placedPos.contains(pos.func_177977_b())) {
                     if ((Boolean)this.predictCalc.getValue() && this.isRightThread()) {
                        selfDamage = -1.0F;
                        if (DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) {
                           selfDamage = DamageUtil.calculateDamage((BlockPos)pos, mc.field_71439_g);
                        }

                        if ((double)selfDamage + 0.5D < (double)EntityUtil.getHealth(mc.field_71439_g) && selfDamage <= (Float)this.maxSelfBreak.getValue()) {
                           Iterator var16 = mc.field_71441_e.field_73010_i.iterator();

                           EntityPlayer player;
                           float damage;
                           do {
                              do {
                                 do {
                                    do {
                                       if (!var16.hasNext()) {
                                          return;
                                       }

                                       player = (EntityPlayer)var16.next();
                                    } while(!(player.func_174818_b(pos) <= MathUtil.square((Float)this.range.getValue())));
                                 } while(!EntityUtil.isValid(player, (double)((Float)this.range.getValue() + (Float)this.breakRange.getValue())));
                              } while((Boolean)this.antiNaked.getValue() && DamageUtil.isNaked(player));

                              damage = DamageUtil.calculateDamage((BlockPos)pos, player);
                           } while(!(damage > selfDamage) && (!(damage > (Float)this.minDamage.getValue()) || DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) && !(damage > EntityUtil.getHealth(player)));

                           if ((Boolean)this.predictRotate.getValue() && (Integer)this.eventMode.getValue() != 2 && (this.rotate.getValue() == AutoCrystal.Rotate.BREAK || this.rotate.getValue() == AutoCrystal.Rotate.ALL)) {
                              this.rotateToPos(pos);
                           }

                           this.attackCrystalPredict(packet.func_149001_c(), pos);
                        }
                     }
                  } else {
                     if (this.isRightThread() && (Boolean)this.superSafe.getValue()) {
                        if (DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) {
                           selfDamage = DamageUtil.calculateDamage((BlockPos)pos, mc.field_71439_g);
                           if ((double)selfDamage - 0.5D > (double)EntityUtil.getHealth(mc.field_71439_g) || selfDamage > (Float)this.maxSelfBreak.getValue()) {
                              return;
                           }
                        }
                     } else if ((Boolean)this.superSafe.getValue()) {
                        return;
                     }

                     this.attackCrystalPredict(packet.func_149001_c(), pos);
                  }
               }
            }
         } else if (!(Boolean)this.soundConfirm.getValue() && event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion packet = (SPacketExplosion)event.getPacket();
            pos = (new BlockPos(packet.func_149148_f(), packet.func_149143_g(), packet.func_149145_h())).func_177977_b();
            this.removePos(pos);
         } else if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet = (SPacketDestroyEntities)event.getPacket();
            int[] var3 = packet.func_149098_c();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               int id = var3[var5];
               Entity entity = mc.field_71441_e.func_73045_a(id);
               if (entity instanceof EntityEnderCrystal) {
                  brokenPos.remove((new BlockPos(entity.func_174791_d())).func_177977_b());
                  placedPos.remove((new BlockPos(entity.func_174791_d())).func_177977_b());
               }
            }
         } else if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
            if (packet.func_149160_c() == 35 && packet.func_149161_a(mc.field_71441_e) instanceof EntityPlayer) {
               this.totemPops.put((EntityPlayer)packet.func_149161_a(mc.field_71441_e), (new Timer()).reset());
            }
         } else if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
            if (packet.func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB) {
               pos = new BlockPos(packet.func_149207_d(), packet.func_149211_e(), packet.func_149210_f());
               if ((Boolean)this.sound.getValue() || this.threadMode.getValue() == AutoCrystal.ThreadMode.SOUND) {
                  NoSoundLag.removeEntities(packet, (Float)this.soundRange.getValue());
               }

               if ((Boolean)this.soundConfirm.getValue()) {
                  this.removePos(pos);
               }

               if (this.threadMode.getValue() == AutoCrystal.ThreadMode.SOUND && this.isRightThread() && mc.field_71439_g != null && mc.field_71439_g.func_174818_b(pos) < MathUtil.square((Float)this.soundPlayer.getValue())) {
                  this.handlePool(true);
               }
            }
         }

      }
   }

   private boolean predictSlowBreak(BlockPos pos) {
      return (Boolean)this.antiCommit.getValue() && lowDmgPos.remove(pos) ? this.shouldSlowBreak(false) : false;
   }

   private boolean isRightThread() {
      return mc.func_152345_ab() || !Phobos.eventManager.ticksOngoing() && !this.threadOngoing.get();
   }

   private void attackCrystalPredict(int entityID, BlockPos pos) {
      if ((Boolean)this.predictRotate.getValue() && ((Integer)this.eventMode.getValue() != 2 || this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) && (this.rotate.getValue() == AutoCrystal.Rotate.BREAK || this.rotate.getValue() == AutoCrystal.Rotate.ALL)) {
         this.rotateToPos(pos);
      }

      CPacketUseEntity attackPacket = new CPacketUseEntity();
      attackPacket.field_149567_a = entityID;
      attackPacket.field_149566_b = Action.ATTACK;
      mc.field_71439_g.field_71174_a.func_147297_a(attackPacket);
      if ((Boolean)this.breakSwing.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.MAIN_HAND));
      }

      if ((Boolean)this.resetBreakTimer.getValue()) {
         this.breakTimer.reset();
      }

      this.predictTimer.reset();
   }

   private void removePos(BlockPos pos) {
      if (this.damageSync.getValue() == AutoCrystal.DamageSync.PLACE) {
         if (placedPos.remove(pos)) {
            this.posConfirmed = true;
         }
      } else if (this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK && brokenPos.remove(pos)) {
         this.posConfirmed = true;
      }

   }

   public void onRender3D(Render3DEvent event) {
      if ((this.offHand || this.mainHand || this.switchMode.getValue() == AutoCrystal.Switch.CALC) && this.renderPos != null && (Boolean)this.render.getValue() && ((Boolean)this.box.getValue() || (Boolean)this.text.getValue() || (Boolean)this.outline.getValue())) {
         RenderUtil.drawBoxESP(this.renderPos, (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.red.getValue(), (Integer)this.green.getValue(), (Integer)this.blue.getValue(), (Integer)this.alpha.getValue()), (Boolean)this.customOutline.getValue(), (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColor() : new Color((Integer)this.cRed.getValue(), (Integer)this.cGreen.getValue(), (Integer)this.cBlue.getValue(), (Integer)this.cAlpha.getValue()), (Float)this.lineWidth.getValue(), (Boolean)this.outline.getValue(), (Boolean)this.box.getValue(), (Integer)this.boxAlpha.getValue(), false);
         if ((Boolean)this.text.getValue()) {
            RenderUtil.drawText(this.renderPos, (Math.floor(this.renderDamage) == this.renderDamage ? (int)this.renderDamage : String.format("%.1f", this.renderDamage)) + "");
         }
      }

   }

   @SubscribeEvent
   public void onKeyInput(KeyInputEvent event) {
      if (Keyboard.getEventKeyState() && !(mc.field_71462_r instanceof PhobosGui) && ((Bind)this.switchBind.getValue()).getKey() == Keyboard.getEventKey()) {
         if ((Boolean)this.switchBack.getValue() && (Boolean)this.offhandSwitch.getValue() && this.offHand) {
            Offhand module = (Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class);
            if (module.isOff()) {
               Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
            } else if (module.type.getValue() == Offhand.Type.NEW) {
               module.setSwapToTotem(true);
               module.doOffhand();
            } else {
               module.setMode(Offhand.Mode2.TOTEMS);
               module.doSwitch();
            }

            return;
         }

         this.switching = !this.switching;
      }

   }

   @SubscribeEvent
   public void onSettingChange(ClientEvent event) {
      if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
         if (this.executor != null) {
            this.executor.shutdown();
         }

         if (this.thread != null) {
            this.shouldInterrupt.set(true);
         }
      }

   }

   private void postProcessing() {
      if (this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && (Integer)this.eventMode.getValue() == 2 && this.rotate.getValue() != AutoCrystal.Rotate.OFF && (Boolean)this.rotateFirst.getValue()) {
         switch((AutoCrystal.Logic)this.logic.getValue()) {
         case BREAKPLACE:
            this.postProcessBreak();
            this.postProcessPlace();
            break;
         case PLACEBREAK:
            this.postProcessPlace();
            this.postProcessBreak();
         }

      }
   }

   private void postProcessBreak() {
      for(; !this.packetUseEntities.isEmpty(); this.breakTimer.reset()) {
         CPacketUseEntity packet = (CPacketUseEntity)this.packetUseEntities.poll();
         mc.field_71439_g.field_71174_a.func_147297_a(packet);
         if ((Boolean)this.breakSwing.getValue()) {
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         }
      }

   }

   private void postProcessPlace() {
      if (this.placeInfo != null) {
         this.placeInfo.runPlace();
         this.placeTimer.reset();
         this.placeInfo = null;
      }

   }

   private void processMultiThreading() {
      if (!this.isOff()) {
         if (this.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
            this.handleWhile();
         } else if (this.threadMode.getValue() != AutoCrystal.ThreadMode.NONE) {
            this.handlePool(false);
         }

      }
   }

   private void handlePool(boolean justDoIt) {
      if (justDoIt || this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || this.syncroTimer.passedMs((long)(Integer)this.syncThreads.getValue()) && (Boolean)this.syncThreadBool.getValue()) {
         if (this.executor != null) {
            this.executor.shutdown();
         }

         this.executor = this.getExecutor();
         this.syncroTimer.reset();
      }

   }

   private void handleWhile() {
      if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncroTimer.passedMs((long)(Integer)this.syncThreads.getValue()) && (Boolean)this.syncThreadBool.getValue()) {
         if (this.thread == null) {
            this.thread = new Thread(AutoCrystal.RAutoCrystal.getInstance(this));
         } else if (this.syncroTimer.passedMs((long)(Integer)this.syncThreads.getValue()) && !this.shouldInterrupt.get() && (Boolean)this.syncThreadBool.getValue()) {
            this.shouldInterrupt.set(true);
            this.syncroTimer.reset();
            return;
         }

         if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
            this.thread = new Thread(AutoCrystal.RAutoCrystal.getInstance(this));
         }

         if (this.thread != null && this.thread.getState() == State.NEW) {
            try {
               this.thread.start();
            } catch (Exception var2) {
               var2.printStackTrace();
            }

            this.syncroTimer.reset();
         }
      }

   }

   private ScheduledExecutorService getExecutor() {
      ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
      service.scheduleAtFixedRate(AutoCrystal.RAutoCrystal.getInstance(this), 0L, (long)(Integer)this.threadDelay.getValue(), TimeUnit.MILLISECONDS);
      return service;
   }

   public void doAutoCrystal() {
      if (this.check()) {
         switch((AutoCrystal.Logic)this.logic.getValue()) {
         case BREAKPLACE:
            this.breakCrystal();
            this.placeCrystal();
            break;
         case PLACEBREAK:
            this.placeCrystal();
            this.breakCrystal();
         }

         this.manualBreaker();
      }

   }

   private boolean check() {
      if (fullNullCheck()) {
         return false;
      } else {
         if (this.syncTimer.passedMs((long)(Integer)this.damageSyncTime.getValue())) {
            this.currentSyncTarget = null;
            this.syncedCrystalPos = null;
            this.syncedPlayerPos = null;
         } else if ((Boolean)this.syncySync.getValue() && this.syncedCrystalPos != null) {
            this.posConfirmed = true;
         }

         this.foundDoublePop = false;
         if (this.renderTimer.passedMs(500L)) {
            this.renderPos = null;
            this.renderTimer.reset();
         }

         this.mainHand = mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP;
         this.offHand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
         this.currentDamage = 0.0D;
         this.placePos = null;
         if (this.lastSlot != mc.field_71439_g.field_71071_by.field_70461_c || AutoTrap.isPlacing || Surround.isPlacing) {
            this.lastSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            this.switchTimer.reset();
         }

         if (!this.offHand && !this.mainHand) {
            this.placeInfo = null;
            this.packetUseEntities.clear();
         }

         if (this.offHand || this.mainHand) {
            this.switching = false;
         }

         if ((this.offHand || this.mainHand || this.switchMode.getValue() != AutoCrystal.Switch.BREAKSLOT || this.switching) && DamageUtil.canBreakWeakness(mc.field_71439_g) && this.switchTimer.passedMs((long)(Integer)this.switchCooldown.getValue())) {
            if ((Boolean)this.mineSwitch.getValue() && Mouse.isButtonDown(0) && (this.switching || this.autoSwitch.getValue() == AutoCrystal.AutoSwitch.ALWAYS) && Mouse.isButtonDown(1) && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemPickaxe) {
               this.switchItem();
            }

            this.mapCrystals();
            if (!this.posConfirmed && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && this.syncTimer.passedMs((long)(Integer)this.confirm.getValue())) {
               this.syncTimer.setMs((long)((Integer)this.damageSyncTime.getValue() + 1));
            }

            return true;
         } else {
            this.renderPos = null;
            target = null;
            this.rotating = false;
            return false;
         }
      }
   }

   private void mapCrystals() {
      this.efficientTarget = null;
      if ((Integer)this.packets.getValue() != 1) {
         this.attackList = new ConcurrentLinkedQueue();
         this.crystalMap = new HashMap();
      }

      this.crystalCount = 0;
      this.minDmgCount = 0;
      Entity maxCrystal = null;
      float maxDamage = 0.5F;
      Iterator var3 = mc.field_71441_e.field_72996_f.iterator();

      Entity crystal;
      while(var3.hasNext()) {
         crystal = (Entity)var3.next();
         if (!crystal.field_70128_L && crystal instanceof EntityEnderCrystal && this.isValid(crystal)) {
            if ((Boolean)this.syncedFeetPlace.getValue() && crystal.func_180425_c().func_177977_b().equals(this.syncedCrystalPos) && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
               ++this.minDmgCount;
               ++this.crystalCount;
               if ((Boolean)this.syncCount.getValue()) {
                  this.minDmgCount = (Integer)this.wasteAmount.getValue() + 1;
                  this.crystalCount = (Integer)this.wasteAmount.getValue() + 1;
               }

               if ((Boolean)this.hyperSync.getValue()) {
                  maxCrystal = null;
                  break;
               }
            } else {
               boolean count = false;
               boolean countMin = false;
               float selfDamage = -1.0F;
               if (DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) {
                  selfDamage = DamageUtil.calculateDamage((Entity)crystal, mc.field_71439_g);
               }

               if ((double)selfDamage + 0.5D < (double)EntityUtil.getHealth(mc.field_71439_g) && selfDamage <= (Float)this.maxSelfBreak.getValue()) {
                  Iterator var10 = mc.field_71441_e.field_73010_i.iterator();

                  label213:
                  while(true) {
                     while(true) {
                        EntityPlayer player;
                        float damage;
                        do {
                           do {
                              while(true) {
                                 do {
                                    if (!var10.hasNext()) {
                                       break label213;
                                    }

                                    player = (EntityPlayer)var10.next();
                                 } while(!(player.func_70068_e(crystal) <= MathUtil.square((Float)this.range.getValue())));

                                 if (EntityUtil.isValid(player, (double)((Float)this.range.getValue() + (Float)this.breakRange.getValue()))) {
                                    break;
                                 }

                                 if ((this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL) && Phobos.friendManager.isFriend(player.func_70005_c_())) {
                                    damage = DamageUtil.calculateDamage((Entity)crystal, player);
                                    if ((double)damage > (double)EntityUtil.getHealth(player) + 0.5D) {
                                       maxCrystal = maxCrystal;
                                       maxDamage = maxDamage;
                                       this.crystalMap.remove(crystal);
                                       if ((Boolean)this.noCount.getValue()) {
                                          count = false;
                                          countMin = false;
                                       }
                                       break label213;
                                    }
                                 }
                              }
                           } while((Boolean)this.antiNaked.getValue() && DamageUtil.isNaked(player));

                           damage = DamageUtil.calculateDamage((Entity)crystal, player);
                        } while(!(damage > selfDamage) && (!(damage > (Float)this.minDamage.getValue()) || DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) && !(damage > EntityUtil.getHealth(player)));

                        if (damage > maxDamage) {
                           maxDamage = damage;
                           maxCrystal = crystal;
                        }

                        if ((Integer)this.packets.getValue() == 1) {
                           if (damage >= (Float)this.minDamage.getValue() || !(Boolean)this.wasteMinDmgCount.getValue()) {
                              count = true;
                           }

                           countMin = true;
                        } else if (this.crystalMap.get(crystal) == null || (Float)this.crystalMap.get(crystal) < damage) {
                           this.crystalMap.put(crystal, damage);
                        }
                     }
                  }
               }

               if (countMin) {
                  ++this.minDmgCount;
                  if (count) {
                     ++this.crystalCount;
                  }
               }
            }
         }
      }

      if (this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK && ((double)maxDamage > this.lastDamage || this.syncTimer.passedMs((long)(Integer)this.damageSyncTime.getValue()) || this.damageSync.getValue() == AutoCrystal.DamageSync.NONE)) {
         this.lastDamage = (double)maxDamage;
      }

      if ((Boolean)this.enormousSync.getValue() && (Boolean)this.syncedFeetPlace.getValue() && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE && this.syncedCrystalPos != null) {
         if ((Boolean)this.syncCount.getValue()) {
            this.minDmgCount = (Integer)this.wasteAmount.getValue() + 1;
            this.crystalCount = (Integer)this.wasteAmount.getValue() + 1;
         }

      } else {
         if ((Boolean)this.webAttack.getValue() && this.webPos != null) {
            if (mc.field_71439_g.func_174818_b(this.webPos.func_177984_a()) > MathUtil.square((Float)this.breakRange.getValue())) {
               this.webPos = null;
            } else {
               var3 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(this.webPos.func_177984_a())).iterator();

               while(var3.hasNext()) {
                  crystal = (Entity)var3.next();
                  if (crystal instanceof EntityEnderCrystal) {
                     this.attackList.add(crystal);
                     this.efficientTarget = crystal;
                     this.webPos = null;
                     this.lastDamage = 0.5D;
                     return;
                  }
               }
            }
         }

         if (!this.shouldSlowBreak(true) || !(maxDamage < (Float)this.minDamage.getValue()) || target != null && EntityUtil.getHealth(target) <= (Float)this.facePlace.getValue() && (this.breakTimer.passedMs((long)(Integer)this.facePlaceSpeed.getValue()) || !(Boolean)this.slowFaceBreak.getValue() || !Mouse.isButtonDown(0) || !(Boolean)this.holdFacePlace.getValue() || !(Boolean)this.holdFaceBreak.getValue())) {
            if ((Integer)this.packets.getValue() == 1) {
               this.efficientTarget = maxCrystal;
            } else {
               this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);

               for(var3 = this.crystalMap.entrySet().iterator(); var3.hasNext(); ++this.minDmgCount) {
                  Entry<Entity, Float> entry = (Entry)var3.next();
                  Entity crystal = (Entity)entry.getKey();
                  float damage = (Float)entry.getValue();
                  if (damage >= (Float)this.minDamage.getValue() || !(Boolean)this.wasteMinDmgCount.getValue()) {
                     ++this.crystalCount;
                  }

                  this.attackList.add(crystal);
               }
            }

         } else {
            this.efficientTarget = null;
         }
      }
   }

   private boolean shouldSlowBreak(boolean withManual) {
      return withManual && (Boolean)this.manual.getValue() && (Boolean)this.manualMinDmg.getValue() && Mouse.isButtonDown(1) && (!Mouse.isButtonDown(0) || !(Boolean)this.holdFacePlace.getValue()) || (Boolean)this.holdFacePlace.getValue() && (Boolean)this.holdFaceBreak.getValue() && Mouse.isButtonDown(0) && !this.breakTimer.passedMs((long)(Integer)this.facePlaceSpeed.getValue()) || (Boolean)this.slowFaceBreak.getValue() && !this.breakTimer.passedMs((long)(Integer)this.facePlaceSpeed.getValue());
   }

   private void placeCrystal() {
      int crystalLimit = (Integer)this.wasteAmount.getValue();
      if (this.placeTimer.passedMs((long)(Integer)this.placeDelay.getValue()) && (Boolean)this.place.getValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == AutoCrystal.Switch.CALC || this.switchMode.getValue() == AutoCrystal.Switch.BREAKSLOT && this.switching)) {
         if ((this.offHand || this.mainHand || this.switchMode.getValue() != AutoCrystal.Switch.ALWAYS && !this.switching) && this.crystalCount >= crystalLimit && (!(Boolean)this.antiSurround.getValue() || this.lastPos == null || !this.lastPos.equals(this.placePos))) {
            return;
         }

         this.calculateDamage(this.getTarget(this.targetMode.getValue() == AutoCrystal.Target.UNSAFE));
         if (target != null && this.placePos != null) {
            if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE && (this.currentDamage > (double)(Float)this.minDamage.getValue() || (Boolean)this.lethalSwitch.getValue() && EntityUtil.getHealth(target) <= (Float)this.facePlace.getValue()) && !this.switchItem()) {
               return;
            }

            if (this.currentDamage < (double)(Float)this.minDamage.getValue() && (Boolean)this.limitFacePlace.getValue()) {
               crystalLimit = 1;
            }

            if (this.currentDamage >= (double)(Float)this.minMinDmg.getValue() && (this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoCrystal.AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (Boolean)this.antiSurround.getValue() && this.lastPos != null && this.lastPos.equals(this.placePos)) && (this.currentDamage > (double)(Float)this.minDamage.getValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0D && (DamageUtil.isArmorLow(target, (Integer)this.minArmor.getValue()) || EntityUtil.getHealth(target) <= (Float)this.facePlace.getValue() || this.currentDamage > (double)(Float)this.minDamage.getValue() || this.shouldHoldFacePlace())) {
               float damageOffset = this.damageSync.getValue() == AutoCrystal.DamageSync.BREAK ? (Float)this.dropOff.getValue() - 5.0F : 0.0F;
               boolean syncflag = false;
               if ((Boolean)this.syncedFeetPlace.getValue() && this.placePos.equals(this.lastPos) && this.isEligableForFeetSync(target, this.placePos) && !this.syncTimer.passedMs((long)(Integer)this.damageSyncTime.getValue()) && target.equals(this.currentSyncTarget) && target.func_180425_c().equals(this.syncedPlayerPos) && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
                  this.syncedCrystalPos = this.placePos;
                  this.lastDamage = this.currentDamage;
                  if ((Boolean)this.fullSync.getValue()) {
                     this.lastDamage = 100.0D;
                  }

                  syncflag = true;
               }

               if (syncflag || this.currentDamage - (double)damageOffset > this.lastDamage || this.syncTimer.passedMs((long)(Integer)this.damageSyncTime.getValue()) || this.damageSync.getValue() == AutoCrystal.DamageSync.NONE) {
                  if (!syncflag && this.damageSync.getValue() != AutoCrystal.DamageSync.BREAK) {
                     this.lastDamage = this.currentDamage;
                  }

                  this.renderPos = this.placePos;
                  this.renderDamage = this.currentDamage;
                  if (this.switchItem()) {
                     this.currentSyncTarget = target;
                     this.syncedPlayerPos = target.func_180425_c();
                     if (this.foundDoublePop) {
                        this.totemPops.put(target, (new Timer()).reset());
                     }

                     this.rotateToPos(this.placePos);
                     if (this.addTolowDmg || (Boolean)this.actualSlowBreak.getValue() && this.currentDamage < (double)(Float)this.minDamage.getValue()) {
                        lowDmgPos.add(this.placePos);
                     }

                     placedPos.add(this.placePos);
                     if (!(Boolean)this.justRender.getValue()) {
                        if ((Integer)this.eventMode.getValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && (Boolean)this.rotateFirst.getValue() && this.rotate.getValue() != AutoCrystal.Rotate.OFF) {
                           this.placeInfo = new AutoCrystal.PlaceInfo(this.placePos, this.offHand, (Boolean)this.placeSwing.getValue(), (Boolean)this.exactHand.getValue());
                        } else {
                           BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, (Boolean)this.placeSwing.getValue(), (Boolean)this.exactHand.getValue());
                        }
                     }

                     this.lastPos = this.placePos;
                     this.placeTimer.reset();
                     this.posConfirmed = false;
                     if (this.syncTimer.passedMs((long)(Integer)this.damageSyncTime.getValue())) {
                        this.syncedCrystalPos = null;
                        this.syncTimer.reset();
                     }
                  }
               }
            }
         } else {
            this.renderPos = null;
         }
      }

   }

   private boolean shouldHoldFacePlace() {
      this.addTolowDmg = false;
      if ((Boolean)this.holdFacePlace.getValue() && Mouse.isButtonDown(0)) {
         this.addTolowDmg = true;
         return true;
      } else {
         return false;
      }
   }

   private boolean switchItem() {
      if (!this.offHand && !this.mainHand) {
         switch((AutoCrystal.AutoSwitch)this.autoSwitch.getValue()) {
         case NONE:
            return false;
         case TOGGLE:
            if (!this.switching) {
               return false;
            }
         case ALWAYS:
            if (this.doSwitch()) {
               return true;
            }
         default:
            return false;
         }
      } else {
         return true;
      }
   }

   private boolean doSwitch() {
      if ((Boolean)this.offhandSwitch.getValue()) {
         Offhand module = (Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class);
         if (module.isOff()) {
            Command.sendMessage("<" + this.getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
            this.switching = false;
            return false;
         } else {
            if (module.type.getValue() == Offhand.Type.NEW) {
               module.setSwapToTotem(false);
               module.setMode(Offhand.Mode.CRYSTALS);
               module.doOffhand();
            } else {
               module.setMode(Offhand.Mode2.CRYSTALS);
               module.doSwitch();
            }

            this.switching = false;
            return true;
         }
      } else {
         if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
            this.mainHand = false;
         } else {
            InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
            this.mainHand = true;
         }

         this.switching = false;
         return true;
      }
   }

   private void calculateDamage(EntityPlayer targettedPlayer) {
      if (targettedPlayer != null || this.targetMode.getValue() == AutoCrystal.Target.DAMAGE || (Boolean)this.fullCalc.getValue()) {
         float maxDamage = 0.5F;
         EntityPlayer currentTarget = null;
         BlockPos currentPos = null;
         float maxSelfDamage = 0.0F;
         this.foundDoublePop = false;
         BlockPos setToAir = null;
         IBlockState state = null;
         if ((Boolean)this.webAttack.getValue() && targettedPlayer != null) {
            BlockPos playerPos = new BlockPos(targettedPlayer.func_174791_d());
            Block web = mc.field_71441_e.func_180495_p(playerPos).func_177230_c();
            if (web == Blocks.field_150321_G) {
               setToAir = playerPos;
               state = mc.field_71441_e.func_180495_p(playerPos);
               mc.field_71441_e.func_175698_g(playerPos);
            }
         }

         Iterator var18 = BlockUtil.possiblePlacePositions((Float)this.placeRange.getValue(), (Boolean)this.antiSurround.getValue(), (Boolean)this.oneDot15.getValue()).iterator();

         while(true) {
            while(true) {
               float selfDamage;
               float playerDamage;
               boolean friendPop;
               BlockPos pos;
               label205:
               do {
                  label180:
                  while(true) {
                     do {
                        do {
                           do {
                              if (!var18.hasNext()) {
                                 if (setToAir != null) {
                                    mc.field_71441_e.func_175656_a(setToAir, state);
                                    this.webPos = currentPos;
                                 }

                                 target = currentTarget;
                                 this.currentDamage = (double)maxDamage;
                                 this.placePos = currentPos;
                                 return;
                              }

                              pos = (BlockPos)var18.next();
                           } while(!BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == AutoCrystal.Raytrace.PLACE || this.raytrace.getValue() == AutoCrystal.Raytrace.FULL) && mc.field_71439_g.func_174818_b(pos) > MathUtil.square((Float)this.placetrace.getValue()), 1.0F));

                           selfDamage = -1.0F;
                           if (DamageUtil.canTakeDamage((Boolean)this.suicide.getValue())) {
                              selfDamage = DamageUtil.calculateDamage((BlockPos)pos, mc.field_71439_g);
                           }
                        } while(!((double)selfDamage + 0.5D < (double)EntityUtil.getHealth(mc.field_71439_g)));
                     } while(!(selfDamage <= (Float)this.maxSelfPlace.getValue()));

                     if (targettedPlayer != null) {
                        playerDamage = DamageUtil.calculateDamage((BlockPos)pos, targettedPlayer);
                        if (!(Boolean)this.calcEvenIfNoDamage.getValue() || this.antiFriendPop.getValue() != AutoCrystal.AntiFriendPop.ALL && this.antiFriendPop.getValue() != AutoCrystal.AntiFriendPop.PLACE) {
                           break label205;
                        }

                        friendPop = false;
                        Iterator var13 = mc.field_71441_e.field_73010_i.iterator();

                        while(var13.hasNext()) {
                           EntityPlayer friend = (EntityPlayer)var13.next();
                           if (friend != null && !mc.field_71439_g.equals(friend) && !(friend.func_174818_b(pos) > MathUtil.square((Float)this.range.getValue() + (Float)this.placeRange.getValue())) && Phobos.friendManager.isFriend(friend)) {
                              float friendDamage = DamageUtil.calculateDamage((BlockPos)pos, friend);
                              if ((double)friendDamage > (double)EntityUtil.getHealth(friend) + 0.5D) {
                                 friendPop = true;
                                 continue label205;
                              }
                           }
                        }
                        break;
                     }

                     Iterator var15 = mc.field_71441_e.field_73010_i.iterator();

                     while(true) {
                        while(true) {
                           EntityPlayer player;
                           float friendDamage;
                           do {
                              while(true) {
                                 if (!var15.hasNext()) {
                                    continue label180;
                                 }

                                 player = (EntityPlayer)var15.next();
                                 if (EntityUtil.isValid(player, (double)((Float)this.placeRange.getValue() + (Float)this.range.getValue()))) {
                                    break;
                                 }

                                 if ((this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.ALL || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.PLACE) && player != null && player.func_174818_b(pos) <= MathUtil.square((Float)this.range.getValue() + (Float)this.placeRange.getValue()) && Phobos.friendManager.isFriend(player)) {
                                    friendDamage = DamageUtil.calculateDamage((BlockPos)pos, player);
                                    if ((double)friendDamage > (double)EntityUtil.getHealth(player) + 0.5D) {
                                       maxDamage = maxDamage;
                                       currentTarget = currentTarget;
                                       currentPos = currentPos;
                                       maxSelfDamage = maxSelfDamage;
                                       continue label180;
                                    }
                                 }
                              }
                           } while((Boolean)this.antiNaked.getValue() && DamageUtil.isNaked(player));

                           friendDamage = DamageUtil.calculateDamage((BlockPos)pos, player);
                           if ((Boolean)this.doublePopOnDamage.getValue() && this.isDoublePoppable(player, friendDamage) && (currentPos == null || player.func_174818_b(pos) < player.func_174818_b(currentPos))) {
                              currentTarget = player;
                              maxDamage = friendDamage;
                              currentPos = pos;
                              maxSelfDamage = selfDamage;
                              this.foundDoublePop = true;
                              if (this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AutoCrystal.AntiFriendPop.PLACE) {
                                 continue label180;
                              }
                           } else if (!this.foundDoublePop && (friendDamage > maxDamage || (Boolean)this.extraSelfCalc.getValue() && friendDamage >= maxDamage && selfDamage < maxSelfDamage) && (friendDamage > selfDamage || friendDamage > (Float)this.minDamage.getValue() && !DamageUtil.canTakeDamage((Boolean)this.suicide.getValue()) || friendDamage > EntityUtil.getHealth(player))) {
                              maxDamage = friendDamage;
                              currentTarget = player;
                              currentPos = pos;
                              maxSelfDamage = selfDamage;
                           }
                        }
                     }
                  }
               } while(friendPop);

               if (this.isDoublePoppable(targettedPlayer, playerDamage) && (currentPos == null || targettedPlayer.func_174818_b(pos) < targettedPlayer.func_174818_b(currentPos))) {
                  currentTarget = targettedPlayer;
                  maxDamage = playerDamage;
                  currentPos = pos;
                  this.foundDoublePop = true;
               } else if (!this.foundDoublePop && (playerDamage > maxDamage || (Boolean)this.extraSelfCalc.getValue() && playerDamage >= maxDamage && selfDamage < maxSelfDamage) && (playerDamage > selfDamage || playerDamage > (Float)this.minDamage.getValue() && !DamageUtil.canTakeDamage((Boolean)this.suicide.getValue()) || playerDamage > EntityUtil.getHealth(targettedPlayer))) {
                  maxDamage = playerDamage;
                  currentTarget = targettedPlayer;
                  currentPos = pos;
                  maxSelfDamage = selfDamage;
               }
            }
         }
      }
   }

   private EntityPlayer getTarget(boolean unsafe) {
      if (this.targetMode.getValue() == AutoCrystal.Target.DAMAGE) {
         return null;
      } else {
         EntityPlayer currentTarget = null;
         Iterator var3 = mc.field_71441_e.field_73010_i.iterator();

         while(var3.hasNext()) {
            EntityPlayer player = (EntityPlayer)var3.next();
            if (!EntityUtil.isntValid(player, (double)((Float)this.placeRange.getValue() + (Float)this.range.getValue())) && (!(Boolean)this.antiNaked.getValue() || !DamageUtil.isNaked(player)) && (!unsafe || !EntityUtil.isSafe(player))) {
               if ((Integer)this.minArmor.getValue() > 0 && DamageUtil.isArmorLow(player, (Integer)this.minArmor.getValue())) {
                  currentTarget = player;
                  break;
               }

               if (currentTarget == null) {
                  currentTarget = player;
               } else if (mc.field_71439_g.func_70068_e(player) < mc.field_71439_g.func_70068_e((Entity)currentTarget)) {
                  currentTarget = player;
               }
            }
         }

         if (unsafe && currentTarget == null) {
            return this.getTarget(false);
         } else {
            if ((Boolean)this.predictPos.getValue() && currentTarget != null) {
               GameProfile profile = new GameProfile(((EntityPlayer)currentTarget).func_110124_au() == null ? UUID.fromString("8af022c8-b926-41a0-8b79-2b544ff00fcf") : ((EntityPlayer)currentTarget).func_110124_au(), ((EntityPlayer)currentTarget).func_70005_c_());
               EntityOtherPlayerMP newTarget = new EntityOtherPlayerMP(mc.field_71441_e, profile);
               Vec3d extrapolatePosition = MathUtil.extrapolatePlayerPosition((EntityPlayer)currentTarget, (Integer)this.predictTicks.getValue());
               newTarget.func_82149_j((Entity)currentTarget);
               newTarget.field_70165_t = extrapolatePosition.field_72450_a;
               newTarget.field_70163_u = extrapolatePosition.field_72448_b;
               newTarget.field_70161_v = extrapolatePosition.field_72449_c;
               newTarget.func_70606_j(EntityUtil.getHealth((Entity)currentTarget));
               newTarget.field_71071_by.func_70455_b(((EntityPlayer)currentTarget).field_71071_by);
               currentTarget = newTarget;
            }

            return (EntityPlayer)currentTarget;
         }
      }
   }

   private void breakCrystal() {
      if ((Boolean)this.explode.getValue() && this.breakTimer.passedMs((long)(Integer)this.breakDelay.getValue()) && (this.switchMode.getValue() == AutoCrystal.Switch.ALWAYS || this.mainHand || this.offHand)) {
         if ((Integer)this.packets.getValue() == 1 && this.efficientTarget != null) {
            if ((Boolean)this.justRender.getValue()) {
               this.doFakeSwing();
               return;
            }

            if ((Boolean)this.syncedFeetPlace.getValue() && (Boolean)this.gigaSync.getValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
               return;
            }

            this.rotateTo(this.efficientTarget);
            this.attackEntity(this.efficientTarget);
            this.breakTimer.reset();
         } else if (!this.attackList.isEmpty()) {
            if ((Boolean)this.justRender.getValue()) {
               this.doFakeSwing();
               return;
            }

            if ((Boolean)this.syncedFeetPlace.getValue() && (Boolean)this.gigaSync.getValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != AutoCrystal.DamageSync.NONE) {
               return;
            }

            for(int i = 0; i < (Integer)this.packets.getValue(); ++i) {
               Entity entity = (Entity)this.attackList.poll();
               if (entity != null) {
                  this.rotateTo(entity);
                  this.attackEntity(entity);
               }
            }

            this.breakTimer.reset();
         }
      }

   }

   private void attackEntity(Entity entity) {
      if (entity != null) {
         if ((Integer)this.eventMode.getValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE && (Boolean)this.rotateFirst.getValue() && this.rotate.getValue() != AutoCrystal.Rotate.OFF) {
            this.packetUseEntities.add(new CPacketUseEntity(entity));
         } else {
            EntityUtil.attackEntity(entity, (Boolean)this.sync.getValue(), (Boolean)this.breakSwing.getValue());
            brokenPos.add((new BlockPos(entity.func_174791_d())).func_177977_b());
         }
      }

   }

   private void doFakeSwing() {
      if ((Boolean)this.fakeSwing.getValue()) {
         EntityUtil.swingArmNoPacket(EnumHand.MAIN_HAND, mc.field_71439_g);
      }

   }

   private void manualBreaker() {
      if (this.rotate.getValue() != AutoCrystal.Rotate.OFF && (Integer)this.eventMode.getValue() != 2 && this.rotating) {
         EntityPlayerSP var10000;
         if (this.didRotation) {
            var10000 = mc.field_71439_g;
            var10000.field_70125_A = (float)((double)var10000.field_70125_A + 4.0E-4D);
            this.didRotation = false;
         } else {
            var10000 = mc.field_71439_g;
            var10000.field_70125_A = (float)((double)var10000.field_70125_A - 4.0E-4D);
            this.didRotation = true;
         }
      }

      if ((this.offHand || this.mainHand) && (Boolean)this.manual.getValue() && this.manualTimer.passedMs((long)(Integer)this.manualBreak.getValue()) && Mouse.isButtonDown(1) && mc.field_71439_g.func_184592_cb().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151153_ao && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151031_f && mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() != Items.field_151062_by) {
         RayTraceResult result = mc.field_71476_x;
         if (result != null) {
            switch(result.field_72313_a) {
            case ENTITY:
               Entity entity = result.field_72308_g;
               if (entity instanceof EntityEnderCrystal) {
                  EntityUtil.attackEntity(entity, (Boolean)this.sync.getValue(), (Boolean)this.breakSwing.getValue());
                  this.manualTimer.reset();
               }
               break;
            case BLOCK:
               BlockPos mousePos = mc.field_71476_x.func_178782_a().func_177984_a();
               Iterator var4 = mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(mousePos)).iterator();

               while(var4.hasNext()) {
                  Entity target = (Entity)var4.next();
                  if (target instanceof EntityEnderCrystal) {
                     EntityUtil.attackEntity(target, (Boolean)this.sync.getValue(), (Boolean)this.breakSwing.getValue());
                     this.manualTimer.reset();
                  }
               }
            }
         }
      }

   }

   private void rotateTo(Entity entity) {
      switch((AutoCrystal.Rotate)this.rotate.getValue()) {
      case OFF:
         this.rotating = false;
      case PLACE:
      default:
         break;
      case BREAK:
      case ALL:
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), entity.func_174791_d());
         if ((Integer)this.eventMode.getValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE) {
            Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
         } else {
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
         }
      }

   }

   private void rotateToPos(BlockPos pos) {
      switch((AutoCrystal.Rotate)this.rotate.getValue()) {
      case OFF:
         this.rotating = false;
         break;
      case PLACE:
      case ALL:
         float[] angle = MathUtil.calcAngle(mc.field_71439_g.func_174824_e(mc.func_184121_ak()), new Vec3d((double)((float)pos.func_177958_n() + 0.5F), (double)((float)pos.func_177956_o() - 0.5F), (double)((float)pos.func_177952_p() + 0.5F)));
         if ((Integer)this.eventMode.getValue() == 2 && this.threadMode.getValue() == AutoCrystal.ThreadMode.NONE) {
            Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
         } else {
            this.yaw = angle[0];
            this.pitch = angle[1];
            this.rotating = true;
         }
      case BREAK:
      }

   }

   private boolean isDoublePoppable(EntityPlayer player, float damage) {
      if ((Boolean)this.doublePop.getValue()) {
         float health = EntityUtil.getHealth(player);
         if ((double)health <= (Double)this.popHealth.getValue() && (double)damage > (double)health + 0.5D && damage <= (Float)this.popDamage.getValue()) {
            Timer timer = (Timer)this.totemPops.get(player);
            return timer == null || timer.passedMs((long)(Integer)this.popTime.getValue());
         }
      }

      return false;
   }

   private boolean isValid(Entity entity) {
      return entity != null && mc.field_71439_g.func_70068_e(entity) <= MathUtil.square((Float)this.breakRange.getValue()) && (this.raytrace.getValue() == AutoCrystal.Raytrace.NONE || this.raytrace.getValue() == AutoCrystal.Raytrace.PLACE || mc.field_71439_g.func_70685_l(entity) || !mc.field_71439_g.func_70685_l(entity) && mc.field_71439_g.func_70068_e(entity) <= MathUtil.square((Float)this.breaktrace.getValue()));
   }

   private boolean isEligableForFeetSync(EntityPlayer player, BlockPos pos) {
      if ((Boolean)this.holySync.getValue()) {
         BlockPos playerPos = new BlockPos(player.func_174791_d());
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing facing = var4[var6];
            if (facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
               BlockPos holyPos = playerPos.func_177977_b().func_177972_a(facing);
               if (pos.equals(holyPos)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public static enum PredictTimer {
      NONE,
      BREAK,
      PREDICT;
   }

   public static enum AntiFriendPop {
      NONE,
      PLACE,
      BREAK,
      ALL;
   }

   public static enum ThreadMode {
      NONE,
      POOL,
      SOUND,
      WHILE;
   }

   public static enum AutoSwitch {
      NONE,
      TOGGLE,
      ALWAYS;
   }

   public static enum Raytrace {
      NONE,
      PLACE,
      BREAK,
      FULL;
   }

   public static enum Switch {
      ALWAYS,
      BREAKSLOT,
      CALC;
   }

   public static enum Logic {
      BREAKPLACE,
      PLACEBREAK;
   }

   public static enum Target {
      CLOSEST,
      UNSAFE,
      DAMAGE;
   }

   public static enum Rotate {
      OFF,
      PLACE,
      BREAK,
      ALL;
   }

   public static enum DamageSync {
      NONE,
      PLACE,
      BREAK;
   }

   public static enum Settings {
      PLACE,
      BREAK,
      RENDER,
      MISC,
      DEV;
   }

   public static class PlaceInfo {
      private final BlockPos pos;
      private final boolean offhand;
      private final boolean placeSwing;
      private final boolean exactHand;

      public PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand) {
         this.pos = pos;
         this.offhand = offhand;
         this.placeSwing = placeSwing;
         this.exactHand = exactHand;
      }

      public void runPlace() {
         BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand);
      }
   }

   private static class RAutoCrystal implements Runnable {
      private static AutoCrystal.RAutoCrystal instance;
      private AutoCrystal autoCrystal;

      public static AutoCrystal.RAutoCrystal getInstance(AutoCrystal autoCrystal) {
         if (instance == null) {
            instance = new AutoCrystal.RAutoCrystal();
            instance.autoCrystal = autoCrystal;
         }

         return instance;
      }

      public void run() {
         if (this.autoCrystal.threadMode.getValue() != AutoCrystal.ThreadMode.WHILE) {
            if (this.autoCrystal.threadMode.getValue() != AutoCrystal.ThreadMode.NONE && this.autoCrystal.isOn()) {
               while(true) {
                  if (!Phobos.eventManager.ticksOngoing()) {
                     this.autoCrystal.threadOngoing.set(true);
                     Phobos.safetyManager.doSafetyCheck();
                     this.autoCrystal.doAutoCrystal();
                     this.autoCrystal.threadOngoing.set(false);
                     break;
                  }
               }
            }
         } else {
            while(this.autoCrystal.isOn() && this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
               while(Phobos.eventManager.ticksOngoing()) {
               }

               if (this.autoCrystal.shouldInterrupt.get()) {
                  this.autoCrystal.shouldInterrupt.set(false);
                  this.autoCrystal.syncroTimer.reset();
                  this.autoCrystal.thread.interrupt();
                  break;
               }

               this.autoCrystal.threadOngoing.set(true);
               Phobos.safetyManager.doSafetyCheck();
               this.autoCrystal.doAutoCrystal();
               this.autoCrystal.threadOngoing.set(false);

               try {
                  Thread.sleep((long)(Integer)this.autoCrystal.threadDelay.getValue());
               } catch (InterruptedException var2) {
                  this.autoCrystal.thread.interrupt();
                  var2.printStackTrace();
               }
            }
         }

      }
   }
}
