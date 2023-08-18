package com.fbla.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;

public class FBLAgame extends ApplicationAdapter {
  private Texture playerImage;

  private SpriteBatch batch;
  private OrthographicCamera camera;
  private Rectangle player;

  private Stage stage;

  @Override
  public void create() {

   /*
    stage = new Stage();
    Gdx.input.setInputProcessor(stage);

    Skin skin = new Skin(Gdx.files.internal("plain-james-ui.json"));

    Label nameLabel = new Label("Name:", skin);
    TextField nameText = new TextField("", skin);
    Label addressLabel = new Label("Address:", skin);
    TextField addressText = new TextField("", skin);

    Table table = new Table();
    table.add(nameLabel);
    table.add(nameText).width(100);
    table.row();
    table.add(addressLabel);
    table.add(addressText).width(100);
    stage.addActor(table);
    */

    // load the images for the droplet and the player, 64x64 pixels each
    playerImage = new Texture(Gdx.files.internal("character.png"));

    // create the camera and the SpriteBatch
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);
    batch = new SpriteBatch();

    // create a Rectangle to logically represent the player
    player = new Rectangle();
    player.x = 800 / 2 - 64 / 2; // center the player horizontally
    player.y = 20; // bottom left corner of the player is 20 pixels above the bottom screen edge
    player.width = 64;
    player.height = 64;

  }

  @Override
  public void render() {
   /* 
   Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	stage.act(Gdx.graphics.getDeltaTime());
	stage.draw();
   */
    // clear the screen with a dark blue color. The
    // arguments to clear are the red, green
    // blue and alpha component in the range [0,1]
    // of the color to be used to clear the screen.
    ScreenUtils.clear(0, 255, 0, 1);
    // tell the camera to update its matrices.
    camera.position.set(player.x, player.y, 0);
    camera.update();

    // tell the SpriteBatch to render in the
    // coordinate system specified by the camera.
    batch.setProjectionMatrix(camera.combined);

    // begin a new batch and draw the player and
    // all drops
    batch.begin();
    batch.draw(playerImage, player.x, player.y);
    batch.end();

    // process user input
    if (Gdx.input.isTouched()) {
      Vector3 touchPos = new Vector3();
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      player.x = touchPos.x - 64 / 2;
    }
    if (Gdx.input.isKeyPressed(Keys.LEFT)) player.x -= 200 * Gdx.graphics.getDeltaTime();
    if (Gdx.input.isKeyPressed(Keys.RIGHT)) player.x += 200 * Gdx.graphics.getDeltaTime();
    if (Gdx.input.isKeyPressed(Keys.UP)) player.y += 200 * Gdx.graphics.getDeltaTime();
    if (Gdx.input.isKeyPressed(Keys.DOWN)) player.y -= 200 * Gdx.graphics.getDeltaTime();
    // make sure the player stays within the screen bounds
    if (player.x < 0) player.x = 0;
    if (player.x > 800 - 64) player.x = 800 - 64;

    //This is how to log
    //Gdx.app.log("Player Pos", "x: " + player.x + " y: " + player.y);
    
  }

  @Override
  public void dispose() {
    playerImage.dispose();
    batch.dispose();
    //stage.dispose();
  }
}