package com.torpill.engine.graphics;

import com.torpill.engine.world.Block;
import com.torpill.engine.world.Chunk;
import com.torpill.engine.world.Entity;
import com.torpill.engine.Window;
import com.torpill.engine.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private static final float FOV = (float) Math.toRadians(80.0f);

    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;

    private static final int RENDER_DISTANCE = 5;
    private static final int RENDER_DISTANCE_SQUARED = RENDER_DISTANCE * RENDER_DISTANCE;

    private static final float SPECULAR_POWER = 10f;

    private static final Vector3f VEC3_NULL = new Vector3f();

    private static final String UNI_PROJECTION = "projection_mat";
    private static final String UNI_MODEL_VIEW = "mv_mat";
    private static final String UNI_TEX_SAMPLER = "tex_sampler";
    private static final String UNI_AMBIENT_LIGHT = "ambient_light";
    private static final String UNI_SPECULAR_POWER = "specular_power";
    private static final String UNI_MATERIAL = "material";
    private static final String UNI_POINT_LIGHT = "point_light";
    private static final String UNI_DIRECTIONAL_LIGHT = "directional_light";

    private ShaderProgram main;

    private final Transformation transformation = new Transformation();

    private final Matrix4f perspective_mat = new Matrix4f();

    private Mesh curr_mesh;
    private final Matrix4f curr_mat = new Matrix4f();

    public void init() throws Exception {
        setupMain();
    }

    private void setupMain() throws Exception {
        main = new ShaderProgram("main");
        main.createVertexShader();
        main.createFragmentShader();
        main.link();
        main.createUniform(UNI_PROJECTION);
        main.createUniform(UNI_MODEL_VIEW);
        main.createUniform(UNI_TEX_SAMPLER);
        main.createUniform(UNI_AMBIENT_LIGHT);
        main.createUniform(UNI_SPECULAR_POWER);
        main.createMaterialUniform(UNI_MATERIAL);
        main.createPointLightUniform(UNI_POINT_LIGHT);
        main.createDirectionalLightUniform(UNI_DIRECTIONAL_LIGHT);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    public void preRender(@NotNull Window window, @NotNull Camera camera, @NotNull Vector3f ambient_light, @NotNull PointLight point_light, @NotNull DirectionalLight directional_light) {
        clear();
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        transformation.setPerspective(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR, perspective_mat);

        main.bind();

        main.setUniform(UNI_PROJECTION, perspective_mat);
        main.setUniform(UNI_TEX_SAMPLER, 0);
        main.setUniform(UNI_AMBIENT_LIGHT, ambient_light);
        main.setUniform(UNI_SPECULAR_POWER, SPECULAR_POWER);

        // Get a copy of the light object and transform its position to view coordinates
        PointLight curr_point_light = new PointLight(point_light);
        Vector3f light_pos = curr_point_light.getPosition();
        Vector4f aux = new Vector4f(light_pos, 1);
        aux.mul(camera.getViewMat());
        light_pos.x = aux.x;
        light_pos.y = aux.y;
        light_pos.z = aux.z;
        main.setUniform(UNI_POINT_LIGHT, curr_point_light);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight curr_directional_light = new DirectionalLight(directional_light);
        Vector4f dir = new Vector4f(curr_directional_light.getDirection(), 0);
        dir.mul(camera.getViewMat());
        curr_directional_light.setDirection(dir.x, dir.y, dir.z);
        main.setUniform(UNI_DIRECTIONAL_LIGHT, curr_directional_light);
    }

    public void renderEntity(@NotNull Entity entity, @NotNull Camera camera) {
        curr_mesh = entity.getMesh();
        entity.recalculate(camera.getViewMat());
        main.setUniform(UNI_MODEL_VIEW, entity.getModelViewMat());
        main.setUniform(UNI_MATERIAL, curr_mesh.getMaterial());
        curr_mesh.render();
    }

    public void renderEntities(@NotNull Entity[] entities, @NotNull Camera camera) {
        for (Entity entity : entities) {
            renderEntity(entity, camera);
        }
    }

    public void renderBlock(@NotNull Block block, @NotNull Vector3f position, @NotNull Camera camera) {
        curr_mesh = block.getMesh();
        transformation.setModelView(position, 1f, VEC3_NULL, camera.getViewMat(), curr_mat);
        main.setUniform(UNI_MODEL_VIEW, curr_mat);
        main.setUniform(UNI_MATERIAL, curr_mesh.getMaterial());
        curr_mesh.render();
    }

    public void renderChunk(@NotNull Chunk chunk, @NotNull Vector2i position, @NotNull Camera camera) {
        Block block;
        for (int i = 0; i < chunk.getWidth(); i ++) {
            for (int j = 0; j < chunk.getHeight(); j ++) {
                for (int k = 0; k < chunk.getDepth(); k ++) {
                    block = chunk.getBlock(i, j, k);
                    if (block != null) {
                        renderBlock(block, new Vector3f(i + position.x, j, k + position.y), camera);
                    }
                }
            }
        }
    }

    public void renderWorld(@NotNull World world, @NotNull Camera camera) {
        Chunk chunk;
        int chunk_width = world.getChunkWidth();
        int chunk_depth = world.getChunkDepth();
        int camera_chunk_x = (int) (camera.getPosition().x / chunk_width);
        int camera_chunk_z = (int) (camera.getPosition().z / chunk_depth);
        int delta_x, delta_z;
        for (int i = 0; i < world.getWidth(); i ++) {
            for (int k = 0; k < world.getDepth(); k ++) {
                delta_x = camera_chunk_x - i;
                delta_z = camera_chunk_z - k;
                if (delta_x * delta_x + delta_z * delta_z < RENDER_DISTANCE_SQUARED) {
                    chunk = world.getChunk(i, k);
                    if (chunk != null) {
                        renderChunk(chunk, new Vector2i(i * chunk_width, k * chunk_depth), camera);
                    }
                }
            }
        }
    }

    public void postRender() {
        main.unbind();
    }

    public void cleanup() {
        main.cleanup();
    }
}
