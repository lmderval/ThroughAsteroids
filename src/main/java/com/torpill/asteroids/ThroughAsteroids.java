package com.torpill.asteroids;

import com.torpill.engine.Entity;
import com.torpill.engine.IGameLogic;
import com.torpill.engine.MouseInput;
import com.torpill.engine.Window;
import com.torpill.engine.graphics.*;
import com.torpill.engine.loader.StaticMeshesLoader;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class ThroughAsteroids implements IGameLogic {

    private static final float CAMERA_POS_STEP = 0.4f;
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Renderer renderer = new Renderer();

    private final Camera camera = new Camera();

    private Mesh[] meshes;
    private Entity[] entities;

    private final Vector3i direction = new Vector3i();
    private final Vector2f rotation = new Vector2f();

    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f
        };
        float[] textures = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7
        };
        Texture texture = new Texture("./textures/grassblock.png");
        Material material = new Material(
                new Vector4f(1f, 1f, 1f, 1f),
                new Vector4f(1f, 1f, 1f, 1f),
                new Vector4f(1f, 1f, 1f, 1f),
                1f
        );
        material.setTexture(texture);

//        Mesh mesh = new Mesh(vertices, textures, new float[] {}, indices);
//        mesh.setMaterial(material);
        Mesh mesh =  StaticMeshesLoader.load("models/house/house.obj", "models/house")[0];
        meshes = new Mesh[]{
                mesh
        };

        Entity entity = new Entity(mesh);
        entities = new Entity[]{
                entity
        };
        entity.setPosition(0f, 0f, -1f);
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
    public void update(float interval, @NotNull MouseInput mouse_input) {
        // Update camera position
        camera.move(direction.x * CAMERA_POS_STEP, direction.y * CAMERA_POS_STEP, direction.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        rotation.set(mouse_input.getDisplayVec());
        if (mouse_input.isRightButtonPressed()) {
            camera.rotate(rotation.x * MOUSE_SENSITIVITY, rotation.y * MOUSE_SENSITIVITY, 0);
        }

        // Update view matrix
        if (direction.lengthSquared() > 0 || (rotation.lengthSquared() > 0 && mouse_input.isRightButtonPressed())) {
            camera.updateViewMat();
        }
    }

    @Override
    public void render(@NotNull Window window) {
        renderer.preRender(window);

        renderer.renderEntities(entities, camera);

        renderer.postRender();
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        for (Mesh mesh : meshes) {
            mesh.cleanup();
        }
    }
}