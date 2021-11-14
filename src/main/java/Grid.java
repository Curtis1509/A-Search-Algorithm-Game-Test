import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.nio.file.Path;

public class Grid {


    Block[][] blocks = new Block[20][20];
    public Block[][] getBlocks(){
        return blocks;
    }
    public Block getBlock(int i, int j){
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
        /*
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (image.getRGB(i, j) == -16711936) {
                    openSet.add(blocks[i][j]);
                }
                if (image.getRGB(i, j) == -65536) {
                    end = blocks[i][j];
                }
            }
        }*/

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                blocks[i][j].addNeighbours();
            }
        }
    }
    Path resourcePath = Path.of("src", "main", "resources");

    public Path resource(String first, String... more) {
        return resourcePath.resolve(Path.of(first, more)).toAbsolutePath();
    }
    class Block {

        public Texture getTexture(){
            return texture;
        }

        public Texture texture = Texture.loadPngTexture(resource("textures", "block.png"));
        public int x;
        public int y;
        public double h;
        public int g;
        public double f;
        public boolean blocked = false;
        Block previous = null;

        float[] gridVerticies;

        public float[] getGridVerticies(){
            return gridVerticies;
        }

        ArrayList<Block> neighbours = new ArrayList<>();

        public Block(int x, int y) throws IOException {
            this.x = x;
            this.y = y;
            Random random = new Random();
            int r = random.nextInt(10);
            if (r == 2) {
                blocked = true;
            }
            x = x-10;
            y = y-10;
            float xf = x*0.1f;
            float yf = y*0.1f;
            System.out.println(xf + " x | y" + yf);
            gridVerticies = new float[]{
                    xf, yf, //top left
                    xf+0.1f, yf, //top right
                    xf, yf - 0.1f, // bottom left
                    xf+0.1f, yf, //top right
                    xf, yf - 0.1f, //bottom left
                    xf+0.1f, yf - 0.1f, //bottom right

            };
            //System.out.println(image.getRGB(x, y));
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
