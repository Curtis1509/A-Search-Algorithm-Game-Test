import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class Renderer {

    private GuiShader shaderProgram;
    private final VertexArrayObject vao = new VertexArrayObject();
    public static Grid grid;
    private RawModel quad;

    public Renderer() {
    }

    public void init() throws Exception {

        swordSprite = new Sprite(Texture.loadPngTexture(App.resource("textures", "sword" + ".png")), new Vector2f(Game.player.x, Game.player.y), new Vector2f(Game.player.width, Game.player.height));
        shaderProgram = new GuiShader();
        grid = new Grid();
        grid.runThreads(Game.entities.size());
        glEnable(GL_TEXTURE_2D);

        for (int i = 0; i < Game.entities.size(); i++) {
            quad = vao.loadToVAO(Game.entities.get(i).vertices);
        }
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                quad = vao.loadToVAO(grid.getBlock(i, j).gridVertices);
            }
        }
    }


    public void render(Window window) throws IOException {

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.start();

        renderGrid();

        for (int i = 0; i < Game.entities.size(); i++) {

            GL30.glBindVertexArray(quad.getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, Game.entities.get(i).sprite.getTexture());
            Game.entities.get(i).update();
            Matrix4f matrix = MathTools.createTransformationMatrix(Game.entities.get(i).sprite.getPosition(), Game.entities.get(i).sprite.getScale());
            shaderProgram.loadTransformation(matrix);
            GL11.glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);

        }

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Game.player.sprite.getTexture());
        Game.player.update();

        if (Game.left && !Game.renderedDirection.equals("left")) {
            Game.player.sprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "playerleft" + ".png")));
            Game.renderedDirection="left";
        }
        if (Game.right && !Game.renderedDirection.equals("right")) {
            Game.player.sprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "playerright" + ".png")));
            Game.renderedDirection="right";
        }
        if (Game.up && !Game.renderedDirection.equals("up")) {
            Game.player.sprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "playerup" + ".png")));
            Game.renderedDirection="up";
        }
        if (Game.down && !Game.renderedDirection.equals("down")) {
            Game.player.sprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "playerdown" + ".png")));
            Game.renderedDirection="down";
        }
        Matrix4f matrix = MathTools.createTransformationMatrix(Game.player.sprite.getPosition(), Game.player.sprite.getScale());
        shaderProgram.loadTransformation(matrix);
        GL11.glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);


        renderSword(swordSprite);

        shaderProgram.stop();
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

    }

    public static Sprite swordSprite;

    public void renderSword(Sprite spriteName) {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, spriteName.getTexture());
        Matrix4f matrix = MathTools.createTransformationMatrix(spriteName.getPosition(), spriteName.getScale());
        shaderProgram.loadTransformation(matrix);
        GL11.glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void renderGrid() throws IOException {

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                GL30.glBindVertexArray(quad.getVaoID());
                GL20.glEnableVertexAttribArray(0);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, grid.getBlock(i, j).sprite.getTexture());
                Matrix4f matrix = MathTools.createTransformationMatrix(grid.getBlock(i, j).sprite.getPosition(), grid.getBlock(i, j).sprite.getScale());
                shaderProgram.loadTransformation(matrix);

                if (Game.showPath)
                    renderPath(i, j);
                else if (Game.pPressed && !grid.getBlock(i, j).blocked)
                    grid.getBlock(i, j).sprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "empty" + ".png")));

                GL11.glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
                GL20.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
            }
        }
    }

    public void renderPath(int i, int j) throws IOException {

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, grid.getBlock(i, j).sprite.getTexture());
        Matrix4f matrix = MathTools.createTransformationMatrix(grid.getBlock(i, j).sprite.getPosition(), grid.getBlock(i, j).sprite.getScale());
        shaderProgram.loadTransformation(matrix);

        for (int k = 0; k < Grid.threads.size(); k++) {
            if (grid.pathContains(grid.getBlock(i, j)) && Grid.threads.get(k).finished && !grid.getBlock(i, j).painted) {
                try {
                    grid.getBlock(i, j).sprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "path" + ".png")));
                    grid.getBlock(i, j).painted = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!grid.pathContains(grid.getBlock(i, j)) && grid.getBlock(i, j).painted) {
                grid.getBlock(i, j).sprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "empty" + ".png")));
                grid.getBlock(i, j).painted = false;
            }
        }
    }


    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanUp();
        }
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }
}