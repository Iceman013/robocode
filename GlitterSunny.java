package GlitterForce;
import robocode.*;
import robocode.ScannedRobotEvent;
import java.awt.*;
import java.lang.Math;
public class GlitterSunny extends TeamRobot {
	int tactic = 0;
	double turnAmount = 1;
	
	double size;
	double power;

	boolean over = false;

	// Run on start
	public void run() {
		size = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		
		Color bodyColor = new Color(240, 165, 63);
		Color hairColor = new Color(233, 86, 24);
		Color bulletColor = new Color(255, 245, 150);

		setBodyColor(bodyColor);
		setGunColor(hairColor);
		setRadarColor(hairColor);
		setScanColor(hairColor);
		setBulletColor(bulletColor);

		while (!over) {
			if (tactic == 0) {
				turnGunRight(360);
			} else if (tactic == 1) {
				tactic = 2;
			} else if (tactic == 2) {
				turnGunRight(turnAmount);
				if (Math.abs(turnAmount) < 360) {
					turnAmount = -2*turnAmount;
				} else if (turnAmount >= 360) {
					turnAmount = -360;
				} else if (turnAmount <= -360) {
					turnAmount = 360;
				}
			}
		}
	}
	public void onWin(WinEvent e) {
		over = true;
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName())){
			return;
		}
		if (tactic != 1) {
			tactic = 1;
			pointForward(e);
			setAhead(e.getDistance()/2);
		} else if (tactic == 1) {
			turnAmount = 1;
		}
	}

	public void shoot(ScannedRobotEvent e) {
		double power = 3;
		if (getEnergy() > power) {
			fire(power);
		} else if (e.getEnergy() == 0) {
			fire(1);
		}
	}

	public void pointForward(ScannedRobotEvent e) {
		double goal = getGunHeading();
		double point = getHeading();
		double calc = (360 + goal - point) % 360;
		shoot(e);
		if (calc < 180) {
			turnRight(calc);
		} else {
			turnLeft(360 - calc);
		}
	}
}