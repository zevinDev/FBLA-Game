package com.fbla.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class AnimationUtil {

    Animation<TextureRegion> downAnimation;
    Animation<TextureRegion> upAnimation;
    Animation<TextureRegion> leftAnimation;
    Animation<TextureRegion> rightAnimation;
    Animation<TextureRegion> idleAnimation;
    TextureRegion currentFrame;
    int currentIdleFrame = 0;
    float stateTime = 0f;

    public AnimationUtil(Animation<TextureRegion> downAnimation, Animation<TextureRegion> upAnimation, Animation<TextureRegion> leftAnimation, Animation<TextureRegion> rightAnimation, Animation<TextureRegion> idleAnimation) {
        this.downAnimation = downAnimation;
        this.upAnimation = upAnimation;
        this.leftAnimation = leftAnimation;
        this.rightAnimation = rightAnimation;
        this.idleAnimation = idleAnimation;            
    }

    public void updateAnimation(String animationName) {
        currentFrame = idleAnimation.getKeyFrames()[(int) currentIdleFrame];
    }

    public void updateAnimation(String animationName, int idleFrame) {
        if (animationName == "downAnimation") {
            currentFrame = downAnimation.getKeyFrame(stateTime, true);
        } else if (animationName == "upAnimation") {
            currentFrame = upAnimation.getKeyFrame(stateTime, true);
        } else if (animationName == "leftAnimation") {
            currentFrame = leftAnimation.getKeyFrame(stateTime, true);
        } else if (animationName == "rightAnimation") {
            currentFrame = rightAnimation.getKeyFrame(stateTime, true);
        }
        currentIdleFrame = idleFrame;
    }

    public void updateStateTime(){
        stateTime += Gdx.graphics.getDeltaTime();
    }

    public TextureRegion getCurrentFrame(){
        return currentFrame;
    }
}
