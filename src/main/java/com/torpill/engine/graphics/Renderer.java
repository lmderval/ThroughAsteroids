package com.torpill.engine.graphics;

import com.torpill.engine.world.blocks.Block;
import com.torpill.engine.world.Chunk;
import com.torpill.engine.world.entities.Entity;
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

    private static final int RENDER_DISTANCE = 3;
    private static final int RENDER_DISTANCE_SQUARED = RENDER_DISTANCE * RENDER_DISTANCE;

    private static final float SPECULAR_POWER = 10f;

    private static final Vector4f DEBUG_COLOR = new Vector4f(1f);

    private static final String UNI_PROJECTION = "projection_mat";
    private static final String UNI_MODEL_VIEW = "mv_mat";
    private static final String UNI_TEX_SAMPLER = "tex_sampler";
    private static final String UNI_AMBIENT_LIGHT = "ambient_light";
    private static final String UNI_SPECULAR_POWER = "specular_power";
    private static final String UNI_MATERIAL = "material";
    private static final String UNI_SELECTED = "selected";
    private static final String UNI_POINT_LIGHT = "point_light";
    private static final String UNI_SPOT_LIGHT = "spot_light";
    private static final String UNI_DIRECTIONAL_LIGHT = "directional_light";
    private static final String UNI_DEBUG_COLOR = "debug_color";

    private ShaderProgram main;

    private final Transformation transformation = new Transformation();

    private final Matrix4f perspective_mat = new Matrix4f();

    private Mesh curr_mesh;
    private Texture curr_texture;
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
        main.createUniform(UNI_SELECTED);

        main.createPointLightUniform(UNI_POINT_LIGHT);
        main.createSpotLightUniform(UNI_SPOT_LIGHT);
        main.createDirectionalLightUniform(UNI_DIRECTIONAL_LIGHT);

        main.createMaterialUniform(UNI_MATERIAL);

        main.createUniform(UNI_DEBUG_COLOR);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer
    }

    public void preRender(@NotNull Window window, @NotNull Camera camera, @NotNull Vector3f ambient_light, @NotNull PointLight point_light, @NotNull SpotLight spot_light, @NotNull DirectionalLight directional_light) {
        clear();
        if (window.isResized()) {
            glViewport(0, 0, window.getFramebufferWidth(), window.getFramebufferHeight());
            window.setResized(false);
        }

        transformation.setPerspective(FOV, window.getFramebufferWidth(), window.getFramebufferHeight(), Z_NEAR, Z_FAR, perspective_mat);

        main.bind();

        main.setUniform(UNI_PROJECTION, perspective_mat);
        main.setUniform(UNI_TEX_SAMPLER, 0);
        main.setUniform(UNI_AMBIENT_LIGHT, ambient_light);
        main.setUniform(UNI_SPECULAR_POWER, SPECULAR_POWER);
        main.setUniform(UNI_DEBUG_COLOR, DEBUG_COLOR);

        // Get a copy of the light object and transform its position to view coordinates
        PointLight curr_point_light = new PointLight(point_light);
        // Position
        Vector3f light_pos = curr_point_light.getPosition().mul(2f);
        Vector4f aux = new Vector4f(light_pos, 1f);
        aux.mul(camera.getViewMat());
        curr_point_light.setPosition(aux.x, aux.y, aux.z);

        main.setUniform(UNI_POINT_LIGHT, curr_point_light);

        // Get a copy of the light object and transform its position to view coordinates
        SpotLight curr_spot_light = new SpotLight(spot_light);
        // Position
        light_pos.set(curr_spot_light.getPosition()).mul(2f);
        aux.set(light_pos, 1f);
        aux.mul(camera.getViewMat());
        curr_spot_light.setPosition(aux.x, aux.y, aux.z);
        // Direction
        Vector4f dir = new Vector4f(curr_spot_light.getDirection(), 0f);
        dir.mul(camera.getViewMat());
        curr_spot_light.setDirection(dir.x, dir.y, dir.z);

        main.setUniform(UNI_SPOT_LIGHT, curr_spot_light);

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight curr_directional_light = new DirectionalLight(directional_light);
        // Direction
        dir.set(curr_directional_light.getDirection(), 0f);
        dir.mul(camera.getViewMat());
        curr_directional_light.setDirection(dir.x, dir.y, dir.z);

        main.setUniform(UNI_DIRECTIONAL_LIGHT, curr_directional_light);
    }

    public void renderEntity(@NotNull Entity entity, @NotNull Matrix4f view_mat) {
        curr_mesh = entity.getMesh();
        entity.recalculate(view_mat);
        main.setUniform(UNI_MODEL_VIEW, entity.getModelViewMat());
        main.setUniform(UNI_MATERIAL, curr_mesh.getMaterial());
        curr_mesh.preRender(true);
        curr_mesh.render();
        curr_mesh.postRender();
    }

    private void preRenderBlocks() {
        Block.BLOCK_MESH.preRender(false);
    }

    public void renderBlock(@NotNull Block block, @NotNull Vector3f position, boolean selected, @NotNull Matrix4f view_mat) {
        curr_mesh = block.getMesh();
        curr_texture = curr_mesh.bindTextureWithTest(curr_texture);
        transformation.setModelView(position, view_mat, curr_mat);
        main.setUniform(UNI_MODEL_VIEW, curr_mat);
        main.setUniform(UNI_MATERIAL, curr_mesh.getMaterial());
        main.setUniform(UNI_SELECTED, selected);
        curr_mesh.render();
    }

    private void postRenderBlocks() {
        Block.BLOCK_MESH.postRender();
        curr_mesh = null;
        curr_texture = null;
    }

    public void renderChunk(@NotNull Chunk chunk, @NotNull World world, @NotNull Vector3i position, @NotNull Matrix4f view_mat) {
        Block block;
        Block[] neighbors = new Block[6];
        Vector3f block_position = new Vector3f();
        int x = position.x;
        int z = position.z;
        boolean render;
        int x0, z0;

        preRenderBlocks();
        for (int i = 0; i < chunk.getWidth(); i++) {
            for (int j = 0; j < chunk.getHeight(); j++) {
                for (int k = 0; k < chunk.getDepth(); k++) {
                    block = chunk.getBlock(i, j, k);
                    if (block != null) {
                        x0 = x + i;
                        z0 = z + k;
                        neighbors[0] = world.getBlock(x0 + 1, j, z0);
                        neighbors[1] = world.getBlock(x0 - 1, j, z0);
                        neighbors[2] = world.getBlock(x0, j + 1, z0);
                        neighbors[3] = world.getBlock(x0, j - 1, z0);
                        neighbors[4] = world.getBlock(x0, j, z0 + 1);
                        neighbors[5] = world.getBlock(x0, j, z0 - 1);

                        render = false;
                        for (Block neighbor : neighbors) {
                            if (neighbor == null) {
                                render = true;
                                break;
                            }
                        }

                        if (render) {
                            block_position.set(i + position.x, j, k + position.z);
                            renderBlock(block, block_position, world.isSelected(x0, j, z0), view_mat);
                        }
                    }
                }
            }
        }
        postRenderBlocks();
    }

    public void renderWorld(@NotNull World world, @NotNull Camera camera) {
        Chunk chunk;
        Matrix4f view_mat =  camera.getViewMat();
        int world_width = world.getWidth();
        int world_depth = world.getDepth();
        int chunk_width = world.getChunkWidth();
        int chunk_depth = world.getChunkDepth();
        int camera_chunk_x = (int) (camera.getPosition().x / chunk_width);
        int camera_chunk_z = (int) (camera.getPosition().z / chunk_depth);
        int delta_x, delta_z;
        int start_x = camera_chunk_x - RENDER_DISTANCE + 1;
        int start_z = camera_chunk_z - RENDER_DISTANCE + 1;
        int end_x = start_x + 2 * RENDER_DISTANCE - 2;
        int end_z = start_z + 2 * RENDER_DISTANCE - 2;
        if (start_x < 0) {
            start_x = 0;
        }
        if (start_z < 0) {
            start_z = 0;
        }
        if (end_x >= world_width) {
            end_x = world_width - 1;
        }
        if (end_z >= world_depth) {
            end_z = world_depth - 1;
        }
        int i, k;
        Vector3i position = new Vector3i();
        Vector3f position_f = new Vector3f();
        for (i = start_x; i <= end_x; i++) {
            for (k = start_z; k <= end_z; k++) {
                delta_x = camera_chunk_x - i;
                delta_z = camera_chunk_z - k;
                if (delta_x * delta_x + delta_z * delta_z < RENDER_DISTANCE_SQUARED) {
                    chunk = world.getChunk(i, k);
                    if (chunk != null) {
                        position.set(i * chunk_width, 0,k * chunk_depth);
                        renderChunk(chunk, world, position, view_mat);
                    }
                }
            }
        }
        for (Entity entity : world.getEntities()) {
            position_f.set(entity.getPosition());
            i = (int) Math.floor(position_f.x / chunk_width);
            k = (int) Math.floor(position_f.z / chunk_depth);
            delta_x = camera_chunk_x - i;
            delta_z = camera_chunk_z - k;
            if (delta_x * delta_x + delta_z * delta_z < RENDER_DISTANCE_SQUARED) {
                renderEntity(entity, view_mat);
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
