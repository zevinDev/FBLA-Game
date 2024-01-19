package com.fbla.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.fbla.game.FBLA;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fbla.game.Util.GifDecoderUtil;
import com.badlogic.gdx.graphics.Texture;


public class TitleScreen extends ScreenAdapter {

    FBLA game;
    Animation<TextureRegion> animation;
    float elapsed;
    TextureRegion[][] startSheet;
    TextureRegion currentFrame;
    Texture settingsButton;
    Texture leaderboardButton;

    public TitleScreen(FBLA game) {
        this.game = game;
    }

    @Override
    public void show(){
        animation = GifDecoderUtil.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("spritesheets/titlescreen.gif").read());
        startSheet = TextureRegion.split(new Texture(Gdx.files.internal("spritesheets/start.png")), 720, 208);
        settingsButton = new Texture(Gdx.files.internal("spritesheets/settings.png"));
        leaderboardButton = new Texture(Gdx.files.internal("spritesheets/leaderboard.png"));
    }

    @Override
    public void render(float delta) {
        elapsed += Gdx.graphics.getDeltaTime();
        
        Gdx.gl.glClearColor(38/255f, 43/255f, 68/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.batch.draw(animation.getKeyFrame(elapsed), 64, 704);
        
        // Get the mouse's x and y coordinates
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
    
        // Check if the mouse is over the start button
        if (mouseX >= 396 && mouseX <= 729 && mouseY >= 726 && mouseY <= 830) {
            // If the mouse is over the start button, change the current frame
            currentFrame = startSheet[1][0]; // Change this to the frame you want
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                game.setScreen(new GameScreen(game));
            }
        } else {
            currentFrame = startSheet[0][0]; // Change this to the default frame
        }
        game.batch.draw(leaderboardButton, 64, 64);
        game.batch.draw(settingsButton, 1152 - (64 + 128), 64);
        game.batch.draw(currentFrame, 396, 450, 360, 104);
        game.batch.end();
    }

    @Override
    public void hide(){
    }
}