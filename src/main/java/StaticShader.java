import java.nio.file.Path;

public class StaticShader extends ShaderProgram{

    static Path resourcePath = Path.of("src", "main", "resources");

    public static Path resource(String first, String... more) {
        return resourcePath.resolve(Path.of(first, more)).toAbsolutePath();
    }

    static Path blockVs = resource("vertex.vs");
    static Path blockFs = resource("fragment.fs");

    private static final String VERTEX_FILE = blockVs.toString();
    private static final String FRAGMENT_FILE = blockFs.toString();

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {

    }


}