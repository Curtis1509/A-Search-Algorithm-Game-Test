

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Game implements IGameLogic {

    private int direction = 0;

    private float color = 0.0f;

    public static Entity[] player;

    private final Renderer renderer;



    public Game() throws IOException {
        renderer = new Renderer();
    }



    @Override
    public void init() throws Exception {

        player = new Entity[15];
        for (int i =0 ; i < player.length; i++) {
            if (i == 0)
            player[i] = new Entity("player", false);
            else
                player[i] = new Entity("enemy", true);
        }
        renderer.init();
    }

    @Override
    public void input(Window window) {
        float speed = 0.01f;
        if (window.isKeyPressed(GLFW_KEY_R)){
            for (int i = 1; i < player.length; i++){
                player[i].reset(true);
                player[i].generateLocation(true);
            }
        }
        if (window.isKeyPressed(GLFW_KEY_F)) {
            Renderer.grid.resetThreads();
        }

            if (window.isKeyPressed(GLFW_KEY_UP) || window.isKeyPressed(GLFW_KEY_W)) {
                player[0].updateLocation(0, speed, false);
            } else if (window.isKeyPressed(GLFW_KEY_DOWN) || window.isKeyPressed(GLFW_KEY_S)) {
                player[0].updateLocation(0, -speed, false);
            }
            if (window.isKeyPressed(GLFW_KEY_LEFT) || window.isKeyPressed(GLFW_KEY_A)) {
                player[0].updateLocation(-speed, 0.0f, false);
            } else if (window.isKeyPressed(GLFW_KEY_RIGHT) || window.isKeyPressed(GLFW_KEY_D)) {
                player[0].updateLocation(speed, 0.0f, false);
            } else {
                direction = 0;
            }


        for (int i = 1; i < player.length; i++) {
            if (player[0].getX() < player[i].getX())
                player[i].updateLocation(player[i].getSpeed(), player[i].getSpeed(),true);
            if (player[0].getX() > player[i].getX())
                player[i].updateLocation(player[i].getSpeed(), player[i].getSpeed(),true);

            if (player[0].getY() < player[i].getY())
                player[i].updateLocation(player[i].getSpeed(), player[i].getSpeed(),true);
            if (player[0].getY() > player[i].getY())
                player[i].updateLocation(player[i].getSpeed(), player[i].getSpeed(),true);
        }
    }

    boolean finding = false;

    @Override
    public void update(float interval) throws IOException {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if ( color < 0 ) {
            color = 0.0f;
        }

        //if (finding)

    }

    @Override
    public void render(Window window) throws IOException {
        window.setClearColor(color, color, color, 0.0f);

        //System.out.println(player[0].getX());
        renderer.render(window, player[0]);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
       // Renderer.grid.tick.stop();
    }
}