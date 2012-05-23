package de.piramja.robotics.robotarm.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.piramja.robotics.robotarm.RobotArmController;

public class RobotArmGui implements MouseListener {

	private JFrame frmRoboticArmControl;
	private JTextField textFieldShoulderYawPosition;
	private JTextField textFieldShoulderPitchPosition;
	private JTextField textFieldElbowPitchPosition;
	private JTextField textFieldWristRotatePosition;
	private JTextField textFieldGripperLeftPosition;
	private JTextField textFieldGripperRightPosition;
	private JTextField textFieldGripperFsrLeftValue;
	private JTextField textFieldGripperFsrRightValue;
	
	private JLabel lblRobotArmImage;
	private ImageIcon icnRobotArm;
	private ImageIcon icnRobotArmShoulderYaw;
	private ImageIcon icnRobotArmShoulderPitch;
	private ImageIcon icnRobotArmElbowPitch;
	private ImageIcon icnRobotArmWristRotate;
	private ImageIcon icnRobotArmGripper;
	
	private JButton btnSettings;
	private JButton btnRecordMovement;
	private JButton btnSave;
	private JComboBox comboBoxSavedMovement;
	private JButton btnLoad;
	private JLabel lblSavedMovementText;
	private JButton btnPlayMovement;
	private JSlider sliderShoulderYawPosition;
	private JSlider sliderShoulderPitchPosition;
	private JSlider sliderElbowPitchPosition;
	private JSlider sliderWristRotatePosition;
	private JSlider sliderGripperLeftPosition;
	private JSlider sliderGripperRightPosition;
	private JLabel lblGripperFsrLeftText;
	private JLabel lblGripperFsrRightText;
	private JSlider sliderShoulderYawAcceleration;
	private JSlider sliderShoulderPitchAcceleration;
	private JSlider sliderElbowPitchAcceleration;
	private JSlider sliderWristRotateAcceleration;
	private JSlider sliderGripperLeftAcceleration;
	private JSlider sliderGripperRightAcceleration;
	private JSpinner spinnerElbowPitchAcceleration;
	private JSpinner spinnerWristRotateAcceleration;
	private JSpinner spinnerGripperLeftAcceleration;
	private JSpinner spinnerGripperRightAcceleration;
	private JSpinner spinnerShoulderYawAcceleration;
	private JSpinner spinnerShoulderPitchAcceleration;
	
	private JComboBox comboBoxPort;
	private JTextPane txtLog;
	private JButton btnConnect;
	private JButton btnDisconnect;
	
	private RobotArmController controller;

	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args)
	{
		/*byte a1 = 0x03;
		byte a2 = 0x04;
		byte a3 = 0x02;
		
		byte b1 = 0x00;
		byte b2 = 0x01;
		byte b3 = 0x08;
		
		ArrayList<Byte> list1 = new ArrayList<Byte>();
		list1.add(a1);
		list1.add(a2);
		list1.add(a3);
		ArrayList<Byte> list2 = new ArrayList<Byte>();
		list2.add(b1);
		list2.add(b2);
		list2.add(b3);
		
		Movement mov = new Movement(list1, list2);
		
		Movement mov2 = new Movement();
		
		try {
			Filesystem.saveMovement("tada", mov);
			mov2 = Filesystem.loadMovement("tada");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(mov2.getMovement1().get(0));
		System.out.println(mov2.getMovement1().get(1));
		System.out.println(mov2.getMovement1().get(2));
		
		System.out.println(mov2.getMovement2().get(0));
		System.out.println(mov2.getMovement2().get(1));
		System.out.println(mov2.getMovement2().get(2));*/
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RobotArmGui window = new RobotArmGui();
					window.frmRoboticArmControl.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RobotArmGui()
	{
		initialize();
		this.controller = new RobotArmController(this);
		txtLog.addKeyListener(controller);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRoboticArmControl = new JFrame();
		frmRoboticArmControl.setIconImage(Toolkit.getDefaultToolkit().getImage(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/industrial_robot.png")));
		frmRoboticArmControl.setTitle("Robotic Arm Control v0.1");
		frmRoboticArmControl.setBounds(100, 100, 761, 681);
		frmRoboticArmControl.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		
		icnRobotArm = new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/RobotArm/RobotArm.png"));
		icnRobotArmShoulderYaw = new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/RobotArm/RobotArmShoulderYaw.png"));;
		icnRobotArmShoulderPitch = new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/RobotArm/RobotArmShoulderPitch.png"));;
		icnRobotArmElbowPitch = new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/RobotArm/RobotArmElbowPitch.png"));;
		icnRobotArmWristRotate = new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/RobotArm/RobotArmWristRotate.png"));;
		icnRobotArmGripper = new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/RobotArm/RobotArmGripper.png"));;
		
		lblRobotArmImage = new JLabel("");
		lblRobotArmImage.setIcon(icnRobotArm);
		
		JPanel panel_1 = new JPanel();
		
		JScrollPane scrollPane = new JScrollPane();
		
		comboBoxPort = new JComboBox();
		
		btnConnect = new JButton("connect");
		btnConnect.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/connect_small.png")));
		btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.connectSerialController();
            }
        });
		
		btnDisconnect = new JButton("disconnect");
		btnDisconnect.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/disconnect_small.png")));
		btnDisconnect.setEnabled(false);
		btnDisconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.disconnectSerialController();
            }
        });
		
		btnSettings = new JButton("settings");
		btnSettings.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/settings_small.png")));
		
		JLabel lblPort = new JLabel("Port:");
		
		JPanel panel_2 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frmRoboticArmControl.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(23)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 334, GroupLayout.PREFERRED_SIZE)
					.addGap(16))
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(23)
							.addComponent(lblPort)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(comboBoxPort, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnConnect)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnDisconnect)
							.addPreferredGap(ComponentPlacement.RELATED, 290, Short.MAX_VALUE)
							.addComponent(btnSettings)
							.addGap(6)))
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(21, Short.MAX_VALUE)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 714, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(scrollPane)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSettings)
						.addComponent(lblPort)
						.addComponent(comboBoxPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnConnect)
						.addComponent(btnDisconnect))
					.addGap(18)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
					.addGap(17))
		);
		
		btnRecordMovement = new JButton("record movement");
		btnRecordMovement.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/record_small.png")));
		btnRecordMovement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.toggleMovementRecording();
            	//controller.sendSerialCmdRecord();
            }
        });
		
		btnSave = new JButton("save");
		btnSave.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/save_small.png")));
		btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.saveRecordedMovement();
            }
        });
		
		comboBoxSavedMovement = new JComboBox();
		
		btnLoad = new JButton("load");
		btnLoad.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/load_small.png")));
		btnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.loadRecordedMovement();
            }
        });
		
		lblSavedMovementText = new JLabel("");
		
		btnPlayMovement = new JButton("play movement");
		btnPlayMovement.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/play_small.png")));
		btnPlayMovement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.toggleMovementPlaying();
                //controller.sendSerialCmdRecord();
            }
        });
		
		JButton btnNew = new JButton("new");
		btnNew.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/new_small.png")));
		btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.newRecordedMovement();
            }
        });
		
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(comboBoxSavedMovement, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnLoad)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSave)
							.addGap(6)
							.addComponent(btnNew, GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnPlayMovement, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnRecordMovement))
							.addGap(110))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(lblSavedMovementText)
							.addContainerGap(704, Short.MAX_VALUE))))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addComponent(btnRecordMovement)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnPlayMovement))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
								.addComponent(comboBoxSavedMovement, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnLoad)
								.addComponent(btnSave)
								.addComponent(btnNew))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblSavedMovementText)))
					.addContainerGap(18, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		JLabel lblServo = new JLabel("Servo");
		lblServo.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblShoulderYaw = new JLabel("shoulder yaw");
		lblShoulderYaw.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblShoulderYaw.addMouseListener(this);
		
		JLabel lblShoulderPitch = new JLabel("shoulder pitch");
		lblShoulderPitch.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblShoulderPitch.addMouseListener(this);
		
		JLabel lblElbowPitch = new JLabel("elbow pitch");
		lblElbowPitch.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblElbowPitch.addMouseListener(this);
		
		JLabel lblWristRotate = new JLabel("wrist rotate");
		lblWristRotate.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblWristRotate.addMouseListener(this);
		
		JLabel lblGripperLeft = new JLabel("gripper left");
		lblGripperLeft.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblGripperLeft.addMouseListener(this);
		
		JLabel lblGripperRight = new JLabel("gripper right");
		lblGripperRight.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		lblGripperRight.addMouseListener(this);
		
		textFieldShoulderYawPosition = new JTextField();
		textFieldShoulderYawPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldShoulderYawPosition.setEditable(false);
		textFieldShoulderYawPosition.setColumns(10);
		/*textFieldShoulderYawPosition.getDocument().addDocumentListener(new DocumentListener() {
				  public void changedUpdate(DocumentEvent e) {
				    update();
				  }
				  public void removeUpdate(DocumentEvent e) {
					  update();
				  }
				  public void insertUpdate(DocumentEvent e) {
					  update();
				  }
				  
				  public void update()
				  {
					  System.out.println("update");
				  }
				});*/
		
		textFieldShoulderPitchPosition = new JTextField();
		textFieldShoulderPitchPosition.setEditable(false);
		textFieldShoulderPitchPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldShoulderPitchPosition.setColumns(10);
		
		JLabel lblPosition = new JLabel("Position");
		lblPosition.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		textFieldElbowPitchPosition = new JTextField();
		textFieldElbowPitchPosition.setEditable(false);
		textFieldElbowPitchPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldElbowPitchPosition.setColumns(10);
		
		textFieldWristRotatePosition = new JTextField();
		textFieldWristRotatePosition.setEditable(false);
		textFieldWristRotatePosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldWristRotatePosition.setColumns(10);
		
		textFieldGripperLeftPosition = new JTextField();
		textFieldGripperLeftPosition.setEditable(false);
		textFieldGripperLeftPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldGripperLeftPosition.setColumns(10);
		
		textFieldGripperRightPosition = new JTextField();
		textFieldGripperRightPosition.setEditable(false);
		textFieldGripperRightPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldGripperRightPosition.setColumns(10);
		
		JLabel lblSensor = new JLabel("Sensor");
		lblSensor.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblGripperFsrLeft = new JLabel("gripper fsr left");
		lblGripperFsrLeft.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblGripperFsrRight = new JLabel("gripper fsr right");
		lblGripperFsrRight.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		JLabel lblValue = new JLabel("Value");
		lblValue.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		textFieldGripperFsrLeftValue = new JTextField();
		textFieldGripperFsrLeftValue.setEditable(false);
		textFieldGripperFsrLeftValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldGripperFsrLeftValue.setColumns(10);
		
		textFieldGripperFsrRightValue = new JTextField();
		textFieldGripperFsrRightValue.setEditable(false);
		textFieldGripperFsrRightValue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		textFieldGripperFsrRightValue.setColumns(10);
		
		sliderShoulderYawPosition = new JSlider();
		sliderShoulderYawPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderShoulderYawPosition.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					textFieldShoulderYawPosition.setText(new Integer(((JSlider)e.getSource()).getValue()).toString());
					if (controller != null && controller.getRobotArm() != null)
					{
						if (((JSlider)e.getSource()).getValue() != controller.getRobotArm().getShoulderYawServo().getPosition())
						{
							controller.getRobotArm().getShoulderYawServo().setPosition(((JSlider)e.getSource()).getValue());
						}
					}
				}
			 });
		
		sliderShoulderPitchPosition = new JSlider();
		sliderShoulderPitchPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderShoulderPitchPosition.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				textFieldShoulderPitchPosition.setText(new Integer(((JSlider)e.getSource()).getValue()).toString());
				if (controller != null && controller.getRobotArm() != null)
				{
					if (((JSlider)e.getSource()).getValue() != controller.getRobotArm().getShoulderPitchServo().getPosition())
					{
						controller.getRobotArm().getShoulderPitchServo().setPosition(((JSlider)e.getSource()).getValue());
					}
				}
			}
		 });
		
		sliderElbowPitchPosition = new JSlider();
		sliderElbowPitchPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderElbowPitchPosition.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				textFieldElbowPitchPosition.setText(new Integer(((JSlider)e.getSource()).getValue()).toString());
				if (controller != null && controller.getRobotArm() != null)
				{
					if (((JSlider)e.getSource()).getValue() != controller.getRobotArm().getElbowPitchServo().getPosition())
					{
						controller.getRobotArm().getElbowPitchServo().setPosition(((JSlider)e.getSource()).getValue());
					}
				}
			}
		 });
		
		sliderWristRotatePosition = new JSlider();
		sliderWristRotatePosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderWristRotatePosition.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				textFieldWristRotatePosition.setText(new Integer(((JSlider)e.getSource()).getValue()).toString());
				if (controller != null && controller.getRobotArm() != null)
				{
					if (((JSlider)e.getSource()).getValue() != controller.getRobotArm().getWristRotateServo().getPosition())
					{
						controller.getRobotArm().getWristRotateServo().setPosition(((JSlider)e.getSource()).getValue());
					}
				}
			}
		 });
		
		sliderGripperLeftPosition = new JSlider();
		sliderGripperLeftPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderGripperLeftPosition.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				textFieldGripperLeftPosition.setText(new Integer(((JSlider)e.getSource()).getValue()).toString());
				if (controller != null && controller.getRobotArm() != null)
				{
					if (((JSlider)e.getSource()).getValue() != controller.getRobotArm().getGripperLeftServo().getPosition())
					{
						controller.getRobotArm().getGripperLeftServo().setPosition(((JSlider)e.getSource()).getValue());
					}
				}
			}
		 });
		
		sliderGripperRightPosition = new JSlider();
		sliderGripperRightPosition.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderGripperRightPosition.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				textFieldGripperRightPosition.setText(new Integer(((JSlider)e.getSource()).getValue()).toString());
				if (controller != null && controller.getRobotArm() != null)
				{
					if (((JSlider)e.getSource()).getValue() != controller.getRobotArm().getGripperRightServo().getPosition())
					{
						controller.getRobotArm().getGripperRightServo().setPosition(((JSlider)e.getSource()).getValue());
					}
				}
			}
		 });
		
		lblGripperFsrLeftText = new JLabel("light touch");
		lblGripperFsrLeftText.setForeground(new Color(34, 139, 34));
		
		lblGripperFsrRightText = new JLabel("no touch");
		
		JLabel lblAcceleration = new JLabel("Acceleration");
		lblAcceleration.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		sliderShoulderYawAcceleration = new JSlider();
		sliderShoulderYawAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderShoulderYawAcceleration.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				spinnerShoulderYawAcceleration.setValue(((JSlider)e.getSource()).getValue());
			}
		 });
		
		sliderShoulderPitchAcceleration = new JSlider();
		sliderShoulderPitchAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderShoulderPitchAcceleration.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				spinnerShoulderPitchAcceleration.setValue(((JSlider)e.getSource()).getValue());
			}
		 });
		
		sliderElbowPitchAcceleration = new JSlider();
		sliderElbowPitchAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderElbowPitchAcceleration.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				spinnerElbowPitchAcceleration.setValue(((JSlider)e.getSource()).getValue());
			}
		 });
		
		sliderWristRotateAcceleration = new JSlider();
		sliderWristRotateAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderWristRotateAcceleration.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				spinnerWristRotateAcceleration.setValue(((JSlider)e.getSource()).getValue());
			}
		 });
		
		sliderGripperLeftAcceleration = new JSlider();
		sliderGripperLeftAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderGripperLeftAcceleration.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				spinnerGripperLeftAcceleration.setValue(((JSlider)e.getSource()).getValue());
			}
		 });
		
		sliderGripperRightAcceleration = new JSlider();
		sliderGripperRightAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		sliderGripperRightAcceleration.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				spinnerGripperRightAcceleration.setValue(((JSlider)e.getSource()).getValue());
			}
		 });
		
		spinnerElbowPitchAcceleration = new JSpinner();
		spinnerElbowPitchAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerElbowPitchAcceleration.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	JSpinner source = (JSpinner)e.getSource();
		        sliderElbowPitchAcceleration.setValue((Integer)source.getValue());
	        }
	     });
		
		spinnerWristRotateAcceleration = new JSpinner();
		spinnerWristRotateAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerWristRotateAcceleration.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	JSpinner source = (JSpinner)e.getSource();
		        sliderWristRotateAcceleration.setValue((Integer)source.getValue());
	        }
	     });
		
		spinnerGripperLeftAcceleration = new JSpinner();
		spinnerGripperLeftAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerGripperLeftAcceleration.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	JSpinner source = (JSpinner)e.getSource();
		        sliderGripperLeftAcceleration.setValue((Integer)source.getValue());
	        }
	     });
		
		spinnerGripperRightAcceleration = new JSpinner();
		spinnerGripperRightAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerGripperRightAcceleration.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	JSpinner source = (JSpinner)e.getSource();
		        sliderGripperRightAcceleration.setValue((Integer)source.getValue());
	        }
	     });
		
		spinnerShoulderYawAcceleration = new JSpinner();
		spinnerShoulderYawAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerShoulderYawAcceleration.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	JSpinner source = (JSpinner)e.getSource();
		        sliderShoulderYawAcceleration.setValue((Integer)source.getValue());
	        }
	     });
		
		spinnerShoulderPitchAcceleration = new JSpinner();
		spinnerShoulderPitchAcceleration.setFont(new Font("Tahoma", Font.PLAIN, 11));
		spinnerShoulderPitchAcceleration.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e) {
		    	JSpinner source = (JSpinner)e.getSource();
		        sliderShoulderPitchAcceleration.setValue((Integer)source.getValue());
	        }
	     });
		
		JButton btnMoveToStart = new JButton("move to start positions");
		btnMoveToStart.setIcon(new ImageIcon(RobotArmGui.class.getResource("/de/piramja/robotics/robotarm/img/move_small2.png")));
		btnMoveToStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controller.moveToStartingPositions();
            }
        });
		
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblServo)
								.addComponent(lblShoulderYaw)
								.addComponent(lblWristRotate)
								.addComponent(lblGripperLeft)
								.addComponent(lblGripperRight)
								.addComponent(lblElbowPitch)
								.addComponent(lblShoulderPitch))
							.addGap(28)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPosition)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(textFieldShoulderYawPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(sliderShoulderYawPosition, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textFieldShoulderPitchPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(sliderShoulderPitchPosition, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(textFieldElbowPitchPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(sliderElbowPitchPosition, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(textFieldWristRotatePosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(sliderWristRotatePosition, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(textFieldGripperLeftPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(sliderGripperLeftPosition, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(textFieldGripperRightPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(sliderGripperRightPosition, GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSensor)
								.addComponent(lblGripperFsrRight)
								.addComponent(lblGripperFsrLeft))
							.addGap(18)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(textFieldGripperFsrRightValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(lblGripperFsrRightText))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(textFieldGripperFsrLeftValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(lblGripperFsrLeftText)
									.addPreferredGap(ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
									.addComponent(btnMoveToStart))
								.addComponent(lblValue))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(lblAcceleration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(sliderGripperRightAcceleration, 0, 0, Short.MAX_VALUE)
						.addComponent(sliderGripperLeftAcceleration, 0, 0, Short.MAX_VALUE)
						.addComponent(sliderWristRotateAcceleration, 0, 0, Short.MAX_VALUE)
						.addComponent(sliderElbowPitchAcceleration, 0, 0, Short.MAX_VALUE)
						.addComponent(sliderShoulderPitchAcceleration, 0, 0, Short.MAX_VALUE)
						.addComponent(sliderShoulderYawAcceleration, GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
					.addGap(27)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(spinnerGripperRightAcceleration)
						.addComponent(spinnerGripperLeftAcceleration)
						.addComponent(spinnerElbowPitchAcceleration)
						.addComponent(spinnerShoulderPitchAcceleration)
						.addComponent(spinnerShoulderYawAcceleration)
						.addComponent(spinnerWristRotateAcceleration))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblServo)
						.addComponent(lblPosition)
						.addComponent(lblAcceleration))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(spinnerShoulderYawAcceleration, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(spinnerShoulderPitchAcceleration, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(spinnerElbowPitchAcceleration, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinnerWristRotateAcceleration, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(spinnerGripperLeftAcceleration, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(sliderShoulderYawPosition, 0, 0, Short.MAX_VALUE)
								.addComponent(sliderShoulderYawAcceleration, GroupLayout.PREFERRED_SIZE, 21, Short.MAX_VALUE)
								.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
									.addComponent(textFieldShoulderYawPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblShoulderYaw)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(sliderShoulderPitchAcceleration, 0, 0, Short.MAX_VALUE)
								.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblShoulderPitch)
									.addComponent(textFieldShoulderPitchPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(sliderShoulderPitchPosition, GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE))
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(12)
									.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
										.addComponent(sliderElbowPitchPosition, GroupLayout.PREFERRED_SIZE, 20, Short.MAX_VALUE)
										.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
											.addComponent(textFieldElbowPitchPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblElbowPitch))))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(13)
									.addComponent(sliderElbowPitchAcceleration, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_panel_1.createSequentialGroup()
										.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
											.addComponent(textFieldWristRotatePosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblWristRotate))
										.addGap(1))
									.addGroup(gl_panel_1.createSequentialGroup()
										.addComponent(sliderWristRotatePosition, GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED)))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(1)
									.addComponent(sliderWristRotateAcceleration, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_panel_1.createSequentialGroup()
										.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
											.addComponent(textFieldGripperLeftPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(lblGripperLeft))
										.addGap(1))
									.addGroup(gl_panel_1.createSequentialGroup()
										.addComponent(sliderGripperLeftPosition, GroupLayout.PREFERRED_SIZE, 17, Short.MAX_VALUE)
										.addPreferredGap(ComponentPlacement.RELATED)))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(1)
									.addComponent(sliderGripperLeftAcceleration, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
									.addComponent(textFieldGripperRightPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblGripperRight))
								.addComponent(sliderGripperRightPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(4)
									.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
										.addComponent(sliderGripperRightAcceleration, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
										.addComponent(spinnerGripperRightAcceleration, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))))
							.addGap(18)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSensor)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(lblValue)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
										.addComponent(textFieldGripperFsrLeftValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblGripperFsrLeftText)
										.addComponent(btnMoveToStart)
										.addComponent(lblGripperFsrLeft))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
										.addComponent(textFieldGripperFsrRightValue, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblGripperFsrRightText)
										.addComponent(lblGripperFsrRight))))
							.addGap(9))))
		);
		panel_1.setLayout(gl_panel_1);
		
		txtLog = new JTextPane();
		txtLog.setFont(new Font("Terminal", Font.PLAIN, 11));
		scrollPane.setViewportView(txtLog);
		/*txtLog.setColumns(27);
		txtLog.setRows(12);*/
		txtLog.setEditable(false);
		txtLog.setText("");
		
		Style style = txtLog.addStyle("colors", null);
	    StyleConstants.setForeground(style, Color.red);
		
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(lblRobotArmImage);
		frmRoboticArmControl.getContentPane().setLayout(groupLayout);
	}
	
	public void toggleConnectedButtons()
	{
		comboBoxPort.setEnabled(!comboBoxPort.isEnabled());
		btnConnect.setEnabled(!btnConnect.isEnabled());
		btnDisconnect.setEnabled(!btnDisconnect.isEnabled());
	}
	
	public void info(String text)
	{
		info(text, true);
	}
	
	public void info(String text, boolean newline)
	{
		StyledDocument doc = txtLog.getStyledDocument();
		StyleConstants.setForeground(txtLog.getStyle("colors"), Color.black);
		String nl = "";
		if (newline)
			nl = "\n";
		try { doc.insertString(doc.getLength(), text + nl,txtLog.getStyle("colors"));}
	    catch (BadLocationException e){}
		txtLog.scrollRectToVisible(new Rectangle(0, txtLog.getHeight()-2, 1, 1));
	}
	
	public void controllerInfo(String text)
	{
		controllerInfo(text, true);
	}
	
	public void controllerInfo(String text, boolean newline)
	{
		StyledDocument doc = txtLog.getStyledDocument();
		StyleConstants.setForeground(txtLog.getStyle("colors"), Color.blue);
		String nl = "";
		if (newline)
			nl = "\n";
		try { doc.insertString(doc.getLength(), text + nl,txtLog.getStyle("colors"));}
	    catch (BadLocationException e){}
		txtLog.scrollRectToVisible(new Rectangle(0, txtLog.getHeight()-2, 1, 1));
	}
	
	public void error(String text)
	{
		error(text, true);
	}
	
	public void error(String text, boolean newline)
	{
		StyledDocument doc = txtLog.getStyledDocument();
		StyleConstants.setForeground(txtLog.getStyle("colors"), Color.red);
		String nl = "";
		if (newline)
			nl = "\n";
		try { doc.insertString(doc.getLength(), text + nl,txtLog.getStyle("colors"));}
	    catch (BadLocationException e){}
		txtLog.scrollRectToVisible(new Rectangle(0, txtLog.getHeight()-2, 1, 1));
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (((JLabel)arg0.getSource()).getText().equals("shoulder yaw"))
		{
			lblRobotArmImage.setIcon(icnRobotArmShoulderYaw);
		}
		if (((JLabel)arg0.getSource()).getText().equals("shoulder pitch"))
		{
			lblRobotArmImage.setIcon(icnRobotArmShoulderPitch);
		}
		if (((JLabel)arg0.getSource()).getText().equals("elbow pitch"))
		{
			lblRobotArmImage.setIcon(icnRobotArmElbowPitch);
		}
		if (((JLabel)arg0.getSource()).getText().equals("wrist rotate"))
		{
			lblRobotArmImage.setIcon(icnRobotArmWristRotate);
		}
		if (((JLabel)arg0.getSource()).getText().equals("gripper left") || ((JLabel)arg0.getSource()).getText().equals("gripper right"))
		{
			lblRobotArmImage.setIcon(icnRobotArmGripper);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public JComboBox getComboBoxPort() {
		return comboBoxPort;
	}

	public RobotArmController getController()
	{
		return controller;
	}

	public JTextField getTextFieldShoulderYawPosition()
	{
		return textFieldShoulderYawPosition;
	}

	public JTextField getTextFieldShoulderPitchPosition()
	{
		return textFieldShoulderPitchPosition;
	}

	public JTextField getTextFieldElbowPitchPosition()
	{
		return textFieldElbowPitchPosition;
	}

	public JTextField getTextFieldWristRotatePosition()
	{
		return textFieldWristRotatePosition;
	}

	public JTextField getTextFieldGripperLeftPosition()
	{
		return textFieldGripperLeftPosition;
	}

	public JTextField getTextFieldGripperRightPosition()
	{
		return textFieldGripperRightPosition;
	}

	public JTextField getTextFieldGripperFsrLeftValue()
	{
		return textFieldGripperFsrLeftValue;
	}

	public JTextField getTextFieldGripperFsrRightValue()
	{
		return textFieldGripperFsrRightValue;
	}

	public JComboBox getComboBoxSavedMovement()
	{
		return comboBoxSavedMovement;
	}

	public JLabel getLblSavedMovementText()
	{
		return lblSavedMovementText;
	}

	public JSlider getSliderShoulderYawPosition()
	{
		return sliderShoulderYawPosition;
	}

	public JSlider getSliderShoulderPitchPosition()
	{
		return sliderShoulderPitchPosition;
	}

	public JSlider getSliderElbowPitchPosition()
	{
		return sliderElbowPitchPosition;
	}

	public JSlider getSliderWristRotatePosition()
	{
		return sliderWristRotatePosition;
	}

	public JSlider getSliderGripperLeftPosition()
	{
		return sliderGripperLeftPosition;
	}

	public JSlider getSliderGripperRightPosition()
	{
		return sliderGripperRightPosition;
	}

	public JLabel getLblGripperFsrLeftText()
	{
		return lblGripperFsrLeftText;
	}

	public JLabel getLblGripperFsrRightText()
	{
		return lblGripperFsrRightText;
	}

	public JSlider getSliderShoulderYawAcceleration()
	{
		return sliderShoulderYawAcceleration;
	}

	public JSlider getSliderShoulderPitchAcceleration()
	{
		return sliderShoulderPitchAcceleration;
	}

	public JSlider getSliderElbowPitchAcceleration()
	{
		return sliderElbowPitchAcceleration;
	}

	public JSlider getSliderWristRotateAcceleration()
	{
		return sliderWristRotateAcceleration;
	}

	public JSlider getSliderGripperLeftAcceleration()
	{
		return sliderGripperLeftAcceleration;
	}

	public JSlider getSliderGripperRightAcceleration()
	{
		return sliderGripperRightAcceleration;
	}

	public JSpinner getSpinnerElbowPitchAcceleration()
	{
		return spinnerElbowPitchAcceleration;
	}

	public JSpinner getSpinnerWristRotateAcceleration()
	{
		return spinnerWristRotateAcceleration;
	}

	public JSpinner getSpinnerGripperLeftAcceleration()
	{
		return spinnerGripperLeftAcceleration;
	}

	public JSpinner getSpinnerGripperRightAcceleration()
	{
		return spinnerGripperRightAcceleration;
	}

	public JSpinner getSpinnerShoulderYawAcceleration()
	{
		return spinnerShoulderYawAcceleration;
	}

	public JSpinner getSpinnerShoulderPitchAcceleration()
	{
		return spinnerShoulderPitchAcceleration;
	}

	public JButton getBtnRecordMovement()
	{
		return btnRecordMovement;
	}

	public JButton getBtnPlayMovement()
	{
		return btnPlayMovement;
	}
}
