#version 440

in vec2 textureCoords;

out vec4 out_color;

uniform sampler2D color_tex_sampler;
uniform sampler2D highlight_tex_sampler;

void main() {
    vec4 scene_color = texture(color_tex_sampler, textureCoords);
    vec4 highlight_color = texture(highlight_tex_sampler, textureCoords);
    out_color = scene_color + highlight_color * 2.0;
}
