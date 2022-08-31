#version 440

in vec2 frag_textures;

out vec4 out_color;

uniform sampler2D color_tex_sampler;
uniform sampler2D highlight_tex_sampler;

void main() {
    vec4 scene_color = texture(color_tex_sampler, frag_textures);
    vec4 highlight_color = texture(highlight_tex_sampler, frag_textures);
    out_color = highlight_color + (1 - highlight_color.a) * scene_color;
}
