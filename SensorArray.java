import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;

// Represents a set of inputs. Can be the same type or different.
//
// Usage:
//
// SensorArray array = new SensorArray();
// array.add(new ConcreteSensor( ... ));
// array.add(new ConcreteSensor( ... ));
//
// Then just use it like any other sensor.

public class SensorArray implements Sensor {
  private List<Sensor> _sensors;

  public SensorArray() { this(new ArrayList<Sensor>()); }
  public SensorArray(List<Sensor> sensors) { _sensors = sensors; }

  public void addSensor(Sensor s) {
    _sensors.add(s);
  }

  public double[] toDoubles() {
    DoubleStream stream = _sensors.stream().flatMapToDouble(s ->
        Arrays.stream(s.toDoubles()));
    return stream.toArray();
  }
}
