package com.fbla.game.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;



public class TextUtil {
    TextureRegion currentFrame;
    float stateTime = 0f;
    boolean isAnimating = false;
    Animation < TextureRegion > paperAnimation;
    BitmapFont font;

    public void loadTextBox(){
    Texture textBoxTexture = new Texture(Gdx.files.internal("spritesheets/textbox.png"));
    TextureRegion[][] tmp = TextureRegion.split(textBoxTexture, 96, 128);
    TextureRegion[] paperFrames = new TextureRegion[16];
    paperFrames[0] = tmp[0][0];
    paperFrames[1] = tmp[0][1];
    paperFrames[2] = tmp[0][2];
    paperFrames[3] = tmp[0][3];
    paperFrames[4] = tmp[0][4];
    paperFrames[5] = tmp[1][0];
    paperFrames[6] = tmp[1][1];
    paperFrames[7] = tmp[1][2];
    paperFrames[8] = tmp[1][3];
    paperFrames[9] = tmp[1][4];
    paperFrames[10] = tmp[2][0];
    paperFrames[11] = tmp[2][1];
    paperFrames[12] = tmp[2][2];
    paperFrames[13] = tmp[2][3];
    paperFrames[14] = tmp[2][4];
    paperFrames[15] = tmp[3][0];

    paperAnimation = new Animation < TextureRegion > (.03f, paperFrames);

    font = new BitmapFont(Gdx.files.internal("fonts/dogica.fnt"));
    font.setColor(0, 0, 0, 1);

    }

    public void renderText(SpriteBatch spriteBatch, float playerX, float playerY){
        stateTime += Gdx.graphics.getDeltaTime();
        if(isAnimating){
        spriteBatch.begin();
        currentFrame = paperAnimation.getKeyFrame(stateTime, false);
        spriteBatch.draw(currentFrame, playerX - 576, playerY - 640, 1152, 1280);
        if(currentFrame == paperAnimation.getKeyFrame(15, false)){
        font.draw(spriteBatch, "Hello, World!", playerX, playerY - 400);
        }
        spriteBatch.end();
        } else {
            stateTime = 0f;
        }
    }

    public void isAnimating(boolean isAnimating){
        this.isAnimating = isAnimating;
    }

}