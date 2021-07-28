#version 440

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec2 normals;

uniform mat4 projection_mat;
uniform mat4 world_mat;

out vec2 out_textures;

void main() {
    gl_Position = projection_mat * world_mat * vec4(position, 1.0);
    out_textures = textures;
}
