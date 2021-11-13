import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    private ShaderProgram shaderProgram;

    public Renderer() {
    }

    private int vaoId;
    private int vboId;

    public void init() throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/fragment.fs"));
        shaderProgram.link();

    }


    public void render(Window window, Entity player){
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();


        for (int i =0 ; i < Game.player.length; i++) {
            FloatBuffer verticesBuffer = null;

            try {
                verticesBuffer = MemoryUtil.memAllocFloat(Game.player[i].vertices.length);
                verticesBuffer.put(Game.player[i].vertices).flip();

                // Create the VAO and bind to it
                vaoId = glGenVertexArrays();
                glBindVertexArray(vaoId);

                // Create the VBO and bint to it
                vboId = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);

                // Enable location 0
                glEnableVertexAttribArray(0);
                // Define structure of the data
                glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

                // Unbind the VBO
                glBindBuffer(GL_ARRAY_BUFFER, 0);
                glDrawArrays(GL_TRIANGLES, 0, 6);

            } finally {
                if (verticesBuffer != null) {
                    MemoryUtil.memFree(verticesBuffer);
                }
            }

            // Draw the vertices

        }

        //glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
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