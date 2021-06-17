package me.earth.phobos.features.modules.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

public class Capes extends Module {
   public static Map<String, String[]> UUIDs = new HashMap();
   public static final ResourceLocation THREEVT_CAPE = new ResourceLocation("textures/3vt2.png");
   public static final ResourceLocation ZBOB_CAPE = new ResourceLocation("textures/zb0b.png");
   public static final ResourceLocation OHARE_CAPE = new ResourceLocation("textures/ohare.png");
   private static Capes instance;

   public Capes() {
      super("Capes", "Renders the client's capes", Module.Category.CLIENT, false, false, false);
      UUIDs.put("Megyn", new String[]{"a5e36d37-5fbe-4481-b5be-1f06baee1f1c", "7de842e8-af08-49ed-9d0c-4071e2a99f00", "8ca55379-c872-4299-987d-d20962badd11", "e6e8bf7e-0b23-4d2e-b2ae-c40c5ff4eecc"});
      UUIDs.put("zb0b", new String[]{"0aa3b04f-786a-49c8-bea9-025ee0dd1e85"});
      UUIDs.put("3vt", new String[]{"19bf3f1f-fe06-4c86-bea5-3dad5df89714", "b0836db9-2472-4ba6-a1b7-92c605f5e80d"});
      UUIDs.put("oHare", new String[]{"453e38dd-f4a9-481f-8ebd-8339e89e5445"});
      instance = this;
   }

   public static Capes getInstance() {
      if (instance == null) {
         instance = new Capes();
      }

      return instance;
   }

   public static ResourceLocation getCapeResource(AbstractClientPlayer player) {
      Iterator var1 = UUIDs.keySet().iterator();

      while(var1.hasNext()) {
         String name = (String)var1.next();
         String[] var3 = (String[])UUIDs.get(name);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String uuid = var3[var5];
            if (name.equalsIgnoreCase("3vt") && player.func_110124_au().toString().equals(uuid)) {
               return THREEVT_CAPE;
            }

            if (name.equalsIgnoreCase("Megyn") && player.func_110124_au().toString().equals(uuid)) {
               return THREEVT_CAPE;
            }

            if (name.equalsIgnoreCase("oHare") && player.func_110124_au().toString().equals(uuid)) {
               return OHARE_CAPE;
            }
         }
      }

      return null;
   }

   public static boolean hasCape(UUID uuid) {
      Iterator var1 = UUIDs.keySet().iterator();
      if (var1.hasNext()) {
         String name = (String)var1.next();
         return Arrays.asList((Object[])UUIDs.get(name)).contains(uuid.toString());
      } else {
         return false;
      }
   }
}
