package de.piramja.robotics.robotarm;

import gnu.io.CommPortIdentifier;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import de.piramja.robotics.robotarm.gui.RobotArmGui;

public class RobotArmController implements KeyListener//, ActionListener
{
	public final static int SHOULDER_YAW_MIN = 1000;
	public final static int SHOULDER_YAW_MAX = 4000;
	public final static int SHOULDER_YAW_BITMASK = 1;

	public final static int SHOULDER_PITCH_MIN = 1000;
	public final static int SHOULDER_PITCH_MAX = 4000;
	public final static int SHOULDER_PITCH_BITMASK = 2;

	public final static int ELBOW_PITCH_MIN = 1000;
	public final static int ELBOW_PITCH_MAX = 3950;
	public final static int ELBOW_PITCH_BITMASK = 4;

	public final static int WRIST_ROTATE_MIN = 1000;
	public final static int WRIST_ROTATE_MAX = 3950;
	public final static int WRIST_ROTATE_BITMASK = 8;

	public final static int GRIPPER_LEFT_MIN = 2100;
	public final static int GRIPPER_LEFT_MAX = 3100;
	
	public final static int GRIPPER_RIGHT_MIN = 1950;
	public final static int GRIPPER_RIGHT_MAX = 2950;
	
	public final static int GRIPPER_BITMASK = 16;
	
	private RobotArmGui gui;
	private RobotArm robotArm;
	private SerialController serial;
	
	private byte move1 = 0;
    private byte move2 = 0;
    
    private byte lastMove1 = 0;
    private byte lastMove2 = 0;
    
    private byte lastPlayedMove1 = 0;
    private byte lastPlayedMove2 = 0;
    
    private Movement recordedMovement = new Movement();
    
    int recordStep = 0;
    private boolean recording = false;
    private boolean playing = false;
    
	HashMap<String, CommPortIdentifier> serialPorts;
	
	//public Timer timer = new Timer(40, this);
	private SerialTimer timer = new SerialTimer(this);
	
	private long lastMillis = 0;
	
	public RobotArmController(RobotArmGui gui)
	{
		this.gui = gui;
		robotArm = new RobotArm(this);
		serial = new SerialController(this);
		
		timer.setPriority(Thread.MAX_PRIORITY);
		
		initialize();
	}
	
	private void initialize()
	{
		view_populatePortList();
		view_populateSavedMovementList();
		view_populateForms();
		info("welcome to robot arm control v0.1");
	}
	
	public void view_populatePortList()
	{
		HashMap<String, CommPortIdentifier> ports = serial.getAvailableSerialPorts();
		Iterator<String> it = ports.keySet().iterator();
		
		while (it.hasNext())
        {
			gui.getComboBoxPort().addItem(it.next());
			gui.getComboBoxPort().setSelectedIndex(gui.getComboBoxPort().getItemCount()-1);
        }
	}
	
	public void view_populateSavedMovementList()
	{
		List <String> files = Filesystem.getFileList();
		
		gui.getComboBoxSavedMovement().removeAllItems();
		
		for (int i=0; i<files.size(); i++)
		{
			gui.getComboBoxSavedMovement().addItem(files.get(i));
		}
	}
	
	public void view_populateForms()
	{
		// populate position text fields
		gui.getTextFieldShoulderYawPosition().setText(new Integer(robotArm.getShoulderYawServo().getPosition()).toString());
		gui.getTextFieldShoulderPitchPosition().setText(new Integer(robotArm.getShoulderPitchServo().getPosition()).toString());
		gui.getTextFieldElbowPitchPosition().setText(new Integer(robotArm.getElbowPitchServo().getPosition()).toString());
		gui.getTextFieldWristRotatePosition().setText(new Integer(robotArm.getWristRotateServo().getPosition()).toString());
		gui.getTextFieldGripperLeftPosition().setText(new Integer(robotArm.getGripperLeftServo().getPosition()).toString());
		gui.getTextFieldGripperRightPosition().setText(new Integer(robotArm.getGripperRightServo().getPosition()).toString());
		
		// set position sliders
		gui.getSliderShoulderYawPosition().setMinimum(robotArm.getShoulderYawServo().getPositionMin());
		gui.getSliderShoulderYawPosition().setMaximum(robotArm.getShoulderYawServo().getPositionMax());
		gui.getSliderShoulderYawPosition().setValue(robotArm.getShoulderYawServo().getPosition());
		
		gui.getSliderShoulderPitchPosition().setMinimum(robotArm.getShoulderPitchServo().getPositionMin());
		gui.getSliderShoulderPitchPosition().setMaximum(robotArm.getShoulderPitchServo().getPositionMax());
		gui.getSliderShoulderPitchPosition().setValue(robotArm.getShoulderPitchServo().getPosition());
		
		gui.getSliderElbowPitchPosition().setMinimum(robotArm.getElbowPitchServo().getPositionMin());
		gui.getSliderElbowPitchPosition().setMaximum(robotArm.getElbowPitchServo().getPositionMax());
		gui.getSliderElbowPitchPosition().setValue(robotArm.getElbowPitchServo().getPosition());
		
		gui.getSliderWristRotatePosition().setMinimum(robotArm.getWristRotateServo().getPositionMin());
		gui.getSliderWristRotatePosition().setMaximum(robotArm.getWristRotateServo().getPositionMax());
		gui.getSliderWristRotatePosition().setValue(robotArm.getWristRotateServo().getPosition());
		
		gui.getSliderGripperLeftPosition().setMinimum(robotArm.getGripperLeftServo().getPositionMin());
		gui.getSliderGripperLeftPosition().setMaximum(robotArm.getGripperLeftServo().getPositionMax());
		gui.getSliderGripperLeftPosition().setValue(robotArm.getGripperLeftServo().getPosition());
		
		gui.getSliderGripperRightPosition().setMinimum(robotArm.getGripperRightServo().getPositionMin());
		gui.getSliderGripperRightPosition().setMaximum(robotArm.getGripperRightServo().getPositionMax());
		gui.getSliderGripperRightPosition().setValue(robotArm.getGripperRightServo().getPosition());
		
		// set speed sliders
		gui.getSliderShoulderYawAcceleration().setMinimum(robotArm.getShoulderYawServo().getSpeedMin());
		gui.getSliderShoulderYawAcceleration().setMaximum(robotArm.getShoulderYawServo().getSpeedMax());
		gui.getSliderShoulderYawAcceleration().setValue(robotArm.getShoulderYawServo().getSpeed());
		
		gui.getSliderShoulderPitchAcceleration().setMinimum(robotArm.getShoulderPitchServo().getSpeedMin());
		gui.getSliderShoulderPitchAcceleration().setMaximum(robotArm.getShoulderPitchServo().getSpeedMax());
		gui.getSliderShoulderPitchAcceleration().setValue(robotArm.getShoulderPitchServo().getSpeed());
		
		gui.getSliderElbowPitchAcceleration().setMinimum(robotArm.getElbowPitchServo().getSpeedMin());
		gui.getSliderElbowPitchAcceleration().setMaximum(robotArm.getElbowPitchServo().getSpeedMax());
		gui.getSliderElbowPitchAcceleration().setValue(robotArm.getElbowPitchServo().getSpeed());
		
		gui.getSliderWristRotateAcceleration().setMinimum(robotArm.getWristRotateServo().getSpeedMin());
		gui.getSliderWristRotateAcceleration().setMaximum(robotArm.getWristRotateServo().getSpeedMax());
		gui.getSliderWristRotateAcceleration().setValue(robotArm.getWristRotateServo().getSpeed());
		
		gui.getSliderGripperLeftAcceleration().setMinimum(robotArm.getGripperLeftServo().getSpeedMin());
		gui.getSliderGripperLeftAcceleration().setMaximum(robotArm.getGripperLeftServo().getSpeedMax());
		gui.getSliderGripperLeftAcceleration().setValue(robotArm.getGripperLeftServo().getSpeed());
		
		gui.getSliderGripperRightAcceleration().setMinimum(robotArm.getGripperRightServo().getSpeedMin());
		gui.getSliderGripperRightAcceleration().setMaximum(robotArm.getGripperRightServo().getSpeedMax());
		gui.getSliderGripperRightAcceleration().setValue(robotArm.getGripperRightServo().getSpeed());
		
		// set speed spinner
		gui.getSpinnerShoulderYawAcceleration().setValue(robotArm.getShoulderYawServo().getSpeed());
		gui.getSpinnerShoulderPitchAcceleration().setValue(robotArm.getShoulderPitchServo().getSpeed());
		gui.getSpinnerElbowPitchAcceleration().setValue(robotArm.getElbowPitchServo().getSpeed());
		gui.getSpinnerWristRotateAcceleration().setValue(robotArm.getWristRotateServo().getSpeed());
		gui.getSpinnerGripperLeftAcceleration().setValue(robotArm.getGripperLeftServo().getSpeed());
		gui.getSpinnerGripperRightAcceleration().setValue(robotArm.getGripperRightServo().getSpeed());
		
		// set fsr text fields
		gui.getTextFieldGripperFsrLeftValue().setText(new Integer(robotArm.getLeftFsr().getValue()).toString());
		gui.getTextFieldGripperFsrRightValue().setText(new Integer(robotArm.getRightFsr().getValue()).toString());
		
		// set fsr text
		if (robotArm.getLeftFsr().getValue() > 30) {
			gui.getLblGripperFsrLeftText().setForeground(Color.red);
			gui.getLblGripperFsrLeftText().setText("big squeeze");
		}else if (robotArm.getLeftFsr().getValue() > 20) {
			gui.getLblGripperFsrLeftText().setForeground(Color.blue);
			gui.getLblGripperFsrLeftText().setText("medium squeeze");
		}else if (robotArm.getLeftFsr().getValue() > 1) {
			gui.getLblGripperFsrLeftText().setForeground(Color.green);
			gui.getLblGripperFsrLeftText().setText("light squeeze");
		}else{
			gui.getLblGripperFsrLeftText().setForeground(Color.black);
			gui.getLblGripperFsrLeftText().setText("no squeeze");
		}
		
		if (robotArm.getRightFsr().getValue() > 30) {
			gui.getLblGripperFsrRightText().setForeground(Color.red);
			gui.getLblGripperFsrRightText().setText("big squeeze");
		}else if (robotArm.getRightFsr().getValue() > 20) {
			gui.getLblGripperFsrRightText().setForeground(Color.blue);
			gui.getLblGripperFsrRightText().setText("medium squeeze");
		}else if (robotArm.getRightFsr().getValue() > 1) {
			gui.getLblGripperFsrRightText().setForeground(Color.green);
			gui.getLblGripperFsrRightText().setText("light squeeze");
		}else{
			gui.getLblGripperFsrRightText().setForeground(Color.black);
			gui.getLblGripperFsrRightText().setText("no squeeze");
		}
	}
	
	public void handleMovement()
	{
		
	if ((move1 & (byte)SHOULDER_YAW_BITMASK) != 0)
	{
		if (robotArm.getShoulderYawServo().getPosition() > SHOULDER_YAW_MIN) robotArm.getShoulderYawServo().setPosition(robotArm.getShoulderYawServo().getPosition()-1);
	}
	if ((move2 & (byte)SHOULDER_YAW_BITMASK) != 0)
	{
		if (robotArm.getShoulderYawServo().getPosition() < SHOULDER_YAW_MAX) robotArm.getShoulderYawServo().setPosition(robotArm.getShoulderYawServo().getPosition()+1);
	}
	
	if ((move1 & (byte)SHOULDER_PITCH_BITMASK) != 0)
	{
		if (robotArm.getShoulderPitchServo().getPosition() > SHOULDER_PITCH_MIN) robotArm.getShoulderPitchServo().setPosition(robotArm.getShoulderPitchServo().getPosition()-1);
	}
	if ((move2 & (byte)SHOULDER_PITCH_BITMASK) != 0)
	{
		if (robotArm.getShoulderPitchServo().getPosition() < SHOULDER_PITCH_MAX) robotArm.getShoulderPitchServo().setPosition(robotArm.getShoulderPitchServo().getPosition()+1);
	}
	
	if ((move1 & (byte)ELBOW_PITCH_BITMASK) != 0)
	{
		if (robotArm.getElbowPitchServo().getPosition() > ELBOW_PITCH_MIN) robotArm.getElbowPitchServo().setPosition(robotArm.getElbowPitchServo().getPosition()-1);
	}
	if ((move2 & (byte)ELBOW_PITCH_BITMASK) != 0)
	{
		if (robotArm.getElbowPitchServo().getPosition() < ELBOW_PITCH_MAX) robotArm.getElbowPitchServo().setPosition(robotArm.getElbowPitchServo().getPosition()+1);
	}
	if ((move1 & (byte)WRIST_ROTATE_BITMASK) != 0)
	{
		if (robotArm.getWristRotateServo().getPosition() < WRIST_ROTATE_MAX)
		{
			robotArm.getWristRotateServo().setPosition(robotArm.getWristRotateServo().getPosition()+1);
		}
	}
	if ((move2 & (byte)WRIST_ROTATE_BITMASK) != 0)
	{
		if (robotArm.getWristRotateServo().getPosition() > WRIST_ROTATE_MIN)
		{
			robotArm.getWristRotateServo().setPosition(robotArm.getWristRotateServo().getPosition()-1);
			
	//sendSerialCmdServoPosition(WRIST_ROTATE_SERVO_NR, wristServoRotatePos);
	//}
		}
	}
	
	// Gripper Code
	  if ((move1 & (byte)GRIPPER_BITMASK) != 0)
	  {
	    if (robotArm.getGripperLeftServo().getPosition() < GRIPPER_LEFT_MAX)
	    {
	    	robotArm.getGripperLeftServo().setPosition(robotArm.getGripperLeftServo().getPosition()+1);
	      //sendSerialCmdServoPosition(GRIPPER_LEFT_SERVO_NR, gripperServoLeftPos);
	    }
	    if (robotArm.getGripperRightServo().getPosition() > GRIPPER_RIGHT_MIN)
	    {
	    	robotArm.getGripperRightServo().setPosition(robotArm.getGripperRightServo().getPosition()-1);
	      //sendSerialCmdServoPosition(GRIPPER_RIGHT_SERVO_NR, gripperServoRightPos);
	    }
	  }
	  if ((move2 & (byte)GRIPPER_BITMASK) != 0)
	  {
	    if (robotArm.getLeftFsr().getValue() < 20 && robotArm.getRightFsr().getValue() < 20)
	    {
	      if (robotArm.getGripperLeftServo().getPosition() > GRIPPER_LEFT_MIN)
	      {
	        robotArm.getGripperLeftServo().setPosition(robotArm.getGripperLeftServo().getPosition()-1);
	        //sendSerialCmdServoPosition(GRIPPER_LEFT_SERVO_NR, gripperServoLeftPos);
	      }
	      if (robotArm.getGripperRightServo().getPosition() < GRIPPER_RIGHT_MAX)
	      {
	        robotArm.getGripperRightServo().setPosition(robotArm.getGripperRightServo().getPosition()+1);
	        //sendSerialCmdServoPosition(GRIPPER_RIGHT_SERVO_NR, gripperServoRightPos);
	      }
	    }
	  }
	
	
	//updateServos();
	
	view_populateForms();
	
	
	
		/*long millis = System.currentTimeMillis();
		System.out.println(millis - lastMillis);
		lastMillis = millis;*/
		
		byte movement1 = getMove1();
		byte movement2 = getMove2();
		
		if (playing)
		{
			if (recordStep <= recordedMovement.size()-1)
			{
				if (recordedMovement.getMovement1Step(recordStep) != lastPlayedMove1 || recordedMovement.getMovement2Step(recordStep) != lastPlayedMove2)
				{
					sendSerialCmdMove(recordedMovement.getMovement1Step(recordStep), recordedMovement.getMovement2Step(recordStep));
					System.out.println("step played: (" + recordedMovement.getMovement1Step(recordStep) + ") - (" + recordedMovement.getMovement2Step(recordStep) + ")");
				}
				lastPlayedMove1 = recordedMovement.getMovement1Step(recordStep);
				lastPlayedMove2 = recordedMovement.getMovement2Step(recordStep);
				//System.out.println("step played: (" + recordedMovement.getMovement1Step(recordStep) + ") - (" + recordedMovement.getMovement2Step(recordStep) + ")");
				recordStep++;
			}else{
				toggleMovementPlaying();
				recordStep = 0;
			}
		}else{
			//if (lastMove)
			if (recording)
			{
				recordedMovement.addStep(movement1, movement2);
				//System.out.println("step recorded: (" + movement1 + ") - (" + movement2 + ")");
			}
			if (lastMove1 == movement1 && lastMove2 == movement2)
				return;
			lastMove1 = movement1;
			lastMove2 = movement2;
			sendSerialCmdMove(movement1, movement2);
		}
		
		//System.out.println("time: " + (System.currentTimeMillis()-millis));
	}
	
	public void keyPressed(KeyEvent e)
    {
    	switch (e.getKeyCode())
    	{
	    	case KeyEvent.VK_LEFT:
	    		move1 |= (1 << 0);
				break;
    		case KeyEvent.VK_RIGHT:
    			move2 |= (1 << 0);
    			break;
    		case KeyEvent.VK_UP:
    			move1 |= (1 << 1);
    			break;
    		case KeyEvent.VK_DOWN:
    			move2 |= (1 << 1);
    			break;
    		case KeyEvent.VK_W:
    			move1 |= (1 << 2);
    			break;
    		case KeyEvent.VK_S:
    			move2 |= (1 << 2);
    			break;
    		case KeyEvent.VK_A:
    			move1 |= (1 << 3);
    			break;
    		case KeyEvent.VK_D:
    			move2 |= (1 << 3);
    			break;
    		case KeyEvent.VK_Q:
    			move1 |= (1 << 4);
    			break;
    		case KeyEvent.VK_E:
    			move2 |= (1 << 4);
    			break;
    	}
    }
    
    public void keyReleased(KeyEvent e)
    {
    	switch (e.getKeyCode())
    	{
	    	case KeyEvent.VK_LEFT:
	    		move1 &= ~(1 << 0);
				break;
    		case KeyEvent.VK_RIGHT:
    			move2 &= ~(1 << 0);
    			break;
    		case KeyEvent.VK_UP:
    			move1 &= ~(1 << 1);
    			break;
    		case KeyEvent.VK_DOWN:
    			move2 &= ~(1 << 1);
    			break;
    		case KeyEvent.VK_W:
    			move1 &= ~(1 << 2);
    			break;
    		case KeyEvent.VK_S:
    			move2 &= ~(1 << 2);
    			break;
    		case KeyEvent.VK_A:
    			move1 &= ~(1 << 3);
    			break;
    		case KeyEvent.VK_D:
    			move2 &= ~(1 << 3);
    			break;
    		case KeyEvent.VK_Q:
    			move1 &= ~(1 << 4);
    			break;
    		case KeyEvent.VK_E:
    			move2 &= ~(1 << 4);
    			break;
    	}
    }
    
    public void keyTyped(KeyEvent e)
    {
    	
    }
	
	public void connectSerialController()
	{
		if (!serial.isConnected())
		{
			HashMap<String, CommPortIdentifier> ports = serial.getAvailableSerialPorts();
			CommPortIdentifier selectedPort = ports.get(gui.getComboBoxPort().getSelectedItem());
			serial.connect(selectedPort);
		}
	}
	
	public void disconnectSerialController()
	{
		if (serial.isConnected())
		{
			timer.stop();
			sendSerialCmdBye((byte)0xfe);
		}
	}
	
	public void updateServos()
	{
		sendSerialCmdSetServoPos(RobotArm.SHOULDER_YAW_SERVO_NR, robotArm.getShoulderYawServo().getPosition());
		sendSerialCmdSetServoPos(RobotArm.SHOULDER_PITCH_SERVO_NR, robotArm.getShoulderPitchServo().getPosition());
		sendSerialCmdSetServoPos(RobotArm.ELBOW_PITCH_SERVO_NR, robotArm.getElbowPitchServo().getPosition());
		sendSerialCmdSetServoPos(RobotArm.WRIST_ROTATE_SERVO_NR, robotArm.getWristRotateServo().getPosition());
		sendSerialCmdSetServoPos(RobotArm.GRIPPER_LEFT_SERVO_NR, robotArm.getGripperLeftServo().getPosition());
		sendSerialCmdSetServoPos(RobotArm.GRIPPER_RIGHT_SERVO_NR, robotArm.getGripperRightServo().getPosition());
	}
	
	public void moveToStartingPositions()
	{
		if (serial.isConnected())
		{
			// set all servo speeds to the minimum before moving to starting positions to avoid fast uncontrolled movements
			sendSerialCmdSetServoSpeed(RobotArm.SHOULDER_YAW_SERVO_NR, 0x01);
			sendSerialCmdSetServoSpeed(RobotArm.SHOULDER_PITCH_SERVO_NR, 0x01);
			sendSerialCmdSetServoSpeed(RobotArm.ELBOW_PITCH_SERVO_NR, 0x01);
			sendSerialCmdSetServoSpeed(RobotArm.WRIST_ROTATE_SERVO_NR, 0x01);
			sendSerialCmdSetServoSpeed(RobotArm.GRIPPER_LEFT_SERVO_NR, 0x01);
			sendSerialCmdSetServoSpeed(RobotArm.GRIPPER_RIGHT_SERVO_NR, 0x01);
			
			robotArm.getShoulderYawServo().setPosition(robotArm.getShoulderYawServo().getStartPosition());
			robotArm.getShoulderPitchServo().setPosition(robotArm.getShoulderPitchServo().getStartPosition());
			robotArm.getElbowPitchServo().setPosition(robotArm.getElbowPitchServo().getStartPosition());
			robotArm.getWristRotateServo().setPosition(robotArm.getWristRotateServo().getStartPosition());
			robotArm.getGripperLeftServo().setPosition(robotArm.getGripperLeftServo().getStartPosition());
			robotArm.getGripperRightServo().setPosition(robotArm.getGripperRightServo().getStartPosition());
			
			sendSerialCmdSetServoPos(RobotArm.SHOULDER_YAW_SERVO_NR, robotArm.getShoulderYawServo().getPosition());
			sendSerialCmdSetServoPos(RobotArm.SHOULDER_PITCH_SERVO_NR, robotArm.getShoulderPitchServo().getPosition());
			sendSerialCmdSetServoPos(RobotArm.ELBOW_PITCH_SERVO_NR, robotArm.getElbowPitchServo().getPosition());
			sendSerialCmdSetServoPos(RobotArm.WRIST_ROTATE_SERVO_NR, robotArm.getWristRotateServo().getPosition());
			sendSerialCmdSetServoPos(RobotArm.GRIPPER_LEFT_SERVO_NR, robotArm.getGripperLeftServo().getPosition());
			sendSerialCmdSetServoPos(RobotArm.GRIPPER_RIGHT_SERVO_NR, robotArm.getGripperRightServo().getPosition());
			
			try {
				Thread.sleep(10000);
			} catch (Exception e) { e.printStackTrace(); }
			
			sendSerialCmdSetServoSpeed(RobotArm.SHOULDER_YAW_SERVO_NR, robotArm.getShoulderYawServo().getSpeed());
			sendSerialCmdSetServoSpeed(RobotArm.SHOULDER_PITCH_SERVO_NR, robotArm.getShoulderPitchServo().getSpeed());
			sendSerialCmdSetServoSpeed(RobotArm.ELBOW_PITCH_SERVO_NR, robotArm.getElbowPitchServo().getSpeed());
			sendSerialCmdSetServoSpeed(RobotArm.WRIST_ROTATE_SERVO_NR, robotArm.getWristRotateServo().getSpeed());
			sendSerialCmdSetServoSpeed(RobotArm.GRIPPER_LEFT_SERVO_NR, robotArm.getGripperLeftServo().getSpeed());
			sendSerialCmdSetServoSpeed(RobotArm.GRIPPER_RIGHT_SERVO_NR, robotArm.getGripperRightServo().getSpeed());
		}
	}
	
	public void startMovementRecording()
	{
		recording = true;
	}
	
	public void stopMovementRecording()
	{
		recording = false;
	}
	
	public void toggleMovementRecording()
	{
		if (recording)
		{
			stopMovementRecording();
			gui.getBtnRecordMovement().setBackground((Color)Toolkit.getDefaultToolkit().getDesktopProperty("control"));
			gui.getBtnPlayMovement().setEnabled(true);
			info("stopped recording movements..");
		}else{
			gui.getBtnRecordMovement().setBackground(Color.green);
			gui.getBtnPlayMovement().setEnabled(false);
			startMovementRecording();
			info("started recording movements..");
		}
	}
	
	public void startMovementPlaying()
	{
		playing = true;
	}
	
	public void stopMovementPlaying()
	{
		playing = false;
	}
	
	public void toggleMovementPlaying()
	{
		if (playing)
		{
			stopMovementPlaying();
			gui.getBtnPlayMovement().setBackground((Color)Toolkit.getDefaultToolkit().getDesktopProperty("control"));
			gui.getBtnRecordMovement().setEnabled(true);
			info("stopped playing movements..");
		}else{
			gui.getBtnPlayMovement().setBackground(Color.green);
			gui.getBtnRecordMovement().setEnabled(false);
			startMovementPlaying();
			info("started playing movements..");
		}
	}
	
	public void saveRecordedMovement()
	{
		String saveName = JOptionPane.showInputDialog(null, "Please enter a name for the movements", "");
		if (saveName != null && !saveName.trim().equals(""))
		{
			try {
				Filesystem.saveMovement(saveName, recordedMovement);
				info("saved movements " + saveName);
				gui.getLblSavedMovementText().setText("'" + saveName + "' saved");
				view_populateSavedMovementList();
			} catch (Exception e) {
				error("could not save movements: " + e.toString());
			}
		}
	}
	
	public void loadRecordedMovement()
	{
		String filename = gui.getComboBoxSavedMovement().getSelectedItem().toString();
		try {
			recordedMovement = Filesystem.loadMovement(filename);
			recordStep = 0;
			info("loaded movements '" + filename + "'");
			gui.getLblSavedMovementText().setText("'" + filename + "' loaded");
		} catch (Exception e) {
			error("could not load movements: " + e.toString());
		}
	}
	
	public void newRecordedMovement()
	{
		recordedMovement.clear();
		recordStep = 0;
	}
	
	public void info(String text)
	{
		gui.info(text);
	}
	
	public void info(String text, boolean newline)
	{
		gui.info(text, newline);
	}
	
	public void controllerInfo(String text)
	{
		gui.controllerInfo(text);
	}
	
	public void controllerInfo(String text, boolean newline)
	{
		gui.controllerInfo(text, newline);
	}
	
	public void error(String text)
	{
		gui.error(text);
	}
	
	public void error(String text, boolean newline)
	{
		gui.error(text, newline);
	}
	
	public void sendSerialCmdHello(byte data)
	{
		if (serial.isConnected())
		{
			serial.write(SerialController.CMD_HELLO);
			serial.write(data);
		}
	}
	
	public void sendSerialCmdBye(byte data)
	{
		if (serial.isConnected())
		{
			serial.write(SerialController.CMD_BYE);
			serial.write(data);
		}
	}
	
	public void sendSerialCmdSetServoSpeed(int servo, int speed)
	{
		if (serial.isConnected())
		{
			serial.write(SerialController.CMD_SERVOSPEED);
			serial.write((byte)servo);
			serial.write((byte)speed);
		}
	}
	
	public void sendSerialCmdMove(byte movehi, byte movelo)
	{
		if (serial.isConnected())
		{
			serial.write(SerialController.CMD_MOVE);
			serial.write(movehi);
			serial.write(movelo);
		}
	}
	
	public void sendSerialCmdSetServoPos(int servo, int position)
	{
		byte positionp2 = (byte)(position & 0x7F);
		byte positionp1 = (byte)(position >> 7);
		
		if (serial.isConnected())
		{
			serial.write(SerialController.CMD_SETSERVOPOS);
			serial.write((byte)servo);
			serial.write(positionp1);
			serial.write(positionp2);
		}
	}
	
	public void sendSerialCmdRecord()
	{
		if (serial.isConnected())
		{
			serial.write(SerialController.CMD_RECORD);
		}
	}
	
	public void sendSerialCmdReplay()
	{
		if (serial.isConnected())
		{
			serial.write(SerialController.CMD_REPLAY);
		}
	}
	
	public void receivedSerialCmdHello(byte data)
	{
		if (data == SerialController.HANDSHAKE_ID)
		{
			controllerInfo("received hello from controller..", true);
			sendSerialCmdHello(SerialController.HANDSHAKE_ID);
			info("connection to controller established");
			gui.toggleConnectedButtons();
			timer.start();
		}else{
			controllerInfo("received hello but wrong handshake.. (" + data + ")", true);
		}
	}
	
	public void receivedSerialCmdBye(byte data)
	{
		if (data == SerialController.HANDSHAKE_ID)
		{
			controllerInfo("received bye from controller..", true);
			serial.disconnect();
			gui.toggleConnectedButtons();
		}else{
			controllerInfo("received bye but wrong handshake..", true);
		}
	}
	
	public void receivedSerialCmdServoPosition(byte servo, byte positionp1, byte positionp2)
	{
		int pos=(int)( ((positionp1&0xFF)<<7) | (positionp2&0x7F) );

		switch (servo)
		{
			case RobotArm.WRIST_ROTATE_SERVO_NR:
				robotArm.getWristRotateServo().setPosition(pos);
				break;
			/*case RobotArm.GRIPPER_LEFT_SERVO_NR:
				robotArm.getGripperLeftServo().setPosition(pos);
				break;
			case RobotArm.GRIPPER_RIGHT_SERVO_NR:
				robotArm.getGripperRightServo().setPosition(pos);
				break;*/
		}
	}
	
	public void receivedSerialCmdFsrValue(byte fsr, byte value)
	{
		if ((int)fsr == RobotArm.LEFT_FSR_NR)
		{
			robotArm.getLeftFsr().setValue((int)value);
		}else if ((int)fsr == RobotArm.RIGHT_FSR_NR) {
			robotArm.getRightFsr().setValue((int)value);
		}
	}

	public RobotArmGui getGui()
	{
		return gui;
	}

	public SerialController getSerial()
	{
		return serial;
	}
	
	synchronized public byte getMove1()
	{
		return move1;
	}

	synchronized public byte getMove2()
	{
		return move2;
	}
	
	synchronized public void setMove1(byte move1)
	{
		this.move1 = move1;
	}

	synchronized public void setMove2(byte move2)
	{
		this.move2 = move2;
	}
	
	private class SerialTimer extends Thread
	{
		//RobotArmController controller;
		long previousMillis = 0;
		
		public SerialTimer(RobotArmController controller)
		{
			//this.controller = controller;
		}
		
		public void run()
		{
			while (true)
			{
				long millis = System.currentTimeMillis();
				if (millis - previousMillis >= 5)
				{
					handleMovement();
					
					previousMillis = millis;
				}
			}
		}
	}

	public RobotArm getRobotArm()
	{
		return robotArm;
	}
}
