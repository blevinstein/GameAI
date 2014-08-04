import java.util.Arrays;

// simple class for tracking ticks per second
class Ticker {
  private int index;
  private int N;
  private long ticks[];
  public Ticker(int n) {
    N = n;
    ticks = new long[N];
    index = 0;
  }

  // registers a tick and gets tick speed
  public long tick() {
    int newIndex = (index + 1) % N; // circular increment
    long newTime = System.nanoTime();
    long duration = newTime - ticks[newIndex];
    if (duration == 0) duration = 1; // prevent divide-by-zero exceptions
    long frameRate = N * 1000000000L / duration;
    ticks[newIndex] = newTime;
    index = newIndex;
    return frameRate;
  }
}
