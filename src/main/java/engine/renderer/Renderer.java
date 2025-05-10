package engine.renderer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
  private static Renderer instance;
  private final List<Sprite> sprites = new ArrayList<>();

  private Renderer() {
    // Private constructor for singleton
  }

  public static Renderer get() {
    if (instance == null) {
      instance = new Renderer();
    }
    return instance;
  }

  public void add(Sprite sprite) {
    sprites.add(sprite);
  }

  public void remove(Sprite sprite) {
    sprites.remove(sprite);
  }

  public void clear() {
    sprites.clear();
  }

  public void render() {
    // Enable texturing
    glEnable(GL_TEXTURE_2D);

    // Enable transparency
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    // Render all sprites
    for (Sprite sprite : sprites) {
      sprite.render();
    }

    // Disable texturing
    glDisable(GL_TEXTURE_2D);

    // Disable transparency
    glDisable(GL_BLEND);
  }

  // Additional utility methods as needed
  public void init() {
    // Set up projection matrix for 2D rendering (0,0 at top-left)
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(0, 1920, 1080, 0, -1, 1);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
  }
}