package com.fbla.game.Util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;

public class TeleporterUtil {
    SceneUtil currentScene;
    SceneUtil nextScene;
    Rectangle teleporter;
    public TeleporterUtil(SceneUtil currentScene, SceneUtil nextScene, float x, float y){
        this.currentScene = currentScene;
        this.nextScene = nextScene;
        this.teleporter = new Rectangle(x/8, y/8, 16, 16);
    }

    public boolean playerTouchedTeleporter(Rectangle player){
        if(Intersector.overlaps(player, teleporter)){
            return true;
        }
        return false;
    }

    public SceneUtil getNextScene(){
        return nextScene;
    }
}
