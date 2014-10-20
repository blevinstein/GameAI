package com.blevinstein.xade;

public interface State {
  // drawable can return null
  Drawable drawable();
  State update(double t);
}
