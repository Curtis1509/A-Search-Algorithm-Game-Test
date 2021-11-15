import org.joml.Vector2f;

public class Sprite {

    public int texture;
    public Vector2f position;
    public Vector2f scale;

    public Sprite(int texture, Vector2f position, Vector2f scale){
        this.position = position;
        this.texture = texture;
        this.scale = scale;
    }
    public void updateTexture (int texture){
        this.texture=texture;
    }

    public int getTexture(){
        return texture;
    }
    public Vector2f getPosition(){
        return position;
    }
    public Vector2f getScale(){
        return scale;
    }

}
