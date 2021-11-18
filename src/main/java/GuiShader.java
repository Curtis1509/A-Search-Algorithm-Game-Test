import org.joml.Matrix4f;

import java.nio.file.Path;

public class GuiShader extends ShaderProgram{

    static Path blockVs = App.resource("block.vs");
    static Path blockFs = App.resource("block.fs");

    private static final String VERTEX_FILE = blockVs.toString();
    private static final String FRAGMENT_FILE = blockFs.toString();

    private int location_transformationMatrix;

    public GuiShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformation(Matrix4f matrix){
        getAllUniformLocations();
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }




}
