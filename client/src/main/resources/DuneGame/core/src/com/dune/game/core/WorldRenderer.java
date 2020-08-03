package com.dune.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.dune.game.screens.ScreenManager;
import com.dune.game.screens.utils.Assets;

public class WorldRenderer {
    private SpriteBatch batch;
    private BitmapFont font32;
    private GameController gc;
    private TextureRegion selectorTexture;

    private FrameBuffer frameBuffer;
    private TextureRegion frameBufferRegion;
    private ShaderProgram shaderProgram;

    public WorldRenderer(SpriteBatch batch, GameController gc) {
        this.batch = batch;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf");
        this.selectorTexture = Assets.getInstance().getAtlas().findRegion("selector");
        this.gc = gc;

        this.frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, ScreenManager.WORLD_WIDTH, ScreenManager.WORLD_HEIGHT, false);
        this.frameBufferRegion = new TextureRegion(frameBuffer.getColorBufferTexture());
        this.frameBufferRegion.flip(false, true);
        this.shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl").readString(), Gdx.files.internal("shaders/fragment.glsl").readString());
        if (!shaderProgram.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shaderProgram.getLog());
        }
    }

    public void render() {
        ScreenManager.getInstance().pointCameraTo(gc.getPointOfView());
        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gc.getMap().render(batch);
        gc.getUnitsController().render(batch);
        gc.getBuildingsController().render(batch);
        gc.getProjectilesController().render(batch);
        gc.getParticleController().render(batch);
        drawSelectionFrame();
        batch.end();
        frameBuffer.end();

        ScreenManager.getInstance().resetCamera();
        batch.begin();
        batch.setShader(shaderProgram);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("time"), gc.getWorldTimer());
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("px"), gc.getPointOfView().x / ScreenManager.WORLD_WIDTH);
        shaderProgram.setUniformf(shaderProgram.getUniformLocation("py"), gc.getPointOfView().y / ScreenManager.WORLD_HEIGHT);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(frameBufferRegion, 0, 0);
        batch.end();
        batch.setShader(null);

        ScreenManager.getInstance().resetCamera();
        gc.getStage().draw();
        if (gc.isPaused()) {
            batch.begin();
            font32.draw(batch, "PAUSED", 0, 360, ScreenManager.WORLD_WIDTH, 1, false);
            batch.end();
        }
    }

    public void drawSelectionFrame() {
        if (gc.getSelectionStart().x > 0 && gc.getSelectionStart().y > 0) {
            batch.draw(selectorTexture, gc.getMouse().x - 8, gc.getMouse().y - 8);
            batch.draw(selectorTexture, gc.getMouse().x - 8, gc.getSelectionStart().y - 8);
            batch.draw(selectorTexture, gc.getSelectionStart().x - 8, gc.getSelectionStart().y - 8);
            batch.draw(selectorTexture, gc.getSelectionStart().x - 8, gc.getMouse().y - 8);
            float minX = Math.min(gc.getSelectionStart().x, gc.getMouse().x);
            float maxX = Math.max(gc.getSelectionStart().x, gc.getMouse().x);
            float minY = Math.min(gc.getSelectionStart().y, gc.getMouse().y);
            float maxY = Math.max(gc.getSelectionStart().y, gc.getMouse().y);
            for (float i = minX; i < maxX; i += 30.0f) {
                batch.draw(selectorTexture, i - 4, minY - 4, 8, 8);
                batch.draw(selectorTexture, i - 4, maxY - 4, 8, 8);
            }
            for (float i = minY; i < maxY; i += 30.0f) {
                batch.draw(selectorTexture, minX - 4, i - 4, 8, 8);
                batch.draw(selectorTexture, maxX - 4, i - 4, 8, 8);
            }
        }
    }
}
