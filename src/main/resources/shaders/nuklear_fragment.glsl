#version 440

in vec3 mv_pos;
in vec2 frag_textures;
in vec4 frag_color;

out vec4 out_color;

uniform sampler2D tex_sampler;

void main() {
    out_color = frag_color * texture(tex_sampler, frag_textures);
}
