
import org.joml.Vector2f;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;


public class Entity {

    float nextX;
    float nextY;
    float size = 0.05f;
    float width = size * 0.75f;
    float height = size;
    float x = 0f;
    float y = 0f;
    float speed = 0f;
    public Sprite sprite;
    Path resourcePath = Path.of("src", "main", "resources");

    public Path resource(String first, String... more) {
        return resourcePath.resolve(Path.of(first, more)).toAbsolutePath();
    }

    public static Texture t;

    public void update() {
        sprite.position.x = x;
        sprite.position.y = y;
    }

    public Entity(String textureName, boolean enemy) throws IOException {
        generateLocation(enemy);
        sprite = new Sprite(Texture.loadPngTexture(resource("textures", textureName + ".png")), new Vector2f(x, y), new Vector2f(width, height));
    }


    public void reset(boolean enemy) {
        generateLocation(enemy);
    }

    public void generateLocation(boolean enemy) {
        Random random = new Random();
        int speedTemp = random.nextInt(3);
        switch (speedTemp) {
            case 0:
                speed = 0.006f;
                break;
            case 1:
                speed = 0.008f;
                break;
            case 2:
                speed = 0.01f;
                break;
        }


        if (!enemy) {

            boolean neg = random.nextBoolean();
            float x = random.nextInt(10);
            x = x / 10;
            if (neg)
                x = -x;
            float y = random.nextInt(10);
            y = y / 10;
            neg = random.nextBoolean();
            if (neg)
                y = -y;
            this.x = x;
            this.y = y;
        }
        else {
            float x = random.nextInt(20);
            float y = random.nextInt(20);
            this.x = x * 0.1f - 1f;
            this.y = y * 0.1f - 1f;
        }

    }

    float[] vertices = new float[]{
            -1, 1, -1, -1, 1, 1, 1, -1
    };

    public boolean calculated = false;

    public void updateLocation(float x, float y, boolean enemy) {

        if (!enemy) {
            this.x += x;
            this.y += y;
        } else {

            if (!calculated) {
                nextX = nextX * 0.1f - 1f;
                nextY = nextY * 0.1f - 1f;
                calculated = true;

              //  System.out.println(nextX + " nx " + this.x + " x " + nextY + " ny " + this.y + " y");
            }

            if (nextX+0.05f > this.x)
                this.x+=speed/3;
            else if (nextX+0.05f < this.x-0.01f)
                this.x-=speed/3;
            if (nextY+0.05f > this.y)
                this.y+=speed/3;
            else if (nextY+0.05f < this.y-0.01f)
                this.y-=speed/3;
       //     else
             //   System.out.println("we all good");

        }

    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSpeed() {
        return speed;
    }

}
