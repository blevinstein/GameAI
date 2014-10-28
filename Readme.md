Some experiments with machine learning to play games. Currently just
  Tic-Tac-Toe.

To play with this code, you just need Java and Ant, then run one of:

ant t3 (tic-tac-toe program) (**does not build**)
ant house (neural network inspection program)
ant boids (physics-based 2D moving "boid" simulation) (**blocked by jbox2d frustrations**)
ant xade (physics-less game simulation)

TODO
====
- implement boids
- neural net improvement via event listeners?
  e.g. FeedbackListener, TrainingListener
- move code into separate folders, setup packages
- use multiple fonts, offsets, rotations for the image classifier
- fix neural net architecture mutation to look cleaner
- write some goddamn tests before everything breaks.. JUnit or something?

OBSERVATIONS
------------
These are some of the observations I've made while watching the neural network,
  and may lead to improvements in the algorithm.
- Backpropagation sometimes leads to "dead neurons", with incoming and/or
  outgoing weights close to zero. Maybe solved by normalization.
- Learning rates need careful tuning. Perhaps I should implement local learning
  rates, or allow learning rates to mutate.
- Weights near zero are like a trap: they decrease their effect, and thus their
  exposure to modification during backpropagation.  Solved by normalizing each
  matrix, so that a matrix cannot become all zeroes, thus disconnecting two
  layers from each other.

ASPIRATIONAL
------------
- experiment with neural net sizes & architectures, or allow mutation
- implement simple image recognition
- implement other TBS games
- implement realtime games

IDEAS
-----
- multi-neural-net learner, has multiple neural nets to represent different
  "questions" (with different network sizes), more complicated games

LIBRARIES USED
--------------
- Apache Commons (Lang, IO, and Math3)
  http://commons.apache.org/
- Gson
  https://code.google.com/p/google-gson/
