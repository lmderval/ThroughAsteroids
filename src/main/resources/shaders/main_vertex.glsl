#version 440

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normal;

uniform mat4 projection_mat;
uniform mat4 mv_mat;

out vec3 mv_pos;
out vec3 mv_normal;
out vec2 out_textures;

void main() {
    vec4 pos = mv_mat * vec4(position, 1.0);
    gl_Position = projection_mat * pos;
    out_textures = textures;
    mv_pos = pos.xyz;
    mv_normal = normalize(mv_mat * vec4(normal, 0.0)).xyz;
}
