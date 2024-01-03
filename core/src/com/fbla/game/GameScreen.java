package com.fbla.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapGroupLayer;


public class GameScreen extends ScreenAdapter {
    FBLA game;


	// Objects used
	Animation<TextureRegion> downAnimation; // Must declare frame type (TextureRegion)
	Animation<TextureRegion> idleAnimation; // Must declare frame type (TextureRegion)
	Animation<TextureRegion> upAnimation; // Must declare frame type (TextureRegion)
	Animation<TextureRegion> leftAnimation; // Must declare frame type (TextureRegion)
	Animation<TextureRegion> rightAnimation; // Must declare frame type (TextureRegion)
	Texture spriteSheet;
	SpriteBatch spriteBatch;
	TextureRegion currentFrame;

	// A variable for tracking elapsed time for the animation
	float stateTime;

	// Character X and Y coords
	float playerX = 100;
	float playerY = 100;
	float playerSpeed = 300;
	boolean playerIdle = true;
	float playerIdleFrame = 0;

	float lerp = 0.1f;

	TiledMapRenderer tiledMapRenderer;
    TiledMap tiledMap;
    OrthographicCamera cam;


    public GameScreen(FBLA game) {
        this.game = game;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }


    @Override
    public void show() {
        // Load the sprite sheet as a Texture
		spriteSheet = new Texture(Gdx.files.internal("zevin.png"));

		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 16, 16);

		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
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


		// Initialize the Animation with the frame interval and array of frames
		downAnimation = new Animation<TextureRegion>(.25f, downFrames);
		upAnimation = new Animation<TextureRegion>(.25f, upFrames);
		leftAnimation = new Animation<TextureRegion>(.25f, leftFrames);
		rightAnimation = new Animation<TextureRegion>(.25f, rightFrames);
		idleAnimation = new Animation<TextureRegion>(.25f, idleFrames);

		// Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		cam = new OrthographicCamera(); 
		cam.setToOrtho(false);
		cam.position.set(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0); 
		cam.update(); // Updates the camera
        tiledMap = new TmxMapLoader().load("new.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 8);
		spriteBatch = new SpriteBatch();
		stateTime = 0f;
    }

	// This method runs for every frame
	// If code is innefiecent game will lag (for loop > if statement)
    @Override
    public void render(float delta) {

		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time
		// Get player input
		playerIdle = true;
		if(Gdx.input.isKeyPressed(Keys.A)) {
			playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
			playerIdle = false;
			playerIdleFrame = 3;
			currentFrame = leftAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.D)) {
			playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
			playerIdle = false;
			playerIdleFrame = 2;
			currentFrame = rightAnimation.getKeyFrame(stateTime, true);
		}
		else if(Gdx.input.isKeyPressed(Keys.W)) {
			playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
			playerIdle = false;
			playerIdleFrame = 1;
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
		}
	 	else if(Gdx.input.isKeyPressed(Keys.S)) {
			playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
			playerIdle = false;
			playerIdleFrame = 0;
			currentFrame = downAnimation.getKeyFrame(stateTime, true);
		}
  
		if(playerIdle){
			currentFrame = idleAnimation.getKeyFrames()[(int)playerIdleFrame];
		}



		cam.position.set(playerX, playerY, 0); // x and y could be changed by Keyboard input for example
		cam.update(); // Don't forget me ;)
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render(); // Clear screen
		spriteBatch.setProjectionMatrix(cam.combined); // Tells the spritebatch to render according to your camera
		spriteBatch.begin();
		spriteBatch.draw(currentFrame, (playerX - 64), (playerY - 64), 128, 128); // Draw player
		spriteBatch.end();


    }

    @Override
    public void hide() {
        spriteBatch.dispose();
		spriteSheet.dispose();
    }
}