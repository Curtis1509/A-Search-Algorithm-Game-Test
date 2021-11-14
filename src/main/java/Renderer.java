import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Renderer {

    private ShaderProgram shaderProgram;
    private ShaderProgram playerShader;
    private ShaderProgram blockShader;

    public Renderer() {
    }

    private int vaoId;
    private int vboId;

    public void init() throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

        playerShader = new ShaderProgram();
        playerShader.createVertexShader(Utils.loadResource("/vertex.vs"));
        playerShader.createFragmentShader(Utils.loadResource("/playerfragment.fs"));
        playerShader.link();

        blockShader = new ShaderProgram();
        blockShader.createVertexShader(Utils.loadResource("/block.vs"));
        blockShader.createFragmentShader(Utils.loadResource("/block.fs"));
        blockShader.link();

        grid = new Grid();

        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glEnable(GL_TEXTURE_2D);

    }

    public Grid grid;
    float vs[] = {
            // positions          // colors           // texture coords
            0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 1.0f, // top right
            0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f,   0.0f, 0.0f, // bottom left
            -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f,   0.0f, 1.0f  // top left
    };

    public void render(Window window, Entity player) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        FloatBuffer verticesBuffer = null;


        for (int ii = 0; ii < 20; ii++) {
            for (int j = 0; j < 20; j++) {

                verticesBuffer = MemoryUtil.memAllocFloat(vs.length);
                verticesBuffer.put(vs).flip();

                glBindVertexArray(vaoId);
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
                MemoryUtil.memFree(verticesBuffer);

                glEnableVertexAttribArray(0);
                glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

                grid.getBlock(ii, j).getTexture().bind();
                blockShader.bind();
                glDrawArrays(GL_TRIANGLES, 0, 6);
                grid.getBlock(ii, j).getTexture().unbind();

            }
        }

        blockShader.unbind();
        playerShader.bind();

        for (int i = 0; i < Game.player.length; i++) {

            if (i > 0) {
                playerShader.unbind();
                shaderProgram.bind();
            }
            glBindVertexArray(vaoId);

            verticesBuffer = MemoryUtil.memAllocFloat(Game.player[i].vertices.length);
            verticesBuffer.put(Game.player[i].vertices).flip();

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            MemoryUtil.memFree(verticesBuffer);

            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

            glDrawArrays(GL_TRIANGLES, 0, 6);

        }
        shaderProgram.unbind();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
            blockShader.cleanup();
            playerShader.cleanup();
        }

        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}