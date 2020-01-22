package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.RobotController;

// Below: Limelight Imports
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

// Color sensor imports
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import com.revrobotics.ColorSensorV3;

import java.lang.reflect.InvocationTargetException;

// Talon SRX imports 
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  // public static OI m_oi;
  private WPI_TalonSRX RobotShooter;

  // defining controllers
  private XboxController xbox;
  private Joystick driverLeft;
  private Joystick driverRight;
  private DriverStation driverStation;

  // defining tank drive
  private TalonSRX frontLeftSpeed;
  private TalonSRX frontRightSpeed;
  private TalonSRX backLeftSpeed;
  private TalonSRX backRightSpeed;

  // defining Limelight Variables
  NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  NetworkTableEntry tx = table.getEntry("tx");
  NetworkTableEntry ty = table.getEntry("ty");
  NetworkTableEntry ta = table.getEntry("ta");

  // defining color sensor variables
  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

  // action recorder
  private ActionRecorder actions;

  // toggles

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    // drive train assignments
    frontLeftSpeed = new TalonSRX(1);
    frontRightSpeed = new TalonSRX(2);
    backLeftSpeed = new TalonSRX(3);
    backRightSpeed = new TalonSRX(4);

    // controller and joystick init
    driverLeft = new Joystick(0);
    driverRight = new Joystick(1);
    xbox = new XboxController(2);

    // input declare
    DriverInput.nameInput("LDrive");
    DriverInput.nameInput("RDrive");
    DriverInput.nameInput("AButton");

    // action recorder setup
    actions = new ActionRecorder().setMethod(this, "robotOperation", DriverInput.class).setUpButton(xbox, 1)
        .setDownButton(xbox, 2).setRecordButton(xbox, 3);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // limelight variable check
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);

    // limelight target distance from crosshair in pixels
    double xydistance = Math.sqrt(x * x + y * y);

    // pushing limelight vars
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);

    // defining color vars
    Color detectedColor = m_colorSensor.getColor();
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
    case kCustomAuto:
      // Put custom auto code here
      break;
    case kDefaultAuto:
    default:
      // Put default auto code here
      break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    try {
      actions.input(new DriverInput()
          .withInput("LDrive", driverLeft.getRawAxis(1)) // used - drives left side
          .withInput("RDrive", driverRight.getRawAxis(1)) // used - drives right side
          .withInput("AButton", xbox.getAButton()) // used - test color
      );
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }
  public void robotOperation(DriverInput input){
    double leftaxis = driverLeft.getRawAxis(1) * .10;
    frontLeftSpeed.set(ControlMode.PercentOutput, leftaxis);

  // test color detector input
    Color detectedColor = m_colorSensor.getColor();
    if(xbox.getAButton()){
      if(!(detectedColor.red > 0.47)){
        frontLeftSpeed.set(ControlMode.PercentOutput, .1);
      }
    }
  }
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
