package me.earth.phobos.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;

public class ColorUtil {
   private ArrayList<ColorUtil.ColorName> initColorList() {
      ArrayList<ColorUtil.ColorName> colorList = new ArrayList();
      colorList.add(new ColorUtil.ColorName("AliceBlue", 240, 248, 255));
      colorList.add(new ColorUtil.ColorName("AntiqueWhite", 250, 235, 215));
      colorList.add(new ColorUtil.ColorName("Aqua", 0, 255, 255));
      colorList.add(new ColorUtil.ColorName("Aquamarine", 127, 255, 212));
      colorList.add(new ColorUtil.ColorName("Azure", 240, 255, 255));
      colorList.add(new ColorUtil.ColorName("Beige", 245, 245, 220));
      colorList.add(new ColorUtil.ColorName("Bisque", 255, 228, 196));
      colorList.add(new ColorUtil.ColorName("Black", 0, 0, 0));
      colorList.add(new ColorUtil.ColorName("BlanchedAlmond", 255, 235, 205));
      colorList.add(new ColorUtil.ColorName("Blue", 0, 0, 255));
      colorList.add(new ColorUtil.ColorName("BlueViolet", 138, 43, 226));
      colorList.add(new ColorUtil.ColorName("Brown", 165, 42, 42));
      colorList.add(new ColorUtil.ColorName("BurlyWood", 222, 184, 135));
      colorList.add(new ColorUtil.ColorName("CadetBlue", 95, 158, 160));
      colorList.add(new ColorUtil.ColorName("Chartreuse", 127, 255, 0));
      colorList.add(new ColorUtil.ColorName("Chocolate", 210, 105, 30));
      colorList.add(new ColorUtil.ColorName("Coral", 255, 127, 80));
      colorList.add(new ColorUtil.ColorName("CornflowerBlue", 100, 149, 237));
      colorList.add(new ColorUtil.ColorName("Cornsilk", 255, 248, 220));
      colorList.add(new ColorUtil.ColorName("Crimson", 220, 20, 60));
      colorList.add(new ColorUtil.ColorName("Cyan", 0, 255, 255));
      colorList.add(new ColorUtil.ColorName("DarkBlue", 0, 0, 139));
      colorList.add(new ColorUtil.ColorName("DarkCyan", 0, 139, 139));
      colorList.add(new ColorUtil.ColorName("DarkGoldenRod", 184, 134, 11));
      colorList.add(new ColorUtil.ColorName("DarkGray", 169, 169, 169));
      colorList.add(new ColorUtil.ColorName("DarkGreen", 0, 100, 0));
      colorList.add(new ColorUtil.ColorName("DarkKhaki", 189, 183, 107));
      colorList.add(new ColorUtil.ColorName("DarkMagenta", 139, 0, 139));
      colorList.add(new ColorUtil.ColorName("DarkOliveGreen", 85, 107, 47));
      colorList.add(new ColorUtil.ColorName("DarkOrange", 255, 140, 0));
      colorList.add(new ColorUtil.ColorName("DarkOrchid", 153, 50, 204));
      colorList.add(new ColorUtil.ColorName("DarkRed", 139, 0, 0));
      colorList.add(new ColorUtil.ColorName("DarkSalmon", 233, 150, 122));
      colorList.add(new ColorUtil.ColorName("DarkSeaGreen", 143, 188, 143));
      colorList.add(new ColorUtil.ColorName("DarkSlateBlue", 72, 61, 139));
      colorList.add(new ColorUtil.ColorName("DarkSlateGray", 47, 79, 79));
      colorList.add(new ColorUtil.ColorName("DarkTurquoise", 0, 206, 209));
      colorList.add(new ColorUtil.ColorName("DarkViolet", 148, 0, 211));
      colorList.add(new ColorUtil.ColorName("DeepPink", 255, 20, 147));
      colorList.add(new ColorUtil.ColorName("DeepSkyBlue", 0, 191, 255));
      colorList.add(new ColorUtil.ColorName("DimGray", 105, 105, 105));
      colorList.add(new ColorUtil.ColorName("DodgerBlue", 30, 144, 255));
      colorList.add(new ColorUtil.ColorName("FireBrick", 178, 34, 34));
      colorList.add(new ColorUtil.ColorName("FloralWhite", 255, 250, 240));
      colorList.add(new ColorUtil.ColorName("ForestGreen", 34, 139, 34));
      colorList.add(new ColorUtil.ColorName("Fuchsia", 255, 0, 255));
      colorList.add(new ColorUtil.ColorName("Gainsboro", 220, 220, 220));
      colorList.add(new ColorUtil.ColorName("GhostWhite", 248, 248, 255));
      colorList.add(new ColorUtil.ColorName("Gold", 255, 215, 0));
      colorList.add(new ColorUtil.ColorName("GoldenRod", 218, 165, 32));
      colorList.add(new ColorUtil.ColorName("Gray", 128, 128, 128));
      colorList.add(new ColorUtil.ColorName("Green", 0, 128, 0));
      colorList.add(new ColorUtil.ColorName("GreenYellow", 173, 255, 47));
      colorList.add(new ColorUtil.ColorName("HoneyDew", 240, 255, 240));
      colorList.add(new ColorUtil.ColorName("HotPink", 255, 105, 180));
      colorList.add(new ColorUtil.ColorName("IndianRed", 205, 92, 92));
      colorList.add(new ColorUtil.ColorName("Indigo", 75, 0, 130));
      colorList.add(new ColorUtil.ColorName("Ivory", 255, 255, 240));
      colorList.add(new ColorUtil.ColorName("Khaki", 240, 230, 140));
      colorList.add(new ColorUtil.ColorName("Lavender", 230, 230, 250));
      colorList.add(new ColorUtil.ColorName("LavenderBlush", 255, 240, 245));
      colorList.add(new ColorUtil.ColorName("LawnGreen", 124, 252, 0));
      colorList.add(new ColorUtil.ColorName("LemonChiffon", 255, 250, 205));
      colorList.add(new ColorUtil.ColorName("LightBlue", 173, 216, 230));
      colorList.add(new ColorUtil.ColorName("LightCoral", 240, 128, 128));
      colorList.add(new ColorUtil.ColorName("LightCyan", 224, 255, 255));
      colorList.add(new ColorUtil.ColorName("LightGoldenRodYellow", 250, 250, 210));
      colorList.add(new ColorUtil.ColorName("LightGray", 211, 211, 211));
      colorList.add(new ColorUtil.ColorName("LightGreen", 144, 238, 144));
      colorList.add(new ColorUtil.ColorName("LightPink", 255, 182, 193));
      colorList.add(new ColorUtil.ColorName("LightSalmon", 255, 160, 122));
      colorList.add(new ColorUtil.ColorName("LightSeaGreen", 32, 178, 170));
      colorList.add(new ColorUtil.ColorName("LightSkyBlue", 135, 206, 250));
      colorList.add(new ColorUtil.ColorName("LightSlateGray", 119, 136, 153));
      colorList.add(new ColorUtil.ColorName("LightSteelBlue", 176, 196, 222));
      colorList.add(new ColorUtil.ColorName("LightYellow", 255, 255, 224));
      colorList.add(new ColorUtil.ColorName("Lime", 0, 255, 0));
      colorList.add(new ColorUtil.ColorName("LimeGreen", 50, 205, 50));
      colorList.add(new ColorUtil.ColorName("Linen", 250, 240, 230));
      colorList.add(new ColorUtil.ColorName("Magenta", 255, 0, 255));
      colorList.add(new ColorUtil.ColorName("Maroon", 128, 0, 0));
      colorList.add(new ColorUtil.ColorName("MediumAquaMarine", 102, 205, 170));
      colorList.add(new ColorUtil.ColorName("MediumBlue", 0, 0, 205));
      colorList.add(new ColorUtil.ColorName("MediumOrchid", 186, 85, 211));
      colorList.add(new ColorUtil.ColorName("MediumPurple", 147, 112, 219));
      colorList.add(new ColorUtil.ColorName("MediumSeaGreen", 60, 179, 113));
      colorList.add(new ColorUtil.ColorName("MediumSlateBlue", 123, 104, 238));
      colorList.add(new ColorUtil.ColorName("MediumSpringGreen", 0, 250, 154));
      colorList.add(new ColorUtil.ColorName("MediumTurquoise", 72, 209, 204));
      colorList.add(new ColorUtil.ColorName("MediumVioletRed", 199, 21, 133));
      colorList.add(new ColorUtil.ColorName("MidnightBlue", 25, 25, 112));
      colorList.add(new ColorUtil.ColorName("MintCream", 245, 255, 250));
      colorList.add(new ColorUtil.ColorName("MistyRose", 255, 228, 225));
      colorList.add(new ColorUtil.ColorName("Moccasin", 255, 228, 181));
      colorList.add(new ColorUtil.ColorName("NavajoWhite", 255, 222, 173));
      colorList.add(new ColorUtil.ColorName("Navy", 0, 0, 128));
      colorList.add(new ColorUtil.ColorName("OldLace", 253, 245, 230));
      colorList.add(new ColorUtil.ColorName("Olive", 128, 128, 0));
      colorList.add(new ColorUtil.ColorName("OliveDrab", 107, 142, 35));
      colorList.add(new ColorUtil.ColorName("Orange", 255, 165, 0));
      colorList.add(new ColorUtil.ColorName("OrangeRed", 255, 69, 0));
      colorList.add(new ColorUtil.ColorName("Orchid", 218, 112, 214));
      colorList.add(new ColorUtil.ColorName("PaleGoldenRod", 238, 232, 170));
      colorList.add(new ColorUtil.ColorName("PaleGreen", 152, 251, 152));
      colorList.add(new ColorUtil.ColorName("PaleTurquoise", 175, 238, 238));
      colorList.add(new ColorUtil.ColorName("PaleVioletRed", 219, 112, 147));
      colorList.add(new ColorUtil.ColorName("PapayaWhip", 255, 239, 213));
      colorList.add(new ColorUtil.ColorName("PeachPuff", 255, 218, 185));
      colorList.add(new ColorUtil.ColorName("Peru", 205, 133, 63));
      colorList.add(new ColorUtil.ColorName("Pink", 255, 192, 203));
      colorList.add(new ColorUtil.ColorName("Plum", 221, 160, 221));
      colorList.add(new ColorUtil.ColorName("PowderBlue", 176, 224, 230));
      colorList.add(new ColorUtil.ColorName("Purple", 128, 0, 128));
      colorList.add(new ColorUtil.ColorName("Red", 255, 0, 0));
      colorList.add(new ColorUtil.ColorName("RosyBrown", 188, 143, 143));
      colorList.add(new ColorUtil.ColorName("RoyalBlue", 65, 105, 225));
      colorList.add(new ColorUtil.ColorName("SaddleBrown", 139, 69, 19));
      colorList.add(new ColorUtil.ColorName("Salmon", 250, 128, 114));
      colorList.add(new ColorUtil.ColorName("SandyBrown", 244, 164, 96));
      colorList.add(new ColorUtil.ColorName("SeaGreen", 46, 139, 87));
      colorList.add(new ColorUtil.ColorName("SeaShell", 255, 245, 238));
      colorList.add(new ColorUtil.ColorName("Sienna", 160, 82, 45));
      colorList.add(new ColorUtil.ColorName("Silver", 192, 192, 192));
      colorList.add(new ColorUtil.ColorName("SkyBlue", 135, 206, 235));
      colorList.add(new ColorUtil.ColorName("SlateBlue", 106, 90, 205));
      colorList.add(new ColorUtil.ColorName("SlateGray", 112, 128, 144));
      colorList.add(new ColorUtil.ColorName("Snow", 255, 250, 250));
      colorList.add(new ColorUtil.ColorName("SpringGreen", 0, 255, 127));
      colorList.add(new ColorUtil.ColorName("SteelBlue", 70, 130, 180));
      colorList.add(new ColorUtil.ColorName("Tan", 210, 180, 140));
      colorList.add(new ColorUtil.ColorName("Teal", 0, 128, 128));
      colorList.add(new ColorUtil.ColorName("Thistle", 216, 191, 216));
      colorList.add(new ColorUtil.ColorName("Tomato", 255, 99, 71));
      colorList.add(new ColorUtil.ColorName("Turquoise", 64, 224, 208));
      colorList.add(new ColorUtil.ColorName("Violet", 238, 130, 238));
      colorList.add(new ColorUtil.ColorName("Wheat", 245, 222, 179));
      colorList.add(new ColorUtil.ColorName("White", 255, 255, 255));
      colorList.add(new ColorUtil.ColorName("WhiteSmoke", 245, 245, 245));
      colorList.add(new ColorUtil.ColorName("Yellow", 255, 255, 0));
      colorList.add(new ColorUtil.ColorName("YellowGreen", 154, 205, 50));
      return colorList;
   }

   public static int toRGBA(double r, double g, double b, double a) {
      return toRGBA((float)r, (float)g, (float)b, (float)a);
   }

   public String getColorNameFromRgb(int r, int g, int b) {
      ArrayList<ColorUtil.ColorName> colorList = this.initColorList();
      ColorUtil.ColorName closestMatch = null;
      int minMSE = Integer.MAX_VALUE;
      Iterator var8 = colorList.iterator();

      while(var8.hasNext()) {
         ColorUtil.ColorName c = (ColorUtil.ColorName)var8.next();
         int mse = c.computeMSE(r, g, b);
         if (mse < minMSE) {
            minMSE = mse;
            closestMatch = c;
         }
      }

      if (closestMatch != null) {
         return closestMatch.getName();
      } else {
         return "No matched color name.";
      }
   }

   public String getColorNameFromHex(int hexColor) {
      int r = (hexColor & 16711680) >> 16;
      int g = (hexColor & '\uff00') >> 8;
      int b = hexColor & 255;
      return this.getColorNameFromRgb(r, g, b);
   }

   public int colorToHex(Color c) {
      return Integer.decode("0x" + Integer.toHexString(c.getRGB()).substring(2));
   }

   public String getColorNameFromColor(Color color) {
      return this.getColorNameFromRgb(color.getRed(), color.getGreen(), color.getBlue());
   }

   public static int toRGBA(int r, int g, int b) {
      return toRGBA(r, g, b, 255);
   }

   public static int toRGBA(int r, int g, int b, int a) {
      return (r << 16) + (g << 8) + b + (a << 24);
   }

   public static int toARGB(int r, int g, int b, int a) {
      return (new Color(r, g, b, a)).getRGB();
   }

   public static int toRGBA(float r, float g, float b, float a) {
      return toRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
   }

   public static int toRGBA(float[] colors) {
      if (colors.length != 4) {
         throw new IllegalArgumentException("colors[] must have a length of 4!");
      } else {
         return toRGBA(colors[0], colors[1], colors[2], colors[3]);
      }
   }

   public static int toRGBA(double[] colors) {
      if (colors.length != 4) {
         throw new IllegalArgumentException("colors[] must have a length of 4!");
      } else {
         return toRGBA((float)colors[0], (float)colors[1], (float)colors[2], (float)colors[3]);
      }
   }

   public static int toRGBA(Color color) {
      return toRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
   }

   public static int[] toRGBAArray(int colorBuffer) {
      return new int[]{colorBuffer >> 16 & 255, colorBuffer >> 8 & 255, colorBuffer & 255, colorBuffer >> 24 & 255};
   }

   public static int changeAlpha(int origColor, int userInputedAlpha) {
      origColor &= 16777215;
      return userInputedAlpha << 24 | origColor;
   }

   public static class HueCycler {
      public int index = 0;
      public int[] cycles;

      public HueCycler(int cycles) {
         if (cycles <= 0) {
            throw new IllegalArgumentException("cycles <= 0");
         } else {
            this.cycles = new int[cycles];
            double hue = 0.0D;
            double add = 1.0D / (double)cycles;

            for(int i = 0; i < cycles; ++i) {
               this.cycles[i] = Color.HSBtoRGB((float)hue, 1.0F, 1.0F);
               hue += add;
            }

         }
      }

      public void reset() {
         this.index = 0;
      }

      public void reset(int index) {
         this.index = index;
      }

      public int next() {
         int a = this.cycles[this.index];
         ++this.index;
         if (this.index >= this.cycles.length) {
            this.index = 0;
         }

         return a;
      }

      public void setNext() {
         int rgb = this.next();
      }

      public void set() {
         int rgb = this.cycles[this.index];
         float red = (float)(rgb >> 16 & 255) / 255.0F;
         float green = (float)(rgb >> 8 & 255) / 255.0F;
         float blue = (float)(rgb & 255) / 255.0F;
         GL11.glColor3f(red, green, blue);
      }

      public void setNext(float alpha) {
         int rgb = this.next();
         float red = (float)(rgb >> 16 & 255) / 255.0F;
         float green = (float)(rgb >> 8 & 255) / 255.0F;
         float blue = (float)(rgb & 255) / 255.0F;
         GL11.glColor4f(red, green, blue, alpha);
      }

      public int current() {
         return this.cycles[this.index];
      }
   }

   public static class Colors {
      public static final int WHITE = ColorUtil.toRGBA(255, 255, 255, 255);
      public static final int BLACK = ColorUtil.toRGBA(0, 0, 0, 255);
      public static final int RED = ColorUtil.toRGBA(255, 0, 0, 255);
      public static final int GREEN = ColorUtil.toRGBA(0, 255, 0, 255);
      public static final int BLUE = ColorUtil.toRGBA(0, 0, 255, 255);
      public static final int ORANGE = ColorUtil.toRGBA(255, 128, 0, 255);
      public static final int PURPLE = ColorUtil.toRGBA(163, 73, 163, 255);
      public static final int GRAY = ColorUtil.toRGBA(127, 127, 127, 255);
      public static final int DARK_RED = ColorUtil.toRGBA(64, 0, 0, 255);
      public static final int YELLOW = ColorUtil.toRGBA(255, 255, 0, 255);
      public static final int RAINBOW = Integer.MIN_VALUE;
   }

   public static class ColorName {
      public int r;
      public int g;
      public int b;
      public String name;

      public ColorName(String name, int r, int g, int b) {
         this.r = r;
         this.g = g;
         this.b = b;
         this.name = name;
      }

      public int computeMSE(int pixR, int pixG, int pixB) {
         return ((pixR - this.r) * (pixR - this.r) + (pixG - this.g) * (pixG - this.g) + (pixB - this.b) * (pixB - this.b)) / 3;
      }

      public int getR() {
         return this.r;
      }

      public int getG() {
         return this.g;
      }

      public int getB() {
         return this.b;
      }

      public String getName() {
         return this.name;
      }
   }
}
