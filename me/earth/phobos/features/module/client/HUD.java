package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.TextManager;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
   private final Setting<Boolean> renderingUp = this.register(new Setting("RenderingUp", false, "Orientation of the HUD-Elements."));
   public Setting<Boolean> colorSync = this.register(new Setting("Sync", false, "Universal colors for hud."));
   public Setting<Boolean> rainbow = this.register(new Setting("Rainbow", false, "Rainbow hud."));
   public Setting<Integer> factor = this.register(new Setting("Factor", 1, 0, 20, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Boolean> rolling = this.register(new Setting("Rolling", false, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Boolean> staticRainbow = this.register(new Setting("Static", false, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> rainbowSpeed = this.register(new Setting("RSpeed", 20, 0, 100, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> rainbowSaturation = this.register(new Setting("Saturation", 255, 0, 255, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Integer> rainbowBrightness = this.register(new Setting("Brightness", 255, 0, 255, (v) -> {
      return (Boolean)this.rainbow.getValue();
   }));
   public Setting<Boolean> potionIcons = this.register(new Setting("PotionIcons", true, "Draws Potion Icons."));
   public Setting<Boolean> shadow = this.register(new Setting("Shadow", false, "Draws the text with a shadow."));
   private final Setting<HUD.WaterMark> watermark;
   private final Setting<String> customWatermark;
   private final Setting<Boolean> modeVer;
   private final Setting<Boolean> arrayList;
   private final Setting<Boolean> moduleColors;
   public Setting<Integer> animationHorizontalTime;
   public Setting<Integer> animationVerticalTime;
   private final Setting<Boolean> alphabeticalSorting;
   private final Setting<Boolean> serverBrand;
   private final Setting<Boolean> ping;
   private final Setting<Boolean> tps;
   private final Setting<Boolean> fps;
   private final Setting<Boolean> coords;
   private final Setting<Boolean> direction;
   private final Setting<Boolean> speed;
   private final Setting<Boolean> potions;
   private final Setting<Boolean> altPotionsColors;
   public Setting<Boolean> textRadar;
   private final Setting<Boolean> armor;
   private final Setting<Boolean> durability;
   private final Setting<Boolean> percent;
   private final Setting<Boolean> totems;
   private final Setting<Boolean> queue;
   private final Setting<HUD.Greeter> greeter;
   private final Setting<String> spoofGreeter;
   public Setting<Boolean> time;
   private final Setting<HUD.LagNotify> lag;
   private final Setting<Boolean> hitMarkers;
   private final Setting<HUD.Sound> sound;
   public Setting<Integer> hudRed;
   public Setting<Integer> hudGreen;
   public Setting<Integer> hudBlue;
   private final Setting<Boolean> grayNess;
   public Setting<Boolean> potions1;
   public Setting<Boolean> MS;
   private static HUD INSTANCE = new HUD();
   private Map<String, Integer> players;
   private Map<Potion, Color> potionColorMap;
   public Map<Module, Float> moduleProgressMap;
   private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
   private static final ItemStack totem;
   private int color;
   private boolean shouldIncrement;
   private int hitMarkerTimer;
   private final Timer timer;
   private final Timer moduleTimer;
   public Map<Integer, Integer> colorMap;
   private static final ResourceLocation codHitmarker;
   private static final ResourceLocation csgoHitmarker;
   public static final SoundEvent COD_EVENT;
   public static final SoundEvent CSGO_EVENT;

   public HUD() {
      super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
      this.watermark = this.register(new Setting("Logo", HUD.WaterMark.NONE, "WaterMark"));
      this.customWatermark = this.register(new Setting("WatermarkName", "megyn.club b1"));
      this.modeVer = this.register(new Setting("Version", false, (v) -> {
         return this.watermark.getValue() != HUD.WaterMark.NONE;
      }));
      this.arrayList = this.register(new Setting("ActiveModules", false, "Lists the active modules."));
      this.moduleColors = this.register(new Setting("ModuleColors", false, (v) -> {
         return (Boolean)this.arrayList.getValue();
      }));
      this.animationHorizontalTime = this.register(new Setting("AnimationHTime", 500, 1, 1000, (v) -> {
         return (Boolean)this.arrayList.getValue();
      }));
      this.animationVerticalTime = this.register(new Setting("AnimationVTime", 50, 1, 500, (v) -> {
         return (Boolean)this.arrayList.getValue();
      }));
      this.alphabeticalSorting = this.register(new Setting("AlphabeticalSorting", false, (v) -> {
         return (Boolean)this.arrayList.getValue();
      }));
      this.serverBrand = this.register(new Setting("ServerBrand", false, "Brand of the server you are on."));
      this.ping = this.register(new Setting("Ping", false, "Your response time to the server."));
      this.tps = this.register(new Setting("TPS", false, "Ticks per second of the server."));
      this.fps = this.register(new Setting("FPS", false, "Your frames per second."));
      this.coords = this.register(new Setting("Coords", false, "Your current coordinates"));
      this.direction = this.register(new Setting("Direction", false, "The Direction you are facing."));
      this.speed = this.register(new Setting("Speed", false, "Your Speed"));
      this.potions = this.register(new Setting("Potions", false, "Active potion effects"));
      this.altPotionsColors = this.register(new Setting("AltPotionColors", false, (v) -> {
         return (Boolean)this.potions.getValue();
      }));
      this.textRadar = this.register(new Setting("TextRadar", false, "A TextRadar"));
      this.armor = this.register(new Setting("Armor", false, "ArmorHUD"));
      this.durability = this.register(new Setting("Durability", false, "Durability"));
      this.percent = this.register(new Setting("Percent", true, (v) -> {
         return (Boolean)this.armor.getValue();
      }));
      this.totems = this.register(new Setting("Totems", false, "TotemHUD"));
      this.queue = this.register(new Setting("2b2tQueue", false, "Shows the 2b2t queue."));
      this.greeter = this.register(new Setting("Greeter", HUD.Greeter.NONE, "Greets you."));
      this.spoofGreeter = this.register(new Setting("GreeterName", "3arthqu4ke", (v) -> {
         return this.greeter.getValue() == HUD.Greeter.CUSTOM;
      }));
      this.time = this.register(new Setting("Time", false, "The time"));
      this.lag = this.register(new Setting("Lag", HUD.LagNotify.GRAY, "Lag Notifier"));
      this.hitMarkers = this.register(new Setting("HitMarkers", true));
      this.sound = this.register(new Setting("Sound", HUD.Sound.NONE, (v) -> {
         return (Boolean)this.hitMarkers.getValue();
      }));
      this.hudRed = this.register(new Setting("Red", 255, 0, 255, (v) -> {
         return !(Boolean)this.rainbow.getValue();
      }));
      this.hudGreen = this.register(new Setting("Green", 0, 0, 255, (v) -> {
         return !(Boolean)this.rainbow.getValue();
      }));
      this.hudBlue = this.register(new Setting("Blue", 0, 0, 255, (v) -> {
         return !(Boolean)this.rainbow.getValue();
      }));
      this.grayNess = this.register(new Setting("FutureColour", true));
      this.potions1 = this.register(new Setting("LevelPotions", false, (v) -> {
         return (Boolean)this.potions.getValue();
      }));
      this.MS = this.register(new Setting("ms", false, (v) -> {
         return (Boolean)this.ping.getValue();
      }));
      this.players = new HashMap();
      this.potionColorMap = new HashMap();
      this.moduleProgressMap = new HashMap();
      this.timer = new Timer();
      this.moduleTimer = new Timer();
      this.colorMap = new HashMap();
      this.setInstance();
      this.potionColorMap.put(MobEffects.field_76424_c, new Color(124, 175, 198));
      this.potionColorMap.put(MobEffects.field_76421_d, new Color(90, 108, 129));
      this.potionColorMap.put(MobEffects.field_76422_e, new Color(217, 192, 67));
      this.potionColorMap.put(MobEffects.field_76419_f, new Color(74, 66, 23));
      this.potionColorMap.put(MobEffects.field_76420_g, new Color(147, 36, 35));
      this.potionColorMap.put(MobEffects.field_76432_h, new Color(67, 10, 9));
      this.potionColorMap.put(MobEffects.field_76433_i, new Color(67, 10, 9));
      this.potionColorMap.put(MobEffects.field_76430_j, new Color(34, 255, 76));
      this.potionColorMap.put(MobEffects.field_76431_k, new Color(85, 29, 74));
      this.potionColorMap.put(MobEffects.field_76428_l, new Color(205, 92, 171));
      this.potionColorMap.put(MobEffects.field_76429_m, new Color(153, 69, 58));
      this.potionColorMap.put(MobEffects.field_76426_n, new Color(228, 154, 58));
      this.potionColorMap.put(MobEffects.field_76427_o, new Color(46, 82, 153));
      this.potionColorMap.put(MobEffects.field_76441_p, new Color(127, 131, 146));
      this.potionColorMap.put(MobEffects.field_76440_q, new Color(31, 31, 35));
      this.potionColorMap.put(MobEffects.field_76439_r, new Color(31, 31, 161));
      this.potionColorMap.put(MobEffects.field_76438_s, new Color(88, 118, 83));
      this.potionColorMap.put(MobEffects.field_76437_t, new Color(72, 77, 72));
      this.potionColorMap.put(MobEffects.field_76436_u, new Color(78, 147, 49));
      this.potionColorMap.put(MobEffects.field_82731_v, new Color(53, 42, 39));
      this.potionColorMap.put(MobEffects.field_180152_w, new Color(248, 125, 35));
      this.potionColorMap.put(MobEffects.field_76444_x, new Color(37, 82, 165));
      this.potionColorMap.put(MobEffects.field_76443_y, new Color(248, 36, 35));
      this.potionColorMap.put(MobEffects.field_188423_x, new Color(148, 160, 97));
      this.potionColorMap.put(MobEffects.field_188424_y, new Color(206, 255, 255));
      this.potionColorMap.put(MobEffects.field_188425_z, new Color(51, 153, 0));
      this.potionColorMap.put(MobEffects.field_189112_A, new Color(192, 164, 77));
   }

   private void setInstance() {
      INSTANCE = this;
   }

   public static HUD getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new HUD();
      }

      return INSTANCE;
   }

   public void onUpdate() {
      Iterator var1 = Phobos.moduleManager.sortedModules.iterator();

      while(var1.hasNext()) {
         Module module = (Module)var1.next();
         if (module.isDisabled() && module.arrayListOffset == 0.0F) {
            module.sliding = true;
         }
      }

      if (this.timer.passedMs((long)(Integer)Managers.getInstance().textRadarUpdates.getValue())) {
         this.players = this.getTextRadarPlayers();
         this.timer.reset();
      }

      if (this.shouldIncrement) {
         ++this.hitMarkerTimer;
      }

      if (this.hitMarkerTimer == 10) {
         this.hitMarkerTimer = 0;
         this.shouldIncrement = false;
      }

   }

   @SubscribeEvent
   public void onModuleToggle(ClientEvent event) {
      if (event.getFeature() instanceof Module) {
         float i;
         if (event.getStage() == 0) {
            for(i = 0.0F; i <= (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
               if (this.moduleTimer.passedMs(1L)) {
                  this.moduleProgressMap.put((Module)event.getFeature(), (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i);
               }

               this.timer.reset();
            }
         } else if (event.getStage() == 1) {
            for(i = 0.0F; i <= (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
               if (this.moduleTimer.passedMs(1L)) {
                  this.moduleProgressMap.put((Module)event.getFeature(), (float)this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i);
               }

               this.timer.reset();
            }
         }
      }

   }

   public void onRender2D(Render2DEvent event) {
      if (!fullNullCheck()) {
         int colorSpeed = 101 - (Integer)this.rainbowSpeed.getValue();
         float hue = (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.hue : (float)(System.currentTimeMillis() % (long)(360 * colorSpeed)) / (360.0F * (float)colorSpeed);
         int width = this.renderer.scaledWidth;
         int height = this.renderer.scaledHeight;
         float tempHue = hue;

         for(int i = 0; i <= height; ++i) {
            if ((Boolean)this.colorSync.getValue()) {
               this.colorMap.put(i, Color.HSBtoRGB(tempHue, (float)(Integer)Colors.INSTANCE.rainbowSaturation.getValue() / 255.0F, (float)(Integer)Colors.INSTANCE.rainbowBrightness.getValue() / 255.0F));
            } else {
               this.colorMap.put(i, Color.HSBtoRGB(tempHue, (float)(Integer)this.rainbowSaturation.getValue() / 255.0F, (float)(Integer)this.rainbowBrightness.getValue() / 255.0F));
            }

            tempHue += 1.0F / (float)height * (float)(Integer)this.factor.getValue();
         }

         if ((Boolean)this.rainbow.getValue() && !(Boolean)this.rolling.getValue()) {
            this.color = (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColorHex() : Color.HSBtoRGB(hue, (float)(Integer)this.rainbowSaturation.getValue() / 255.0F, (float)(Integer)this.rainbowBrightness.getValue() / 255.0F);
         } else if (!(Boolean)this.rainbow.getValue()) {
            this.color = (Boolean)this.colorSync.getValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA((Integer)this.hudRed.getValue(), (Integer)this.hudGreen.getValue(), (Integer)this.hudBlue.getValue());
         }

         String grayString = (Boolean)this.grayNess.getValue() ? "§7" : "";
         switch((HUD.WaterMark)this.watermark.getValue()) {
         case PHOBOS:
            this.renderer.drawString("Phobos" + ((Boolean)this.modeVer.getValue() ? " v1.7.2" : ""), 2.0F, 2.0F, (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2) : this.color, true);
            break;
         case EARTH:
            this.renderer.drawString("3arthh4ck" + ((Boolean)this.modeVer.getValue() ? " v1.7.2" : ""), 2.0F, 2.0F, (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2) : this.color, true);
            break;
         case CUSTOM:
            this.renderer.drawString((String)this.customWatermark.getValue() + ((Boolean)this.modeVer.getValue() ? " v1.7.2" : ""), 2.0F, 2.0F, (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2) : this.color, true);
         }

         if ((Boolean)this.textRadar.getValue()) {
            this.drawTextRadar(!ToolTips.getInstance().isOff() && (Boolean)ToolTips.getInstance().shulkerSpy.getValue() && (Boolean)ToolTips.getInstance().render.getValue() ? ToolTips.getInstance().getTextRadarY() : 0);
         }

         int j = (Boolean)this.renderingUp.getValue() ? 0 : (mc.field_71462_r instanceof GuiChat ? 14 : 0);
         int i;
         String text;
         TextManager var10000;
         float var10002;
         if ((Boolean)this.arrayList.getValue()) {
            Module module;
            Color moduleColor;
            if ((Boolean)this.renderingUp.getValue()) {
               for(i = 0; i < ((Boolean)this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++i) {
                  module = (Boolean)this.alphabeticalSorting.getValue() ? (Module)Phobos.moduleManager.alphabeticallySortedModules.get(i) : (Module)Phobos.moduleManager.sortedModules.get(i);
                  text = module.getDisplayName() + "§7" + (module.getDisplayInfo() != null ? " [§f" + module.getDisplayInfo() + "§7" + "]" : "");
                  moduleColor = (Color)Phobos.moduleManager.moduleColorMap.get(module);
                  this.renderer.drawString(text, (float)(width - 2 - this.renderer.getStringWidth(text)) + ((Integer)this.animationHorizontalTime.getValue() == 1 ? 0.0F : module.arrayListOffset), (float)(2 + j * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(MathUtil.clamp(2 + j * 10, 0, height)) : ((Boolean)this.moduleColors.getValue() && moduleColor != null ? moduleColor.getRGB() : this.color), true);
                  ++j;
               }
            } else {
               for(i = 0; i < ((Boolean)this.alphabeticalSorting.getValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); ++i) {
                  module = (Boolean)this.alphabeticalSorting.getValue() ? (Module)Phobos.moduleManager.alphabeticallySortedModules.get(Phobos.moduleManager.alphabeticallySortedModules.size() - 1 - i) : (Module)Phobos.moduleManager.sortedModules.get(i);
                  text = module.getDisplayName() + "§7" + (module.getDisplayInfo() != null ? " [§f" + module.getDisplayInfo() + "§7" + "]" : "");
                  moduleColor = (Color)Phobos.moduleManager.moduleColorMap.get(module);
                  var10000 = this.renderer;
                  var10002 = (float)(width - 2 - this.renderer.getStringWidth(text)) + ((Integer)this.animationHorizontalTime.getValue() == 1 ? 0.0F : module.arrayListOffset);
                  j += 10;
                  var10000.drawString(text, var10002, (float)(height - j), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(MathUtil.clamp(height - j, 0, height)) : ((Boolean)this.moduleColors.getValue() && moduleColor != null ? moduleColor.getRGB() : this.color), true);
               }
            }
         }

         i = !(Boolean)this.renderingUp.getValue() ? 0 : (mc.field_71462_r instanceof GuiChat ? 0 : 0);
         String fpsText;
         Iterator var21;
         PotionEffect effect;
         int itemDamage;
         String text;
         if ((Boolean)this.renderingUp.getValue()) {
            int var10003;
            if ((Boolean)this.serverBrand.getValue()) {
               fpsText = grayString + "Server brand " + "§f" + Phobos.serverManager.getServerBrand();
               var10000 = this.renderer;
               var10002 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
               var10003 = height - 2;
               i += 10;
               var10000.drawString(fpsText, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
            }

            if ((Boolean)this.potions.getValue()) {
               var21 = Phobos.potionManager.getOwnPotions().iterator();

               while(var21.hasNext()) {
                  effect = (PotionEffect)var21.next();
                  text = (Boolean)this.altPotionsColors.getValue() ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
                  var10000 = this.renderer;
                  var10002 = (float)(width - (this.renderer.getStringWidth(text) + 2));
                  var10003 = height - 2;
                  i += 10;
                  var10000.drawString(text, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : ((Boolean)this.altPotionsColors.getValue() ? ((Color)this.potionColorMap.get(effect.func_188419_a())).getRGB() : this.color), true);
               }
            }

            if ((Boolean)this.speed.getValue()) {
               fpsText = grayString + "Speed " + "§f" + Phobos.speedManager.getSpeedKpH() + " km/h";
               var10000 = this.renderer;
               var10002 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
               var10003 = height - 2;
               i += 10;
               var10000.drawString(fpsText, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
            }

            if ((Boolean)this.time.getValue()) {
               fpsText = grayString + "Time " + "§f" + (new SimpleDateFormat("h:mm a")).format(new Date());
               var10000 = this.renderer;
               var10002 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
               var10003 = height - 2;
               i += 10;
               var10000.drawString(fpsText, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
            }

            if ((Boolean)this.durability.getValue()) {
               itemDamage = mc.field_71439_g.func_184614_ca().func_77958_k() - mc.field_71439_g.func_184614_ca().func_77952_i();
               if (itemDamage > 0) {
                  text = grayString + "Durability " + "§a" + itemDamage;
                  var10000 = this.renderer;
                  var10002 = (float)(width - (this.renderer.getStringWidth(text) + 2));
                  var10003 = height - 2;
                  i += 10;
                  var10000.drawString(text, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
               }
            }

            if ((Boolean)this.tps.getValue()) {
               fpsText = grayString + "TPS " + "§f" + Phobos.serverManager.getTPS();
               var10000 = this.renderer;
               var10002 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
               var10003 = height - 2;
               i += 10;
               var10000.drawString(fpsText, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
            }

            fpsText = grayString + "FPS " + "§f" + Minecraft.field_71470_ab;
            text = grayString + "Ping " + "§f" + (ServerModule.getInstance().isConnected() ? ServerModule.getInstance().getServerPing() : (long)Phobos.serverManager.getPing()) + ((Boolean)this.MS.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
               if ((Boolean)this.ping.getValue()) {
                  var10000 = this.renderer;
                  var10002 = (float)(width - (this.renderer.getStringWidth(text) + 2));
                  var10003 = height - 2;
                  i += 10;
                  var10000.drawString(text, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
               }

               if ((Boolean)this.fps.getValue()) {
                  var10000 = this.renderer;
                  var10002 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
                  var10003 = height - 2;
                  i += 10;
                  var10000.drawString(fpsText, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
               }
            } else {
               if ((Boolean)this.fps.getValue()) {
                  var10000 = this.renderer;
                  var10002 = (float)(width - (this.renderer.getStringWidth(fpsText) + 2));
                  var10003 = height - 2;
                  i += 10;
                  var10000.drawString(fpsText, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
               }

               if ((Boolean)this.ping.getValue()) {
                  var10000 = this.renderer;
                  var10002 = (float)(width - (this.renderer.getStringWidth(text) + 2));
                  var10003 = height - 2;
                  i += 10;
                  var10000.drawString(text, var10002, (float)(var10003 - i), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(height - i) : this.color, true);
               }
            }
         } else {
            if ((Boolean)this.serverBrand.getValue()) {
               fpsText = grayString + "Server brand " + "§f" + Phobos.serverManager.getServerBrand();
               this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
            }

            if ((Boolean)this.potions.getValue()) {
               var21 = Phobos.potionManager.getOwnPotions().iterator();

               while(var21.hasNext()) {
                  effect = (PotionEffect)var21.next();
                  text = (Boolean)this.altPotionsColors.getValue() ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
                  this.renderer.drawString(text, (float)(width - (this.renderer.getStringWidth(text) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : ((Boolean)this.altPotionsColors.getValue() ? ((Color)this.potionColorMap.get(effect.func_188419_a())).getRGB() : this.color), true);
               }
            }

            if ((Boolean)this.speed.getValue()) {
               fpsText = grayString + "Speed " + "§f" + Phobos.speedManager.getSpeedKpH() + " km/h";
               this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
            }

            if ((Boolean)this.time.getValue()) {
               fpsText = grayString + "Time " + "§f" + (new SimpleDateFormat("h:mm a")).format(new Date());
               this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
            }

            if ((Boolean)this.durability.getValue()) {
               itemDamage = mc.field_71439_g.func_184614_ca().func_77958_k() - mc.field_71439_g.func_184614_ca().func_77952_i();
               if (itemDamage > 0) {
                  text = grayString + "Durability " + "§a" + itemDamage;
                  this.renderer.drawString(text, (float)(width - (this.renderer.getStringWidth(text) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
               }
            }

            if ((Boolean)this.tps.getValue()) {
               fpsText = grayString + "TPS " + "§f" + Phobos.serverManager.getTPS();
               this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
            }

            fpsText = grayString + "FPS " + "§f" + Minecraft.field_71470_ab;
            text = grayString + "Ping " + "§f" + Phobos.serverManager.getPing();
            if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
               if ((Boolean)this.ping.getValue()) {
                  this.renderer.drawString(text, (float)(width - (this.renderer.getStringWidth(text) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
               }

               if ((Boolean)this.fps.getValue()) {
                  this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
               }
            } else {
               if ((Boolean)this.fps.getValue()) {
                  this.renderer.drawString(fpsText, (float)(width - (this.renderer.getStringWidth(fpsText) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
               }

               if ((Boolean)this.ping.getValue()) {
                  this.renderer.drawString(text, (float)(width - (this.renderer.getStringWidth(text) + 2)), (float)(2 + i++ * 10), (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2 + i * 10) : this.color, true);
               }
            }
         }

         boolean inHell = mc.field_71441_e.func_180494_b(mc.field_71439_g.func_180425_c()).func_185359_l().equals("Hell");
         int posX = (int)mc.field_71439_g.field_70165_t;
         int posY = (int)mc.field_71439_g.field_70163_u;
         int posZ = (int)mc.field_71439_g.field_70161_v;
         float nether = !inHell ? 0.125F : 8.0F;
         int hposX = (int)(mc.field_71439_g.field_70165_t * (double)nether);
         int hposZ = (int)(mc.field_71439_g.field_70161_v * (double)nether);
         if ((Boolean)this.renderingUp.getValue()) {
            Phobos.notificationManager.handleNotifications(height - (i + 16));
         } else {
            Phobos.notificationManager.handleNotifications(height - (j + 16));
         }

         i = mc.field_71462_r instanceof GuiChat ? 14 : 0;
         String coordinates = grayString + "XYZ " + "§f" + posX + ", " + posY + ", " + posZ + " " + grayString + "[" + "§f" + hposX + ", " + hposZ + grayString + "]";
         String text = ((Boolean)this.direction.getValue() ? Phobos.rotationManager.getDirection4D(false) + " " : "") + ((Boolean)this.coords.getValue() ? coordinates : "") + "";
         var10000 = this.renderer;
         i += 10;
         float var28 = (float)(height - i);
         int var10004;
         if ((Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue()) {
            i += 10;
            var10004 = (Integer)this.colorMap.get(height - i);
         } else {
            var10004 = this.color;
         }

         var10000.drawString(text, 2.0F, var28, var10004, true);
         if ((Boolean)this.armor.getValue()) {
            this.renderArmorHUD((Boolean)this.percent.getValue());
         }

         if ((Boolean)this.totems.getValue()) {
            this.renderTotemHUD();
         }

         if (this.greeter.getValue() != HUD.Greeter.NONE) {
            this.renderGreeter();
         }

         if (this.lag.getValue() != HUD.LagNotify.NONE) {
            this.renderLag();
         }

         if ((Boolean)this.hitMarkers.getValue() && this.hitMarkerTimer > 0) {
            this.drawHitMarkers();
         }

      }
   }

   public Map<String, Integer> getTextRadarPlayers() {
      return EntityUtil.getTextRadarPlayers();
   }

   public void renderGreeter() {
      int width = this.renderer.scaledWidth;
      String text = "";
      switch((HUD.Greeter)this.greeter.getValue()) {
      case TIME:
         text = text + MathUtil.getTimeOfDay() + mc.field_71439_g.getDisplayNameString();
         break;
      case LONG:
         text = text + "Welcome to Phobos.eu " + mc.field_71439_g.getDisplayNameString() + " :^)";
         break;
      case CUSTOM:
         text = text + (String)this.spoofGreeter.getValue();
         break;
      default:
         text = text + "Welcome " + mc.field_71439_g.getDisplayNameString();
      }

      this.renderer.drawString(text, (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(2) : this.color, true);
   }

   public void renderLag() {
      int width = this.renderer.scaledWidth;
      if (Phobos.serverManager.isServerNotResponding()) {
         String text = (this.lag.getValue() == HUD.LagNotify.GRAY ? "§7" : "§c") + "Server not responding: " + MathUtil.round((float)Phobos.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
         this.renderer.drawString(text, (float)width / 2.0F - (float)this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(20) : this.color, true);
      }

   }

   public void renderArrayList() {
   }

   public void renderTotemHUD() {
      int width = this.renderer.scaledWidth;
      int height = this.renderer.scaledHeight;
      int totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
         return itemStack.func_77973_b() == Items.field_190929_cY;
      }).mapToInt(ItemStack::func_190916_E).sum();
      if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
         totems += mc.field_71439_g.func_184592_cb().func_190916_E();
      }

      if (totems > 0) {
         GlStateManager.func_179098_w();
         int i = width / 2;
         int iteration = false;
         int y = height - 55 - (mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f() ? 10 : 0);
         int x = i - 189 + 180 + 2;
         GlStateManager.func_179126_j();
         RenderUtil.itemRender.field_77023_b = 200.0F;
         RenderUtil.itemRender.func_180450_b(totem, x, y);
         RenderUtil.itemRender.func_180453_a(mc.field_71466_p, totem, x, y, "");
         RenderUtil.itemRender.field_77023_b = 0.0F;
         GlStateManager.func_179098_w();
         GlStateManager.func_179140_f();
         GlStateManager.func_179097_i();
         this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), 16777215);
         GlStateManager.func_179126_j();
         GlStateManager.func_179140_f();
      }

   }

   public void renderArmorHUD(boolean percent) {
      int width = this.renderer.scaledWidth;
      int height = this.renderer.scaledHeight;
      GlStateManager.func_179098_w();
      int i = width / 2;
      int iteration = 0;
      int y = height - 55 - (mc.field_71439_g.func_70090_H() && mc.field_71442_b.func_78763_f() ? 10 : 0);
      Iterator var7 = mc.field_71439_g.field_71071_by.field_70460_b.iterator();

      while(var7.hasNext()) {
         ItemStack is = (ItemStack)var7.next();
         ++iteration;
         if (!is.func_190926_b()) {
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.func_179126_j();
            RenderUtil.itemRender.field_77023_b = 200.0F;
            RenderUtil.itemRender.func_180450_b(is, x, y);
            RenderUtil.itemRender.func_180453_a(mc.field_71466_p, is, x, y, "");
            RenderUtil.itemRender.field_77023_b = 0.0F;
            GlStateManager.func_179098_w();
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            String s = is.func_190916_E() > 1 ? is.func_190916_E() + "" : "";
            this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
            if (percent) {
               int dmg = false;
               int itemDurability = is.func_77958_k() - is.func_77952_i();
               float green = ((float)is.func_77958_k() - (float)is.func_77952_i()) / (float)is.func_77958_k();
               float red = 1.0F - green;
               int dmg;
               if (percent) {
                  dmg = 100 - (int)(red * 100.0F);
               } else {
                  dmg = itemDurability;
               }

               this.renderer.drawStringWithShadow(dmg + "", (float)(x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float)(y - 11), ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0));
            }
         }
      }

      GlStateManager.func_179126_j();
      GlStateManager.func_179140_f();
   }

   public void drawHitMarkers() {
      ScaledResolution resolution = new ScaledResolution(mc);
      RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0F - 4.0F, (float)resolution.func_78328_b() / 2.0F - 4.0F, (float)resolution.func_78326_a() / 2.0F - 8.0F, (float)resolution.func_78328_b() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
      RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0F + 4.0F, (float)resolution.func_78328_b() / 2.0F - 4.0F, (float)resolution.func_78326_a() / 2.0F + 8.0F, (float)resolution.func_78328_b() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
      RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0F - 4.0F, (float)resolution.func_78328_b() / 2.0F + 4.0F, (float)resolution.func_78326_a() / 2.0F - 8.0F, (float)resolution.func_78328_b() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
      RenderUtil.drawLine((float)resolution.func_78326_a() / 2.0F + 4.0F, (float)resolution.func_78328_b() / 2.0F + 4.0F, (float)resolution.func_78326_a() / 2.0F + 8.0F, (float)resolution.func_78328_b() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
   }

   public void drawTextRadar(int yOffset) {
      if (!this.players.isEmpty()) {
         int y = this.renderer.getFontHeight() + 7 + yOffset;

         int textheight;
         for(Iterator var3 = this.players.entrySet().iterator(); var3.hasNext(); y += textheight) {
            Entry<String, Integer> player = (Entry)var3.next();
            String text = (String)player.getKey() + " ";
            textheight = this.renderer.getFontHeight() + 1;
            this.renderer.drawString(text, 2.0F, (float)y, (Boolean)this.rolling.getValue() && (Boolean)this.rainbow.getValue() ? (Integer)this.colorMap.get(y) : this.color, true);
         }
      }

   }

   static {
      totem = new ItemStack(Items.field_190929_cY);
      codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
      csgoHitmarker = new ResourceLocation("earthhack", "csgo_hitmarker");
      COD_EVENT = new SoundEvent(codHitmarker);
      CSGO_EVENT = new SoundEvent(csgoHitmarker);
   }

   public static enum Sound {
      NONE,
      COD,
      CSGO;
   }

   public static enum WaterMark {
      NONE,
      PHOBOS,
      EARTH,
      CUSTOM;
   }

   public static enum LagNotify {
      NONE,
      RED,
      GRAY;
   }

   public static enum Greeter {
      NONE,
      NAME,
      TIME,
      LONG,
      CUSTOM;
   }
}
