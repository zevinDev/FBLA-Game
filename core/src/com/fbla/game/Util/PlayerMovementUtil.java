package com.fbla.game.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class PlayerMovementUtil {

    private float playerX;
    private float playerY;
    private float playerSpeed;
    private AnimationUtil animationUtil;

    public PlayerMovementUtil(float playerX, float playerY, float playerSpeed, AnimationUtil animationUtil){
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerSpeed = playerSpeed;
        this.animationUtil = animationUtil;
    }

    public void updateMovement(){
        if (Gdx.input.isKeyPressed(Keys.W) && Gdx.input.isKeyPressed(Keys.D)) {
            this.playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
            this.playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
            animationUtil.updateAnimation("upAnimation", 1);
          } else if (Gdx.input.isKeyPressed(Keys.W) && Gdx.input.isKeyPressed(Keys.A)) {
            this.playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
            this.playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
            animationUtil.updateAnimation("upAnimation", 1);
          } else if (Gdx.input.isKeyPressed(Keys.S) && Gdx.input.isKeyPressed(Keys.D)) {
            this.playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
            this.playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
            animationUtil.updateAnimation("downAnimation", 0);
          } else if (Gdx.input.isKeyPressed(Keys.S) && Gdx.input.isKeyPressed(Keys.A)) {
            this.playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
            this.playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25) / 1.5;
            animationUtil.updateAnimation("downAnimation", 0);
          } else if (Gdx.input.isKeyPressed(Keys.A)) {
            this.playerX -= Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
            animationUtil.updateAnimation("leftAnimation", 3);
          } else if (Gdx.input.isKeyPressed(Keys.D)) {
            this.playerX += Gdx.graphics.getDeltaTime() * (playerSpeed + 25);
            animationUtil.updateAnimation("rightAnimation", 2);
          } else if (Gdx.input.isKeyPressed(Keys.W)) {
            this.playerY += Gdx.graphics.getDeltaTime() * playerSpeed;
            animationUtil.updateAnimation("upAnimation", 1);
          } else if (Gdx.input.isKeyPressed(Keys.S)) {
            this.playerY -= Gdx.graphics.getDeltaTime() * playerSpeed;
            animationUtil.updateAnimation("downAnimation", 0);
          } else {
            animationUtil.updateAnimation("idleAnimation");
          }
    }

    public void updatePosition(float playerX, float playerY){
        this.playerX = playerX;
        this.playerY = playerY;
    }

    public float getPlayerX(){
        return playerX;
    }

    public float getPlayerY(){
        return playerY;
    }
    
}
