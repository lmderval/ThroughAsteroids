package com.torpill.asteroids;

import com.torpill.asteroids.gui.pause.NkPauseScene;
import com.torpill.engine.IGameLogic;
import com.torpill.engine.KeyboardInput;
import com.torpill.engine.MouseInput;
import com.torpill.engine.Window;
import com.torpill.engine.graphics.*;
import com.torpill.engine.graphics.post.FBO;
import com.torpill.engine.graphics.post.PostProcessing;
import com.torpill.engine.gui.Nuklear;
import com.torpill.engine.gui.NuklearScene;
import com.torpill.engine.loader.MeshCache;
import com.torpill.engine.world.World;
import com.torpill.engine.world.blocks.Blocks;
import org.jetbrains.annotations.NotNull;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static com.torpill.asteroids.GameState.*;
import static com.torpill.engine.graphics.post.FBO.DEPTH_RENDER_BUFFER;
import static java.lang.Float.POSITIVE_INFINITY;
import static org.lwjgl.glfw.GLFW.*;

public class ThroughAsteroids implements IGameLogic {

    private static final float CAMERA_POS_STEP = 0.6f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Renderer renderer = new Renderer();
    private final Camera camera = new Camera();
    private FBO fbo;

    private final Vector3f ambient_light = new Vector3f(0.1f);
    private final PointLight.Attenuation att = new PointLight.Attenuation(0f, 0.1f, 0.5f);
    private final PointLight point_light = new PointLight(new Vector3f(1f), new Vector3f(10f, 9f, 8f), 0.7f, att);
    private final PointLight spot_point_light = new PointLight(new Vector3f(1f), new Vector3f(10f, 9f, 8f), 1.25f, att);
    private final SpotLight spot_light = new SpotLight(new Vector3f(0f, 0f, -1f), 20f, spot_point_light);
    private final DirectionalLight directional_light = new DirectionalLight(new Vector3f(1f, 0.9f, 0.75f), new Vector3f(-1f, -1f, -1f), 0.8f);

    private final Vector3i direction = new Vector3i();
    private final Vector2f rotation = new Vector2f();
    private final Vector3i playerDirection = new Vector3i();

    private final NuklearScene nkPause = new NkPauseScene();

    public GameState gameState = MENU;
    public GameState lastGameState = MENU;
    private World world;

    private long tick = 0L;
    private int rollTick = 0;

    @Override
    public void init(@NotNull Window window) throws Exception {
        renderer.init();
        fbo = new FBO(Window.getWidth(), Window.getHeight(), DEPTH_RENDER_BUFFER);
        PostProcessing.init();

        Blocks.load();
        MeshCache.load();

        edit(true);
    }

    @Override
    public void input(@NotNull Window window, @NotNull MouseInput mouse_input, @NotNull KeyboardInput keyboard_input) {
        direction.zero();
        if (keyboard_input.isPressed(GLFW_KEY_A)) {
            direction.x -= 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_D)) {
            direction.x += 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_SPACE)) {
            direction.y += 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_LEFT_SHIFT)) {
            direction.y -= 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_W)) {
            direction.z -= 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_S)) {
            direction.z += 1;
        }
        playerDirection.zero();
        if (keyboard_input.isPressed(GLFW_KEY_LEFT)) {
            playerDirection.x += 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_RIGHT)) {
            playerDirection.x -= 1;
        }
    }

    @Override
    public void update(float interval, @NotNull Window window, @NotNull MouseInput mouse_input, @NotNull KeyboardInput keyboard_input) {
        if (gameState == QUIT) {
            window.setShouldClose(true);
            return;
        }
        if (keyboard_input.use(GLFW_KEY_ESCAPE)) {
            switch (gameState) {
                case PAUSE:
                    gameState = lastGameState;
                    break;
                case EDIT:
                case PLAY:
                    lastGameState = gameState;
                    gameState = PAUSE;
            }
        }
        switch (gameState) {
            case PLAY:
                world.getPlayer().rotate(1f);
                world.getPlayer().forward(0.1f);

                Vector3f pos = world.getPlayer().getPosition();
                float dy = 10f;
                camera.setPosition(pos.x, pos.y + dy, pos.z + dy / (float) Math.tan(Math.toRadians(camera.getRotation().x)));

                window.disableCursor();

                camera.updateViewMat();

                tick++;
                break;
            case EDIT:
                // Update camera based on mouse
                rotation.set(mouse_input.getDisplayVec());

                // Update camera position
                camera.move(direction.x * CAMERA_POS_STEP, direction.y * CAMERA_POS_STEP, direction.z * CAMERA_POS_STEP);

                camera.rotate(rotation.x * MOUSE_SENSITIVITY, rotation.y * MOUSE_SENSITIVITY, 0);
                if (camera.getRotation().x > 90f) {
                    camera.getRotation().x = 90f;
                }
                if (camera.getRotation().x < -90f) {
                    camera.getRotation().x = -90f;
                }
                window.disableCursor();

                // Update view matrix
                if (direction.lengthSquared() > 0 || (rotation.lengthSquared() > 0 /* && mouse_input.isRightButtonPressed() */)) {
                    camera.updateViewMat();
                }

                world.getPlayer().applyForce(new Vector3f(playerDirection.x * 20f, 0f, 0f).rotateY((float) Math.toRadians(world.getPlayer().getRotation().y)));
                world.getPlayer().roll(-playerDirection.x * 10f);
                world.getPlayer().update();

                cameraSelection();

                spot_light.setDirection((float) Math.cos(Math.toRadians(tick)), (float) Math.sin(Math.toRadians(tick)), -1f);

                // Next tick
                tick++;
                break;
            default:
                window.showCursor();
        }
    }

    @Override
    public void updateGui(@NotNull Window window, @NotNull Nuklear nk) {
//        nkDemo.update(window, nk);
        if (gameState == PAUSE)
            nkPause.update(window, nk);
    }

    @Override
    public void render(@NotNull Window window) {
        {
            // Nuklear rendering
        }
        if (gameState != MENU) {
            // World rendering
            fbo.bindFrameBuffer();
            renderer.preRender(window, camera, ambient_light, point_light, spot_light, directional_light);
            renderer.renderWorld(world, camera);
            renderer.postRender();
            fbo.unbindFrameBuffer();
            PostProcessing.doPostProcessing(fbo.getColourTexture());
        }
    }

    @Override
    public void cleanup() {
        PostProcessing.cleanup();
        fbo.cleanup();
        renderer.cleanup();
    }

    public void cameraSelection() {
        Vector3f dir = new Vector3f();
        dir = camera.getViewMat().positiveZ(dir).negate();

        Vector3f min = new Vector3f(-.5f, -.5f, -.5f);
        Vector3f max = new Vector3f(.5f, .5f, .5f);
        Vector2f result = new Vector2f();
        int distance = 12;
        float closest = POSITIVE_INFINITY;

        Vector3i selected = new Vector3i(-1);

        float x, y, z;

        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                for (int k = -distance; k <= distance; k++) {
                    if (i * i + j * j + k * k < distance * distance) {
                        x = Math.round(camera.getPosition().x) + i;
                        y = Math.round(camera.getPosition().y) + j;
                        z = Math.round(camera.getPosition().z) + k;
                        if (world.getBlock((int) x, (int) y, (int) z) != null) {
                            min.set(x, y, z);
                            max.set(x, y, z);
                            min.add(-0.5f, -0.5f, -0.5f);
                            max.add(0.5f, 0.5f, 0.5f);
                            if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, result) && result.x < closest) {
                                closest = result.x;
                                selected.set((int) x, (int) y, (int) z);
                            }
                        }
                    }
                }
            }
        }

        world.setSelected(selected);
    }

    private void loadWorld() {

        world = new World(64, 16, 64, 16, 16);
        for (int i = 0; i < world.getChunkWidth() * world.getWidth(); i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < world.getChunkDepth() * world.getDepth(); k++) {
                    world.setBlock(i, j, k, (i + k) % 2 < 1 ? Blocks.DIRT : Blocks.GRASS);
                }
            }
        }
        world.setBlock(10, 4, 0, Blocks.GRASS);
        for (int i = 0; i < 9; i++) {
            for (int k = 0; k < 7; k++) {
                world.setBlock(16 + i, 3, 23 + k, null);
            }
        }

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if ((i - 5f) * (i - 5f) + (j - 5f) * (j - 5f) < 17) {
                    world.setBlock(5 + i, j + 4, 5, Blocks.DIRT);
                }
            }
        }

        world.getPlayer().setPosition(16f, 4.7f, 1f);
        world.getPlayer().setScale(2.0f);

        camera.setPosition(10f, 9f, 12f);
        camera.setRotation(0f, 0f, 0f);
        camera.updateViewMat();
    }

    public void edit(boolean reload) {
        if (reload) loadWorld();
        gameState = EDIT;
    }

    public void play(boolean reload) {
        if (reload) loadWorld();
        gameState = PLAY;

        camera.setRotation(70f, 0f, 0f);
        camera.updateViewMat();
    }

    public void pause() {
        gameState = PAUSE;
    }

    public void resume() {
        gameState = lastGameState;
    }

    public void quit() {
        gameState = QUIT;
    }
}