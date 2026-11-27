# TeamCode unit tests

Local JVM unit tests with **JUnit 4** and **Mockito 5**. They run on your computer, so you don't need a robot or Control Hub.

## Running

- **Android Studio:** right-click `TeamCode/src/test/java` and choose *Run 'Tests in ...'*. You can also click the green arrow next to any test class or method.
- **Command line:** `./gradlew :TeamCode:testDebugUnitTest`
  - HTML report: `TeamCode/build/reports/tests/testDebugUnitTest/index.html`

## Conventions

- **Mirror the main package layout.** `Resources/ServoPlus.java` gets `Resources/ServoPlusTest.java`.
- **Test names** follow `methodUnderTest_condition_expectedResult`.
- **Mock what you don't own:** `HardwareMap`, `DcMotor`, `Servo`, `GoBildaPinpointDriver`, `Telemetry`, `ElapsedTime`.
- **Use real objects for what you do own:** value objects (`Pose2D`, `Pos2D`) and static helpers (`Range`, `Utlities`).
- Classes that use hardware run with `@RunWith(MockitoJUnitRunner.class)`. This runner creates `@Mock` / `@Captor` fields and fails a test that has unused stubs.
- Pure-logic classes (`Utlities`, `BioBuzzRobotMovementParameters`) use plain JUnit, with no runner.

## Writing a test (Arrange / Act / Assert)

1. **Arrange:** add the `@Mock` fields listed in the class's `TODO` block. Stub them with `when(...).thenReturn(...)`, then construct the real class under test.
2. **Act:** call the method under test.
3. **Assert:** use `assertEquals(expected, actual, DELTA)` for return values, and `verify(mock).method(args)` or an `ArgumentCaptor` for hardware commands.

## Known gotchas

- **Hidden `new ElapsedTime()`** in `RotationControl`, `LauncherRamp`, `GobildaPinpointModuleFirmware` and `BioBuzzRobot`. Use `Mockito.mockConstruction(ElapsedTime.class, ...)` or add a constructor that accepts a timer. Never rely on real time in a test.
- **`@Config` static fields** (`TrajectoryKinematics`, `LauncherRamp`, `BioBuzzRobotMovementParameters`) keep their values between tests. Restore them in `@After`.
- `returnDefaultValues = true` is set in `TeamCode/build.gradle`. Android framework stubs return 0 / false / null instead of throwing an exception.
- OpModes (`opmodes/`) are not unit tested. They are thin glue code around the FTC event loop.
