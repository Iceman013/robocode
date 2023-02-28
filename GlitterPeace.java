package GlitterForce;
import robocode.*;
import robocode.ScannedRobotEvent;
import java.awt.*;
import java.lang.Math;
public class GlitterPeace extends TeamRobot {
	int tactic = 0;

	boolean over = false;
	public void run() {
		Color bodyColor = new Color(252, 214, 11);
		Color hairColor = new Color(252, 214, 11);
		Color bulletColor = new Color(255, 243, 181);

		setBodyColor(bodyColor);
		setGunColor(hairColor);
		setRadarColor(hairColor);
		setScanColor(hairColor);
		setBulletColor(bulletColor);

		while (!over){
			if (tactic == 0) {
				turnGunRight(60);
			} else if (tactic == 1) {
				turnGunRight(1);
			}
		}
	}
	public void onWin(WinEvent e) {
		over = true;
	}

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
		if (e.getVelocity() == 0) {
			shoot(3);
		}
		if (e.getEnergy() == 0) {
			tactic = 1;
			shoot(3);
			stop();
		} else {
			tactic = 0;
		}
		shoot(3);
		setNewTarget(90 - getGunHeading(), e.getDistance());
	}
	public void setNewTarget(double point, double distance) {
        double xpos = getX();
        double ypos = getY();
        
        double enemyX;
		double enemyY;

		enemyX = Math.cos(Math.PI*point/180)*distance + xpos;
		enemyY = Math.sin(Math.PI*point/180)*distance + ypos;

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
	public void shoot(double power) {
		if (getEnergy() > power) {
			fire(power);
		}
	}
}