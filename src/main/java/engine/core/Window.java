package engine.core;

import engine.renderer.Renderer;
import game.GameManager;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
  private final int width, height;
  private final String title;

  // The window handle
  private long window;

  private final Renderer renderer;

  private GameManager gameManager;

  // Game state flags
  private boolean initialized = false;

  // Delta time tracking
  private double lastFrameTime = 0.0;
  private double deltaTime = 0.0;

  public Window() {
    this.width = 1920;
    this.height = 1080;
    this.title = "RPG Strategy Game";

    // Initialize the renderer instance
    this.renderer = Renderer.get();
  }

  public void run() {
    System.out.println("Hello LWJGL " + Version.getVersion() + "!");

    init();
    loop();

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  private void init() {
    // Set up an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW");

    // Configure GLFW
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

    // Create the window
    window = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
    if (window == NULL)
      throw new RuntimeException("Failed to create the GLFW window");

    // Set up callbacks
    setupCallbacks();

    // Get the thread stack and push a new frame
    try (MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(window, pWidth, pHeight);

      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      // Center the window
      glfwSetWindowPos(
          window,
          (vidmode.width() - pWidth.get(0)) / 2,
          (vidmode.height() - pHeight.get(0)) / 2);
    } // the stack frame is popped automatically

    // Make the OpenGL context current
    glfwMakeContextCurrent(window);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(window);
  }

  private void setupCallbacks() {
    // Set up a key callback
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        glfwSetWindowShouldClose(window, true);
    });

    // Mouse callbacks
    glfwSetCursorPosCallback(window, MouseListener::mousePosCallback);
    glfwSetMouseButtonCallback(window, MouseListener::mouseButtonCallback);
    glfwSetScrollCallback(window, MouseListener::mouseScrollCallback);
  }

  private void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    // Initialize renderer
    renderer.init();

    // Initialize game state
    initializeGame();

    // Set the clear color
    glClearColor(0.1f, 0.1f, 0.15f, 1.0f);

    // Initial time
    lastFrameTime = glfwGetTime();

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!glfwWindowShouldClose(window)) {
      // Calculate delta time
      double currentTime = glfwGetTime();
      deltaTime = currentTime - lastFrameTime;
      lastFrameTime = currentTime;

      // Update game state
      update(deltaTime);

      // Render frame
      render();

      // Swap buffers and poll events
      glfwSwapBuffers(window); // swap the color buffers
      glfwPollEvents();

      // End frame for input handlers
      MouseListener.endFrame();
    }
  }

  private void initializeGame() {
    if (!initialized) {
      // Create game manager
      gameManager = new GameManager(renderer);
      initialized = true;
    }
  }

  private void update(double deltaTime) {
    // Update game state
    if (gameManager != null) {
      gameManager.update(deltaTime);
    }
  }

  private void render() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

    // Render the game
    renderer.render();
  }

  // Accessor methods for other game systems
  public Renderer getRenderer() {
    return renderer;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public double getDeltaTime() {
    return deltaTime;
  }
}