import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Sprite {

    public int texture;
    public Vector2f position;
    public Vector2f scale;

    public Sprite(int texture, Vector2f position, Vector2f scale) {
        this.position = position;
        this.texture = texture;
        this.scale = scale;
    }

    public void updateTexture(int texture) {
        this.texture = texture;
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }

    public void render() {
        GL30.glBindVertexArray(Renderer.quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTexture());
        Matrix4f matrix = MathTools.createTransformationMatrix(getPosition(), getScale());
        Renderer.shaderProgram.loadTransformation(matrix);
        GL11.glDrawArrays(GL_TRIANGLE_STRIP, 0, Renderer.quad.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

}
