package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.*;

//  This class was made by Heitz
//  You know who to blame now
//  Represents a pair of motors connected to the PDB, and is used to check 
//  if the currents they are receiving are within 10% difference of each other
public class PairOfMotors {

    private static PowerDistributionPanel PDB = new PowerDistributionPanel();

    private String pairName;
    int breakerPort1;
    int breakerPort2;
    double allowedDifference;
    double previousdifference = 0;
    SpeedController controller1;
    SpeedController controller2;

    boolean inBounds;
    long startedOut;
    long msecAmpsDifferent;

    public PairOfMotors(String name, int breaker1, int breaker2) {

        pairName = name;
        breakerPort1 = breaker1;
        breakerPort2 = breaker2;
        allowedDifference=0.1;
        inBounds = true;
        startedOut = -1;
        msecAmpsDifferent = 0;
        controller1=null;
        controller2=null;
       }

    public PairOfMotors(String name, int breaker1, int breaker2, double alDiff) {

        pairName = name;
        breakerPort1 = breaker1;
        breakerPort2 = breaker2;
        allowedDifference=alDiff;
        inBounds = true;
        startedOut = -1;
        msecAmpsDifferent = 0;
        controller1=null;
        controller2=null;
        }

        public PairOfMotors(String name, SpeedController c1, int breaker1, SpeedController c2, int breaker2) {

            pairName = name;
            breakerPort1 = breaker1;
            breakerPort2 = breaker2;
            allowedDifference=0.1;
            inBounds = true;
            startedOut = -1;
            msecAmpsDifferent = 0;
            controller1=c1;
            controller2=c2;
           }
    
        public PairOfMotors(String name, SpeedController c1, int breaker1, SpeedController c2, int breaker2, double alDiff) {
    
            pairName = name;
            breakerPort1 = breaker1;
            breakerPort2 = breaker2;
            allowedDifference=alDiff;
            inBounds = true;
            startedOut = -1;
            msecAmpsDifferent = 0;
            controller1=c1;
            controller2=c2;
            }
       public String getName() {
        return pairName;
    }

    public boolean isCurrentDifferent() {
        double current1 = PDB.getCurrent(breakerPort1);
        double current2 = PDB.getCurrent(breakerPort2);
        
        double differenceRatio = 0;
        differenceRatio = (Math.max(current1, current2) - Math.min(current1, current2) ) / Math.max(current1, current2);
        boolean TheyreDifferent = differenceRatio > allowedDifference;

        double maxAmps = Math.max(current1, current2);
        double minAmps = Math.min(current1, current2);

        SmartDashboard.putString(
            "Motors/Current/" + pairName,
            String.valueOf(maxAmps) + "/" + String.valueOf(minAmps) + 
            "@" + System.currentTimeMillis() +":" + current1 + "/" + current2);

        if ((maxAmps > 10.0) && (minAmps < (maxAmps - 10))) {
            if (inBounds == true) {
                inBounds = false;
                startedOut = System.currentTimeMillis();
            }
        } else {
            if (inBounds == false) {
                inBounds = true;
                msecAmpsDifferent += System.currentTimeMillis() - startedOut; 
            }
        }
        
        SmartDashboard.putString(
            "Motors/" + pairName, 
            String.format("%.1fS unbal", msecAmpsDifferent/1000.0));          

        
        return inBounds  ;
    }

    public void reset() {
        inBounds = true;
        startedOut=-1;
        msecAmpsDifferent=0;
        
        if (controller1 != null) {
            initSparkMax(controller1);
        }

        if (controller2 != null) {
            initSparkMax(controller2);
        }
    }

    public void initSparkMax(SpeedController sc) {

        if (sc instanceof CANSparkMax) {
            CANSparkMax csm = (CANSparkMax) sc;
            csm.restoreFactoryDefaults();
            csm.setMotorType(MotorType.kBrushless);
            csm.setIdleMode(IdleMode.kCoast);
            csm.setSmartCurrentLimit(50);
        }
    }

}