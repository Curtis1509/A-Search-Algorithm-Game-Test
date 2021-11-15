import java.io.IOException;

public interface IGameLogic {

    void init() throws Exception;

    void input(Window window);

    void update(float interval) throws IOException;

    void render(Window window) throws IOException;

    void cleanup();
}