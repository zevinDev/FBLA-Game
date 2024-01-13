package com.fbla.game;

import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Scene {

  private String name;
  private float X;
  private float Y;
  private TiledMapRenderer mapRenderer;
  private TiledMapTileLayer[] layers;

  public Scene(String name, float X, float Y, float startX, float startY, TiledMapRenderer mapRenderer, TiledMapTileLayer[] layers) {
    this.name = name;
    if (X == -1 || Y == -1) {
      this.X = startX;
      this.Y = startY;
    } else {
      this.X = X;
      this.Y = Y;
    }
    this.mapRenderer = mapRenderer;
    this.layers = layers;
  }

  public float getX() {
    return X;
  }

  public float getY() {
    return Y;
  }

  public void setX(float X) {
    this.X = X;
  }

  public void setY(float Y) {
    this.Y = Y;
  }

  public TiledMapRenderer getMapRenderer() {
    return mapRenderer;
  }

  public TiledMapTileLayer[] getLayers() {
    return layers;
  }
}