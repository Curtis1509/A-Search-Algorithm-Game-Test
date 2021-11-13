import java.util.Random;

public class Entity {

    float size = 0.05f;
    float width=size*0.75f;
    float height=size;
    float x = 0f;
    float y = 0f;
    float speed = 0f;

    public Entity(){
        generateLocation();
    }
    public void generateLocation(){
        Random random = new Random();
        int speedTemp = random.nextInt(3);
        switch (speedTemp){
            case 0:
                speed = 0.002f;
                break;
            case 1:
                speed = 0.004f;
                break;
            case 2:
                speed = 0.006f;
                break;
        }

        boolean neg = random.nextBoolean();
         float x = random.nextFloat();
        if (neg)
            x=-x;
         float y = random.nextFloat();
        neg = random.nextBoolean();
        if (neg)
            y-=y;
        updateLocation(x,y);
    }

    float[] vertices = new float[]{
            -width+x,-height+y, //top left
            width+x,height+y, //top right
            -width+x,height+y, //bottom left
            width+x,-height+y, //bottom right
            width+x,height+y, //top right
            -width+x,-height+y, //bottom left
    };

    public void updateLocation(float x, float y){
        this.x+=x;
        this.y+=y;
        for (int i = 0 ; i < vertices.length; i+=2){
            vertices[i]+=x;
        }
        for (int i = 1 ; i < vertices.length; i+=2){
            vertices[i]+=y;
        }
    }


    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }

    public float getSpeed(){
        return speed;
    }

}
