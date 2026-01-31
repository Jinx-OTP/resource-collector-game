package com.jinx.otp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {

    private final float WORLD_WIDTH = 25f;
    private final float WORLD_HEIGHT = 25f;

    private final float PLAYER_WIDTH = 1f;
    private final float PLAYER_HEIGHT = 1f;
    private final float PLAYER_SPEED = 3f;

    private final float RESOURCE_WIDTH = 1f;
    private final float RESOURCE_HEIGHT = 1f;
    private final int DISPLAYED_RESOURCES_LIMIT = 10;

    private final String PLAYER_IMAGE_FILE_NAME = "player.png";
    private final String RESOURCE_IMAGE_FILE_NAME = "resource.png";
    private final String BACKGROUND_IMAGE_FILE_NAME = "resource-collector-background.png";

    private final float DEFAULT_CAMERA_WIDTH = 10f;
    private final float DEFAULT_CAMERA_ZOOM = 1f;

    private Texture playerTexture;
    private Texture resourceTexture;
    private Texture backgroundTexture;

    private Sprite playerSprite;
    private Sprite backgroundSprite;
    private Array<Sprite> resourceSprites;
    private BitmapFont font;

    private OrthographicCamera camera;
    private SpriteBatch batch; // reference for conveniece. DO NOT DISPOSE

    private ResourceCollectorGame game;

    private int resourcesCollected;

    public GameScreen(ResourceCollectorGame game) {
        this.game = game;
        batch = game.getBatch();
        setupBackground();
        setupPlayer();
        setupCamera(); // need to perform after player setup!!
        setupResources();
        setupLabel();
    }
    
    private void setupBackground() {
        backgroundTexture = new Texture(Gdx.files.internal(BACKGROUND_IMAGE_FILE_NAME));
        backgroundSprite = new Sprite(backgroundTexture);
        float posX = 0f;
        float posY = 0f;
        backgroundSprite.setPosition(posX, posY);
        backgroundSprite.setSize(WORLD_WIDTH, WORLD_HEIGHT);
    }

    private void setupPlayer() {
        playerTexture = new Texture(Gdx.files.internal(PLAYER_IMAGE_FILE_NAME));
        playerSprite = new Sprite(playerTexture);
        float posX = (WORLD_WIDTH / 2) - (PLAYER_WIDTH / 2);
        float posY = (WORLD_HEIGHT / 2) - (PLAYER_HEIGHT / 2);
        playerSprite.setPosition(posX, posY);
        playerSprite.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    private void setupCamera() {
        float pixelWidth = Gdx.graphics.getWidth();
        float pixelHeight = Gdx.graphics.getHeight();
        float cameraDefaultHeight = DEFAULT_CAMERA_WIDTH * (pixelHeight / pixelWidth);
        camera = new OrthographicCamera(DEFAULT_CAMERA_WIDTH, cameraDefaultHeight);
        float posX = playerSprite.getX() + PLAYER_WIDTH / 2;
        float posY = playerSprite.getY() + PLAYER_HEIGHT / 2;
        float posZ = 0f;
        camera.position.set(posX, posY, posZ);
        camera.zoom = DEFAULT_CAMERA_ZOOM;
        camera.update();
    }

    private void setupResources() {
        resourcesCollected = 0;
        resourceSprites = new Array<>();
        resourceTexture = new Texture(Gdx.files.internal(RESOURCE_IMAGE_FILE_NAME));
        for (int i = 0; i < DISPLAYED_RESOURCES_LIMIT; ++i) {
            Sprite resource = new Sprite(resourceTexture);
            resource.setSize(RESOURCE_WIDTH, RESOURCE_HEIGHT);
            replaceResource(resource);
            resourceSprites.add(resource);
        }
    }
    
    private void setupLabel() {
        font = new BitmapFont();
        font.setUseIntegerPositions(false);
        font.getData().setScale(WORLD_HEIGHT / Gdx.graphics.getHeight());
        font.setColor(Color.BLUE);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic();
        draw();
    }

    private void input(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movePlayer(Direction.LEFT, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movePlayer(Direction.RIGHT, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movePlayer(Direction.UP, delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movePlayer(Direction.DOWN, delta);
        }
    }

    /**
     * Move player and camera according to direction. Clamp user to world size. 
     * Camera cannot show anything outside of the game world
     * 
     * @param direction
     * @param delta
     */
    private void movePlayer(Direction direction, float delta) {
        float playerMoveX = 0f;
        float playerMoveY = 0f;
        float cameraMoveX = 0f;
        float cameraMoveY = 0f;
        float defaultSpeed = PLAYER_SPEED * delta;
        switch (direction) {
            case LEFT:
                playerMoveX = -defaultSpeed;
                if (isPlayerNotInCameraLock(direction)) {
                    cameraMoveX = -defaultSpeed;
                }
                break;
            case RIGHT:
                playerMoveX = defaultSpeed;
                if (isPlayerNotInCameraLock(direction)) {
                    cameraMoveX = defaultSpeed;
                }
                break;
            case UP:
                playerMoveY = defaultSpeed;
                if (isPlayerNotInCameraLock(direction)) {
                    cameraMoveY = defaultSpeed;
                }
                break;
            case DOWN:
                playerMoveY = -defaultSpeed;
                if (isPlayerNotInCameraLock(direction)) {
                    cameraMoveY = -defaultSpeed;
                }
                break;
            default:
                break;
        }
        playerSprite.translate(playerMoveX, playerMoveY);
        camera.translate(cameraMoveX, cameraMoveY);

        float camaraMinX = camera.viewportWidth / 2;
        float cameraMaxX = WORLD_WIDTH - (camera.viewportWidth / 2);
        camera.position.x = MathUtils.clamp(camera.position.x, camaraMinX, cameraMaxX);
        float cameraMinY = camera.viewportHeight / 2;
        float cameraMaxY = WORLD_HEIGHT - camera.viewportHeight / 2;
        camera.position.y = MathUtils.clamp(camera.position.y, cameraMinY, cameraMaxY);

        float playerMinX = 0f;
        float playerMaxX = WORLD_WIDTH - PLAYER_WIDTH;
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), playerMinX, playerMaxX));

        float playerMinY = 0f;
        float playerMaxY = WORLD_HEIGHT - PLAYER_HEIGHT;
        playerSprite.setY(MathUtils.clamp(playerSprite.getY(), playerMinY, playerMaxY));
    }

    private boolean isPlayerNotInCameraLock(Direction direction) {
        float bufferZone;
        switch (direction) {
            case RIGHT:
                bufferZone = (camera.viewportWidth - PLAYER_WIDTH) / 2;
                return (playerSprite.getX() > bufferZone);
            case LEFT:
                bufferZone = WORLD_WIDTH - (camera.viewportWidth + PLAYER_WIDTH) / 2;
                return (playerSprite.getX() < bufferZone);
            case DOWN:
                bufferZone = WORLD_HEIGHT - (camera.viewportHeight + PLAYER_HEIGHT) / 2;
                return (playerSprite.getY() < bufferZone);
            case UP:
                bufferZone = (camera.viewportHeight - PLAYER_HEIGHT) / 2;
                return (playerSprite.getY() > bufferZone);
            default:
                return false;
        }
    }

    private void logic() {
        checkResourceCollection();
    }

    private void checkResourceCollection() {
        float playerX = playerSprite.getX();
        float playerY = playerSprite.getY();
        Rectangle player = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        float initialX = 0f;
        float initialY = 0f;
        Rectangle resource = new Rectangle(initialX, initialY, RESOURCE_WIDTH, RESOURCE_HEIGHT);

        for (Sprite resourceSprite : resourceSprites) {
            float resourceX = resourceSprite.getX();
            float resourceY = resourceSprite.getY();
            resource.setPosition(resourceX, resourceY);
            if (player.overlaps(resource)) {
                ++resourcesCollected;
                System.out.println("Resources collected: " + resourcesCollected);
                replaceResource(resourceSprite);
            }
        }
    }

    private void replaceResource(Sprite resource) {
        float minX = 0f;
        float maxX = WORLD_WIDTH - RESOURCE_WIDTH;
        float newX = MathUtils.random(minX, maxX);

        float minY = 0f;
        float maxY = WORLD_HEIGHT - RESOURCE_HEIGHT;
        float newY = MathUtils.random(minY, maxY);

        resource.setPosition(newX, newY);
    }

    private void draw() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        
        backgroundSprite.draw(batch);
        for (Sprite resource : resourceSprites) {
            resource.draw(batch);
        }
        playerSprite.draw(batch);

        String resourceLabelText = "Resources: " + resourcesCollected;
        float labelX = camera.position.x - (camera.viewportWidth / 2);
        float labelY = camera.position.y - (camera.viewportHeight / 2 - 1);
        font.draw(batch, resourceLabelText, labelX, labelY);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        camera.viewportWidth = DEFAULT_CAMERA_WIDTH;
        float ratio = (float) height / (float) width;
        camera.viewportHeight = DEFAULT_CAMERA_WIDTH * ratio;
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        playerTexture.dispose();
        resourceTexture.dispose();
        backgroundTexture.dispose();
    }

    private enum Direction {
        UP, 
        DOWN,
        LEFT,
        RIGHT
    }

}
