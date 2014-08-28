import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

// Very similar to the SensorArray, but for output.
//
// HACK: stores a list of actuators and their bit widths.
// TODO: build a better way of dealing with bit widths
// NOTE: adding a second abstract method to Actuator (e.g. length()) will make
//   it not a functional interface

public class ActuatorArray implements Actuator {
  private List<Pair<Actuator, Integer>> _actuators;

  public ActuatorArray() { this(new ArrayList<>()); }
  public ActuatorArray(List<Pair<Actuator, Integer>> actuators) {
    _actuators = actuators;
  }

  public void addActuator(Actuator a, int bits) {
    _actuators.add(new ImmutablePair<>(a, bits));
  }

  public void send(double doubles[]) {
    int i = 0;
    for (Pair<Actuator, Integer> p : _actuators) {
      Actuator a = p.getLeft();
      int width = p.getRight();

      // copy the selected inputs
      a.send(Arrays.copyOfRange(doubles, i, width));
      // update the current position
      i += width;
    }
  }
}
