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

public class AIEntity {
    
    private StateMachine<AIEntity, AIState> stateMachine;
    private Vector2 position;
    private Vector2 homePosition;
    private Vector2 targetPosition;
    private Animation<TextureRegion> animation;
    private float animationTime;
    private float deltaTime;
    private float speed = 200;
    private Rectangle boundingBox;  // Bounding rectangle for collision detection

    public AIEntity(Vector2 homePosition, Texture spriteSheet) {
        this.homePosition = homePosition;
        this.position = new Vector2(homePosition);
        this.stateMachine = new DefaultStateMachine<>(this, AIState.WANDER);

        // Split the sprite sheet into frames
        int frameWidth = spriteSheet.getWidth() / 3;
        int frameHeight = spriteSheet.getHeight() / 4;
        TextureRegion[][] frames = TextureRegion.split(spriteSheet, frameWidth, frameHeight);

        // Create the animation
        animation = new Animation<>(.1f, frames[0]);

        animationTime = 0;

        // Initialize the bounding box
        boundingBox = new Rectangle(position.x, position.y, 14, 8);
    }

    public void update(float deltaTime) {
        stateMachine.update();
        this.deltaTime = deltaTime;
        // Update the animation time
        animationTime += deltaTime;

        // Update the bounding box position
        boundingBox.setPosition(position.x/8, position.y/8);
    }

    public enum AIState implements State<AIEntity> {
        WANDER {
            @Override
            public void update(AIEntity entity) {
                if (entity.targetPosition == null || entity.position.dst(entity.targetPosition) < 1) {
                    // If we've reached the target position or don't have one, choose a new target position
                    // Choose a random direction (up, down, left, or right)
                    int direction = (int)(Math.random() * 4);
                    switch (direction) {
                        case 0:  // Up
                            entity.targetPosition = new Vector2(entity.position.x, entity.position.y + 300);
                            break;
                        case 1:  // Down
                            entity.targetPosition = new Vector2(entity.position.x, entity.position.y - 300);
                            break;
                        case 2:  // Left
                            entity.targetPosition = new Vector2(entity.position.x - 300, entity.position.y);
                            break;
                        case 3:  // Right
                            entity.targetPosition = new Vector2(entity.position.x + 300, entity.position.y);
                            break;
                    }
                }
        
                // Calculate a direction vector towards the target position
                Vector2 direction = new Vector2(entity.targetPosition).sub(entity.position).nor();
        
                // Move in the chosen direction
                entity.position.x += entity.speed * entity.deltaTime * direction.x;
                entity.position.y += entity.speed * entity.deltaTime * direction.y;
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
        return animation.getKeyFrame(animationTime);
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
    }

    public void wander(){
        stateMachine.changeState(AIState.WANDER);
    }

    public StateMachine<AIEntity, AIState> getStateMachine() {
        return stateMachine;
    }
}