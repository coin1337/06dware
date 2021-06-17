package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;

public class RenderTest extends Module {
   public RenderTest() {
      super("RenderTest", "RenderTest", Module.Category.RENDER, true, false, false);
   }

   public void onRender3D(Render3DEvent event) {
   }
}
