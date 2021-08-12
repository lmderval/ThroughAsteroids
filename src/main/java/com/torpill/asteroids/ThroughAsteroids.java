package com.torpill.asteroids;

import com.torpill.engine.*;
import com.torpill.engine.graphics.*;
import com.torpill.engine.loader.MeshCache;
import com.torpill.engine.loader.StaticMeshesLoader;
import com.torpill.engine.loader.TextureCache;
import com.torpill.engine.world.Block;
import com.torpill.engine.world.Chunk;
import com.torpill.engine.world.Entity;
import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;

public class ThroughAsteroids implements IGameLogic {

    private static final float CAMERA_POS_STEP = 0.6f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Renderer renderer = new Renderer();

    private final Camera camera = new Camera();

    private final Vector3f ambient_light = new Vector3f(0.1f);
    private final PointLight.Attenuation att = new PointLight.Attenuation(0f, 0.1f, 0.5f);
    private final PointLight point_light = new PointLight(new Vector3f(1f), new Vector3f(10f, 9f, 8f), 0.7f, att);
    private final PointLight spot_point_light = new PointLight(new Vector3f(1f), new Vector3f(10f, 9f, 8f), 1.25f, att);
    private final SpotLight spot_light = new SpotLight(new Vector3f(0f, 0f, -1f), 20f, spot_point_light);
    private final DirectionalLight directional_light = new DirectionalLight(new Vector3f(1f, 0.9f, 0.75f), new Vector3f(-1f, -1f, -1f), 0.8f);

    private final Vector3i direction = new Vector3i();
    private final Vector2f rotation = new Vector2f();

    private World world;

    private long tick = 0L;

    @Override
    public void init() throws Exception {
        renderer.init();

        Material grass_material = new Material();
        grass_material.setTexture(TextureCache.getInstance().getTexture("textures/blocks/grass.png"));
        Block grass_block = new Block(grass_material);

        Material dirt_material = new Material();
        dirt_material.setTexture(TextureCache.getInstance().getTexture("textures/blocks/dirt.png"));
        Block dirt_block = new Block(dirt_material);

        Mesh mesh = MeshCache.getInstance().getStaticMeshes("models/entities/creeper.obj", "textures/entities")[0];
        mesh.getMaterial().setTexture(TextureCache.getInstance().getTexture("textures/entities/creeper.png"));

        world = new World(64, 16, 64, 16, 16);
        for (int i = 0; i < world.getChunkWidth() * world.getWidth(); i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < world.getChunkDepth() * world.getDepth(); k++) {
                    world.setBlock(i, j, k, (i + k) % 2 < 1 ? dirt_block : grass_block);
                }
            }
        }
        world.setBlock(10, 4, 0, grass_block);
        for (int i = 0; i < 9; i ++) {
            for (int k = 0; k < 7; k ++) {
                world.setBlock(16 + i, 3, 23 + k, null);
            }
        }

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if ((i - 5f) * (i - 5f) + (j - 5f) * (j - 5f) < 17) {
                    world.setBlock(5 + i, j + 4, 5, dirt_block);
                }
            }
        }

        Entity entity = new Entity(mesh);
        entity.setPosition(16f, 3.5f, 0f);
        world.addEntity(entity);

        camera.setPosition(10f, 9f, 12f);
        camera.updateViewMat();
    }

    @Override
    public void input(@NotNull Window window, @NotNull MouseInput mouse_input) {
        direction.zero();
        if (window.isKeyPressed(GLFW_KEY_A)) {
            direction.x -= 1;
        }
        if (window.isKeyPressed(GLFW_KEY_D)) {
            direction.x += 1;
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            direction.y += 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            direction.y -= 1;
        }
        if (window.isKeyPressed(GLFW_KEY_W)) {
            direction.z -= 1;
        }
        if (window.isKeyPressed(GLFW_KEY_S)) {
            direction.z += 1;
        }
    }

    @Override
    public void update(float interval, @NotNull Window window, @NotNull MouseInput mouse_input) {
        // Update camera position
        camera.move(direction.x * CAMERA_POS_STEP, direction.y * CAMERA_POS_STEP, direction.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        rotation.set(mouse_input.getDisplayVec());
//        if (mouse_input.isRightButtonPressed()) {
        camera.rotate(rotation.x * MOUSE_SENSITIVITY, rotation.y * MOUSE_SENSITIVITY, 0);
        if (camera.getRotation().x > 90f) {
            camera.getRotation().x = 90f;
        }
        if (camera.getRotation().x < -90f) {
            camera.getRotation().x = -90f;
        }
        window.hideCursor();
//        } else {
//            window.showCursor();
//        }

        // Update view matrix
        if (direction.lengthSquared() > 0 || (rotation.lengthSquared() > 0 /* && mouse_input.isRightButtonPressed() */)) {
            camera.updateViewMat();
        }

        spot_light.setDirection((float) Math.cos(Math.toRadians(tick)), (float) Math.sin(Math.toRadians(tick)), -1f);

        // Next tick
        tick++;
    }

    @Override
    public void render(@NotNull Window window) {
        renderer.preRender(window, camera, ambient_light, point_light, spot_light, directional_light);

        renderer.renderWorld(world, camera);

        renderer.postRender();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}