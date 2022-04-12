#version 440

in vec2 frag_textures[11];

out vec4 out_colour;

uniform sampler2D tex_sampler;

const float sigma = 2.0;

void main(){
	out_colour = vec4(0.0);
	out_colour += texture(tex_sampler, frag_textures[0]) * 0.008764150;
	out_colour += texture(tex_sampler, frag_textures[1]) * 0.026995483;
	out_colour += texture(tex_sampler, frag_textures[2]) * 0.064758798;
	out_colour += texture(tex_sampler, frag_textures[3]) * 0.120985362;
	out_colour += texture(tex_sampler, frag_textures[4]) * 0.176032663;
	out_colour += texture(tex_sampler, frag_textures[5]) * 0.199471140;
	out_colour += texture(tex_sampler, frag_textures[6]) * 0.176032663;
	out_colour += texture(tex_sampler, frag_textures[7]) * 0.120985362;
	out_colour += texture(tex_sampler, frag_textures[8]) * 0.064758798;
	out_colour += texture(tex_sampler, frag_textures[9]) * 0.026995483;
	out_colour += texture(tex_sampler, frag_textures[10]) * 0.008764150;
}