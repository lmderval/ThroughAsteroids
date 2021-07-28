package com.torpill.engine.graphics;

import com.torpill.engine.Utils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ShaderProgram {

    private final String name;

    private final int program;

    private int vertexShader;
    private int fragmentShader;

    private final Map<String, Integer> uniforms = new HashMap<>();

    public ShaderProgram(@NotNull String name) throws Exception {
        this.name = name;
        program = glCreateProgram();
        if (program == 0) {
            throw new Exception("Could not create Shader");
        }
    }

    public void createVertexShader() throws Exception {
        createVertexShader(Utils.loadSource("/shaders/" + name + "_vertex.glsl"));
    }

    public void createVertexShader(@NotNull String code) throws Exception {
        vertexShader = createShader(code, GL_VERTEX_SHADER);
    }

    public void createFragmentShader() throws Exception {
        createFragmentShader(Utils.loadSource("/shaders/" + name + "_fragment.glsl"));
    }

    public void createFragmentShader(@NotNull String code) throws Exception {
        fragmentShader = createShader(code, GL_FRAGMENT_SHADER);
    }

    public int createShader(@NotNull String code, int type) throws Exception {
        int shader = glCreateShader(type);
        if (shader == 0) {
            throw new Exception("Error creating shader. Type: " + type);
        }

        glShaderSource(shader, code);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shader, 1024));
        }

        glAttachShader(program, shader);

        return shader;
    }

    public void link() throws Exception {
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(program, 1024));
        }

        if (vertexShader != 0) {
            glDetachShader(program, vertexShader);
        }

        if (fragmentShader != 0) {
            glDetachShader(program, fragmentShader);
        }

        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(program, 1024));
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void createUniform(String uniform) throws Exception {
        int loc = glGetUniformLocation(program, uniform);
        if (loc < 0) {
            throw new Exception("Couldn't find uniform " + uniform);
        }
        uniforms.put(uniform, loc);
    }

    public void setUniform(String uniform, Matrix4f mat) {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);
            mat.get(buf);
            glUniformMatrix4fv(uniforms.get(uniform), false, buf);
        }
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void cleanup() {
        unbind();
        if (program != 0) {
            glDeleteProgram(program);
        }
    }
}
