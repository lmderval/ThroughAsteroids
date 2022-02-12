package com.torpill.engine.graphics.shaders;

import com.torpill.engine.Utils;
import com.torpill.engine.graphics.lights.DirectionalLight;
import com.torpill.engine.graphics.meshes.Material;
import com.torpill.engine.graphics.lights.PointLight;
import com.torpill.engine.graphics.lights.SpotLight;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20C.glDeleteProgram;
import static org.lwjgl.opengl.GL20C.glDeleteShader;
import static org.lwjgl.opengl.GL20C.glDetachShader;
import static org.lwjgl.system.MemoryStack.stackPush;

public abstract class ShaderProgram {

    private final String vertexName;
    private final String fragmentName;

    private final int program;

    private int vertexShader;
    private int fragmentShader;

    private final Map<String, Integer> uniforms = new HashMap<>();

    public ShaderProgram(@NotNull String name) throws Exception {
        this(name, name);
    }

    public ShaderProgram(@NotNull String vertexName, @NotNull String fragmentName) throws Exception {
        this.vertexName = vertexName;
        this.fragmentName = fragmentName;
        program = glCreateProgram();
        if (program == 0) {
            throw new Exception("Could not create Shader");
        }
    }

    public abstract void setup() throws Exception;

    public void createVertexShader() throws Exception {
        createVertexShader(Utils.loadSource("/shaders/" + vertexName + "_vertex.glsl"));
    }

    public void createVertexShader(@NotNull String code) throws Exception {
        vertexShader = createShader(code, GL_VERTEX_SHADER);
    }

    public void createFragmentShader() throws Exception {
        createFragmentShader(Utils.loadSource("/shaders/" + fragmentName + "_fragment.glsl"));
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

    public void createUniform(@NotNull String uniform) throws Exception {
        int loc = glGetUniformLocation(program, uniform);
        if (loc < 0) {
            throw new Exception("Couldn't find uniform " + uniform);
        }
        uniforms.put(uniform, loc);
    }

    public void createPointLightUniform(@NotNull String uniform) throws Exception {
        createUniform(uniform + ".color");
        createUniform(uniform + ".position");
        createUniform(uniform + ".intensity");
        createUniform(uniform + ".att.constant");
        createUniform(uniform + ".att.linear");
        createUniform(uniform + ".att.exponent");
    }

    public void createSpotLightUniform(@NotNull String uniform) throws Exception {
        createUniform(uniform + ".direction");
        createUniform(uniform + ".cut_off");
        createPointLightUniform(uniform + ".point");
    }

    public void createDirectionalLightUniform(@NotNull String uniform) throws Exception {
        createUniform(uniform + ".color");
        createUniform(uniform + ".direction");
        createUniform(uniform + ".intensity");
    }

    public void createMaterialUniform(@NotNull String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".emissive");
        createUniform(uniformName + ".reflectance");
        createUniform(uniformName + ".emissivity");
        createUniform(uniformName + ".is_textured");
    }

    public void setUniform(@NotNull String uniform, @NotNull Matrix4f value) {
        try (MemoryStack stack = stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);
            value.get(buf);
            glUniformMatrix4fv(uniforms.get(uniform), false, buf);
        }
    }

    public void setUniform(@NotNull String uniform, @NotNull Vector3f value) {
        glUniform3f(uniforms.get(uniform), value.x, value.y, value.z);
    }

    public void setUniform(@NotNull String uniform, @NotNull Vector4f value) {
        glUniform4f(uniforms.get(uniform), value.x, value.y, value.z, value.w);
    }

    public void setUniform(@NotNull String uniform, int value) {
        glUniform1i(uniforms.get(uniform), value);
    }

    public void setUniform(@NotNull String uniform, boolean value) {
        setUniform(uniform, value ? 1 : 0);
    }

    public void setUniform(@NotNull String uniform, float value) {
        glUniform1f(uniforms.get(uniform), value);
    }

    public void setUniform(@NotNull String uniform, @NotNull PointLight light) {
        setUniform(uniform + ".color", light.getColor() );
        setUniform(uniform + ".position", light.getPosition());
        setUniform(uniform + ".intensity", light.getIntensity());
        PointLight.Attenuation att = light.getAttenuation();
        setUniform(uniform + ".att.constant", att.getConstant());
        setUniform(uniform + ".att.linear", att.getLinear());
        setUniform(uniform + ".att.exponent", att.getExponent());
    }

    public void setUniform(@NotNull String uniform, @NotNull SpotLight light) {
        setUniform(uniform + ".direction", light.getDirection());
        setUniform(uniform + ".cut_off", light.getCutOff());
        setUniform(uniform + ".point", light.getPointLight());
    }

    public void setUniform(@NotNull String uniform, @NotNull DirectionalLight light) {
        setUniform(uniform + ".color", light.getColor());
        setUniform(uniform + ".direction", light.getDirection());
        setUniform(uniform + ".intensity", light.getIntensity());
    }

    public void setUniform(@NotNull String uniform, @NotNull Material material) {
        setUniform(uniform + ".ambient", material.getAmbient());
        setUniform(uniform + ".diffuse", material.getDiffuse());
        setUniform(uniform + ".specular", material.getSpecular());
        setUniform(uniform + ".emissive", material.getEmissive());
        setUniform(uniform + ".reflectance", material.getReflectance());
        setUniform(uniform + ".emissivity", material.getEmissivity());
        setUniform(uniform + ".is_textured", material.isTextured());
    }

    public void cleanup() {
        unbind();
        if (program != 0) {
            glDetachShader(program, vertexShader);
            glDetachShader(program, fragmentShader);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            glDeleteProgram(program);
        }
    }
}
