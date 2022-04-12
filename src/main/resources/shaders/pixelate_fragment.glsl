#version 440

in vec2 frag_textures;

out vec4 out_color;

uniform sampler2D tex_sampler;
uniform int width;
uniform int height;

const float pixel_w = 5;
const float pixel_h = 5;

void main(void){
	vec2 uv = frag_textures.xy;
	vec3 tc = vec3(0.0, 0.0, 0.0);
	float dx = pixel_w * (1. / width);
	float dy = pixel_h * (1. / height);
	vec2 coord = vec2(dx * floor(uv.x / dx), dy * floor(uv.y / dy));
	tc = texture(tex_sampler, coord).rgb;
	out_color = vec4(tc, 1.0);
}