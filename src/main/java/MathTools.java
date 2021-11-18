import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class MathTools {

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.translate(new Vector3f(translation.x, translation.y, 1f));
        matrix.scale(new Vector3f(scale.x, scale.y, 1f));
        return matrix;
    }

}
