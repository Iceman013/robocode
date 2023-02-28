package GlitterForce;
import robocode.*;
import robocode.ScannedRobotEvent;
import java.awt.*;
import java.lang.Math;
public class GlitterLucky extends TeamRobot {
	double turnAmount = 1;
	int tactic = 0;

	boolean over = false;

	// Runs on start
	public void run() {
		Color bodyColor = new Color(242, 159, 195);
		Color hairColor = new Color(233, 83, 154);
		Color bulletColor = new Color(255, 168, 209);

		setBodyColor(bodyColor);
		setGunColor(hairColor);
		setRadarColor(hairColor);
		setScanColor(hairColor);
		setBulletColor(bulletColor);

		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);

		while (!over){
			if (tactic == 0) {
				turnRadarRight(turnAmount);
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

	// Stop
	public void onWin(WinEvent e) {
		over = true;
	}

	// Events
	public void onHitByBullet(HitByBulletEvent e) {
		double target = e.getBearing();
		if (target < -90) {
			turnLeft(target + 90);
			setAhead(100);
		} else if (target < 0) {
			turnRight(90 + target);
			setAhead(100);
		} else if (target < 90) {
			turnLeft(90 - target);
			setAhead(100);
		} else {
			turnRight(target - 90);
			setAhead(100);
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName())){
			return;
		}
		/*
		if (e.getEnergy() == 0) {
			tactic = 1;
		} else {
			tactic = 0;
		}
		*/
		setNewTarget(e);
		aim(e);
		turnAmount = 1;
	}

	// Personal functions
	// Movement
	public void track(double targetX, double targetY) {
		double xpos = getX() - targetX;
		double ypos = getY() - targetY;
		if (Math.sqrt(xpos*xpos + ypos*ypos) < 100) {
			return;
		}

		double target = Math.atan(ypos/xpos);

		double total = getHeading() - 90 + 180*target/Math.PI;
		if (xpos > 0) {
			total += 180;
		}
		total = (total + 360) % 360;
		if (total <= 180) {
			turnLeft(total);
		} else {
			turnRight(360 - total);
		}

		setAhead(Math.sqrt(xpos*xpos + ypos*ypos));
	}
	public void setNewTarget(ScannedRobotEvent e) {
		double point = getRadarHeading();
		double distance = e.getDistance();

        double enemyX = Math.cos(Math.PI*point/180)*distance + getX();
		double enemyY = Math.sin(Math.PI*point/180)*distance + getY();

		double width = getBattleFieldWidth();
		double height = getBattleFieldHeight();

		double targetX;
		double targetY;

		if (enemyX < 0.5*width) {
			targetX = (enemyX + width)/2;
		} else {
			targetX = (enemyX + 0)/2;
		}
		if (enemyY < 0.5*height) {
			targetY = (enemyY + height)/2;
		} else {
			targetY = (enemyY + 0)/2;
		}

		track(targetX, targetY);
    }

	// Shooting
	public void aim(ScannedRobotEvent e) {
		double point = getRadarHeading();
		double distance = e.getDistance();

        double enemyX = Math.cos(Math.PI*point/180)*distance + getX();
		double enemyY = Math.sin(Math.PI*point/180)*distance + getY();

		double power = 3;
		double move = e.getVelocity()*distance/getBulletVelocity(power);

		double neX = enemyX + Math.cos(Math.PI*e.getHeading()/180)*move;
		double neY = enemyY + Math.sin(Math.PI*e.getHeading()/180)*move;

		double aimDirection = (180/Math.PI)*Math.atan((neY - getY())/(neX - getX()));
		if (neX < getX()) {
			aimDirection += 180;
		}
		
		turnGunTo(aimDirection);
		shoot(power);
    }

	public void turnGunTo(double direction) {
		double goal = (360 + direction - getGunHeading()) % 360;
		if (goal > 180) {
			goal = goal - 360;
		}
		turnGunRight(goal);
	}

	public double getBulletVelocity(double power) {
		return 20 - (3*power);
	}

	public void shoot(double power) {
		if (getEnergy() > power) {
			fire(power);
		}
	}
}