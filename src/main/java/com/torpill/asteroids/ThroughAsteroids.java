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

    private static final float CAMERA_POS_STEP = 0.2f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Renderer renderer = new Renderer();

    private final Camera camera = new Camera();

    private Entity[] entities;

    private final Vector3f ambient_light = new Vector3f(0.1f);
    private final PointLight.Attenuation att = new PointLight.Attenuation(0.1f, 0f, 0f);
    private final PointLight point_light = new PointLight(new Vector3f(1f), new Vector3f(-10f, 25f, 15f), 20f, att);
    private final DirectionalLight directional_light = new DirectionalLight(new Vector3f(1f, 0.8f, 0.5f), new Vector3f(-1f, -1f, -1f), 0.8f);

    private final Vector3i direction = new Vector3i();
    private final Vector2f rotation = new Vector2f();

    private World world;

    @Override
    public void init() throws Exception {
        renderer.init();

        Mesh mesh =  MeshCache.getInstance().getStaticMeshes("models/entities/creeper.obj", "textures/entities")[0];
        mesh.getMaterial().setTexture(TextureCache.getInstance().getTexture("textures/entities/creeper.png"));

        Entity entity = new Entity(mesh);
        entities = new Entity[]{
                entity
        };
        entity.setPosition(16f, 0.5f, 0f);

        Material grass_material = new Material();
        grass_material.setTexture(TextureCache.getInstance().getTexture("textures/blocks/grass.png"));
        Block grass_block = new Block(grass_material);

        Material dirt_material = new Material();
        dirt_material.setTexture(TextureCache.getInstance().getTexture("textures/blocks/dirt.png"));
        Block dirt_block = new Block(dirt_material);

        world = new World(64, 16, 64, 16, 16);
        world.setBlock(0, 0, 15, dirt_block);
        world.setBlock(0, 0, 17, grass_block);
        world.setBlock(0, 0, 0, grass_block);
        world.setBlock(16, 0, 0, grass_block);
        world.setBlock(9, 0, 1, grass_block);
        world.setBlock(10, 0, 1, grass_block);
        world.setBlock(10, 0, 0, dirt_block);
        world.setBlock(10, 1, 0, grass_block);
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
        if (mouse_input.isRightButtonPressed()) {
            camera.rotate(rotation.x * MOUSE_SENSITIVITY, rotation.y * MOUSE_SENSITIVITY, 0);
            if (camera.getRotation().x > 90f) {
                camera.getRotation().x = 90f;
            }
            if (camera.getRotation().x < -90f) {
                camera.getRotation().x = -90f;
            }
            window.hideCursor();
        } else {
            window.showCursor();
        }

        // Update view matrix
        if (direction.lengthSquared() > 0 || (rotation.lengthSquared() > 0 && mouse_input.isRightButtonPressed())) {
            camera.updateViewMat();
        }
    }

    @Override
    public void render(@NotNull Window window) {
        renderer.preRender(window, camera, ambient_light, point_light, directional_light);

        renderer.renderWorld(world, camera);
        renderer.renderEntities(entities, camera);

        renderer.postRender();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}