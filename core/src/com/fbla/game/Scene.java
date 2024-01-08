package com.fbla.game;

import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.MapLayer;

public class Scene {

  private String name;
  private float X;
  private float Y;
  private TiledMapRenderer mapRenderer;
  private MapLayer collisionLayer;

  public Scene(String name, float X, float Y, float startX, float startY, TiledMapRenderer mapRenderer, MapLayer collisionLayer) {
    this.name = name;
    if (X == -1 || Y == -1) {
      this.X = startX;
      this.Y = startY;
    } else {
      this.X = X;
      this.Y = Y;
    }
    this.mapRenderer = mapRenderer;
    this.collisionLayer = collisionLayer;
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
}