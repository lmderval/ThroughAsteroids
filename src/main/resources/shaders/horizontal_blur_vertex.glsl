#version 440

layout (location = 0) in vec2 position;

out vec2 blurTextureCoords[11];

uniform float target_width;

void main(){
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTexCoords = position * 0.5 + 0.5;
	float pixelSize = 1.0 / target_width;

	for (int i = -5; i <= 5; i ++) {
		blurTextureCoords[i + 5] = centerTexCoords + vec2(pixelSize * i, 0.0);
	}
}