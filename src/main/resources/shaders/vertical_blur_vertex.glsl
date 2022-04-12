#version 440

layout (location = 0) in vec2 position;

out vec2 frag_textures[11];

uniform float target_height;

void main(){
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTexCoords = position * 0.5 + 0.5;
	float pixelSize = 1.0 / target_height;

	for (int i = -5; i <= 5; i ++) {
		frag_textures[i + 5] = centerTexCoords + vec2(0.0, pixelSize * i);
	}
}