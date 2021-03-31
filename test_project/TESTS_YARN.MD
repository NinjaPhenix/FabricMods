# How to write tests for Mojang's Game Test Framework
Firstly let me start this with a disclaimer,
I've only just started experimenting with this new code, some information
may be incorrect and this is not an exhaustive tutorial.

## Test Classes
Creating a test class is easy as it's just the same as any other java class.

The name of the test class will in conjunction with the test method to create the test name.
```java
public class SampleTest {
    
}
```
To register your test class simply call `TestFunctions.method_36068(SampleTest.class);`, replace SampleTest with your class name.

Until Fabric API has a functional test API you will need to set `SharedConstants.isDevelopment` to `true` before running any tests.

Additionally, you will need to register the `TestCommand`, you may be able to through fabric's command api, if not then a simple mixin will suffice.

## Test Methods
Creating a test method is also easy, it just needs to be a `public void` method which sole parameter is a `StartupParameter`

```java
@class_6302
public void sandFalls(StartupParameter arg) {

}
```

## Test Structures
For this part I recommend you create a stub test method beforehand.

Once in game run the command `/test create [Test Class Name in lowercase].[Test Method Name in lowercase] WIDTH HEIGHT DEPTH`.

For the example gave in previous sections I would run:

`/test create sampletests.sandfallstest 3 10 3`
![Image of Empty Sand Falls Structure](TEST_STRUCTURE.png)

Next save the structure, use the structure (purple) block and click the `SAVE` button on the right of the screen.
![Image of Structure Screen](TEST_STRUCTURE_SAVE.png)
Now you can export your test by running `/test exportthis`.

To run the test, simply click the stone button the command block.

If you are in a different world you can import and run the test by running:

`/test import [Test Class Name in lowercase].[Test Method Name in lowercase]`

`/test run [Test Class Name in lowercase].[Test Method Name in lowercase]`
## Implementing a test
For this section I will continue with my sand falls example. Please see the following structure as it will be important for the test implementation.
![Image of Sand Falls Structure](SAND_FALLS_STRUCTURE.png)

To make testing easy minecraft makes all blockpos used whilst testing relative to the structure block.

To easily find the position of the block, look at it and the run `/test pos`.
![Image of fallen sand position](FALLEN_SAND_TEST_POS.png)

For this test I would like to remove the stone block stopping the sand from falling, 
wait for the sand to fall, and then finally check if the sand exists in the fallen position.

```java
@class_6302
public void sandFalls(StartupParameter arg) {
    arg.method_36044(new BlockPos(1, 9, 1)); // Break stone block
    arg.method_36003(100, () -> { // After 5 seconds
        arg.method_35972(Blocks.SAND, new BlockPos(1, 2, 1)); // Pass if a Sand block is at 1,2,1 ( which is where it should fall )
        // Should probably also check that the block at 1, 10, 1 is an air block.
    });
}
```