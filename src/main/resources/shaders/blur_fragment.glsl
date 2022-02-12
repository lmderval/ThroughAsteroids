#version 440

in vec2 blurTextureCoords[11];

out vec4 out_colour;

uniform sampler2D tex_sampler;

const float sigma = 2.0;

void main(){
	out_colour = vec4(0.0);
	out_colour += texture(tex_sampler, blurTextureCoords[0]) * 0.008764150;
	out_colour += texture(tex_sampler, blurTextureCoords[1]) * 0.026995483;
	out_colour += texture(tex_sampler, blurTextureCoords[2]) * 0.064758798;
	out_colour += texture(tex_sampler, blurTextureCoords[3]) * 0.120985362;
	out_colour += texture(tex_sampler, blurTextureCoords[4]) * 0.176032663;
	out_colour += texture(tex_sampler, blurTextureCoords[5]) * 0.199471140;
	out_colour += texture(tex_sampler, blurTextureCoords[6]) * 0.176032663;
	out_colour += texture(tex_sampler, blurTextureCoords[7]) * 0.120985362;
	out_colour += texture(tex_sampler, blurTextureCoords[8]) * 0.064758798;
	out_colour += texture(tex_sampler, blurTextureCoords[9]) * 0.026995483;
	out_colour += texture(tex_sampler, blurTextureCoords[10]) * 0.008764150;
}