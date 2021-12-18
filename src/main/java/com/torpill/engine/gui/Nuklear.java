package com.torpill.engine.gui;

import com.torpill.engine.Window;
import com.torpill.engine.graphics.ShaderProgram;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.nuklear.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static com.torpill.engine.Utils.ioResourceToByteBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class Nuklear {

    private static final int BUFFER_INITIAL_SIZE = 4 * 1024;

    private static final int MAX_VERTEX_BUFFER = 512 * 1024;
    private static final int MAX_ELEMENT_BUFFER = 128 * 1024;

    private static final NkAllocator ALLOCATOR;

    private static final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;

    private static final String UNI_PROJECTION = "projection_mat";
    private static final String UNI_TEX_SAMPLER = "tex_sampler";

    static {
        ALLOCATOR = NkAllocator.create()
                .alloc((handle, old, size) -> nmemAllocChecked(size))
                .mfree((handle, ptr) -> nmemFree(ptr));

        VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
                .position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
                .position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
                .position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
                .position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
                .flip();
    }

    private final ByteBuffer ttf;
    private final NkContext ctx = NkContext.create();
    private final NkUserFont default_font = NkUserFont.create();
    private final NkBuffer cmds = NkBuffer.create();
    private final NkDrawNullTexture null_texture = NkDrawNullTexture.create();
    private int width, height;
    private int display_width, display_height;
    private ShaderProgram nuklear;

    private int vbo, vao, ebo;

    public Nuklear(@NotNull String nkFont) {
        try {
            this.ttf = ioResourceToByteBuffer(nkFont, 512 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull NkContext init(long win) {
        setupWindow(win);

        int BITMAP_W = 1024;
        int BITMAP_H = 1024;

        int FONT_HEIGHT = 18;
        int fontTexID = glGenTextures();

        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(95);

        float scale;
        float descent;

        try (MemoryStack stack = stackPush()) {
            stbtt_InitFont(fontInfo, ttf);
            scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

            IntBuffer d = stack.mallocInt(1);
            stbtt_GetFontVMetrics(fontInfo, null, d, null);
            descent = d.get(0) * scale;

            ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

            STBTTPackContext pc = STBTTPackContext.mallocStack(stack);
            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
            stbtt_PackSetOversampling(pc, 4, 4);
            stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
            stbtt_PackEnd(pc);

            // Convert R8 to RGBA8
            ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);
            for (int i = 0; i < bitmap.capacity(); i++) {
                texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
            }
            texture.flip();

            glBindTexture(GL_TEXTURE_2D, fontTexID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            memFree(texture);
            memFree(bitmap);
        }

        default_font
                .width((handle, h, text, len) -> {
                    float text_width = 0;
                    try (MemoryStack stack = stackPush()) {
                        IntBuffer unicode = stack.mallocInt(1);

                        int glyph_len = nnk_utf_decode(text, memAddress(unicode), len);
                        int text_len = glyph_len;

                        if (glyph_len == 0) {
                            return 0;
                        }

                        IntBuffer advance = stack.mallocInt(1);
                        while (text_len <= len && glyph_len != 0) {
                            if (unicode.get(0) == NK_UTF_INVALID) {
                                break;
                            }

                            /* query currently drawn glyph information */
                            stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
                            text_width += advance.get(0) * scale;

                            /* offset next glyph */
                            glyph_len = nnk_utf_decode(text + text_len, memAddress(unicode), len - text_len);
                            text_len += glyph_len;
                        }
                    }
                    return text_width;
                })
                .height(FONT_HEIGHT)
                .query((handle, font_height, glyph, codepoint, next_codepoint) -> {
                    try (MemoryStack stack = stackPush()) {
                        FloatBuffer x = stack.floats(0.0f);
                        FloatBuffer y = stack.floats(0.0f);

                        STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
                        IntBuffer advance = stack.mallocInt(1);

                        stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false);
                        stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

                        NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

                        ufg.width(q.x1() - q.x0());
                        ufg.height(q.y1() - q.y0());
                        ufg.offset().set(q.x0(), q.y0() + (FONT_HEIGHT + descent));
                        ufg.xadvance(advance.get(0) * scale);
                        ufg.uv(0).set(q.s0(), q.t0());
                        ufg.uv(1).set(q.s1(), q.t1());
                    }
                })
                .texture(it -> it
                        .id(fontTexID));

        nk_style_set_font(ctx, default_font);
        return ctx;
    }

    public void input(@NotNull Window window) {

        width = window.getFramebufferWidth();
        height = window.getFramebufferHeight();

        display_width = window.getFramebufferWidth();
        display_height = window.getFramebufferHeight();

        nk_input_begin(ctx);
        glfwPollEvents();

        NkMouse mouse = ctx.input().mouse();
        if (mouse.grab()) {
            window.hideCursor();
        } else if (mouse.grabbed()) {
            float prevX = mouse.prev().x();
            float prevY = mouse.prev().y();
            window.setCursorPos(prevX, prevY);
            mouse.pos().x(prevX);
            mouse.pos().y(prevY);
        } else if (mouse.ungrab()) {
            window.showCursor();
        }

        nk_input_end(ctx);
    }

    private void setupWindow(long win) {
        nk_init(ctx, ALLOCATOR, null);
        ctx.clip()
                .copy((handle, text, len) -> {
                    if (len == 0) {
                        return;
                    }

                    try (MemoryStack stack = stackPush()) {
                        ByteBuffer str = stack.malloc(len + 1);
                        memCopy(text, memAddress(str), len);
                        str.put(len, (byte) 0);

                        glfwSetClipboardString(win, str);
                    }
                })
                .paste((handle, edit) -> {
                    long text = nglfwGetClipboardString(win);
                    if (text != NULL) {
                        nnk_textedit_paste(edit, text, nnk_strlen(text));
                    }
                });
    }

    public @NotNull NkContext setupContext() throws Exception {
        nk_buffer_init(cmds, ALLOCATOR, BUFFER_INITIAL_SIZE);
        nuklear = new ShaderProgram("nuklear");
        nuklear.createVertexShader();
        nuklear.createFragmentShader();
        nuklear.link();

        nuklear.createUniform(UNI_PROJECTION);
        nuklear.createUniform(UNI_TEX_SAMPLER);

        {
            // buffer setup
            vbo = glGenBuffers();
            ebo = glGenBuffers();
            vao = glGenVertexArrays();

            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glEnableVertexAttribArray(2);

            glVertexAttribPointer(0, 2, GL_FLOAT, false, 20, 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 8);
            glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, 20, 16);
        }

        {
            // null texture setup
            int nullTexID = glGenTextures();

            null_texture.texture().id(nullTexID);
            null_texture.uv().set(0.5f, 0.5f);

            glBindTexture(GL_TEXTURE_2D, nullTexID);
            try (MemoryStack stack = stackPush()) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, stack.ints(0xFFFFFFFF));
            }
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        }

        glBindTexture(GL_TEXTURE_2D, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        return ctx;
    }

    public void render() {
        // setup global state
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_SCISSOR_TEST);
        glActiveTexture(GL_TEXTURE0);

        // setup program
        nuklear.bind();
        nuklear.setUniform(UNI_TEX_SAMPLER, 0);
        nuklear.setUniform(UNI_PROJECTION, new Matrix4f(
                2.0f / width, 0.0f, 0.0f, 0.0f,
                0.0f, -2.0f / height, 0.0f, 0.0f,
                0.0f, 0.0f, -1.0f, 0.0f,
                -1.0f, 1.0f, 0.0f, 1.0f
        ));
        glViewport(0, 0, display_width, display_height);

        {
            // convert from command queue into draw list and draw to screen

            // allocate vertex and element buffer
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

            glBufferData(GL_ARRAY_BUFFER, MAX_VERTEX_BUFFER, GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_ELEMENT_BUFFER, GL_STREAM_DRAW);

            // load draw vertices & elements directly into vertex + element buffer
            ByteBuffer vertices = Objects.requireNonNull(glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_VERTEX_BUFFER, null));
            ByteBuffer elements = Objects.requireNonNull(glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_ELEMENT_BUFFER, null));
            try (MemoryStack stack = stackPush()) {
                // fill convert configuration
                NkConvertConfig config = NkConvertConfig.callocStack(stack)
                        .vertex_layout(VERTEX_LAYOUT)
                        .vertex_size(20)
                        .vertex_alignment(4)
                        .null_texture(null_texture)
                        .circle_segment_count(22)
                        .curve_segment_count(22)
                        .arc_segment_count(22)
                        .global_alpha(1.0f)
                        .shape_AA(NK_ANTI_ALIASING_ON)
                        .line_AA(NK_ANTI_ALIASING_ON);

                // setup buffers to load vertices and elements
                NkBuffer vbuf = NkBuffer.mallocStack(stack);
                NkBuffer ebuf = NkBuffer.mallocStack(stack);

                nk_buffer_init_fixed(vbuf, vertices/*, MAX_VERTEX_BUFFER*/);
                nk_buffer_init_fixed(ebuf, elements/*, MAX_ELEMENT_BUFFER*/);
                nk_convert(ctx, cmds, vbuf, ebuf, config);
            }
            glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            glUnmapBuffer(GL_ARRAY_BUFFER);

            // iterate over and execute each draw command
            float fb_scale_x = (float) display_width / (float) width;
            float fb_scale_y = (float) display_height / (float) height;

            long offset = NULL;
            for (NkDrawCommand cmd = nk__draw_begin(ctx, cmds); cmd != null; cmd = nk__draw_next(cmd, cmds, ctx)) {
                if (cmd.elem_count() == 0) {
                    continue;
                }
                glBindTexture(GL_TEXTURE_2D, cmd.texture().id());
                glScissor(
                        (int) (cmd.clip_rect().x() * fb_scale_x),
                        (int) ((height - (int) (cmd.clip_rect().y() + cmd.clip_rect().h())) * fb_scale_y),
                        (int) (cmd.clip_rect().w() * fb_scale_x),
                        (int) (cmd.clip_rect().h() * fb_scale_y)
                );
                glDrawElements(GL_TRIANGLES, cmd.elem_count(), GL_UNSIGNED_SHORT, offset);
                offset += cmd.elem_count() * 2L;
            }
            nk_clear(ctx);
        }

        // default OpenGL state
        nuklear.unbind();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        glDisable(GL_BLEND);
        glDisable(GL_SCISSOR_TEST);
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    public NkContext getContext() {
        return ctx;
    }

    private void destroy() {
        nuklear.cleanup();
        glDeleteTextures(default_font.texture().id());
        glDeleteTextures(null_texture.texture().id());
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        nk_buffer_free(cmds);
    }

    public void cleanup() {
        Objects.requireNonNull(ctx.clip().copy()).free();
        Objects.requireNonNull(ctx.clip().paste()).free();
        nk_free(ctx);
        destroy();
        Objects.requireNonNull(default_font.query()).free();
        Objects.requireNonNull(default_font.width()).free();
        Objects.requireNonNull(ALLOCATOR.alloc()).free();
        Objects.requireNonNull(ALLOCATOR.mfree()).free();
    }
}
