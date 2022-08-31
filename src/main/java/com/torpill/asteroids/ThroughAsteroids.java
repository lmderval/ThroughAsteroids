package com.torpill.asteroids;

import com.torpill.asteroids.gui.death.NkDeathScene;
import com.torpill.asteroids.gui.pause.edit.NkEditPauseScene;
import com.torpill.asteroids.gui.pause.game.NkGamePauseScene;
import com.torpill.asteroids.gui.win.NkWinScene;
import com.torpill.asteroids.hud.EditHud;
import com.torpill.engine.IGameLogic;
import com.torpill.engine.KeyboardInput;
import com.torpill.engine.MouseInput;
import com.torpill.engine.Window;
import com.torpill.engine.graphics.Camera;
import com.torpill.engine.graphics.Renderer;
import com.torpill.engine.graphics.lights.DirectionalLight;
import com.torpill.engine.graphics.lights.PointLight;
import com.torpill.engine.graphics.lights.SpotLight;
import com.torpill.engine.graphics.post.DeathPostProcessing;
import com.torpill.engine.graphics.post.FBO;
import com.torpill.engine.graphics.post.MainPostProcessing;
import com.torpill.engine.gui.Nuklear;
import com.torpill.engine.hud.IHud;
import com.torpill.engine.loader.MeshCache;
import com.torpill.engine.world.World;
import com.torpill.engine.world.blocks.Block;
import com.torpill.engine.world.blocks.Blocks;
import com.torpill.engine.world.entities.projectiles.EntityProjectile;
import org.jetbrains.annotations.NotNull;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static com.torpill.asteroids.GameState.*;
import static com.torpill.engine.graphics.post.FBO.DEPTH_RENDER_BUFFER;
import static com.torpill.engine.world.blocks.Block.Face.*;
import static java.lang.Float.POSITIVE_INFINITY;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ThroughAsteroids implements IGameLogic {

    private static final float CAMERA_POS_STEP = 0.6f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Renderer renderer = new Renderer();
    private final Camera camera = new Camera();
    private final Vector3f ambient_light = new Vector3f(0.35f);
    //    private final PointLight.Attenuation att = new PointLight.Attenuation(0f, 0.1f, 0.5f);
//    private final PointLight point_light = new PointLight(new Vector3f(1f), new Vector3f(10f, 9f, 8f), 0.7f, att);
    private final PointLight point_light = PointLight.NULL;
    //    private final PointLight spot_point_light = new PointLight(new Vector3f(1f), new Vector3f(10f, 9f, 8f), 1.25f, att);
//    private final SpotLight spot_light = new SpotLight(new Vector3f(0f, 0f, -1f), 20f, spot_point_light);
    private final SpotLight spot_light = SpotLight.NULL;
    private final DirectionalLight directional_light = new DirectionalLight(new Vector3f(1f, 0.9f, 0.75f), new Vector3f(0f, -1f, 1f), 0.45f);
    private final Vector3i direction = new Vector3i();
    //    private final DirectionalLight directional_light = DirectionalLight.NULL;
    private final Vector2f rotation = new Vector2f();
    private final Vector3i playerDirection = new Vector3i();

    private final NkEditPauseScene nkEditPause = new NkEditPauseScene();
    private final NkGamePauseScene nkGamePause = new NkGamePauseScene();
    private final NkDeathScene nkDeath = new NkDeathScene();
    private final NkWinScene nkWin = new NkWinScene();

    private final EditHud editHud = new EditHud();

    public GameState gameState = MENU;
    public GameState lastGameState = MENU;
    private boolean perspective = true;
    private FBO fbo;
    private FBO hudFbo;
    private World world;

    private long tick = 0L;
    private long deathTick = 0L;
    private long winTick = 0L;
    private int clickTick;

    private int currentBlock = 0;

    @Override
    public void init(@NotNull Window window) throws Exception {
        renderer.init();
        fbo = new FBO(window, window.getWidth(), window.getHeight(), DEPTH_RENDER_BUFFER);
        hudFbo = new FBO(window, window.getWidth(), window.getHeight(), DEPTH_RENDER_BUFFER);
        MainPostProcessing.init(window);
        DeathPostProcessing.init(window);

        Blocks.load();
        MeshCache.load();

        editHud.load();

        world = new World(64, 16, 64, 16, 16);
        edit(true);
    }

    @Override
    public void input(@NotNull Window window, @NotNull MouseInput mouse_input, @NotNull KeyboardInput keyboard_input) {
        direction.zero();
        playerDirection.zero();
        if (keyboard_input.isPressed(GLFW_KEY_A)) {
            direction.x -= 1;
            playerDirection.x += 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_D)) {
            direction.x += 1;
            playerDirection.x -= 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_SPACE)) {
            direction.y += 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_LEFT_SHIFT)) {
            direction.y -= 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_W)) {
            direction.z -= 1;
            playerDirection.z += 1;
        }
        if (keyboard_input.isPressed(GLFW_KEY_S)) {
            direction.z += 1;
            playerDirection.z -= 1;
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
                    pause();
            }
        }
        switch (gameState) {
            case PLAY:
                world.setSelected(new Vector3i(-1));

                world.getPlayer().control(-playerDirection.x);
                if (mouse_input.isLeftButtonPressed()) {
                    world.getPlayer().throwLaser(world);
                }
                world.update();

                Vector3f pos = world.getPlayer().getPosition();
                float dy = 10f;
                camera.setPosition(pos.x, pos.y + dy, pos.z + dy / (float) Math.tan(Math.toRadians(camera.getRotation().x)));

                if (world.isTerminated()) {
                    gameState = WIN;
                    winTick = tick;
                }

                if (!world.getPlayer().isAlive()) {
                    gameState = END;
                    deathTick = tick;
                }

                window.disableCursor();

                camera.updateViewMat();

                tick++;
                break;
            case WIN:
                world.getPlayer().resetControl();
                world.update();
            case END:
                window.showCursor();

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

                currentBlock += mouse_input.getScroll();
                currentBlock %= Blocks.blocks.size();
                if (currentBlock < 0) currentBlock += Blocks.blocks.size();
                mouse_input.setScroll(0);
                editHud.setCurrentBlock(currentBlock);

                Vector3i selected = cameraSelection();
                world.setSelected(selected);

                if (clickTick == 0) {
                    if (mouse_input.isLeftButtonPressed()) {
                        world.setBlock(selected.x, selected.y, selected.z, null);
                        clickTick = 3;
                    }
                    if (mouse_input.isMiddlePressed()) {
                        if (selected.x >= 0) {
                            currentBlock = Blocks.blocks.indexOf(world.getBlock(selected.x, selected.y, selected.z));
                        }
                    }
                    if (mouse_input.isRightButtonPressed()) {
                        switch (faceCameraSelection(selected)) {
                            case TOP:
                                world.setBlock(selected.x, selected.y + 1, selected.z, Blocks.blocks.get(currentBlock));
                                break;
                            case BOTTOM:
                                world.setBlock(selected.x, selected.y - 1, selected.z, Blocks.blocks.get(currentBlock));
                                break;
                            case RIGHT:
                                world.setBlock(selected.x - 1, selected.y, selected.z, Blocks.blocks.get(currentBlock));
                                break;
                            case LEFT:
                                world.setBlock(selected.x + 1, selected.y, selected.z, Blocks.blocks.get(currentBlock));
                                break;
                            case FRONT:
                                world.setBlock(selected.x, selected.y, selected.z + 1, Blocks.blocks.get(currentBlock));
                                break;
                            case BACK:
                                world.setBlock(selected.x, selected.y, selected.z - 1, Blocks.blocks.get(currentBlock));
                                break;
                        }
                        clickTick = 4;
                    }
                } else if (clickTick > 0) {
                    clickTick--;
                }

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
        if (gameState == PAUSE) {
            if (lastGameState == PLAY) nkGamePause.update(window, nk);
            else if (lastGameState == EDIT) nkEditPause.update(window, nk);
        } else if (gameState == WIN) {
            nkWin.setOpacity(tick - winTick);
            nkWin.update(window, nk);
        } else if (gameState == END) {
            nkDeath.setOpacity(tick - deathTick);
            nkDeath.update(window, nk);
        }
    }

    @Override
    public void render(@NotNull Window window) {
        if (window.isResized()) {
            glViewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
            fbo.recreate(window.getWidth(), window.getHeight(), DEPTH_RENDER_BUFFER);
            hudFbo.recreate(window.getWidth(), window.getHeight(), DEPTH_RENDER_BUFFER);
            MainPostProcessing.resize(window);
            DeathPostProcessing.resize(window);
            editHud.resize(window);
            window.setResized(false);
        }
        {
            // Nuklear rendering
        }
        if (gameState != MENU) {
            // World rendering
            fbo.bindFrameBuffer();
            window.setClearColor(15, 15, 19, 255);
            renderer.preRender(window, camera, perspective, ambient_light, point_light, spot_light, directional_light);
            renderer.renderWorld(world, camera);
            renderer.postRender();
            fbo.unbindFrameBuffer();
            hudFbo.bindFrameBuffer();
            window.setClearColor(0, 0, 0, 0);
            renderer.clear();
            if (gameState == EDIT) {
                renderer.renderHud(window, editHud, new Vector3f(-1f, -3f, -5f));
            }
            hudFbo.unbindFrameBuffer();
            if (gameState == END) {
                DeathPostProcessing.doPostProcessing(window, fbo.getColourTexture());
            } else {
                MainPostProcessing.doPostProcessing(window, fbo.getColourTexture(), hudFbo.getColourTexture());
            }
        }
    }

    @Override
    public void cleanup() {
        MainPostProcessing.cleanup();
        DeathPostProcessing.cleanup();
        fbo.cleanup();
        hudFbo.cleanup();
        renderer.cleanup();
    }

    private Vector3i cameraSelection() {
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

        return selected;
    }

    private Block.Face faceCameraSelection(@NotNull Vector3i position) {
        Block.Face face = NULL;

        Vector3f dir = new Vector3f();
        dir = camera.getViewMat().positiveZ(dir).negate();

        Vector2f result = new Vector2f();
        float closest = POSITIVE_INFINITY;

        Vector3f min = new Vector3f(-.5f, .5f, -.5f).add(position.x, position.y, position.z);
        Vector3f max = new Vector3f(.5f, .51f, .5f).add(position.x, position.y, position.z);
        if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, result) && result.x < closest) {
            closest = result.x;
            face = TOP;
        }

        max.add(0f, -1.01f, 0f);
        min.add(0f, -1.01f, 0f);
        if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, result) && result.x < closest) {
            closest = result.x;
            face = BOTTOM;
        }

        max.add(-1f, 1f, 0f);
        min.add(-0.01f, 0.01f, 0f);
        if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, result) && result.x < closest) {
            closest = result.x;
            face = RIGHT;
        }

        max.add(1.01f, 0f, 0f);
        min.add(1.01f, 0f, 0f);
        if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, result) && result.x < closest) {
            closest = result.x;
            face = LEFT;
        }

        max.add(-0.01f, 0f, -1f);
        min.add(-1f, 0f, -0.01f);
        if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, result) && result.x < closest) {
            closest = result.x;
            face = BACK;
        }

        max.add(0f, 0f, 1.01f);
        min.add(0f, 0f, 1.01f);
        if (Intersectionf.intersectRayAab(camera.getPosition(), dir, min, max, result) && result.x < closest) {
            face = FRONT;
        }

        return face;
    }

    private void loadWorld() {
//        world = new World(64, 16, 64, 16, 16);
//        for (int i = 0; i < 32; i++) {
//            for (int j = 0; j < 18; j++) {
//                world.setBlock(i, 0, j, Blocks.WALL);
//            }
//        }
//
//        for (int i = 0; i < 18; i++) {
//            for (int j = 9; j < 14; j++) {
//                world.setBlock(i, 0, j, null);
//            }
//        }
//
//        for (int i = 18; i < 23; i++) {
//            for (int j = 9; j < 13; j++) {
//                world.setBlock(i, 0, j, null);
//            }
//        }
//
//        for (int i = 23; i < 32; i++) {
//            for (int j = 10; j < 13; j++) {
//                world.setBlock(i, 0, j, null);
//            }
//        }
//
//        world.setBlock(24, 0, 1, Blocks.WALL_BRICK);
//        for (int i = 25; i < 28; i++) {
//            for (int j = 0; j < 2; j++) {
//                world.setBlock(i, 0, j, Blocks.WALL_BRICK);
//            }
//        }
//        world.setBlock(28, 0, 0, Blocks.WALL_BRICK);
//
//        for (int i = 12; i < 16; i++) {
//            for (int j = 5; j < 7; j++) {
//                world.setBlock(i, 0, j, Blocks.WALL_BRICK);
//            }
//        }
//
//        for (int i = 0; i < 2; i++) {
//            for (int j = 15; j < 18; j++) {
//                world.setBlock(i, 0, j, Blocks.WALL_BRICK);
//            }
//        }
//        world.setBlock(2, 0, 17, Blocks.WALL_BRICK);
//
//        for (int i = 18; i < 22; i++) {
//            for (int j = 16; j < 18; j++) {
//                world.setBlock(i, 0, j, Blocks.WALL_BRICK);
//            }
//        }
//
//        world.setBlock(31, 0, 7, Blocks.WALL_BRICK);
//        world.setBlock(31, 0, 8, Blocks.WALL_BRICK);
//
//        for (int i = 3; i < 6; i++) {
//            world.setBlock(i, 0, 1, Blocks.WALL_ORE);
//        }
//        world.setBlock(4, 0, 2, Blocks.WALL_ORE);
//
//        for (int i = 4; i < 7; i++) {
//            world.setBlock(20, 0, i, Blocks.WALL_ORE);
//        }
//        world.setBlock(21, 0, 5, Blocks.WALL_ORE);
//
//        for (int i = 27; i < 30; i++) {
//            world.setBlock(i, 0, 16, Blocks.WALL_ORE);
//        }
//        world.setBlock(28, 0, 15, Blocks.WALL_ORE);
//
//        world.setBlock(1, 0, 8, Blocks.WALL_DUST);
//        world.setBlock(2, 0, 8, Blocks.WALL_DUST);
//        world.setBlock(4, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(5, 0, 8, Blocks.WALL_DUST);
//        world.setBlock(6, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(7, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(8, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(11, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(13, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(14, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(16, 0, 8, Blocks.WALL_DUST);
//        world.setBlock(18, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(19, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(21, 0, 8, Blocks.WALL_EDGE);
//        world.setBlock(22, 0, 8, Blocks.WALL_DUST);
//        world.setBlock(23, 0, 8, Blocks.WALL_DUST);
//        world.setBlock(23, 0, 9, Blocks.WALL_EDGE);
//        world.setBlock(24, 0, 9, Blocks.WALL_EDGE);
//        world.setBlock(26, 0, 9, Blocks.WALL_DUST);
//        world.setBlock(27, 0, 9, Blocks.WALL_EDGE);
//        world.setBlock(28, 0, 9, Blocks.WALL_EDGE);
//        world.setBlock(29, 0, 9, Blocks.WALL_DUST);
//
//        world.setBlock(0, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(2, 0, 14, Blocks.WALL_DUST);
//        world.setBlock(3, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(4, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(5, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(8, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(9, 0, 14, Blocks.WALL_DUST);
//        world.setBlock(10, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(11, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(13, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(15, 0, 14, Blocks.WALL_DUST);
//        world.setBlock(16, 0, 14, Blocks.WALL_DUST);
//        world.setBlock(17, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(18, 0, 14, Blocks.WALL_EDGE);
//        world.setBlock(18, 0, 13, Blocks.WALL_EDGE);
//        world.setBlock(19, 0, 13, Blocks.WALL_EDGE);
//        world.setBlock(22, 0, 13, Blocks.WALL_EDGE);
//        world.setBlock(23, 0, 14, Blocks.WALL_DUST);
//        world.setBlock(25, 0, 13, Blocks.WALL_EDGE);
//        world.setBlock(26, 0, 13, Blocks.WALL_EDGE);
//        world.setBlock(27, 0, 13, Blocks.WALL_EDGE);
//        world.setBlock(28, 0, 13, Blocks.WALL_EDGE);
//        world.setBlock(29, 0, 14, Blocks.WALL_DUST);
//        world.setBlock(30, 0, 14, Blocks.WALL_DUST);
//        world.setBlock(31, 0, 13, Blocks.WALL_EDGE);

        world.getPlayer().setPosition(2f, -0.3f, 11f);
        world.getPlayer().setRotation(0f, 90f, 0f);

        camera.setPosition(15.5f, 12f, 13.5f);
        camera.setRotation(65f, 0f, 0f);
        camera.updateViewMat();
    }

    public void edit(boolean reload) {
        if (reload) world.load("save/level1");
        world.removeAll(entity -> entity instanceof EntityProjectile);
        gameState = EDIT;
        perspective = true;

        world.getPlayer().setPosition(2f, 0.0f, 11f);
        world.getPlayer().setRotation(0f, 90f, 0f);
        camera.setPosition(world.getPlayer().getPosition());
        camera.move(-6f, 3f, 0f);
        camera.setRotation(world.getPlayer().getRotation());

        camera.updateViewMat();
    }

    public void play(boolean reload) {
        if (reload) world.load("save/level1");
        world.removeAll(entity -> entity instanceof EntityProjectile);
        gameState = PLAY;
        perspective = false;

        world.getPlayer().setAlive(true);
        world.getPlayer().resetControl();
        world.getPlayer().setPosition(2f, 0.0f, 11f);
        world.getPlayer().setRotation(0f, 90f, 0f);

        camera.setRotation(70f, 0f, 0f);
        camera.updateViewMat();

        world.start();
    }

    public void pause() {
        lastGameState = gameState;
        gameState = PAUSE;
    }

    public void save() {
        world.save("save/level1");
    }

    public void resume() {
        gameState = lastGameState;
    }

    public void quit() {
        gameState = QUIT;
    }
}