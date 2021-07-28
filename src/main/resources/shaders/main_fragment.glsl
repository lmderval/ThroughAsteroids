#version 440

in vec2 out_textures;

out vec4 frag_color;

uniform sampler2D tex_sampler;

void main() {
    frag_color = texture(tex_sampler, out_textures);
}
