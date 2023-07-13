#version 440

in vec2 frag_textures;

out vec4 out_color;

uniform sampler2D tex_sampler;

const float contrast = 0.15;

void main(void){
    out_color = texture(tex_sampler, frag_textures);
    out_color.rgb = (out_color.rgb - 0.5) * (1.0 + contrast) + 0.5;
}