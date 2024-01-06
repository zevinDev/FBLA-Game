package com.fbla.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;

public class GameScreen extends ScreenAdapter {
  FBLA game;

  Texture spriteSheet;
  SpriteBatch spriteBatch;
  Animation < TextureRegion > downAnimation;
  Animation < TextureRegion > idleAnimation;
  Animation < TextureRegion > upAnimation;
  Animation < TextureRegion > leftAnimation;
  Animation < TextureRegion > rightAnimation;
  TextureRegion currentFrame;

  float playerX;
  float playerY;
  float playerIdleFrame = 0;

  Sound step;
  boolean stepPlaying;

  OrthographicCamera cam;

  float stateTime;

  Scene currentScene;
  Scene mainScene;

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
    stateTime = 0f;
    currentScene = mainScene;
  }

  @Override
  public void render(float delta) {
    stateTime += Gdx.graphics.getDeltaTime();
    handlePlayerMovement();
    handleCollision();
    handleAudio();
  }

  @Override
  public void hide() {
    spriteBatch.dispose();
    spriteSheet.dispose();
  }

  private void setupAnimations() {
    spriteSheet = new Texture(Gdx.files.internal("zevin.png"));
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

    TextureRegion[] idleFrames = new TextureRegion[1];
    idleFrames[0] = tmp[0][0];
    idleFrames[1] = tmp[1][0];
    idleFrames[2] = tmp[2][0];
    idleFrames[3] = tmp[2][2];

    downAnimation = new Animation < TextureRegion > (.2f, downFrames);
    upAnimation = new Animation < TextureRegion > (.2f, upFrames);
    leftAnimation = new Animation < TextureRegion > (.2f, leftFrames);
    rightAnimation = new Animation < TextureRegion > (.2f, rightFrames);
    idleAnimation = new Animation < TextureRegion > (.2f, idleFrames);

    spriteBatch = new SpriteBatch();
  }

  private void setupCamera() {
    cam = new OrthographicCamera();
    cam.setToOrtho(false);
    cam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0);
    cam.update();
  }

  private void setupMainScene() {
    TiledMap tiledMap = new TmxMapLoader().load("map.tmx");
    TiledMapRenderer tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 8);

    TiledMapTileLayer mainLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Main");
    TiledMapTileLayer mainAccessoriesLayer = (TiledMapTileLayer) tiledMap.getLayers().get("MainAccessories");
    TiledMapTileLayer mainCollisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("MainCollision");
    TiledMapTileLayer accessoriesCollisionLayer = (TiledMapTileLayer) tiledMap.getLayers().get("AccessoriesCollision");

    mainScene = new Scene("main", -1, -1, 100, 100, tiledMapRenderer, new TiledMapTileLayer[] {mainLayer, mainAccessoriesLayer, mainCollisionLayer, accessoriesCollisionLayer});
  }

  private void setupAudio() {
    Music music = Gdx.audio.newMusic(Gdx.files.internal("hometownOST.mp3"));
    music.setLooping(true);
    music.play();
    music.setVolume(0.1f);

    step = Gdx.audio.newSound(Gdx.files.internal("shoestep.wav"));
  }

  private void handlePlayerMovement() {
    playerX = currentScene.getX();
    playerY = currentScene.getY();
    float playerSpeed = 300;
    if (Gdx.input.isKeyPressed(Keys.W) && Gdx.input.isKeyPressed(Keys.D)) {
      playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
      playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
      playerIdleFrame = 1;
      currentFrame = upAnimation.getKeyFrame(stateTime, true);
    } else if (Gdx.input.isKeyPressed(Keys.W) && Gdx.input.isKeyPressed(Keys.A)) {
      playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
      playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
      playerIdleFrame = 1;
      currentFrame = upAnimation.getKeyFrame(stateTime, true);
    } else if (Gdx.input.isKeyPressed(Keys.S) && Gdx.input.isKeyPressed(Keys.D)) {
      playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
      playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
      playerIdleFrame = 0;
      currentFrame = downAnimation.getKeyFrame(stateTime, true);
    } else if (Gdx.input.isKeyPressed(Keys.S) && Gdx.input.isKeyPressed(Keys.A)) {
      playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
      playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
      playerIdleFrame = 0;
      currentFrame = downAnimation.getKeyFrame(stateTime, true);
    } else if (Gdx.input.isKeyPressed(Keys.A)) {
      playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
      playerIdleFrame = 3;
      currentFrame = leftAnimation.getKeyFrame(stateTime, true);
    } else if (Gdx.input.isKeyPressed(Keys.D)) {
      playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
      playerIdleFrame = 2;
      currentFrame = rightAnimation.getKeyFrame(stateTime, true);
    } else if (Gdx.input.isKeyPressed(Keys.W)) {
      playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
      playerIdleFrame = 1;
      currentFrame = upAnimation.getKeyFrame(stateTime, true);
    } else if (Gdx.input.isKeyPressed(Keys.S)) {
      playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
      playerIdleFrame = 0;
      currentFrame = downAnimation.getKeyFrame(stateTime, true);
    } else {
      currentFrame = idleAnimation.getKeyFrames()[(int) playerIdleFrame];
    }
  }

  private void handleCollision() {
    TiledMapTileLayer[] layers = currentScene.getLayers();
    TiledMapTileLayer mainLayer = layers[0];
    TiledMapTileLayer mainAccessoriesLayer = layers[1];
    TiledMapTileLayer mainCollisionLayer = layers[2];
    TiledMapTileLayer accessoriesCollisionLayer = layers[3];
    float oldX = currentScene.getX();
    float oldY = currentScene.getY();
    String mainCollision = checkCollision(playerX, playerY, mainCollisionLayer);
    String accessoriesCollision = checkCollision(playerX, playerY, accessoriesCollisionLayer);
    if (mainCollision == "blocked") {
      playerY = oldY;
      playerX = oldX;
    } else if (mainCollision == "opaque") {
      mainLayer.setOpacity(.25f);
      mainAccessoriesLayer.setOpacity(.25f);
      spriteBatch.setColor(1, 1, 1, .75f);
    } else if (accessoriesCollision == "blocked") {
      playerY = oldY;
      playerX = oldX;
    } else if (accessoriesCollision == "opaque") {
      accessoriesCollisionLayer.setOpacity(.25f);
      spriteBatch.setColor(1, 1, 1, .75f);
    } else {
      mainLayer.setOpacity(1f);
      mainAccessoriesLayer.setOpacity(1f);
      accessoriesCollisionLayer.setOpacity(1f);
      spriteBatch.setColor(1, 1, 1, 1f);
    }
    renderScene(playerX, playerY);
  }

  private void handleAudio() {
    if ((currentFrame != idleAnimation.getKeyFrames()[(int) playerIdleFrame]) && !stepPlaying) {
      step.setLooping(step.play(0.025f), true);
      stepPlaying = true;
    } else if (currentFrame == idleAnimation.getKeyFrames()[(int) playerIdleFrame]) {
      step.stop();
      stepPlaying = false;
    }
  }

  private String checkCollision(float x, float y, TiledMapTileLayer layer) {
    Cell topRightCell = null;
    Cell topLeftCell = null;
    Cell bottomLeftCell = null;
    Cell bottomRightCell = null;

    topRightCell = layer.getCell((int)((x + 32) / 128), (int)(y / 128));
    topLeftCell = layer.getCell((int)((x - 32) / 128), (int)(y / 128));
    bottomLeftCell = layer.getCell((int)((x - 32) / 128), (int)((y - 56) / 128));
    bottomRightCell = layer.getCell((int)((x + 32) / 128), (int)((y - 56) / 128));

    return checkCellCollision(topRightCell, topLeftCell, bottomLeftCell, bottomRightCell);
  }

  private String checkCellCollision(Cell...cells) {
    ArrayList < String > collisionOutput = new ArrayList < String > ();
    for (Cell cell: cells) {
      if (cell != null && cell.getTile() != null) {
        if (cell.getTile().getProperties().containsKey("blocked")) {
          collisionOutput.add("blocked");
        } else if (cell.getTile().getProperties().containsKey("opaque")) {
          collisionOutput.add("opaque");
        }
      }
    }
    if (collisionOutput.size() > 0) {
      if (collisionOutput.contains("blocked")) {
        return "blocked";
      } else if (collisionOutput.contains("opaque")) {
        return "opaque";
      }
    }
    return null;
  }

  private void renderScene(float playX, float playY) {
    TiledMapRenderer tiledMapRenderer = currentScene.getMapRenderer();
    currentScene.setX(playX);
    currentScene.setY(playY);
    cam.position.set(playX, playY, 0);
    cam.update();
    tiledMapRenderer.setView(cam);
    tiledMapRenderer.render(new int[] {0,1,2,3,5});
    spriteBatch.setProjectionMatrix(cam.combined);
    spriteBatch.begin();
    spriteBatch.draw(currentFrame, (playX - 64), (playY - 64), 128, 128);
    spriteBatch.end();
  }
}