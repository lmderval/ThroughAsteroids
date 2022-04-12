#version 440

in vec2 frag_textures;

out vec4 out_color;

uniform sampler2D tex_sampler;

void main(void){
	vec4 color = texture(tex_sampler, frag_textures);
	float brightness = color.r * 0.2126 + color.g * 0.7152 + color.b * 0.0722;
	out_color = vec4(brightness, brightness, brightness, 0);
	if (brightness > 0.6) {
		out_color.gb = vec2(0, 0);
	} else {
		out_color.gb = out_color.gb - vec2(0.05, 0.05);
	}
}