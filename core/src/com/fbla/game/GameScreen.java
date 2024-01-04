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

public class GameScreen extends ScreenAdapter {
    FBLA game;

	Animation<TextureRegion> downAnimation;
	Animation<TextureRegion> idleAnimation;
	Animation<TextureRegion> upAnimation;
	Animation<TextureRegion> leftAnimation;
	Animation<TextureRegion> rightAnimation;
	Texture spriteSheet;
	SpriteBatch spriteBatch;
	TextureRegion currentFrame;

	float stateTime;

	float playerX = 100;
	float playerY = 100;
	float oldX = 100;
	float oldY = 100;
	float playerSpeed = 300;
	float playerIdleFrame = 0;

//Variable for step audio
	String bip = "shoestep.mp3";
	Music step;
	boolean stepPlaying;
	long id;

	TiledMapRenderer tiledMapRenderer;
    TiledMap tiledMap;
    OrthographicCamera cam;
	TiledMapTileLayer collisionLayer;
	TiledMapTileLayer buildingsLayer;
	TiledMapTileLayer accesoriesLayer;
	int[] layerRenderOrder = {0, 1, 2};

    public GameScreen(FBLA game) {
        this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void show() {
		setupAnimations();
		setupCamera();
		setupTileMap();
		setupAudio();
		stateTime = 0f;
    }

    @Override
    public void render(float delta) {
		stateTime += Gdx.graphics.getDeltaTime();
		playerMovement();
		handleCollision();
        handleAudio();
    }

    @Override
    public void hide() {
        spriteBatch.dispose();
		spriteSheet.dispose();
    }

    private void handleAudio() {
        
		// Player Steps sound
			if ((currentFrame != idleAnimation.getKeyFrames()[(int)playerIdleFrame]) && !stepPlaying) {
				step.play();
				step.setVolume(0.025f);
				stepPlaying = true;
			} else if (currentFrame == idleAnimation.getKeyFrames()[(int)playerIdleFrame]) {
				step.stop();
				stepPlaying = false;
			}
    }
	private void handleCollision(){
		if(checkCollision(playerX, playerY, collisionLayer) == "blocked"){
			playerY = oldY;
			playerX = oldX;
			spriteBatch.setColor(1,1,1,1f);
			buildingsLayer.setOpacity(1f);
			renderScene(oldX, oldY);
		}else if(checkCollision(playerX, playerY, collisionLayer) == "opaque"){
			buildingsLayer.setOpacity(.25f);
			accesoriesLayer.setOpacity(.25f);
			spriteBatch.setColor(1,1,1,.75f);
			renderScene(playerX, playerY);
		} else if(checkCollision(playerX, playerY, accesoriesLayer) == "blocked"){
			playerY = oldY;
			playerX = oldX;
			spriteBatch.setColor(1,1,1,1f);
			accesoriesLayer.setOpacity(1f);
			renderScene(playerX, playerY);
		} else if(checkCollision(playerX, playerY, accesoriesLayer) == "opaque"){
			accesoriesLayer.setOpacity(.25f);
			spriteBatch.setColor(1,1,1,.75f);
			renderScene(playerX, playerY);
		}else {
			buildingsLayer.setOpacity(1f);
			accesoriesLayer.setOpacity(1f);
			spriteBatch.setColor(1,1,1,1f);
			renderScene(playerX, playerY);
		}
	}

	private void renderScene(float playX, float playY){
		cam.position.set(playX, playY, 0);
		cam.update();
		tiledMapRenderer.setView(cam);
		tiledMapRenderer.render(layerRenderOrder);
		spriteBatch.setProjectionMatrix(cam.combined);
		spriteBatch.begin();
		spriteBatch.draw(currentFrame, (playX - 64), (playY - 64), 128, 128);
		spriteBatch.end();
	}

	private String checkCollision(float x, float y, TiledMapTileLayer layer) {
		Cell topRightCell = null;
		Cell topLeftCell = null;
		Cell bottomLeftCell = null;
		Cell bottomRightCell = null;
	
		topRightCell = layer.getCell((int) ((x+32) / 128), (int) (y / 128));
		topLeftCell = layer.getCell((int) ((x-32) / 128), (int) (y / 128));
		bottomLeftCell = layer.getCell((int) ((x-32) / 128), (int) (y / 128));
		bottomRightCell = layer.getCell((int) ((x+32) / 128), (int) (y / 128));

		return checkCellCollision(topRightCell, topLeftCell, bottomLeftCell, bottomRightCell);
	}

	private String checkCellCollision(Cell... cells){
		for(Cell cell : cells){
			if (cell != null && cell.getTile() != null) {
				if (cell.getTile().getProperties().containsKey("blocked")) {
					return "blocked";
				} else if (cell.getTile().getProperties().containsKey("opaque")) {
					return "opaque";
				}
			}
		}
		return "nothing";
	}

	private void playerMovement(){
		oldX = playerX;
		oldY = playerY;
		// Get player input
		if(Gdx.input.isKeyPressed(Keys.W) && Gdx.input.isKeyPressed(Keys.D)) {
			playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
			playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25)/1.5;
			playerIdleFrame = 1;
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.W) && Gdx.input.isKeyPressed(Keys.A)) {
			playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
			playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25)/1.5;
			playerIdleFrame = 1;
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.S) && Gdx.input.isKeyPressed(Keys.D)) {
			playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
			playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25)/1.5;
			playerIdleFrame = 0;
			currentFrame = downAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.S) && Gdx.input.isKeyPressed(Keys.A)) {
			playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
			playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25)/1.5;
			playerIdleFrame = 0;
			currentFrame = downAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.A)) {
			playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
			playerIdleFrame = 3;
			currentFrame = leftAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.D)) {
			playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
			playerIdleFrame = 2;
			currentFrame = rightAnimation.getKeyFrame(stateTime, true);
		}
		else if(Gdx.input.isKeyPressed(Keys.W)) {
			playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
			playerIdleFrame = 1;
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.S)) {
			playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
			playerIdleFrame = 0;
			currentFrame = downAnimation.getKeyFrame(stateTime, true);
		} else {
			currentFrame = idleAnimation.getKeyFrames()[(int)playerIdleFrame];
		}
	}

	private void setupAnimations(){
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

		downAnimation = new Animation<TextureRegion>(.2f, downFrames);
		upAnimation = new Animation<TextureRegion>(.2f, upFrames);
		leftAnimation = new Animation<TextureRegion>(.2f, leftFrames);
		rightAnimation = new Animation<TextureRegion>(.2f, rightFrames);
		idleAnimation = new Animation<TextureRegion>(.2f, idleFrames);

		spriteBatch = new SpriteBatch();
	}

	private void setupCamera(){
		cam = new OrthographicCamera(); 
		cam.setToOrtho(false);
		cam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0); 
		cam.update();
	}
	
	private void setupTileMap(){
        tiledMap = new TmxMapLoader().load("map.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 8);
		collisionLayer = (TiledMapTileLayer)tiledMap.getLayers().get("Collision");
		buildingsLayer = (TiledMapTileLayer)tiledMap.getLayers().get("Buildings");
		accesoriesLayer = (TiledMapTileLayer)tiledMap.getLayers().get("Accesories");
	}

	private void setupAudio(){
		//main menu music
		Music music = Gdx.audio.newMusic(Gdx.files.internal("hometownOST.mp3"));
		music.play();
		music.setVolume(0.1f);

		//Load step audio
		step = Gdx.audio.newMusic(Gdx.files.internal("shoesteplooped.mp3"));
	}
}