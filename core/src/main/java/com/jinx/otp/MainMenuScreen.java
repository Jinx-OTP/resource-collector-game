package com.jinx.otp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** First screen of the application. Displayed after the application is created. */
public class MainMenuScreen implements Screen {

    private ResourceCollectorGame game;

    private final float WORLD_WIDTH = 5f;
    private final float WORLD_HEIGHT = 3f;
    private Viewport viewport;
    private SpriteBatch batch; // local for convenience. DO NOT DISPOSE

    private BitmapFont font;


    public MainMenuScreen(ResourceCollectorGame game) {
        this.game = game;
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        batch = game.getBatch();
        setupFont();
    }

    private void setupFont() {
        font = new BitmapFont();
        font.setColor(Color.PURPLE);
        font.setUseIntegerPositions(false);
        float windowAdjustedHeight = WORLD_HEIGHT / Gdx.graphics.getHeight();
        font.getData().setScale(windowAdjustedHeight);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        input();
        draw();
    }

    private void input() {
        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.PINK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        float titleX = WORLD_WIDTH / 2;
        float titleY = WORLD_HEIGHT / 2 + 0.25f;
        font.draw(batch, "Welcome to my little test game <3", titleX, titleY);

        float subTitleX = WORLD_WIDTH / 2;
        float subTitleY = WORLD_HEIGHT / 2 - 0.25f;
        font.draw(batch, "Tap anywhere to start the game", subTitleX, subTitleY);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
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
        font.dispose();
    }
}