Some experiments with machine learning to play games. Currently just
  Tic-Tac-Toe.

To play with this code, you just need Java and Ant, then run one of:

ant t3 (tic-tac-toe program)
ant diag (neural network diagnostic program)

TODO
====
- rename NetDiag => House
- give NetDiag separate tabs ("labs"), add a tab for population inspection
- play with pruning and other means of intervention in network
- abstract out input (double[]) and output from neural networks
- fix uglines of double[] <=> boolean[], dtob, btod, etc
- implement mutation in neural net architecture
- write some goddamn tests before everything breaks.. JUnit or something?
- at least make tests for neural net propagate etc

OBSERVATIONS
------------
These are some of the observations I've made while watching the neural network,
  and may lead to improvements in the algorithm.
- Backpropagation sometimes leads to "dead neurons", with incoming and/or
  outgoing weights close to zero.
- Learning rates need careful tuning. Perhaps I should implement local learning
  rates, or allow learning rates to mutate.
- Weights near zero are like a trap: they decrease their effect, and thus their
  exposure to modification during backpropagation. Better results when removing
  initial weights farther from zero. Still see problems when a whole layer of
  synapses (i.e. one matrix) contains small values.

ASPIRATIONAL
------------
- experiment with neural net sizes & architectures, or allow mutation
- implement other TBS games
- implement realtime game

IDEAS
-----
- multi-neural-net learner, has multiple neural nets to represent different
  "questions" (with different network sizes), more complicated games
