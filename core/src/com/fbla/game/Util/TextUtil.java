package com.fbla.game.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class TextUtil {
    TextureRegion currentFrame;
    float stateTime = 0f;
    boolean isAnimating = false;
    Animation < TextureRegion > paperAnimation;

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

    }

    public void renderText(SpriteBatch spriteBatch, float playerX, float playerY){
        if(isAnimating){
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, playerX - 576, playerY - 640, 1152, 1280);
        spriteBatch.end();
        }
    }

    public void isAnimating(boolean isAnimating){
        this.isAnimating = isAnimating;
    }

    public void updateStateTime(){
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = paperAnimation.getKeyFrame(stateTime, false);
        if(!isAnimating){
            stateTime = 0f;
        }
    }

}
