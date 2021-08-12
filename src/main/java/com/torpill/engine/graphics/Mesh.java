package com.torpill.engine.graphics;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Mesh {

    private final int vao;
    private final int vertices_count;
    private final List<Integer> vbos = new ArrayList<>();

    private Material material;

    public Mesh(float[] vertices, float[] textures, float[] normals, int[] indices) {
        FloatBuffer verticesBuffer = memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();

        FloatBuffer texturesBuffer = memAllocFloat(textures.length);
        texturesBuffer.put(textures).flip();

        FloatBuffer normalsBuffer = memAllocFloat(normals.length);
        normalsBuffer.put(normals).flip();

        IntBuffer indicesBuffer = memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();

        vertices_count = indices.length;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, texturesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

        vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        memFree(verticesBuffer);
        memFree(texturesBuffer);
        memFree(normalsBuffer);
        memFree(indicesBuffer);
    }

    public Mesh(@NotNull Mesh mesh) {
        vao = mesh.vao;
        vertices_count = mesh.vertices_count;
        material = mesh.material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void bindTexture() {
        assert material.getTexture() != null;
        glActiveTexture(GL_TEXTURE0);
        material.getTexture().bind();
    }

    public Texture bindTextureWithTest(Texture texture) {
        if (material != null) {
            Texture material_texture = material.getTexture();
            if (material_texture != null && (material_texture != texture)) {
                bindTexture();
                texture = material_texture;
            }
        }
        return texture;
    }

    public void preRender(boolean bind_texture) {
        if (bind_texture) {
            bindTexture();
        }

        glBindVertexArray(vao);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }

    public void render() {
        glDrawElements(GL_TRIANGLES, vertices_count, GL_UNSIGNED_INT, 0);
    }

    public void postRender() {
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        vbos.forEach(GL15::glDeleteBuffers);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vao);
    }
}
