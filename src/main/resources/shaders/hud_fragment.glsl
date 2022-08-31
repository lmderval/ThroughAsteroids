#version 440

in vec3 m_normal;
in vec2 frag_textures;

out vec4 out_color;

uniform sampler2D tex_sampler;
uniform bool is_3d;
uniform vec4 color;
uniform vec3 light_direction;

vec4 applyLight(vec3 to_light, vec3 normal, vec4 color) {
    float factor = max(0.0, dot(normal, to_light));
    return vec4(factor * color.rgb, color.a);
}

void main() {
    out_color = color * texture(tex_sampler, frag_textures);
    if (is_3d) {
        out_color = applyLight(normalize(-light_direction), m_normal, out_color);
    }
}
