package GlitterForce;
import robocode.*;
import robocode.ScannedRobotEvent;
import java.awt.*;
import java.lang.Math;
public class GlitterBreeze extends TeamRobot {
	boolean hitEdge = true;
	double edge = 0.1;
	double turnAmount = 1;
	int tactic = 0;

	boolean over = false;
	public void run() {
		Color bodyColor = new Color(122, 159, 212);
		Color hairColor = new Color(104, 154, 206);
		Color bulletColor = new Color(196, 225, 255);

		setBodyColor(bodyColor);
		setGunColor(hairColor);
		setRadarColor(hairColor);
		setScanColor(hairColor);
		setBulletColor(bulletColor);

		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);

		turnLeft(getHeading());
		while (!over){
			if (getVelocity() == 0) {
				hitEdge = true;
				turnRight(45);
			}
			if (hitEdge) {
				hitEdge = false;
				if (getHeading() == 0) {
					setAhead((1 - edge)*getBattleFieldHeight() - getY());
				} else if (getHeading() == 90) {
					setAhead((1 - edge)*getBattleFieldWidth() - getX());
				} else if (getHeading() == 180) {
					setAhead(getY() - edge*getBattleFieldHeight());
				} else if (getHeading() == 270) {
					setAhead(getX() - edge*getBattleFieldWidth());
				}
			}
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
	public void onWin(WinEvent e) {
		over = true;
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName())){
			return;
		}
		aim(e);
		turnAmount = 1;
	}
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
		shoot(0.1);
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