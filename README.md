### Info
This simple project is meant to be a series of example showing how to use
Phosphor. Bear in mind that I created this in the process of learning
how to use Phosphor myself, so there are bound to be mistakes/inefficiencies in the examples.
These are solely my responsibility and should not be viewed as a reflection of anyone elses work.


### Running
In order to run the examples, you must download the Phosphor project and build it.
See [Phosphor](https://github.com/Programming-Systems-Lab/phosphor) for more information. 
Running `mvn install` as done below will create the 
Phosphor jar and instrument various JREs which the examples rely upon.

```
cd /path/to/Phosphor/project
mvn install # put jar in maven local repository
cd /path/to/phophor-examples
mvn package # creates jar with examples)
./run_examples.sh <folder-instrumented-jre-and-phosphor-jar> # see ./run_examples.sh -h
```

(I'm sure there is a nicer way of doing this with maven, but I'm not as familiar with it,
so feel free to modify and put in a PR if you are)

If you'd like to read along with the notes I made for this project, run
`mvn latex:latex` which creates a pdf report at target/site/notes.pdf