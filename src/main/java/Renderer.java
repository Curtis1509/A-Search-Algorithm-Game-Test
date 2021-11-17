import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
    private ShaderProgram playerShader;
    private ShaderProgram blockShader;

    public Renderer() throws IOException {
    }

    private VertexArrayObject vao;

    private RawModel quad;
    Path resourcePath = Path.of("src", "main", "resources");

    public Path resource(String first, String... more) {
        return resourcePath.resolve(Path.of(first, more)).toAbsolutePath();
    }

    Sprite sprite;
    public void init() throws Exception {

        //sprite = new Sprite(Texture.loadPngTexture(resource("textures", "block.png")),new Vector2f(0.5f,0.5f),new Vector2f(0.25f,0.25f));

        shaderProgram = new GuiShader();

        grid = new Grid();


        grid.runThreads(Game.player.length-1);

        glEnable(GL_TEXTURE_2D);

        for (int i =0 ; i < Game.player.length;i++) {
            quad = loadToVAO(Game.player[i].vertices);
        }
        for (int i =0; i < 20; i++){
            for (int j =0; j < 20; j++) {
                quad = loadToVAO(grid.getBlock(i,j).gridVertices);
            }
        }
    }

    public static Grid grid;

    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    public Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(new Vector3f(translation.x, translation.y, 1f));
        matrix.scale(new Vector3f(scale.x, scale.y, 1f));
        return matrix;
    }

    private int createVAO(){
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }
    private void storeDataInAttributeList(int attributeNumber, int coordinateSize,float[] data){
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber,coordinateSize, GL11.GL_FLOAT,false,0,0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
    private FloatBuffer storeDataInFloatBuffer(float[] data){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    private void unbindVAO(){
        GL30.glBindVertexArray(0);
    }

    public RawModel loadToVAO(float positions[]){
        int vaoID = createVAO();
        this.storeDataInAttributeList(0,2,positions);
        unbindVAO();
        return new RawModel (vaoID, positions.length/2);
    }


    public void render(Window window, Entity player) throws IOException {

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }



        FloatBuffer verticesBuffer = null;
/*

        for (int ii = 0; ii < 20; ii++) {
            for (int j = 0; j < 20; j++) {


                grid.getBlock(ii, j).getTexture().bind();
                blockShader.bind();
                //glDrawArrays(GL_TRIANGLES, 0, 6);
                grid.getBlock(ii, j).getTexture().unbind();

            }
        }

        blockShader.unbind();
        blockShader.bind();
        */

        shaderProgram.start();

        for (int i = 0; i < 20; i++){
            for (int j = 0; j < 20; j++){
                GL30.glBindVertexArray(quad.getVaoID());
                GL20.glEnableVertexAttribArray(0);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, grid.getBlock(i,j).sprite.getTexture());
                Matrix4f matrix = createTransformationMatrix(grid.getBlock(i,j).sprite.getPosition(), grid.getBlock(i,j).sprite.getScale());
                shaderProgram.loadTransformation(matrix);
                //if (grid.getBlock(i,j).blocked)

                for (int k = 0; k < Grid.threads.size(); k++) {
                    if (grid.pathContains(grid.getBlock(i, j)) && Grid.threads.get(k).finished && !grid.getBlock(i, j).painted) {
                        try {
                            grid.getBlock(i, j).sprite.updateTexture(Texture.loadPngTexture(resource("textures", "path" + ".png")));
                            grid.getBlock(i, j).painted = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (!grid.pathContains(grid.getBlock(i, j)) && grid.getBlock(i, j).painted) {
                        grid.getBlock(i, j).sprite.updateTexture(Texture.loadPngTexture(resource("textures", "empty" + ".png")));
                        grid.getBlock(i, j).painted = false;
                    }
                }
                GL11.glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
                GL20.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
            }
        }

        for (int i = 0; i < Game.player.length; i++) {

            if (i > 0) {
            }


           //Game.player[i].mesh.render();
            GL30.glBindVertexArray(quad.getVaoID());
            GL20.glEnableVertexAttribArray(0);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, Game.player[i].sprite.getTexture());
            Game.player[i].update();
            Matrix4f matrix = createTransformationMatrix(Game.player[i].sprite.getPosition(), Game.player[i].sprite.getScale());
            shaderProgram.loadTransformation(matrix);
            GL11.glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
            GL20.glDisableVertexAttribArray(0);
            GL30.glBindVertexArray(0);

        }
        shaderProgram.stop();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanUp();
        }

        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Delete the VAO
        glBindVertexArray(0);
    }
}