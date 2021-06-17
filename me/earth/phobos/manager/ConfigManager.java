package me.earth.phobos.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.NoDDoS;
import me.earth.phobos.features.modules.render.XRay;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.EnumConverter;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Util;

public class ConfigManager implements Util {
   public ArrayList<Feature> features = new ArrayList();
   public String config = "phobos/config/";
   public boolean loadingConfig;
   public boolean savingConfig;

   public void loadConfig(String name) {
      this.loadingConfig = true;
      List<File> files = (List)Arrays.stream((Object[])Objects.requireNonNull((new File("phobos")).listFiles())).filter(File::isDirectory).collect(Collectors.toList());
      if (files.contains(new File("phobos/" + name + "/"))) {
         this.config = "phobos/" + name + "/";
      } else {
         this.config = "phobos/config/";
      }

      Phobos.friendManager.onLoad();
      Iterator var3 = this.features.iterator();

      while(var3.hasNext()) {
         Feature feature = (Feature)var3.next();

         try {
            this.loadSettings(feature);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      this.saveCurrentConfig();
      this.loadingConfig = false;
   }

   public void saveConfig(String name) {
      this.savingConfig = true;
      this.config = "phobos/" + name + "/";
      File path = new File(this.config);
      if (!path.exists()) {
         path.mkdir();
      }

      Phobos.friendManager.saveFriends();
      Iterator var3 = this.features.iterator();

      while(var3.hasNext()) {
         Feature feature = (Feature)var3.next();

         try {
            this.saveSettings(feature);
         } catch (IOException var6) {
            var6.printStackTrace();
         }
      }

      this.saveCurrentConfig();
      this.savingConfig = false;
   }

   public void saveCurrentConfig() {
      File currentConfig = new File("phobos/currentconfig.txt");

      try {
         FileWriter writer;
         String tempConfig;
         if (currentConfig.exists()) {
            writer = new FileWriter(currentConfig);
            tempConfig = this.config.replaceAll("/", "");
            writer.write(tempConfig.replaceAll("phobos", ""));
            writer.close();
         } else {
            currentConfig.createNewFile();
            writer = new FileWriter(currentConfig);
            tempConfig = this.config.replaceAll("/", "");
            writer.write(tempConfig.replaceAll("phobos", ""));
            writer.close();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public String loadCurrentConfig() {
      File currentConfig = new File("phobos/currentconfig.txt");
      String name = "config";

      try {
         if (currentConfig.exists()) {
            Scanner reader;
            for(reader = new Scanner(currentConfig); reader.hasNextLine(); name = reader.nextLine()) {
            }

            reader.close();
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return name;
   }

   public void resetConfig(boolean saveConfig, String name) {
      Iterator var3 = this.features.iterator();

      while(var3.hasNext()) {
         Feature feature = (Feature)var3.next();
         feature.reset();
      }

      if (saveConfig) {
         this.saveConfig(name);
      }

   }

   public void saveSettings(Feature feature) throws IOException {
      new JsonObject();
      File directory = new File(this.config + this.getDirectory(feature));
      if (!directory.exists()) {
         directory.mkdir();
      }

      String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
      Path outputFile = Paths.get(featureName);
      if (!Files.exists(outputFile, new LinkOption[0])) {
         Files.createFile(outputFile);
      }

      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      String json = gson.toJson(this.writeSettings(feature));
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile)));
      writer.write(json);
      writer.close();
   }

   public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
      String var3 = setting.getType();
      byte var4 = -1;
      switch(var3.hashCode()) {
      case -1808118735:
         if (var3.equals("String")) {
            var4 = 4;
         }
         break;
      case -672261858:
         if (var3.equals("Integer")) {
            var4 = 3;
         }
         break;
      case 2070621:
         if (var3.equals("Bind")) {
            var4 = 5;
         }
         break;
      case 2165025:
         if (var3.equals("Enum")) {
            var4 = 6;
         }
         break;
      case 67973692:
         if (var3.equals("Float")) {
            var4 = 2;
         }
         break;
      case 1729365000:
         if (var3.equals("Boolean")) {
            var4 = 0;
         }
         break;
      case 2052876273:
         if (var3.equals("Double")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         setting.setValue(element.getAsBoolean());
         break;
      case 1:
         setting.setValue(element.getAsDouble());
         break;
      case 2:
         setting.setValue(element.getAsFloat());
         break;
      case 3:
         setting.setValue(element.getAsInt());
         break;
      case 4:
         String str = element.getAsString();
         setting.setValue(str.replace("_", " "));
         break;
      case 5:
         setting.setValue((new Bind.BindConverter()).doBackward(element));
         break;
      case 6:
         try {
            EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
            Enum value = converter.doBackward(element);
            setting.setValue(value == null ? setting.getDefaultValue() : value);
         } catch (Exception var8) {
         }
         break;
      default:
         Phobos.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
      }

   }

   public void init() {
      this.features.addAll(Phobos.moduleManager.modules);
      this.features.add(Phobos.friendManager);
      String name = this.loadCurrentConfig();
      this.loadConfig(name);
      Phobos.LOGGER.info("Config loaded.");
   }

   private void loadSettings(Feature feature) throws IOException {
      String featureName = this.config + this.getDirectory(feature) + feature.getName() + ".json";
      Path featurePath = Paths.get(featureName);
      if (Files.exists(featurePath, new LinkOption[0])) {
         this.loadPath(featurePath, feature);
      }
   }

   private void loadPath(Path path, Feature feature) throws IOException {
      InputStream stream = Files.newInputStream(path);

      try {
         loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
      } catch (IllegalStateException var5) {
         Phobos.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
         loadFile(new JsonObject(), feature);
      }

      stream.close();
   }

   private static void loadFile(JsonObject input, Feature feature) {
      Iterator var2 = input.entrySet().iterator();

      while(true) {
         String settingName;
         boolean settingFound;
         do {
            if (!var2.hasNext()) {
               return;
            }

            Entry<String, JsonElement> entry = (Entry)var2.next();
            settingName = (String)entry.getKey();
            JsonElement element = (JsonElement)entry.getValue();
            if (feature instanceof FriendManager) {
               try {
                  Phobos.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
               } catch (Exception var10) {
                  var10.printStackTrace();
               }
               break;
            }

            settingFound = false;
            Iterator var7 = feature.getSettings().iterator();

            while(var7.hasNext()) {
               Setting setting = (Setting)var7.next();
               if (settingName.equals(setting.getName())) {
                  try {
                     setValueFromJson(feature, setting, element);
                  } catch (Exception var11) {
                     var11.printStackTrace();
                  }

                  settingFound = true;
               }
            }
         } while(settingFound);

         if (feature instanceof XRay) {
            feature.register(new Setting(settingName, true, (v) -> {
               return (Boolean)((XRay)feature).showBlocks.getValue();
            }));
         } else if (feature instanceof NoDDoS) {
            NoDDoS noDDoS = (NoDDoS)feature;
            Setting<Boolean> setting = feature.register(new Setting(settingName, true, (v) -> {
               return (Boolean)noDDoS.showServer.getValue() && !(Boolean)noDDoS.full.getValue();
            }));
            noDDoS.registerServer(setting);
         }
      }
   }

   public JsonObject writeSettings(Feature feature) {
      JsonObject object = new JsonObject();
      JsonParser jp = new JsonParser();
      Iterator var4 = feature.getSettings().iterator();

      while(var4.hasNext()) {
         Setting setting = (Setting)var4.next();
         if (setting.isEnumSetting()) {
            EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
            object.add(setting.getName(), converter.doForward((Enum)setting.getValue()));
         } else {
            if (setting.isStringSetting()) {
               String str = (String)setting.getValue();
               setting.setValue(str.replace(" ", "_"));
            }

            try {
               object.add(setting.getName(), jp.parse(setting.getValueAsString()));
            } catch (Exception var7) {
               var7.printStackTrace();
            }
         }
      }

      return object;
   }

   public String getDirectory(Feature feature) {
      String directory = "";
      if (feature instanceof Module) {
         directory = directory + ((Module)feature).getCategory().getName() + "/";
      }

      return directory;
   }
}
