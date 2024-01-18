package com.fbla.game.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.ArrayList;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;

public class CollisionUtil {
    public static boolean checkCollision(MapObjects objects, Rectangle player) {
        for (int i = 0; i < objects.getCount(); i++)
        {
            MapObject mapObject = objects.get(i);
    
            if (mapObject instanceof RectangleMapObject)
            {
                RectangleMapObject rectangleObject = (RectangleMapObject) mapObject;
                Rectangle rectangle = rectangleObject.getRectangle();
                if (Intersector.overlaps(rectangle, player)) {
                  return true;
               }
                
            }
            else if (mapObject instanceof EllipseMapObject)
            {
                EllipseMapObject circleMapObject = (EllipseMapObject) mapObject;
                Ellipse ellipse = circleMapObject.getEllipse();
    
    
                if (ellipse.width != ellipse.height)
                    throw new IllegalArgumentException("Only circles are allowed.");
    
                Circle circle = new Circle(ellipse.x + ellipse.width / 2, ellipse.y + ellipse.height / 2, ellipse.width / 2);
                if(Intersector.overlaps(circle, player)) {
                  return true;
                }
            }
        }
        return false;
    }
    public static boolean checkOpacity(TiledMapTileLayer layer, float x, float y){
        Cell topRightCell = null;
        Cell topLeftCell = null;
        Cell bottomLeftCell = null;
        Cell bottomRightCell = null;

        topRightCell = layer.getCell((int)((x + 32) / 128), (int)(y / 128));
        topLeftCell = layer.getCell((int)((x - 32) / 128), (int)(y / 128));
        bottomLeftCell = layer.getCell((int)((x - 32) / 128), (int)((y - 56) / 128));
        bottomRightCell = layer.getCell((int)((x + 32) / 128), (int)((y - 56) / 128));

    return checkCellCollision(topRightCell, topLeftCell, bottomLeftCell, bottomRightCell);
    }

    private static boolean checkCellCollision(Cell...cells) {
        ArrayList < String > collisionOutput = new ArrayList < String > ();
        for (Cell cell: cells) {
          if (cell != null && cell.getTile() != null) {
            if (cell.getTile().getProperties().containsKey("opaque")) {
              return true;
            }
          }
        }
        if (collisionOutput.size() > 0) {
          if (collisionOutput.contains("opaque")) {
            return true;
          }
        }
        return false;
      }
}
