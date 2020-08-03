package com.dune.game.core;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dune.game.core.controllers.BuildingsController;
import com.dune.game.core.controllers.ParticleController;
import com.dune.game.core.controllers.ProjectilesController;
import com.dune.game.core.controllers.UnitsController;
import com.dune.game.core.gui.GuiPlayerInfo;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.users_logic.AiLogic;
import com.dune.game.core.users_logic.PlayerLogic;
import com.dune.game.core.utils.Collider;
import com.dune.game.screens.ScreenManager;
import com.dune.game.screens.utils.Assets;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private static final float CAMERA_SPEED = 240.0f;

    private BattleMap map;
    private GuiPlayerInfo guiPlayerInfo;
    private PlayerLogic playerLogic;
    private AiLogic aiLogic;
    private ProjectilesController projectilesController;
    private ParticleController particleController;
    private UnitsController unitsController;
    private BuildingsController buildingsController;
    private Vector2 tmp;
    private Vector2 selectionStart;
    private Vector2 selectionEnd;
    private Vector2 mouse;
    private Collider collider;
    private Vector2 pointOfView;
    private float worldTimer;
    private boolean paused;

//    private Music music;
//    private Sound sound;

    public float getWorldTimer() {
        return worldTimer;
    }

    private List<AbstractUnit> selectedUnits;

    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public PlayerLogic getPlayerLogic() {
        return playerLogic;
    }

    public AiLogic getAiLogic() {
        return aiLogic;
    }

    public Vector2 getSelectionStart() {
        return selectionStart;
    }


    public boolean isPaused() {
        return paused;
    }

    public Vector2 getSelectionEnd() {
        return selectionEnd;
    }

    public Vector2 getPointOfView() {
        return pointOfView;
    }

    public UnitsController getUnitsController() {
        return unitsController;
    }

    public List<AbstractUnit> getSelectedUnits() {
        return selectedUnits;
    }

    public Vector2 getMouse() {
        return mouse;
    }

    public ProjectilesController getProjectilesController() {
        return projectilesController;
    }

    public BattleMap getMap() {
        return map;
    }

    public BuildingsController getBuildingsController() {
        return buildingsController;
    }

    public GameController() {
        this.mouse = new Vector2();
        this.tmp = new Vector2();
        this.playerLogic = new PlayerLogic(this);
        this.aiLogic = new AiLogic(this);
        this.collider = new Collider(this);
        this.selectionStart = new Vector2(-1, -1);
        this.selectionEnd = new Vector2(-1, -1);
        this.selectedUnits = new ArrayList<>();
        this.map = new BattleMap();
        this.projectilesController = new ProjectilesController(this);
        this.particleController = new ParticleController();
        this.buildingsController = new BuildingsController(this);
        this.unitsController = new UnitsController(this);
        this.pointOfView = new Vector2(ScreenManager.HALF_WORLD_WIDTH, ScreenManager.HALF_WORLD_HEIGHT);
        this.buildingsController.setup(3, 3, playerLogic);
        this.buildingsController.setup(14, 8, aiLogic);
//        this.music = Gdx.audio.newMusic(Gdx.files.internal("1.mp3"));
//        this.sound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        createGuiAndPrepareGameInput();
    }

    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }
        if (!paused) {
            worldTimer += dt;
            ScreenManager.getInstance().pointCameraTo(getPointOfView());
            mouse.set(Gdx.input.getX(), Gdx.input.getY());
            ScreenManager.getInstance().getViewport().unproject(mouse);
            unitsController.update(dt);
            playerLogic.update(dt);
            aiLogic.update(dt);
            buildingsController.update(dt);
            projectilesController.update(dt);
            map.update(dt);
            collider.checkCollisions();
            particleController.update(dt);
//        for (int i = 0; i < 5; i++) {
//            particleController.setup(mouse.x, mouse.y, MathUtils.random(-15.0f, 15.0f), MathUtils.random(-30.0f, 30.0f), 0.5f,
//                    0.3f, 1.4f, 1, 1, 0, 1, 1, 0, 0, 0.5f);
//        }
            guiPlayerInfo.update(dt);
        }
        ScreenManager.getInstance().resetCamera();
        stage.act(dt);
        changePOV(dt);
    }

    public void changePOV(float dt) {
        if (Gdx.input.getY() < 10) {
            pointOfView.y += CAMERA_SPEED * dt;
            if (pointOfView.y + ScreenManager.HALF_WORLD_HEIGHT > BattleMap.MAP_HEIGHT_PX) {
                pointOfView.y = BattleMap.MAP_HEIGHT_PX - ScreenManager.HALF_WORLD_HEIGHT;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getY() > 710) {
            pointOfView.y -= CAMERA_SPEED * dt;
            if (pointOfView.y < ScreenManager.HALF_WORLD_HEIGHT) {
                pointOfView.y = ScreenManager.HALF_WORLD_HEIGHT;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getX() < 10) {
            pointOfView.x -= CAMERA_SPEED * dt;
            if (pointOfView.x < ScreenManager.HALF_WORLD_WIDTH) {
                pointOfView.x = ScreenManager.HALF_WORLD_WIDTH;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
        if (Gdx.input.getX() > 1270) {
            pointOfView.x += CAMERA_SPEED * dt;
            if (pointOfView.x + ScreenManager.HALF_WORLD_WIDTH > BattleMap.MAP_WIDTH_PX) {
                pointOfView.x = BattleMap.MAP_WIDTH_PX - ScreenManager.HALF_WORLD_WIDTH;
            }
            ScreenManager.getInstance().pointCameraTo(pointOfView);
        }
    }

    public boolean isUnitSelected(AbstractUnit abstractUnit) {
        return selectedUnits.contains(abstractUnit);
    }

    public InputProcessor prepareInput() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    selectionStart.set(mouse);
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    tmp.set(mouse);

                    if (tmp.x < selectionStart.x) {
                        float t = tmp.x;
                        tmp.x = selectionStart.x;
                        selectionStart.x = t;
                    }
                    if (tmp.y > selectionStart.y) {
                        float t = tmp.y;
                        tmp.y = selectionStart.y;
                        selectionStart.y = t;
                    }

                    selectedUnits.clear();
                    if (Math.abs(tmp.x - selectionStart.x) > 20 & Math.abs(tmp.y - selectionStart.y) > 20) {
                        for (int i = 0; i < unitsController.getPlayerUnits().size(); i++) {
                            AbstractUnit t = unitsController.getPlayerUnits().get(i);
                            if (t.getPosition().x > selectionStart.x && t.getPosition().x < tmp.x
                                    && t.getPosition().y > tmp.y && t.getPosition().y < selectionStart.y
                            ) {
                                selectedUnits.add(t);
                            }
                        }
                    } else {
                        for (int i = 0; i < unitsController.getUnits().size(); i++) {
                            AbstractUnit t = unitsController.getUnits().get(i);
                            if (t.getPosition().dst(tmp) < 30.0f) {
                                selectedUnits.add(t);
                            }
                        }
                    }

                    selectionStart.set(-1, -1);
                }
                return true;
            }
        };
    }

    public void createGuiAndPrepareGameInput() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), ScreenManager.getInstance().getBatch());
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, prepareInput()));
        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        BitmapFont font14 = Assets.getInstance().getAssetManager().get("fonts/font14.ttf");
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(
                skin.getDrawable("smButton"), null, null, font14);
        final TextButton menuBtn = new TextButton("Menu", textButtonStyle);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        final TextButton testBtn = new TextButton("Test", textButtonStyle);
        testBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Test");
                ;
            }
        });
        Group menuGroup = new Group();
        menuBtn.setPosition(0, 0);
        testBtn.setPosition(130, 0);
        menuGroup.addActor(menuBtn);
        menuGroup.addActor(testBtn);
        menuGroup.setPosition(900, 680);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font14, Color.WHITE);
        skin.add("simpleLabel", labelStyle);

        guiPlayerInfo = new GuiPlayerInfo(playerLogic, skin);
        guiPlayerInfo.setPosition(0, 700);
        stage.addActor(guiPlayerInfo);
        stage.addActor(menuGroup);
        skin.dispose();
    }
}