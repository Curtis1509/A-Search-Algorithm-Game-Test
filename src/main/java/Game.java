

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic {

    private int direction = 0;
    private float color = 0.0f;
    public static Entity[] entities;
    public static Entity player;
    private final Renderer renderer;
    public static boolean showPath = false;


    public Game() {
        renderer = new Renderer();
    }


    @Override
    public void init() throws Exception {

        player = new Entity("player", false);
        entities = new Entity[3];
        for (int i = 0; i < entities.length; i++) {
            entities[i] = new Entity("enemy", true);
        }
        renderer.init();
    }

    public static int playerHealth = 100;
    boolean attack = false;
    public static boolean up = false;
    public static boolean down = false;
    public static boolean right = false;
    public static boolean left = false;
    public static String renderedDirection = "";
    public static boolean pPressed = false;

    @Override
    public void input(Window window) {
        float speed = 0.005f;
        if (window.isKeyPressed(GLFW_KEY_R)) {
            for (Entity entity : entities) {
                entity.reset(true);
                entity.generateLocation(true);
            }
        }
        if (window.isKeyPressed(GLFW_KEY_F)) {
            Renderer.grid.resetThreads();
        }
        if (window.isKeyPressed(GLFW_KEY_P)){
            if (!pPressed) {
                showPath = !showPath;
                pPressed = true;
            }
        }
        else
            pPressed = false;
        if (window.isKeyPressed(GLFW_KEY_SPACE) && !attack) {
            attack = true;
            attack();
        }else{
            attack = false;
        }

        if (window.isKeyPressed(GLFW_KEY_UP) || window.isKeyPressed(GLFW_KEY_W)) {
            player.updateLocation(0, speed, false);
            up = true; down = false; right = false; left = false;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN) || window.isKeyPressed(GLFW_KEY_S)) {
            player.updateLocation(0, -speed, false);
            up = false; down = true; right = false; left = false;
        }
        else if (window.isKeyPressed(GLFW_KEY_LEFT) || window.isKeyPressed(GLFW_KEY_A)) {
            player.updateLocation(-speed, 0.0f, false);
            up = false; down = false; right = false; left = true;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT) || window.isKeyPressed(GLFW_KEY_D)) {
            player.updateLocation(speed, 0.0f, false);
            up = false; down = false; right = true; left = false;
        } else {
            direction = 0;
        }

        for (Entity entity : entities) {
            entity.updateLocation(entity.getSpeed(), entity.getSpeed(), true);
        }
    }

    @Override
    public void update(float interval) {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if (color < 0) {
            color = 0.0f;
        }
    }

    public void attack() {
        for (Entity entity : entities) {
            if (entity.getX() < player.getX() + 0.2f && !(entity.getX() < player.getX()) && right) {
                entity.x = entity.x + 0.2f;
            }
            if (entity.getX() > player.getX() - 0.2f && !(entity.getX() > player.getX()) && left) {
                entity.x = entity.x - 0.2f;
            }
            if (entity.getY() < player.getY() + 0.2f && !(entity.getY() < player.getY()) && up) {
                entity.y = entity.y + 0.2f;
            }
            if (entity.getY() > player.getY() - 0.2f && !(entity.getY() > player.getY()) && down) {
                entity.y = entity.y - 0.2f;
            }
        }
    }


    @Override
    public void render(Window window) throws IOException {
        window.setClearColor(color, color, color, 0.0f);
        renderer.render(window);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}