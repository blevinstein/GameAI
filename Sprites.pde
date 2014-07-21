//sprites = loadSprites("chess_sprites.png", 0, 0, 64, 64);

ArrayList<PImage> loadSprites(String fname, int x, int y, int w, int h) {
  PImage img = loadImage(fname);
  ArrayList<PImage> sprites = new ArrayList<PImage>();
  for(int iy = y; iy < img.width; iy += h) {
    for(int ix = x; ix < img.height; ix += w) {
      PImage sprite = new PImage(w, h, ARGB);
      sprite.copy(img, ix, iy, w, h, 0, 0, w, h);
      sprites.add(sprite);
    }
  }
  return sprites;
}
