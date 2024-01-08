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
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.maps.MapObject;


public class GameScreen extends ScreenAdapter {
  FBLA game;

  Sprite player;
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

  Scene mainScene;
  Scene currentScene;


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
    MapLayer collisionObjectLayer = tiledMap.getLayers().get("Collision");
    mainScene = new Scene("main", -1, -1, 100, 100, tiledMapRenderer, collisionObjectLayer);
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
    player.setPosition((playerX-70)/8, (playerY-70)/8);
  }

  private void handleCollision() {
    float oldX = currentScene.getX();
    float oldY = currentScene.getY();
    MapLayer collisionObjectLayer = currentScene.getCollisionLayer();
    boolean mainCollision = checkCollision(collisionObjectLayer);
    if (mainCollision) {
      playerY = oldY;
      playerX = oldX;
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

  private boolean checkCollision(MapLayer layer) {
    MapLayer collisionObjectLayer = currentScene.getCollisionLayer();
    MapObjects objects = collisionObjectLayer.getObjects();
    for (int i = 0; i < objects.getCount(); i++)
    {
        MapObject mapObject = objects.get(i);

        if (mapObject instanceof RectangleMapObject)
        {
            RectangleMapObject rectangleObject = (RectangleMapObject) mapObject;
            Rectangle rectangle = rectangleObject.getRectangle();
            if (Intersector.overlaps(rectangle, player.getBoundingRectangle())) {
              return true;
           }
            
        }
        else if (mapObject instanceof EllipseMapObject)
        {
            EllipseMapObject circleMapObject = (EllipseMapObject) mapObject;
            Ellipse ellipse = circleMapObject.getEllipse();


            if (ellipse.width != ellipse.height)
                throw new IllegalArgumentException("Only circles are allowed.");

            Circle circle = new Circle(ellipse.x + ellipse.width / 2, ellipse.y + ellipse.height / 2, ellipse.width / 2);
            if(Intersector.overlaps(circle, player.getBoundingRectangle())) {
              return true;
            }
        }
    }
    return false;
  }
  

  private void renderScene(float playX, float playY) {
    TiledMapRenderer tiledMapRenderer = currentScene.getMapRenderer();
    currentScene.setPosition(playX, playY);
    cam.position.set(playX, playY, 0);
    cam.update();
    tiledMapRenderer.setView(cam);
    tiledMapRenderer.render(new int[] {0,1,2});
    spriteBatch.setProjectionMatrix(cam.combined);
    spriteBatch.begin();
    spriteBatch.draw(currentFrame, (playX - 64), (playY - 64), 128, 128);
    spriteBatch.end();
    tiledMapRenderer.render(new int[] {3});
  }
}