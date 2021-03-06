

import java.io.IOException;
import java.util.LinkedList;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic {

    private int direction = 0;
    private float color = 0.0f;
    public static LinkedList<Entity> entities = new LinkedList<>();
    public static Entity player;
    private final Renderer renderer;
    public static boolean showPath = false;
    WavPlayer wavPlayer = new WavPlayer();


    public Game() {
        renderer = new Renderer();
    }


    @Override
    public void init() throws Exception {

        player = new Entity("player", false);
        for (int i = 0; i < 5; i++) {
            entities.add(new Entity("enemy", true));
        }
        renderer.init();
    }

    public static int playerHealth = 100;
    public static boolean attack = false;
    public static boolean up = false;
    public static boolean down = false;
    public static boolean right = false;
    public static boolean left = false;
    public static String renderedDirection = "";
    public static boolean pPressed = false;
    private boolean rPressed = false;

    @Override
    public void input(Window window) throws IOException {
        float speed = 0.005f;
        if (window.isKeyPressed(GLFW_KEY_R) && !rPressed) {
            rPressed = true;
            for (int i = 0; i < 5; i++) {
                entities.add(new Entity("enemy", true));
            }
            for (Entity entity : entities) {
                entity.reset(true);
                entity.generateLocation(true);
            }
            Renderer.grid.adjustIndex();
        } else if (!window.isKeyPressed(GLFW_KEY_R)) {
            rPressed = false;
        }
        if (window.isKeyPressed(GLFW_KEY_F)) {
            Renderer.grid.resetThreads();
        }
        if (window.isKeyPressed(GLFW_KEY_P)) {
            if (!pPressed) {
                showPath = !showPath;
                pPressed = true;
            }
        } else
            pPressed = false;
        if (window.isKeyPressed(GLFW_KEY_SPACE) && !attack) {
            attack = true;
            swordAnimationTimer = 0;
            attack();
        } else {
            if (swordAnimationTimer > 50 && attack) {
                switch (renderedDirection) {
                    case "left", "right" -> Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "sword" + ".png")));
                    case "up" -> Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "swordup" + ".png")));
                    case "down" -> Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "sworddown" + ".png")));
                }
                swordAnimationTimer = 0;
                attack = false;
                player.updateLocation(0, 0, false);
            } else if (attack) {
                player.updateLocation(0, 0, false);
                swordAnimationTimer++;
            }
        }


        if (window.isKeyPressed(GLFW_KEY_UP) || window.isKeyPressed(GLFW_KEY_W)) {
            boolean dontUpdate = false;
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    if (Renderer.grid.getBlock(i, j).blocked) {
                        float gX = Renderer.grid.getBlock(i, j).x * 0.1f - 1f;
                        float gY = Renderer.grid.getBlock(i, j).y * 0.1f - 1f;
                        if (player.x > gX && player.x < gX + 0.10f) {
                            if (player.y > gY-0.05f && player.y < gY) {
                                System.out.println(gY);
                                dontUpdate = true;
                            }
                        }
                    }
                }
            }
            if (!dontUpdate)
                player.updateLocation(0, speed, false);
            up = true;
            down = false;
            right = false;
            left = false;
            if (!renderedDirection.equals("up"))
                Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "swordup" + ".png")));
        } else if (window.isKeyPressed(GLFW_KEY_DOWN) || window.isKeyPressed(GLFW_KEY_S)) {
            boolean dontUpdate = false;
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    if (Renderer.grid.getBlock(i, j).blocked) {
                        float gX = Renderer.grid.getBlock(i, j).x * 0.1f - 1f;
                        float gY = Renderer.grid.getBlock(i, j).y * 0.1f - 1f;
                        if (player.x > gX && player.x < gX + 0.10f) {
                            if (player.y > gY-0.05f && player.y < gY+0.15f) {
                                System.out.println(gY);
                                dontUpdate = true;
                            }
                        }
                    }
                }
            }
            if (!dontUpdate)
            player.updateLocation(0, -speed, false);
            up = false;
            down = true;
            right = false;
            left = false;
            if (!renderedDirection.equals("down"))
                Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "sworddown" + ".png")));
        } if (window.isKeyPressed(GLFW_KEY_LEFT) || window.isKeyPressed(GLFW_KEY_A)) {
            boolean dontUpdate = false;
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    if (Renderer.grid.getBlock(i, j).blocked) {
                        float gX = Renderer.grid.getBlock(i, j).x * 0.1f - 1f;
                        float gY = Renderer.grid.getBlock(i, j).y * 0.1f - 1f;
                        if (player.x > gX && player.x < gX + 0.15f) {
                            if (player.y > gY && player.y < gY+0.10f) {
                                dontUpdate = true;
                            }
                        }
                    }
                }
            }
            if (!dontUpdate)
            player.updateLocation(-speed, 0.0f, false);
            up = false;
            down = false;
            right = false;
            left = true;
            if (!renderedDirection.equals("left") )
                Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "sword" + ".png")));
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT) || window.isKeyPressed(GLFW_KEY_D)) {
            boolean dontUpdate = false;
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    if (Renderer.grid.getBlock(i, j).blocked) {
                        float gX = Renderer.grid.getBlock(i, j).x * 0.1f - 1f;
                        float gY = Renderer.grid.getBlock(i, j).y * 0.1f - 1f;
                        if (player.x > gX-0.01f && player.x < gX + 0.10f) {
                            if (player.y > gY && player.y < gY+0.10f) {
                                System.out.println(gY);
                                dontUpdate = true;
                            }
                        }
                    }
                }
            }
            if (!dontUpdate)
            player.updateLocation(speed, 0.0f, false);
            up = false;
            down = false;
            right = true;
            left = false;
            if (!renderedDirection.equals("right") )
                Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "sword" + ".png")));
        } else {
            direction = 0;
        }

        for (Entity entity : entities) {
            entity.updateLocation(entity.getSpeed(), entity.getSpeed(), true);
        }
    }

    int swordAnimationTimer = 0;

    @Override
    public void update(float interval) {

        color += direction * 0.01f;
        if (color > 1) {
            color = 1.0f;
        } else if (color < 0) {
            color = 0.0f;
        }
    }

    public void attack() throws IOException {
        wavPlayer.playSound(0, false);
        if (renderedDirection.equals("left"))
            Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "swordleft" + ".png")));
        else if (renderedDirection.equals("right"))
            Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "swordright" + ".png")));
        else if (renderedDirection.equals("up"))
            Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "swordupdown" + ".png")));
        else if (renderedDirection.equals("down"))
            Renderer.swordSprite.updateTexture(Texture.loadPngTexture(App.resource("textures", "sworddowndown" + ".png")));
        boolean hit = false;
        float close = 0.15f;
        LinkedList<Entity> deadEntity = new LinkedList<>();
        for (Entity entity : entities) {
            if (entity.getX() < player.getX() + close && !(entity.getX() < player.getX()) && right && entity.getY() > player.getY() - 0.15f && entity.getY() < player.getY() + 0.235f) {
                entity.x = entity.x + 0.2f;
                hit = true;
                entity.health -= 25;
            }
            if (entity.getX() > player.getX() - close && !(entity.getX() > player.getX()) && left && entity.getY() > player.getY() - 0.15f && entity.getY() < player.getY() + 0.235f) {
                entity.x = entity.x - 0.2f;
                hit = true;
                entity.health -= 25;
            }
            if (entity.getY() < player.getY() + close && !(entity.getY() < player.getY()) && up && entity.getX() > player.getX() - 0.15f && entity.getX() < player.getX() + 0.235f) {
                entity.y = entity.y + 0.2f;
                hit = true;
                entity.health -= 25;
            }
            if (entity.getY() > player.getY() - close && !(entity.getY() > player.getY()) && down && entity.getX() > player.getX() - 0.15f && entity.getX() < player.getX() + 0.235f) {
                entity.y = entity.y - 0.2f;
                hit = true;
                entity.health -= 25;
            }
            if (entity.health <= 0) {
                wavPlayer.playSound(2, false);
                deadEntity.add(entity);
            }
        }
        if (hit) {
            wavPlayer.playSound(1, false);
            for (Entity dead : deadEntity) {
                entities.remove(dead);
                Renderer.grid.adjustIndex();
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