import org.joml.Vector2f;
import org.lwjgl.opengl.GL;

import javax.swing.Timer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.nio.file.Path;
import java.util.Stack;

public class Grid {


    Block[][] blocks = new Block[20][20];

    public Block[][] getBlocks() {
        return blocks;
    }

    public Block getBlock(int i, int j) {
        return blocks[i][j];
    }


    public Grid() throws IOException {

        int x = 0;
        int y = 0;

        while (x < 20) {
            y = 0;
            while (y < 20 && x < 20) {
                blocks[x][y] = new Block(x, y);
                y++;
            }
            x++;
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                if (!blocks[i][j].blocked && (i == 0 && j == 0)) {
                    openSet.add(blocks[i][j]);
                }
                float xf = (blocks[i][j].x * 0.1f) - 1f;
                float yf = (blocks[i][j].y * 0.1f) - 1f;
                //  System.out.println(xf + " x " + yf + " y " + " player: " + Game.player[0].x + " x " + Game.player[0].y + " y");

                if (Game.player[0].x >= xf && Game.player[0].x < xf + 2f / 20) {
                    if (Game.player[0].y >= yf && Game.player[0].y < yf + 2f / 20) {
                        end = blocks[i][j];
                        //  System.out.println(xf + " x " + yf + " y " + " player: " + Game.player[0].x + " x " + Game.player[0].y + " y :: MATCH FOUND");
                    }
                }
            }
        }


        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                blocks[i][j].addNeighbours();
            }
        }
    }

    Path resourcePath = Path.of("src", "main", "resources");

    boolean finished = false;
    //Block currentspot;
    //  int spot;
    // int wait = 0;


    volatile ArrayList<Block> openSet = new ArrayList<>();
    volatile ArrayList<Block> closedSet = new ArrayList<>();
    volatile ArrayList<Block> path = new ArrayList<>();
    volatile Block end;

    public synchronized void reset() {
        //  System.out.println("resetting");
        openSet.clear();
        closedSet.clear();
        path.clear();

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                blocks[i][j].previous = null;
            }
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {

                float xf = (blocks[i][j].x * 0.1f) - 1f;
                float yf = (blocks[i][j].y * 0.1f) - 1f;
                //System.out.println(xf + " x " + yf + " y " + " player: " + Game.player[0].x + " x " + Game.player[0].y + " y");

                if (Game.player[1].x >= xf && Game.player[1].x < xf + 2f / 20) {
                    if (Game.player[1].y >= yf && Game.player[1].y < yf + 2f / 20) {
                        if (!blocks[i][j].blocked) {
                            openSet.add(blocks[i][j]);

                            //  System.out.println("found player 1 block");
                        } else {
                            try{
                            reset();
                            }
                            catch(StackOverflowError e ){
                             System.out.println("Stack overflew");
                                try {
                                    Thread.sleep(500);
                                    reset();
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            // System.out.println("found origin block");
                        }
                        //System.out.println(xf + " x " + yf + " y " + " player: " + Game.player[0].x + " x " + Game.player[0].y + " y :: MATCH FOUND");
                    }
                }


                if (Game.player[0].x >= xf && Game.player[0].x < xf + 2f / 20) {
                    if (Game.player[0].y >= yf && Game.player[0].y < yf + 2f / 20) {
                        if (blocks[i][j].blocked) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            reset();
                        }
                        end = blocks[i][j];

                        System.out.println("located a player at " + i + "i | j" +j );
                        // System.out.println(xf + " x " + yf + " y " + " player: " + Game.player[0].x + " x " + Game.player[0].y + " y :: MATCH FOUND");
                    }
                }
                if (end == null){
                    try{
                        reset();
                    }catch (StackOverflowError e ){
                        System.out.println("failed to locate a player, tried to reset but stack overflowed");
                    }
                }
                //   System.out.println(openSet.size());
            }
        }
        finished = false;
    }

    public static boolean set = false;

    public double heuristic(Block a, Block b) {
        return Math.sqrt((Math.pow(a.x, 2) + Math.pow(a.y, 2)) - (Math.pow(b.x, 2) + Math.pow(b.y, 2)));
    }

    private LinkedList<Thread> threads = new LinkedList<Thread>();

    public void runThreads(int count){
        for (int i = 0; i < 1; i++) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                        //   System.out.println("running");
                        int timeInside = 0;
                        while (!finished) {
                            timeInside++;
                            if (timeInside > 1000) {
                                reset();
                                break;
                            }
                            // System.out.println("in");
                            if (openSet.size() > 0) {

                                int winner = 0;
                                for (int i = 0; i < openSet.size(); i++) {
                                    if (openSet.get(winner).f < openSet.get(winner).f) {
                                        winner = i;
                                    }
                                }

                                Block current = openSet.get(winner);


                                if (openSet.get(winner) == end) {
                                    //  wait = 0;

                                    //  System.out.println("Done");


                                    path.clear();
                                    Block temp = current;

                                    path.add(current);
                                    int count = 0;
                                    while (temp.previous != null) {
                                        count++;
                                        if (count == 1) {

                                        }
                                        path.add(temp.previous);
                                        temp = temp.previous;
                                    }

                                    if (path.size() > 2) {
                                        Game.player[1].calculated = false;
                                        Game.player[1].nextX = path.get(path.size() - 2).x;
                                        Game.player[1].nextY = path.get(path.size() - 2).y;
                                    }
                                    //Game.player[1].updateLocation(0.1f,0.1f,true);
                                    // System.out.println(Game.player[0].x + " px " + ((temp.x * 0.1f) - 1f));
                                    // spot = path.size() - 1;
                                    // currentspot = path.get(spot);
                                    finished = true;
                                    // System.out.println("done");

                                    break;
                                }

                                path.clear();
                                Block temp = current;
                                path.add(current);
                                int add = 0;
                                while (temp.previous != null) {
                                    // System.out.println("adding" + add);
                                    if (add > 500)
                                        reset();
                                    add++;
                                    path.add(temp.previous);
                                    temp = temp.previous;
                                }


                                openSet.remove(current);
                                closedSet.add(current);

                                ArrayList<Block> neighbors = current.neighbours;
                                for (int i = 0; i < neighbors.size(); i++) {
                                    Block neighbor = neighbors.get(i);
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
                                            reset();
                                            break;
                                        }
                                        neighbor.f = neighbor.g + neighbor.h;
                                        neighbor.previous = current;
                                    }
                                }
                            }
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //         System.out.println("out");
                        reset();
                    }
                }
            }));
            threads.get(i).start();
        }
    }



    public Path resource(String first, String... more) {
        return resourcePath.resolve(Path.of(first, more)).toAbsolutePath();
    }

    class Block {
        Sprite sprite;
        //  public Texture getTexture(){
        // return texture;
        // }


        public int x;
        public int y;
        public double h;
        public int g;
        public double f;
        public boolean blocked = false;
        Block previous = null;

        float[] gridVerticies;

        public float[] getGridVerticies() {
            return gridVerticies;
        }

        ArrayList<Block> neighbours = new ArrayList<>();

        public boolean painted = false;

        public Block(int x, int y) throws IOException {
            this.x = x;
            this.y = y;

            Random random = new Random();
            int r = random.nextInt(2);
            if (r == 1 ) {
                blocked = true;
            }
            x = x - 10;
            y = y - 10;
            float xf = x * 0.1f + 0.05f;
            float yf = y * 0.1f + 0.05f;

            if (blocked)
                sprite = new Sprite(Texture.loadPngTexture(resource("textures", "block" + ".png")), new Vector2f(xf, yf), new Vector2f(2f / 20 - 0.05f, 2f / 20 - 0.05f));
            else
                sprite = new Sprite(Texture.loadPngTexture(resource("textures", "empty" + ".png")), new Vector2f(xf, yf), new Vector2f(2f / 20 - 0.05f, 2f / 20 - 0.05f));

            //  System.out.println(xf + " x | y" + yf);

            gridVerticies = new float[]{
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
