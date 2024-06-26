package com.fbla.game.Screens;

import com.fbla.game.FBLA;
import com.fbla.game.Util.AnimationUtil;
import com.fbla.game.Util.PlayerMovementUtil;
import com.fbla.game.Util.SceneUtil;
import com.fbla.game.Util.CollisionUtil;
import com.fbla.game.Entity.AIEntity;
import com.fbla.game.Util.TeleporterUtil;
import com.fbla.game.Util.TextUtil;
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
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;




public class GameScreen extends ScreenAdapter {
  FBLA game;

  Sprite player;
  Texture spriteSheet;
  SpriteBatch spriteBatch;
  TextureRegion currentFrame;

  OrthographicCamera cam;

  SceneUtil classroomScene;
  SceneUtil hallwayScene;
  SceneUtil currentScene;

  AnimationUtil animationUtil;
  TextUtil textUtil;
  PlayerMovementUtil playerMovementUtil;



  public GameScreen(FBLA game) {
    this.game = game;
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
  }

  @Override
  public void show() {
    setupAnimations();
    setupCamera();
    setupClassroomScene();
    setupHallwayScene();
    setupClassroomSceneTeleporters();
    setupHallwaySceneTeleporters();
    setupClassroomSceneAI();
    setupAudio();
    textUtil = new TextUtil();
    textUtil.loadTextBox();
    currentScene = classroomScene;
    playerMovementUtil = new PlayerMovementUtil(currentScene.getX(), currentScene.getY(), 300, animationUtil);
  }

  @Override
  public void render(float delta) {
    animationUtil.updateStateTime();
    updateAI(delta);
    handlePlayerMovement();
    handleCollision();
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

  private void updateAI(float delta) {
    ArrayList<AIEntity> aiEntities = currentScene.getAIEntities();
    if(aiEntities != null){
    for (AIEntity aiEntity : aiEntities) {
      aiEntity.update(delta);
    }
    }
  }

  private void setupCamera() {
    cam = new OrthographicCamera();
    cam.setToOrtho(false);
    cam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    cam.update();
  }

  private void setupClassroomScene() {
    classroomScene = new SceneUtil("classroom", -1, -1, 200, 200, new TmxMapLoader().load("tilemaps/untitled.tmx"));
  }

  private void setupClassroomSceneTeleporters() {
    ArrayList<TeleporterUtil> teleporters = new ArrayList<TeleporterUtil>();
    teleporters.add(new TeleporterUtil(classroomScene, hallwayScene, 1536, 128));
    classroomScene.setTeleporters(teleporters);
  }

  private void setupClassroomSceneAI(){
    ArrayList<AIEntity> aiEntities = new ArrayList<AIEntity>();
    aiEntities.add(new AIEntity(new Vector2(1792, 768), new Texture(Gdx.files.internal("spritesheets/astronaut.png")), true));
    classroomScene.setAIEntities(aiEntities);
  }

  private void setupHallwayScene() {
    hallwayScene = new SceneUtil("hallway", -1, -1, 640, 768, new TmxMapLoader().load("tilemaps/hallway.tmx"));
  }

  private void setupHallwaySceneTeleporters() {
    ArrayList<TeleporterUtil> teleporters = new ArrayList<TeleporterUtil>();
    teleporters.add(new TeleporterUtil(hallwayScene, classroomScene, 640, 780));
    hallwayScene.setTeleporters(teleporters);
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
    ArrayList<TeleporterUtil> teleporters = currentScene.getTeleporters();
    for (TeleporterUtil teleporter : teleporters) {
      if (teleporter.playerTouchedTeleporter(player.getBoundingRectangle())) {
        currentScene = teleporter.getNextScene();
        playerX = currentScene.getX();
        playerY = currentScene.getY();
      }
    }
    ArrayList<AIEntity> aiEntities = currentScene.getAIEntities();
    if(aiEntities != null){
    for (AIEntity aiEntity : aiEntities) {
      if (CollisionUtil.checkCollision(currentScene.getCollisionObjects(), aiEntity.getBoundingBox())) {
        aiEntity.handleCollision();
      } else if (Intersector.overlaps(aiEntity.getBoundingBox(), player.getBoundingRectangle())) {
        aiEntity.playerCollided();
        textUtil.isAnimating(true);
        playerMovementUtil.disableMovement();
        Rectangle shrunkRectangle = new Rectangle(player.getBoundingRectangle().x + 32, player.getBoundingRectangle().y + 32, player.getBoundingRectangle().width - 64, player.getBoundingRectangle().height - 64);
        if (Intersector.overlaps(shrunkRectangle, aiEntity.getBoundingBox())) {
          playerX = currentScene.getX();
          playerY = currentScene.getY();
        }
      } else {
        aiEntity.wander();
        textUtil.isAnimating(false);
      }
    }
  }
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
    ArrayList<AIEntity> aiEntities = currentScene.getAIEntities();
    if(aiEntities != null){
    for (AIEntity aiEntity : aiEntities){
      spriteBatch.begin();
      spriteBatch.draw(aiEntity.getCurrentFrame(), aiEntity.getPosition().x, aiEntity.getPosition().y, 128, 128);
      spriteBatch.end();
    }
  }
    tiledMapRenderer.render(new int[] {1,4,5});
    spriteBatch.begin();
    spriteBatch.draw(animationUtil.getCurrentFrame(), (playX - 64), (playY - 64), 128, 128);
    spriteBatch.end();

    textUtil.renderText(spriteBatch, playX, playY);
  }
}