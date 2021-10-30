#version 440

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec4 color;

uniform mat4 projection_mat;

out vec3 mv_pos;
out vec2 frag_textures;
out vec4 frag_color;

void main() {
    frag_textures = textures;
    frag_color = color;
    gl_Position = projection_mat * vec4(position, 1.0);
}
