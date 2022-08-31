#version 440

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textures;
layout (location = 2) in vec3 normal;

uniform mat4 projection_mat;
uniform mat4 m_mat;

out vec3 m_normal;
out vec2 frag_textures;

void main() {
    vec4 pos = m_mat * vec4(position, 1.0);
    frag_textures = textures;
    gl_Position = projection_mat * pos;
    m_normal = normalize(m_mat * vec4(normal, 0.0)).xyz;
}
