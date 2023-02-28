package GlitterForce;
import robocode.*;
import robocode.ScannedRobotEvent;
import java.awt.*;
import java.lang.Math;
public class GlitterSpring extends TeamRobot {
	double confidence = 100;
	double edge = 0.2;

	double dir = 1;
	double dis;
	int tactic = 0;

	double size;

	boolean over = false;
	public void run() {
		size = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		dis = size/2;
		if (Math.random() < 0.5) {
			dir = -1;
		}
		
		Color bodyColor = new Color(0, 170, 57);
		Color hairColor = new Color(123, 193, 94);
		Color bulletColor = new Color(202, 255, 191);

		setBodyColor(bodyColor);
		setGunColor(hairColor);
		setRadarColor(hairColor);
		setScanColor(hairColor);
		setBulletColor(bulletColor);

		while (!over) {
			if (tactic == 0) {
				turnGunRight(-1*dir*180);
				setAhead(dir*dis);
				if (dir == 1) {
					turnRight(20);
				} else {
					turnLeft(20);
				}
			} else if (tactic == 1) {
				turnGunRight(-1*dir*60);
			} else if (tactic == 2) {
				turnGunRight(-1*dir*180);
				setAhead(dir*dis);
				if (dir == 1) {
					turnRight(10);
				} else {
					turnLeft(10);
				}
			}
			checkPosition();
		}
	}
	public void checkPosition() {
		double xpos = getX();
		double ypos = getY();
		if (xpos < edge*getBattleFieldWidth() || ypos < edge*getBattleFieldHeight() || xpos > (1 - edge)*getBattleFieldWidth() || ypos > (1 - edge)*getBattleFieldHeight()) {
			setBack(dir*dis);
			turnRight(-90);
			dir = -1*dir;
		}
	}
	public void onWin(WinEvent e) {
		over = true;
	}
	public void onHitWall(HitWallEvent e) {
		setBack(dir*dis);
		turnRight(-90);
		dir = -1*dir;
	}
	public void onHitRobot(HitRobotEvent e) {
		tactic = 2;
	}
	public void onBulletHit(BulletHitEvent e) {
		confidence++;
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		if (isTeammate(e.getName())){
			return;
		}
		
		confidence += 0.1;
		if (e.getEnergy() == 0) {
			tactic = 1;
		}

		if (tactic == 0) {
			shoot(Math.min(3, Math.max(2, 2 + (confidence/100))));
		} else if (tactic == 1) {
			shoot(3);
		} else if (tactic == 2) {
			shoot(3);
		}

		dis = e.getDistance();
	}
	public void shoot(double power) {
		if (getEnergy() > power) {
			fire(power);
			confidence--;
		}
	}
}