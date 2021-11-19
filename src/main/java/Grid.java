import org.joml.Vector2f;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Grid {


    Block[][] blocks = new Block[20][20];
    public Block getBlock(int i, int j) {
        return blocks[i][j];
    }

    public void resetThreads() {
        for (PathFinder thread : threads) {
            thread.stop();
        }

        threads = new LinkedList<>();
        runThreads(index);
    }

    public Grid() throws IOException {

        int x = 0;
        int y;

        while (x < 20) {
            y = 0;
            while (y < 20) {
                blocks[x][y] = new Block(x, y);
                y++;
            }
            x++;
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                blocks[i][j].addNeighbours();
            }
        }
    }

    static LinkedList<PathFinder> threads = new LinkedList<>();


    int index;

    public void runThreads(int index) {
        this.index = index;
        for (int i = 0; i < index; i++) {
            threads.add(new PathFinder());
            threads.get(i).index = i;
            threads.get(i).start();
        }
    }

    public boolean pathContains(Block b) {
        for (PathFinder thread : threads) {
            if (thread.path.contains(b))
                return true;
        }
        return false;
    }


    class PathFinder extends Thread {

        boolean finished = false;
        ArrayList<Block> openSet = new ArrayList<>();
        ArrayList<Block> closedSet = new ArrayList<>();
        ArrayList<Block> path = new ArrayList<>();
        Block end;

        public void reset(int index) {
            openSet.clear();
            closedSet.clear();
            path.clear();

            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    blocks[i][j].previous.set(index, null);
                }
            }

            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {

                    float xf = (blocks[i][j].x * 0.1f) - 1f;
                    float yf = (blocks[i][j].y * 0.1f) - 1f;

                    if (Game.entities.get(index).x >= xf && Game.entities.get(index).x < xf + 2f / 20) {
                        if (Game.entities.get(index).y >= yf && Game.entities.get(index).y < yf + 2f / 20) {
                            if (!blocks[i][j].blocked) {
                                openSet.add(blocks[i][j]);
                            } else {
                                try {
                                    reset(index);
                                } catch (StackOverflowError e) {
                                   // System.out.println("Stack overflew");
                                    try {
                                        Thread.sleep(500);
                                        reset(index);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!openSet.isEmpty()) {


                for (int i = 0; i < 20; i++) {
                    for (int j = 0; j < 20; j++) {

                        float xf = (blocks[i][j].x * 0.1f) - 1f;
                        float yf = (blocks[i][j].y * 0.1f) - 1f;


                        if (Game.player.x >= xf && Game.player.x <= xf + 2f / 20) {
                            if (Game.player.y >= yf && Game.player.y <= yf + 2f / 20) {
                                if (blocks[i][j].blocked) {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    reset(index);
                                }
                                end = blocks[i][j];
                                i = 20;
                                j = 20;
                            }
                        }
                        if (end == null) {
                            try {
                                reset(index);
                            } catch (StackOverflowError e) {
                            }
                        }
                    }
                }
                finished = false;
            } else
                reset(index);
        }

        public double heuristic(Block a, Block b) {
            return Math.sqrt((Math.pow(a.x, 2) + Math.pow(a.y, 2)) - (Math.pow(b.x, 2) + Math.pow(b.y, 2)));
        }

        int index;

        @Override
        public void run() {

            reset(index);

            while (true) {
                int timeInside = 0;
                while (!finished) {
                    timeInside++;
                    if (timeInside > 1000) {
                        reset(index);
                        break;
                    }
                    if (openSet.size() > 0) {

                        int winner = 0;
                        for (int i1 = 0; i1 < openSet.size(); i1++) {
                            if (openSet.get(winner).f < openSet.get(winner).f) {
                                winner = i1;
                            }
                        }

                        Block current = openSet.get(winner);

                        if (openSet.get(winner) == end) {
                            path.clear();
                            Block temp = current;

                            path.add(current);
                            while (temp.previous.get(index) != null) {
                                path.add(temp.previous.get(index));
                                temp = temp.previous.get(index);
                            }

                            if (path.size() > 2) {
                                Game.entities.get(index).calculated = false;
                                Game.entities.get(index).nextX = path.get(path.size() - 2).x;
                                Game.entities.get(index).nextY = path.get(path.size() - 2).y;
                            }
                            else {
                                if (Game.playerHealth>0) {
                                    Game.playerHealth--;
                                    System.out.println("Player health: " + Game.playerHealth);
                                }
                                else
                                    System.out.println("You died!");
                            }
                            finished = true;

                            break;
                        }

                        path.clear();
                        Block temp = current;
                        path.add(current);
                        int add = 0;
                        while (temp.previous.get(index) != null) {
                            if (add > 500)
                                reset(index);
                            add++;
                            path.add(temp.previous.get(index));
                            temp = temp.previous.get(index);
                        }


                        openSet.remove(current);
                        closedSet.add(current);

                        ArrayList<Block> neighbors = current.neighbours;
                        for (Block neighbor : neighbors) {
                            if (!closedSet.contains(neighbor)) {
                                int tempG = current.g + 1;

                                if (openSet.contains(neighbor)) {
                                    if (tempG < neighbor.g)
                                        neighbor.g = tempG;

                                } else {
                                    neighbor.g = tempG;
                                    openSet.add(neighbor);

                                }
                                if (end != null)
                                    neighbor.h = heuristic(neighbor, end);
                                else {
                                    reset(index);
                                    break;
                                }
                                neighbor.f = neighbor.g + neighbor.h;
                                neighbor.previous.set(index, current);
                            }
                        }
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reset(index);
            }

        }
    }

    class Block {

        Sprite sprite;
        public int x;
        public int y;
        public double h;
        public int g;
        public double f;
        public boolean blocked = false;
        ArrayList<Block> previous = new ArrayList<>();
        float[] gridVertices;
        ArrayList<Block> neighbours = new ArrayList<>();
        public boolean painted = false;

        public Block(int x, int y) throws IOException {

            for (int i = 0; i < Game.entities.size()+1; i++) {
                previous.add(null);
            }
            this.x = x;
            this.y = y;
            x = x - 10;
            y = y - 10;
            float xf = x * 0.1f + 0.05f;
            float yf = y * 0.1f + 0.05f;

            Random random = new Random();
            int r = random.nextInt(5);
            if (r == 1) {
                blocked = true;
            }

            if (blocked)
                sprite = new Sprite(Texture.loadPngTexture(App.resource("textures", "block" + ".png")), new Vector2f(xf, yf), new Vector2f(2f / 20 - 0.05f, 2f / 20 - 0.05f));
            else
                sprite = new Sprite(Texture.loadPngTexture(App.resource("textures", "empty" + ".png")), new Vector2f(xf, yf), new Vector2f(2f / 20 - 0.05f, 2f / 20 - 0.05f));

            gridVertices = new float[]{
                    -1, 1, -1, -1, 1, 1, 1, -1
            };
        }

        public void addNeighbours() {
            if (x > 0 && (!blocks[x - 1][y].blocked)) {
                neighbours.add(blocks[x - 1][y]);
            }
            if (x < 19 && (!blocks[x + 1][y].blocked)) {
                neighbours.add(blocks[x + 1][y]);
            }
            if (y > 0 && (!blocks[x][y - 1].blocked)) {
                neighbours.add(blocks[x][y - 1]);
            }
            if (y < 19 && (!blocks[x][y + 1].blocked)) {
                neighbours.add(blocks[x][y + 1]);
            }
        }

    }
}
