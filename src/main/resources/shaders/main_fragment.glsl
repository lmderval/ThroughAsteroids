#version 440

in vec3 mv_pos;
in vec3 mv_normal;
in vec2 frag_textures;

out vec4 out_color;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

struct SpotLight {
    vec3 direction;
    float cut_off;
    PointLight point;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec4 emissive;
    float reflectance;
    float emissivity;
    bool is_textured;
};

uniform sampler2D tex_sampler;
uniform vec3 ambient_light;
uniform float specular_power;
uniform int selected;
uniform Material material;
uniform PointLight point_light;
uniform SpotLight spot_light;
uniform DirectionalLight directional_light;
uniform vec4 debug_color;

vec4 ambient_color;
vec4 diffuse_color;
vec4 specular_color;
vec4 emissive_color;

void setupColors(Material material, vec2 textures) {
    if (material.is_textured) {
        ambient_color = texture(tex_sampler, textures);
        diffuse_color = ambient_color;
        specular_color = ambient_color;
        emissive_color = ambient_color;
    } else {
        ambient_color = material.ambient;
        diffuse_color = material.diffuse;
        specular_color = material.specular;
        emissive_color = material.emissive;
    }
}

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light, vec3 normal) {
    float diffuse_factor = max(0.0, dot(normal, to_light));
    vec4 diffuse = diffuse_color * vec4(light_color, 1.0) * diffuse_factor * light_intensity;

    vec3 from_light = -to_light;
    vec3 reflected = normalize(reflect(from_light, normal));
    vec3 to_camera = normalize(-position);
    float specular_factor = max(0.0, dot(to_camera, reflected));
    vec4 specular = specular_color * vec4(light_color, 1.0) * pow(specular_factor, specular_power) * light_intensity * material.reflectance;

    return diffuse + specular;
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 light_dir = light.position - position;
    vec3 to_light = normalize(light_dir);
    vec4 light_color = calcLightColor(light.color, light.intensity, position, to_light, normal);

    float distance = length(light_dir);
    float att = light.att.exponent * distance * distance + light.att.linear * distance + light.att.constant;
    return light_color / att;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal) {
    vec3 from_light = normalize(position - light.point.position);
    vec3 light_dir = normalize(light.direction);
    float cos_a = dot(from_light, light_dir);

    vec4 color = vec4(0, 0, 0, 0);
    if (cos_a > light.cut_off) {
        color = calcPointLight(light.point, position, normal);
        color *= (1.0 - (1.0 - cos_a) / (1.0 - light.cut_off));
    }
    return color;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calcLightColor(light.color, light.intensity, position, normalize(-light.direction), normal);
}

void main() {
    setupColors(material, frag_textures);
    vec4 diffuse_specular_color = calcDirectionalLight(directional_light, mv_pos, mv_normal);
    diffuse_specular_color += calcPointLight(point_light, mv_pos, mv_normal);
    diffuse_specular_color += calcSpotLight(spot_light, mv_pos, mv_normal);
    out_color = emissive_color * material.emissivity + ambient_color * vec4(ambient_light, 1.0) + diffuse_specular_color;
    out_color *= debug_color;
    if (selected == 1) {
        out_color *= vec4(0.5, 0.5, 0.5, 1.0);
    }
}
