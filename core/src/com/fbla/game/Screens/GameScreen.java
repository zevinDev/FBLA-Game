package com.fbla.game.Screens;

import com.fbla.game.FBLA;
import com.fbla.game.Util.AnimationUtil;
import com.fbla.game.Util.PlayerMovementUtil;
import com.fbla.game.Util.SceneUtil;
import com.fbla.game.Util.CollisionUtil;
import com.fbla.game.Entity.AIEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Intersector;


public class GameScreen extends ScreenAdapter {
  FBLA game;

  Sprite player;
  Texture spriteSheet;
  SpriteBatch spriteBatch;
  TextureRegion currentFrame;

  OrthographicCamera cam;

  SceneUtil mainScene;
  SceneUtil currentScene;

  AnimationUtil animationUtil;
  PlayerMovementUtil playerMovementUtil;

  private AIEntity aiEntity;


  public GameScreen(FBLA game) {
    this.game = game;
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
  }

  @Override
  public void show() {
    setupAnimations();
    setupCamera();
    setupMainScene();
    setupAudio();
    currentScene = mainScene;
    playerMovementUtil = new PlayerMovementUtil(currentScene.getX(), currentScene.getY(), 300, animationUtil);
    aiEntity = new AIEntity(new Vector2(1000, 1000), new Texture(Gdx.files.internal("spritesheets/astronaut.png")));
  }

  @Override
  public void render(float delta) {
    animationUtil.updateStateTime();
    handlePlayerMovement();
    handleCollision();
    aiEntity.update(delta);
  }

  @Override
  public void hide() {
    spriteBatch.dispose();
    spriteSheet.dispose();
  }

  private void setupAnimations() {
    spriteSheet = new Texture(Gdx.files.internal("spritesheets/taggart.png"));
    player = new Sprite(spriteSheet, 0, 0, 16, 8);
    TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 16, 16);
    TextureRegion[] downFrames = new TextureRegion[2];
    downFrames[0] = tmp[0][1];
    downFrames[1] = tmp[0][2];

    TextureRegion[] upFrames = new TextureRegion[2];
    upFrames[0] = tmp[1][1];
    upFrames[1] = tmp[1][2];

    TextureRegion[] leftFrames = new TextureRegion[2];
    leftFrames[0] = tmp[2][2];
    leftFrames[1] = tmp[3][0];

    TextureRegion[] rightFrames = new TextureRegion[2];
    rightFrames[0] = tmp[2][0];
    rightFrames[1] = tmp[2][1];

    TextureRegion[] idleFrames = new TextureRegion[4];
    idleFrames[0] = tmp[0][0];
    idleFrames[1] = tmp[1][0];
    idleFrames[2] = tmp[2][0];
    idleFrames[3] = tmp[2][2];

    Animation < TextureRegion > downAnimation = new Animation < TextureRegion > (.2f, downFrames);
    Animation < TextureRegion > upAnimation = new Animation < TextureRegion > (.2f, upFrames);
    Animation < TextureRegion > leftAnimation = new Animation < TextureRegion > (.2f, leftFrames);
    Animation < TextureRegion > rightAnimation = new Animation < TextureRegion > (.2f, rightFrames);
    Animation < TextureRegion > idleAnimation = new Animation < TextureRegion > (.2f, idleFrames);

    spriteBatch = new SpriteBatch();

    animationUtil = new AnimationUtil(downAnimation, upAnimation, leftAnimation, rightAnimation, idleAnimation);
  }

  private void setupCamera() {
    cam = new OrthographicCamera();
    cam.setToOrtho(false);
    cam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    cam.update();
  }

  private void setupMainScene() {
    mainScene = new SceneUtil("main", -1, -1, 200, 200, new TmxMapLoader().load("tilemaps/untitled.tmx"));
  }

  private void setupAudio() {
    Music music = Gdx.audio.newMusic(Gdx.files.internal("audio/hometownOST.mp3"));
    music.setLooping(true);
    music.play();
    music.setVolume(0f);

    Sound step = Gdx.audio.newSound(Gdx.files.internal("audio/shoestep.wav"));
  }

  private void handlePlayerMovement() {
    playerMovementUtil.updateMovement();
    player.setPosition(((playerMovementUtil.getPlayerX())-70)/8, ((playerMovementUtil.getPlayerY())-70)/8);
  }

  private void handleCollision() {
    float playerX = playerMovementUtil.getPlayerX();
    float playerY = playerMovementUtil.getPlayerY();
    if (CollisionUtil.checkCollision(currentScene.getCollisionObjects(), player.getBoundingRectangle())) {
      playerX = currentScene.getX();
      playerY = currentScene.getY();
    } else if (CollisionUtil.checkOpacity(currentScene.getLayer("ObjectOpacity"), playerX, playerY)){
      currentScene.setLayerOpacity("Object", 0.5f);
    } else if (CollisionUtil.checkOpacity(currentScene.getLayer("BuildingOpacity"), playerX, playerY)){
      currentScene.setLayerOpacity("Buildings", .5f);
      currentScene.setLayerOpacity("BuildingAccesories", .5f);
    } else {
      currentScene.setLayerOpacity("Object", 1f);
      currentScene.setLayerOpacity("Buildings", 1f);
      currentScene.setLayerOpacity("BuildingAccesories", 1f);
    }
    if(CollisionUtil.checkCollision(currentScene.getCollisionObjects(), aiEntity.getBoundingBox())){
      aiEntity.handleCollision();
    } else if (Intersector.overlaps(aiEntity.getBoundingBox(), player.getBoundingRectangle())){
      aiEntity.playerCollided();
      playerX = currentScene.getX();
      playerY = currentScene.getY();
    } else {
      aiEntity.wander();
    }
    // || (Intersector.overlaps(aiEntity.getBoundingBox(), player.getBoundingRectangle())) && aiEntity.getStateMachine().isInState(AIEntity.AIState.WANDER))
    renderScene(playerX, playerY);
  }

  private void renderScene(float playX, float playY) {
    TiledMapRenderer tiledMapRenderer = currentScene.getMapRenderer();
    playerMovementUtil.updatePosition(playX, playY);
    currentScene.setPosition(playX, playY);
    cam.position.set(playX, playY, 0);
    cam.update();
    tiledMapRenderer.setView(cam);

    spriteBatch.setProjectionMatrix(cam.combined);

    // Draw the current frame at the AI entity's position
    tiledMapRenderer.render(new int[] {0,2});
    spriteBatch.begin();
    spriteBatch.draw(aiEntity.getCurrentFrame(), aiEntity.getPosition().x, aiEntity.getPosition().y, 128, 128);
    spriteBatch.end();
    tiledMapRenderer.render(new int[] {1,4,5});
    spriteBatch.begin();
    spriteBatch.draw(animationUtil.getCurrentFrame(), (playX - 64), (playY - 64), 128, 128);
    spriteBatch.end();

  }
}