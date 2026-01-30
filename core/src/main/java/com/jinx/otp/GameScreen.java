package com.jinx.otp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {

    private final float WORLD_WIDTH = 10f;
    private final float WORLD_HEIGHT = 10f;

    private final float PLAYER_WIDTH = 1f;
    private final float PLAYER_HEIGHT = 1f;
    private final float PLAYER_SPEED = 1f;

    private final float RESOURCE_WIDTH = 1f;
    private final float RESOURCE_HEIGHT = 1f;

    private final String PLAYER_IMAGE_FILE_NAME = "player.png";
    private final String RESOURCE_IMAGE_FILE_NAME = "resource.png";
    private final String BACKGROUND_IMAGE_FILE_NAME = "resource-collector-background.png";

    private final float DEFAULT_CAMERA_WIDTH = 10f;
    private final float DEFAULT_CAMERA_ZOOM = 0.02f;

    private Texture playerTexture;
    private Texture resourceTexture;
    private Texture backgroundTexture;

    private Sprite playerSprite;
    private Sprite backgroundSprite;
    private Array<Sprite> resourceSprites;

    private OrthographicCamera camera;
    private SpriteBatch batch; // reference for conveniece. DO NOT DISPOSE

    private ResourceCollectorGame game;

    public GameScreen(ResourceCollectorGame game) {
        this.game = game;
        batch = game.getBatch();
        setupBackground();
        setupPlayer();
        setupCamera(); // need to perform after player setup!!
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
        float pixelHeight = Gdx. graphics.getHeight();
        float cameraDefaultHeight = DEFAULT_CAMERA_WIDTH * (pixelHeight / pixelWidth);
        camera = new OrthographicCamera(DEFAULT_CAMERA_WIDTH, cameraDefaultHeight);
        float posX = playerSprite.getX();
        float posY = playerSprite.getY();
        float posZ = 0f;
        camera.position.set(posX, posY, posZ);
        camera.zoom = DEFAULT_CAMERA_ZOOM;
        camera.update();
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

    private void movePlayer(Direction direction, float delta) {
        float speedX = 0f;
        float speedY = 0f;
        float defaultSpeed = PLAYER_SPEED * delta;
        switch (direction) {
            case LEFT:
                speedX = -defaultSpeed;
                break;
            case RIGHT:
                speedX = defaultSpeed;
                break;
            case UP:
                speedY = defaultSpeed;
                break;
            case DOWN:
                speedY = -defaultSpeed;
                break;
            default:
                break;
        }
        playerSprite.translate(speedX, speedY);
        camera.translate(speedX, speedY);
    }

    private void logic() {

    }

    private void draw() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        backgroundSprite.draw(batch);
        playerSprite.draw(batch);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        camera.viewportWidth = width;
        camera.viewportHeight = height;
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
