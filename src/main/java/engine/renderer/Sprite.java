package engine.renderer;

import org.lwjgl.opengl.GL11;

public class Sprite {
  private Texture texture;
  private float x, y; // Position
  private float width, height; // Size
  private float rotation = 0.0f; // Rotation in degrees

  // Color tint (RGBA)
  private float r = 1.0f;
  private float g = 1.0f;
  private float b = 1.0f;
  private float a = 1.0f;

  public Sprite(String texturePath, float x, float y, float width, float height) {
    this.texture = Texture.getTexture(texturePath);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public Sprite(Texture texture, float x, float y, float width, float height) {
    this.texture = texture;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public void render() {
    // Ensure the texture is bound
    texture.bind();

    // Save matrix state
    GL11.glPushMatrix();

    // Translate to position
    GL11.glTranslatef(x + width / 2, y + height / 2, 0);

    // Rotate
    if (rotation != 0) {
      GL11.glRotatef(rotation, 0, 0, 1);
    }

    // Set color/tint
    GL11.glColor4f(r, g, b, a);

    // Draw the textured quad
    GL11.glBegin(GL11.GL_QUADS);
    {
      GL11.glTexCoord2f(0, 0);
      GL11.glVertex2f(-width / 2, -height / 2);

      GL11.glTexCoord2f(1, 0);
      GL11.glVertex2f(width / 2, -height / 2);

      GL11.glTexCoord2f(1, 1);
      GL11.glVertex2f(width / 2, height / 2);

      GL11.glTexCoord2f(0, 1);
      GL11.glVertex2f(-width / 2, height / 2);
    }
    GL11.glEnd();

    // Reset color
    GL11.glColor4f(1, 1, 1, 1);

    // Restore matrix state
    GL11.glPopMatrix();

    // Unbind texture
    texture.unbind();
  }

  // Getters and setters
  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getWidth() {
    return width;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float getHeight() {
    return height;
  }

  public void setHeight(float height) {
    this.height = height;
  }

  public float getRotation() {
    return rotation;
  }

  public void setRotation(float rotation) {
    this.rotation = rotation;
  }

  public void setColor(float r, float g, float b, float a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public Texture getTexture() {
    return texture;
  }

  public void setTexture(Texture texture) {
    this.texture = texture;
  }

  public void setTexture(String texturePath) {
    this.texture = Texture.getTexture(texturePath);
  }
}