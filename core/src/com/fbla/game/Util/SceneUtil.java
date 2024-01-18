package com.fbla.game.Util;

import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;


public class SceneUtil {

  private String name;
  private float X;
  private float Y;
  private TiledMapRenderer mapRenderer;
  private MapLayer collisionLayer;
  private TiledMap tilemap;

  public SceneUtil(String name, float X, float Y, float startX, float startY, TiledMap tilemap) {
    this.name = name;
    if (X == -1 || Y == -1) {
      this.X = startX;
      this.Y = startY;
    } else {
      this.X = X;
      this.Y = Y;
    }
    this.tilemap = tilemap;
    this.mapRenderer = new OrthogonalTiledMapRenderer(tilemap, 8);
    this.collisionLayer = tilemap.getLayers().get("Collision");
  }

  public float getX() {
    return X;
  }

  public float getY() {
    return Y;
  }

  public void setPosition(float X, float Y) {
    this.X = X;
    this.Y = Y;
  }

  public TiledMapRenderer getMapRenderer() {
    return mapRenderer;
  }

  public MapLayer getCollisionLayer() {
    return collisionLayer;
  }

  public MapObjects getCollisionObjects() {
    return collisionLayer.getObjects();
  }

  public void setLayerOpacity(String layerName, float opacity) {
    if(tilemap.getLayers().get(layerName) != null) {
      ((TiledMapTileLayer) tilemap.getLayers().get(layerName)).setOpacity(opacity);
    } 
  }

  public TiledMapTileLayer getLayer(String layerName) {
    return (TiledMapTileLayer) tilemap.getLayers().get(layerName);
  }

}
