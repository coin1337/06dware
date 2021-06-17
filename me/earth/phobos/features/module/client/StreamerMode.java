package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JFrame;
import javax.swing.JPanel;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.PotionEffect;

public class StreamerMode extends Module {
   private StreamerMode.SecondScreenFrame window = null;
   public Setting<Integer> width = this.register(new Setting("Width", 600, 100, 3160));
   public Setting<Integer> height = this.register(new Setting("Height", 900, 100, 2140));

   public StreamerMode() {
      super("StreamerMode", "Displays client info in a second window.", Module.Category.CLIENT, false, false, false);
   }

   public void onEnable() {
      EventQueue.invokeLater(() -> {
         if (this.window == null) {
            this.window = new StreamerMode.SecondScreenFrame();
         }

         this.window.setVisible(true);
      });
   }

   public void onDisable() {
      if (this.window != null) {
         this.window.setVisible(false);
      }

      this.window = null;
   }

   public void onLogout() {
      if (this.window != null) {
         ArrayList<String> drawInfo = new ArrayList();
         drawInfo.add("Phobos v1.7.2");
         drawInfo.add("");
         drawInfo.add("No Connection.");
         this.window.setToDraw(drawInfo);
      }

   }

   public void onUnload() {
      this.disable();
   }

   public void onLoad() {
      this.disable();
   }

   public void onUpdate() {
      if (this.window != null) {
         ArrayList<String> drawInfo = new ArrayList();
         drawInfo.add("Phobos v1.7.2");
         drawInfo.add("");
         drawInfo.add("Fps: " + Minecraft.field_71470_ab);
         drawInfo.add("TPS: " + Phobos.serverManager.getTPS());
         drawInfo.add("Ping: " + Phobos.serverManager.getPing() + "ms");
         drawInfo.add("Speed: " + Phobos.speedManager.getSpeedKpH() + "km/h");
         drawInfo.add("Time: " + (new SimpleDateFormat("h:mm a")).format(new Date()));
         boolean inHell = mc.field_71441_e.func_180494_b(mc.field_71439_g.func_180425_c()).func_185359_l().equals("Hell");
         int posX = (int)mc.field_71439_g.field_70165_t;
         int posY = (int)mc.field_71439_g.field_70163_u;
         int posZ = (int)mc.field_71439_g.field_70161_v;
         float nether = !inHell ? 0.125F : 8.0F;
         int hposX = (int)(mc.field_71439_g.field_70165_t * (double)nether);
         int hposZ = (int)(mc.field_71439_g.field_70161_v * (double)nether);
         String coordinates = "XYZ " + posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]";
         String text = Phobos.rotationManager.getDirection4D(false);
         drawInfo.add("");
         drawInfo.add(text);
         drawInfo.add(coordinates);
         drawInfo.add("");
         Iterator var11 = Phobos.moduleManager.sortedModules.iterator();

         String potionText;
         while(var11.hasNext()) {
            Module module = (Module)var11.next();
            potionText = TextUtil.stripColor(module.getFullArrayString());
            drawInfo.add(potionText);
         }

         drawInfo.add("");
         var11 = Phobos.potionManager.getOwnPotions().iterator();

         while(var11.hasNext()) {
            PotionEffect effect = (PotionEffect)var11.next();
            potionText = TextUtil.stripColor(Phobos.potionManager.getColoredPotionString(effect));
            drawInfo.add(potionText);
         }

         drawInfo.add("");
         Map<String, Integer> map = EntityUtil.getTextRadarPlayers();
         if (!map.isEmpty()) {
            Iterator var17 = map.entrySet().iterator();

            while(var17.hasNext()) {
               Entry<String, Integer> player = (Entry)var17.next();
               String playerText = TextUtil.stripColor((String)player.getKey());
               drawInfo.add(playerText);
            }
         }

         this.window.setToDraw(drawInfo);
      }

   }

   public class SecondScreen extends JPanel {
      private final int B_WIDTH;
      private final int B_HEIGHT;
      private Font font;
      private ArrayList<String> toDraw;

      public void setToDraw(ArrayList<String> list) {
         this.toDraw = list;
         this.repaint();
      }

      public void setFont(Font font) {
         this.font = font;
      }

      public SecondScreen() {
         this.B_WIDTH = (Integer)StreamerMode.this.width.getValue();
         this.B_HEIGHT = (Integer)StreamerMode.this.height.getValue();
         this.font = new Font("Verdana", 0, 20);
         this.toDraw = new ArrayList();
         this.initBoard();
      }

      public void setWindowSize(int width, int height) {
         this.setPreferredSize(new Dimension(width, height));
      }

      private void initBoard() {
         this.setBackground(Color.black);
         this.setFocusable(true);
         this.setPreferredSize(new Dimension(this.B_WIDTH, this.B_HEIGHT));
      }

      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         this.drawScreen(g);
      }

      private void drawScreen(Graphics g) {
         Font small = this.font;
         FontMetrics metr = this.getFontMetrics(small);
         g.setColor(Color.white);
         g.setFont(small);
         int y = 40;

         for(Iterator var5 = this.toDraw.iterator(); var5.hasNext(); y += 20) {
            String msg = (String)var5.next();
            g.drawString(msg, (this.getWidth() - metr.stringWidth(msg)) / 2, y);
         }

         Toolkit.getDefaultToolkit().sync();
      }
   }

   public class SecondScreenFrame extends JFrame {
      private StreamerMode.SecondScreen panel;

      public SecondScreenFrame() {
         this.initUI();
      }

      private void initUI() {
         this.panel = StreamerMode.this.new SecondScreen();
         this.add(this.panel);
         this.setResizable(true);
         this.pack();
         this.setTitle("Phobos - Info");
         this.setLocationRelativeTo((Component)null);
         this.setDefaultCloseOperation(2);
      }

      public void setToDraw(ArrayList<String> list) {
         this.panel.setToDraw(list);
      }
   }
}
