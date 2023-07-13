#version 440

in vec2 frag_textures;

out vec4 out_color;

uniform sampler2D tex_sampler;

void main() {
    vec4 color = texture(tex_sampler, frag_textures);
    float brightness = color.r * 0.2126 + color.g * 0.7152 + color.b * 0.0722;
    if (brightness > 0.6) {
        out_color = color * brightness * brightness;
    } else if (color.r > 0.9){
        out_color = color * 0.7;
    } else {
        out_color = vec4(0.0);
    }
}
