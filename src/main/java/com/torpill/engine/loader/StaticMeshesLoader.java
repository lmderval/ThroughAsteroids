package com.torpill.engine.loader;

import com.torpill.engine.Utils;
import com.torpill.engine.graphics.Material;
import com.torpill.engine.graphics.Mesh;
import com.torpill.engine.graphics.Texture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class StaticMeshesLoader {

    public static Mesh[] load(@NotNull String resource_path, @Nullable String textures_dir) throws Exception {
        return load(resource_path, textures_dir, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }

    public static Mesh[] load(@NotNull String resource_path, @Nullable String textures_dir, int flags) throws Exception {
        AIScene aiScene = aiImportFile(resource_path, flags);
        if (aiScene == null) {
            throw new Exception("Error loading model");
        }

        int num_materials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < num_materials; i++) {
            assert aiMaterials != null;
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, textures_dir);
        }

        int num_meshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[num_meshes];
        for (int i = 0; i < num_meshes; i++) {
            if (aiMeshes != null) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                Mesh mesh = processMesh(aiMesh, materials);
                meshes[i] = mesh;
            }
        }

        return meshes;
    }

    private static void processMaterial(@NotNull AIMaterial aiMaterial, @NotNull List<Material> materials, @Nullable String textures_dir) throws Exception {
        AIColor4D color = AIColor4D.create();

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String tex_path = path.dataString();
        Texture texture = null;
        if (textures_dir != null && tex_path.length() > 0) {
            TextureCache tex_cache = TextureCache.getInstance();
            texture = tex_cache.getTexture(textures_dir + "/" + tex_path);
        }

        Vector4f ambient = Material.DEFAULT_COLOR;
        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, color);
        if (result == 0) {
            ambient = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Vector4f diffuse = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);
        if (result == 0) {
            diffuse = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Vector4f specular = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, color);
        if (result == 0) {
            specular = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Vector4f emissive = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE, aiTextureType_NONE, 0, color);
        if (result == 0) {
            emissive = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Material material = new Material(ambient, diffuse, specular, emissive, 1f, 0f);
        material.setTexture(texture);
        materials.add(material);
    }

    private static Mesh processMesh(@NotNull AIMesh aiMesh, @NotNull List<Material> materials) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processTexCoords(aiMesh, textures);
        processNormals(aiMesh, normals);
        processIndices(aiMesh, indices);

        Mesh mesh = new Mesh(
                Utils.listToArrayFloat(vertices),
                Utils.listToArrayFloat(textures),
                Utils.listToArrayFloat(normals),
                Utils.listToArrayInt(indices)
        );
        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }
        mesh.setMaterial(material);

        return mesh;
    }

    private static void processVertices(@NotNull AIMesh aiMesh, @NotNull List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    private static void processTexCoords(@NotNull AIMesh aiMesh, @NotNull List<Float> textures) {
        AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
        int num_tex_coords = aiTexCoords != null ? aiTexCoords.remaining() : 0;
        for (int i = 0; i < num_tex_coords; i++) {
            AIVector3D aiTexCoord = aiTexCoords.get();
            textures.add(aiTexCoord.x());
            textures.add(1 - aiTexCoord.y());
        }
    }

    private static void processNormals(@NotNull AIMesh aiMesh, @NotNull List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private static void processIndices(@NotNull AIMesh aiMesh, @NotNull List<Integer> indices) {
        int num_faces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < num_faces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }
}
