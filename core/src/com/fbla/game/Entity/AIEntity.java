package com.fbla.game.Entity;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;
import com.fbla.game.Util.AnimationUtil;

public class AIEntity {
    
    private StateMachine<AIEntity, AIState> stateMachine;
    private Vector2 position;
    private Vector2 homePosition;
    private Vector2 targetPosition;
    private float deltaTime;
    private float speed = 200;
    private Rectangle boundingBox;  // Bounding rectangle for collision detection
    private AnimationUtil animationUtil;
    private String animationDirection = "downAnimation";
    private int animationIdleFrame = 0;
    

    public AIEntity(Vector2 homePosition, Texture spriteSheet) {
        this.homePosition = homePosition;
        this.position = new Vector2(homePosition);
        this.stateMachine = new DefaultStateMachine<>(this, AIState.WANDER);

        // Split the sprite sheet into frames
        int frameWidth = spriteSheet.getWidth() / 3;
        int frameHeight = spriteSheet.getHeight() / 4;
        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

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
    
        Animation < TextureRegion > downAnimation = new Animation < TextureRegion > (.2f, downFrames);
        Animation < TextureRegion > upAnimation = new Animation < TextureRegion > (.2f, upFrames);
        Animation < TextureRegion > leftAnimation = new Animation < TextureRegion > (.2f, leftFrames);
        Animation < TextureRegion > rightAnimation = new Animation < TextureRegion > (.2f, rightFrames);
        Animation < TextureRegion > idleAnimation = new Animation < TextureRegion > (.2f, idleFrames);

        animationUtil = new AnimationUtil(downAnimation, upAnimation, leftAnimation, rightAnimation, idleAnimation);
        animationUtil.updateAnimation("idle", 0);
        animationUtil.updateAnimation("idle");
        // Initialize the bounding box
        boundingBox = new Rectangle(position.x, position.y, 14, 8);
    }

    public void update(float deltaTime) {
        animationUtil.updateStateTime(deltaTime);
        stateMachine.update();
        this.deltaTime = deltaTime;

        // Update the bounding box position
        boundingBox.setPosition(position.x/8, position.y/8);
    }

    public enum AIState implements State<AIEntity> {
        WANDER {
            @Override
            public void update(AIEntity entity) {
                if (entity.targetPosition == null || entity.position.dst(entity.targetPosition) < 1) {
                    // If we've reached the target position or don't have one, choose a new target positionw
                    // Choose a random direction (up, down, left, or right)
                    int direction = (int)(Math.random() * 4);
                    switch (direction) {
                        case 0:  // Up
                            entity.targetPosition = new Vector2(entity.position.x, entity.position.y + 300);
                            entity.animationDirection = "upAnimation";
                            entity.animationIdleFrame = 1;
                            break;
                        case 1:  // Down
                            entity.targetPosition = new Vector2(entity.position.x, entity.position.y - 300);
                            entity.animationDirection = "downAnimation";
                            entity.animationIdleFrame = 0;
                            break;
                        case 2:  // Left
                            entity.targetPosition = new Vector2(entity.position.x - 300, entity.position.y);
                            entity.animationDirection = "leftAnimation";
                            entity.animationIdleFrame = 3;
                            break;
                        case 3:  // Right
                            entity.targetPosition = new Vector2(entity.position.x + 300, entity.position.y);
                            entity.animationDirection = "rightAnimation";
                            entity.animationIdleFrame = 2;
                            break;
                    }
                }
        
                // Calculate a direction vector towards the target position
                Vector2 direction = new Vector2(entity.targetPosition).sub(entity.position).nor();
        
                // Move in the chosen direction
                entity.position.x += entity.speed * entity.deltaTime * direction.x;
                entity.position.y += entity.speed * entity.deltaTime * direction.y;
                entity.animationUtil.updateAnimation(entity.animationDirection, entity.animationIdleFrame);
                entity.animationUtil.updateStateTime();
            }
    
            @Override
            public boolean onMessage(AIEntity entity, Telegram telegram) {
                // Handle messages here if needed
                return false;
            }

            @Override
            public void enter(AIEntity entity) {
                // Implement enter logic here
            } 
            
            @Override
            public void exit(AIEntity entity) {
                // Implement exit logic here
            }
        },
        IDLE {
            @Override
            public void update(AIEntity entity) {
                // Implement idle logic here
            }

            @Override
            public boolean onMessage(AIEntity entity, Telegram telegram) {
                // Handle messages here if needed
                return false;
            }

            @Override
            public void enter(AIEntity entity) {
                // Implement enter logic here
            } 
            
            @Override
            public void exit(AIEntity entity) {
                // Implement exit logic here
            }
        },
        RETURN_HOME {
            @Override
            public void update(AIEntity entity) {
                // Implement return home logic here
                Vector2 directionToHome = new Vector2(entity.getHomePosition()).sub(entity.getPosition()).nor();
                entity.getPosition().add(directionToHome);
            }

            @Override
            public boolean onMessage(AIEntity entity, Telegram telegram) {
                // Handle messages here if needed
                return false;
            }

            @Override
            public void enter(AIEntity entity) {
                // Implement enter logic here
            } 
            
            @Override
            public void exit(AIEntity entity) {
                // Implement exit logic here
            }
        }
    }

    public TextureRegion getCurrentFrame() {
        // Get the current frame based on the animation time
        return animationUtil.getCurrentFrame();
    }

    public Vector2 getPosition() {
        return position;
    }


    public Vector2 getHomePosition() {
        return homePosition;
    }

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public void playerCollided(){
        stateMachine.changeState(AIState.IDLE);
    }

    public void handleCollision() {
        // Reverse the direction of the AI's movement
        targetPosition = new Vector2(position.x - (targetPosition.x - position.x), position.y - (targetPosition.y - position.y));
        
        // Update the animation direction based on the new movement direction
        if (targetPosition.x > position.x) {
            animationDirection = "rightAnimation";
        } else if (targetPosition.x < position.x) {
            animationDirection = "leftAnimation";
        } else if (targetPosition.y > position.y) {
            animationDirection = "upAnimation";
        } else if (targetPosition.y < position.y) {
            animationDirection = "downAnimation";
        }
    }

    public void wander(){
        stateMachine.changeState(AIState.WANDER);
    }

    public StateMachine<AIEntity, AIState> getStateMachine() {
        return stateMachine;
    }
}