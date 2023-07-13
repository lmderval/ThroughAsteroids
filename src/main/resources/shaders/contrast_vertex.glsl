#version 440

layout (location = 0) in vec2 position;

out vec2 frag_textures;

void main(void){
    gl_Position = vec4(position, 0.0, 1.0);
    frag_textures = position * 0.5 + 0.5;
}