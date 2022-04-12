#version 440

in vec2 frag_textures;

out vec4 out_color;

uniform sampler2D tex_sampler;

void main() {
    vec4 color = texture(tex_sampler, frag_textures);
    if (color.r > 0.6) {
        out_color = vec4(color.r * color.r, 0, 0, 0);
    } else {
        out_color = vec4(0.0);
    }
}
