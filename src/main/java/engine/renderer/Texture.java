package engine.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class Texture {
  private int textureId;
  private final String filePath;
  private int width, height;

  // Cache to prevent loading the same texture multiple times
  private static final Map<String, Texture> textureCache = new HashMap<>();

  private Texture(String filePath) {
    this.filePath = filePath;

    // Generate texture on GPU
    textureId = GL11.glGenTextures();
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

    // Set texture parameters
    // Repeat image in both directions
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

    // When stretching, pixelate
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    // When shrinking, pixelate
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

    // Load image from file
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer channels = stack.mallocInt(1);

      // Load the image
      ByteBuffer image = STBImage.stbi_load(filePath, w, h, channels, 4);
      if (image == null) {
        throw new RuntimeException(
            "Failed to load texture file: " + filePath + ", reason: " + STBImage.stbi_failure_reason());
      }

      this.width = w.get(0);
      this.height = h.get(0);

      // Upload image data to GPU
      GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
          GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

      // Free image memory
      STBImage.stbi_image_free(image);
    }
  }

  public static Texture getTexture(String filePath) {
    File file = new File(filePath);
    if (!file.exists()) {
      System.err.println("Texture file not found: " + filePath);
      return getDefaultTexture();
    }

    if (textureCache.containsKey(filePath)) {
      return textureCache.get(filePath);
    }

    Texture texture = new Texture(filePath);
    textureCache.put(filePath, texture);
    return texture;
  }

  private static Texture getDefaultTexture() {
    // Create a purple/black checkered texture if the file is not found
    if (textureCache.containsKey("default")) {
      return textureCache.get("default");
    }

    int textureId = GL11.glGenTextures();
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

    // Set texture parameters
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

    // Create a default checker pattern (8x8)
    int size = 16;
    ByteBuffer data = BufferUtils.createByteBuffer(size * size * 4);
    for (int y = 0; y < size; y++) {
      for (int x = 0; x < size; x++) {
        byte color = (x + y) % 2 == 0 ? (byte) 0xFF : (byte) 0x00;
        data.put(color); // R
        data.put((byte) 0x00); // G
        data.put((byte) 0xFF); // B
        data.put((byte) 0xFF); // A
      }
    }
    data.flip();

    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, size, size, 0,
        GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);

    Texture texture = new Texture("default");
    texture.textureId = textureId;
    texture.width = size;
    texture.height = size;
    textureCache.put("default", texture);

    return texture;
  }

  public void bind() {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
  }

  public void unbind() {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getTextureId() {
    return textureId;
  }

  public String getFilePath() {
    return filePath;
  }
}