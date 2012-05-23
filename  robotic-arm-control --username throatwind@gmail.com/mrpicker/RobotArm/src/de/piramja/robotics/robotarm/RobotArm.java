package de.piramja.robotics.robotarm;

public class RobotArm
{
	private RobotArmController controller;
	
	final static int SHOULDER_YAW_SERVO_NR = 0;
	final static int SHOULDER_PITCH_SERVO_NR = 1;
	final static int ELBOW_PITCH_SERVO_NR = 2;
	final static int WRIST_ROTATE_SERVO_NR = 3;
	final static int GRIPPER_LEFT_SERVO_NR = 4;
	final static int GRIPPER_RIGHT_SERVO_NR = 5;
	
	final static int LEFT_FSR_NR = 0;
	final static int RIGHT_FSR_NR = 1;
	
	
	private Servo shoulderYawServo;
	private Servo shoulderPitchServo;
	private Servo elbowPitchServo;
	private Servo wristRotateServo;
	private Servo gripperLeftServo;
	private Servo gripperRightServo;
	
	private Fsr leftFsr;
	private Fsr rightFsr;
	
	public RobotArm(RobotArmController controller)
	{
		this.controller = controller;
		
		this.shoulderYawServo = new Servo(SHOULDER_YAW_SERVO_NR, 0, 0, 127, 1300, 1300, 1000, 4000);
		this.shoulderPitchServo = new Servo(SHOULDER_PITCH_SERVO_NR, 0, 0, 127, 3550, 3550, 1000, 4000);
		this.elbowPitchServo = new Servo(ELBOW_PITCH_SERVO_NR, 0, 0, 127, 2000, 2600, 1000, 3950);
		this.wristRotateServo = new Servo(WRIST_ROTATE_SERVO_NR, 60, 0, 127, 2500, 2500, 1000, 3950);
		this.gripperLeftServo = new Servo(GRIPPER_LEFT_SERVO_NR, 0, 0, 127, 2600, 2600, 2100, 3100);
		this.gripperRightServo = new Servo(GRIPPER_RIGHT_SERVO_NR, 0, 0, 127, 2450, 2450, 1950, 2950);
		
		this.leftFsr = new Fsr(0, 0);
		this.rightFsr = new Fsr(1, 0);
	}
	
	
	
	public RobotArm(RobotArmController controller, Servo shoulderYawServo,
			Servo shoulderPitchServo, Servo elbowPitchServo,
			Servo wristRotateServo, Servo gripperLeftServo,
			Servo gripperRightServo, Fsr leftFsr, Fsr rightFsr)
	{
		super();
		this.controller = controller;
		this.shoulderYawServo = shoulderYawServo;
		this.shoulderPitchServo = shoulderPitchServo;
		this.elbowPitchServo = elbowPitchServo;
		this.wristRotateServo = wristRotateServo;
		this.gripperLeftServo = gripperLeftServo;
		this.gripperRightServo = gripperRightServo;
		
		this.leftFsr = leftFsr;
		this.rightFsr = rightFsr;
	}

	public RobotArmController getController()
	{
		return controller;
	}



	public void setController(RobotArmController controller)
	{
		this.controller = controller;
	}



	public Servo getShoulderYawServo()
	{
		return shoulderYawServo;
	}



	public void setShoulderYawServo(Servo shoulderYawServo)
	{
		this.shoulderYawServo = shoulderYawServo;
	}



	public Servo getShoulderPitchServo()
	{
		return shoulderPitchServo;
	}



	public void setShoulderPitchServo(Servo shoulderPitchServo)
	{
		this.shoulderPitchServo = shoulderPitchServo;
	}



	public Servo getElbowPitchServo()
	{
		return elbowPitchServo;
	}



	public void setElbowPitchServo(Servo elbowPitchServo)
	{
		this.elbowPitchServo = elbowPitchServo;
	}



	public Servo getWristRotateServo()
	{
		return wristRotateServo;
	}



	public void setWristRotateServo(Servo wristRotateServo)
	{
		this.wristRotateServo = wristRotateServo;
	}



	public Servo getGripperLeftServo()
	{
		return gripperLeftServo;
	}



	public void setGripperLeftServo(Servo gripperLeftServo)
	{
		this.gripperLeftServo = gripperLeftServo;
	}



	public Servo getGripperRightServo()
	{
		return gripperRightServo;
	}



	public void setGripperRightServo(Servo gripperRightServo)
	{
		this.gripperRightServo = gripperRightServo;
	}


	public class Servo
	{
		int number;
		int speed;
		int speedMin;
		int speedMax;
		int startPosition;
		int position;
		int positionMin;
		int positionMax;

		public Servo()
		{
			this.number = -1;
			this.speed = 1;
			this.position = 2500;
			this.positionMin = 0;
			this.positionMax = 5000;
		}

		public Servo(int number, int speed, int speedMin, int speedMax, int startPosition, int position, int positionMin, int positionMax)
		{
			super();
			this.number = number;
			this.speed = speed;
			this.speedMin = speedMin;
			this.speedMax = speedMax;
			this.startPosition = startPosition;
			this.position = position;
			this.positionMin = positionMin;
			this.positionMax = positionMax;
		}

		public int getNumber()
		{
			return number;
		}

		public void setNumber(int number)
		{
			this.number = number;
		}

		public int getSpeed()
		{
			return speed;
		}

		public void setSpeed(int speed)
		{
			this.speed = speed;
		}

		public int getPosition()
		{
			return position;
		}

		public void setPosition(int position)
		{
			this.position = position;
		}

		public int getSpeedMin()
		{
			return speedMin;
		}

		public void setSpeedMin(int speedMin)
		{
			this.speedMin = speedMin;
		}

		public int getSpeedMax()
		{
			return speedMax;
		}

		public void setSpeedMax(int speedMax)
		{
			this.speedMax = speedMax;
		}

		public int getPositionMin()
		{
			return positionMin;
		}

		public void setPositionMin(int positionMin)
		{
			this.positionMin = positionMin;
		}

		public int getPositionMax()
		{
			return positionMax;
		}

		public void setPositionMax(int positionMax)
		{
			this.positionMax = positionMax;
		}

		public int getStartPosition()
		{
			return startPosition;
		}

		public void setStartPosition(int startPosition)
		{
			this.startPosition = startPosition;
		}
	}
	
	
	public class Fsr
	{
		int number;
		int value;
		
		public Fsr()
		{
			this.number = -1;
			this.value = 0;
		}

		public Fsr(int number, int value)
		{
			super();
			this.number = number;
			this.value = value;
		}

		public int getNumber()
		{
			return number;
		}

		public void setNumber(int number)
		{
			this.number = number;
		}

		public int getValue()
		{
			return value;
		}

		public void setValue(int value)
		{
			this.value = value;
		}
	}


	public Fsr getLeftFsr()
	{
		return leftFsr;
	}



	public void setLeftFsr(Fsr leftFsr)
	{
		this.leftFsr = leftFsr;
	}



	public Fsr getRightFsr()
	{
		return rightFsr;
	}



	public void setRightFsr(Fsr rightFsr)
	{
		this.rightFsr = rightFsr;
	}
}
