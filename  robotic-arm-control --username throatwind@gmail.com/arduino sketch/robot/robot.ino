#include <SoftwareSerialPololu.h>

#define RXPIN 2 // receiver pin for serial communication with servo controller
#define TXPIN 3 // transceiver pin for serial communication with servo controller

#define SPEAKERPIN 5 // pin for piezo buzzer

#define LEDPIN 4 // pin for status led

#define FSRLEFTPIN 0 // analog pin for left fsr
#define FSRRIGHTPIN 1 // analog pin for right fsr

#define LED_OFF 0
#define LED_ON 1
#define LED_BLINK 2

// ports where servos connected on Pololu Micro Serial Servo Controller
#define SHOULDER_YAW_SERVO_NR 0
#define SHOULDER_PITCH_SERVO_NR 1
#define ELBOW_PITCH_SERVO_NR 2
#define WRIST_ROTATE_SERVO_NR 3
#define GRIPPER_LEFT_SERVO_NR 4
#define GRIPPER_RIGHT_SERVO_NR 5

// minimum and maximum position of shoulder yaw servo
#define SHOULDER_YAW_MIN 1000
#define SHOULDER_YAW_MAX 4000
#define SHOULDER_YAW_BITMASK 1 // Bitmask to get right bit for shoulder yaw servo

#define SHOULDER_PITCH_MIN 1000
#define SHOULDER_PITCH_MAX 4000
#define SHOULDER_PITCH_BITMASK 2

#define ELBOW_PITCH_MIN 1000
#define ELBOW_PITCH_MAX 3950
#define ELBOW_PITCH_BITMASK 4

#define WRIST_ROTATE_MIN 1000
#define WRIST_ROTATE_MAX 3950
#define WRIST_ROTATE_BITMASK 8

#define GRIPPER_LEFT_MIN 2100
#define GRIPPER_LEFT_MAX 3100
#define GRIPPER_RIGHT_MIN 1950
#define GRIPPER_RIGHT_MAX 2950
#define GRIPPER_BITMASK 16

#define HELLO_BYTE1 (byte)0x01
#define HELLO_BYTE2 (byte)0xfe

#define BYE_BYTE1 (byte)0x02
#define BYE_BYTE2 (byte)0xfe

// serial protocol commands PC <-> Controller
#define CMD_HELLO (byte)0x01 // controller or pc says hello
#define CMD_BYE (byte)0x02 // controller or pc says bye

// serial protocol commands PC -> Controller
#define CMD_SERVOSPEED (byte)0x03 // pc sets servo speed
#define CMD_MOVE (byte)0x04 // pc moves servos
#define CMD_SETSERVOPOS (byte)0x05 // pc sets servo position
#define CMD_RECORD (byte)0x06
#define CMD_REPLAY (byte)0x07

// serial protocol commands Controller -> PC
#define CMD_SERVOPOSITION (byte)0x08 // controller sends servo position
#define CMD_FSRVALUE (byte)0x09 // controller sends fsr (force sensitive resistor) value

// id for handshake
#define HANDSHAKE_ID (byte)0xfe

#define DATA_CMD 0
#define DATA_1 1
#define DATA_2 2
#define DATA_3 3

#define MAX_RECORD_STEPS 200

byte serialData[4];
int byteCount = 0;

int shoulderServoYawPos = 1300;
int shoulderServoPitchPos = 3550;
int elbowServoPitchPos = 2000;
int wristServoRotatePos = 2500;
int gripperServoLeftPos = 2600;
int gripperServoRightPos = 2450;

int previousShoulderServoYawPos = shoulderServoYawPos;
int previousShoulderServoPitchPos = shoulderServoPitchPos;
int previousElbowServoPitchPos = elbowServoPitchPos;
int previousWristServoRotatePos = wristServoRotatePos;
int previousGripperServoLeftPos = gripperServoLeftPos;
int previousGripperServoRightPos = gripperServoRightPos; 

boolean connected = false;

byte temp = 0;
byte movedata1 = 0;
byte movedata2 = 0;

int leftFSR = 0;
int rightFSR = 0;
int tmpLeftFSR = 0;
int tmpRightFSR = 0;

int tmpMillis = 0;

boolean ledModeChanged = false;
int ledMode = LED_BLINK;
int ledState = LOW;             // ledState used to set the LED
long previousMillis = 0;        // will store last time LED was updated
long interval = 1000;           // interval at which to blink (milliseconds)

long mainTimerPreviousMillis = 0;
long mainTimerInterval = 5;

/*int recordstep = 0;
byte move1record[MAX_RECORD_STEPS];
byte move2record[MAX_RECORD_STEPS];*/

boolean record = false;
boolean replay = false;

boolean sendfsr = true;

SoftwareSerialPololu pololu(RXPIN, TXPIN);
 
void setup() 
{ 
  Serial.begin(200000);
  pololu.begin(38400);
  
  pinMode(LEDPIN, OUTPUT);
  
  setServoSpeed(SHOULDER_YAW_SERVO_NR, 1);
  setServoSpeed(SHOULDER_PITCH_SERVO_NR, 1);
  setServoSpeed(ELBOW_PITCH_SERVO_NR, 1);
  setServoSpeed(WRIST_ROTATE_SERVO_NR, 1);
  setServoSpeed(GRIPPER_LEFT_SERVO_NR, 1);
  setServoSpeed(GRIPPER_RIGHT_SERVO_NR, 1);
  
  setServoPosition(SHOULDER_YAW_SERVO_NR, shoulderServoYawPos);
  setServoPosition(SHOULDER_PITCH_SERVO_NR, shoulderServoPitchPos);
  setServoPosition(ELBOW_PITCH_SERVO_NR, elbowServoPitchPos);
  setServoPosition(WRIST_ROTATE_SERVO_NR, wristServoRotatePos);
  setServoPosition(GRIPPER_LEFT_SERVO_NR, gripperServoLeftPos);
  setServoPosition(GRIPPER_RIGHT_SERVO_NR, gripperServoRightPos);
  
  setServoSpeed(SHOULDER_YAW_SERVO_NR, 0);
  setServoSpeed(SHOULDER_PITCH_SERVO_NR, 0);
  setServoSpeed(ELBOW_PITCH_SERVO_NR, 0);
  setServoSpeed(WRIST_ROTATE_SERVO_NR, 0);
  setServoSpeed(GRIPPER_LEFT_SERVO_NR, 0);
  setServoSpeed(GRIPPER_RIGHT_SERVO_NR, 0);
  
  // send hello to remote pc
  sendSerialCmdHello(HANDSHAKE_ID);
} 
 
 
void loop()
{
  listenSerialPort(); // listen for serial commands
  statusLed(); // update status led
  
  if (connected)
  {
    unsigned long currentMillis = millis();
    
    readAnalogInput();
    //sendFsrValues();
    
    if (currentMillis - mainTimerPreviousMillis >= mainTimerInterval)
    {
      /*if (replay)
      {
        if (move1record[recordstep] == '\0')
        {
          recordstep = 0;
          Serial.println("end of replay");
          replay = false;
        }else{
          movedata1 = move1record[recordstep];
          movedata2 = move2record[recordstep++];
        }
      }*/
      
      if (sendfsr)
        sendFsrValues();
      sendfsr = !sendfsr;
      
      setServoPositions();
      updateServos();
      
      /*if (record)
      {
        if (recordstep < MAX_RECORD_STEPS-1)
        {
          move1record[recordstep] = movedata1;
          move2record[recordstep++] = movedata2;
        }else{
          move1record[recordstep] = '\0';
          move2record[recordstep++] = '\0';
          record = false;
          Serial.println("reached max record steps");
        }
      }*/
      
      mainTimerPreviousMillis = currentMillis;
    }
  }
}

void listenSerialPort()
{
  if (Serial.available())
  {
    byte data = Serial.read();
    
    if (byteCount == 0)
    {
      switch (data)
      {
        case CMD_HELLO: // received command hello
          serialData[DATA_CMD] = data;
          byteCount++;
          break;
        case CMD_BYE: // received command bye
          serialData[DATA_CMD] = data;
          byteCount++;
          break;
        case CMD_SERVOSPEED:
          serialData[DATA_CMD] = data;
          byteCount++;
          break;
        case CMD_MOVE:
          serialData[DATA_CMD] = data;
          byteCount++;
          break;
        case CMD_SETSERVOPOS:
          serialData[DATA_CMD] = data;
          byteCount++;
          break;
        case CMD_RECORD:
          receivedSerialCmdRecord();
          break;
        case CMD_REPLAY:
          receivedSerialCmdReplay();
          break;
      }
    }else{
      switch(serialData[DATA_CMD])
      {
        case CMD_HELLO: // receive data for command hello
          serialData[byteCount] = data;
          if (byteCount == DATA_1)
          {
            receivedSerialCmdHello(serialData[DATA_1]);
            byteCount = 0;
            break;
          }
          byteCount++;
          break;
        case CMD_BYE: // receive data for command bye
          serialData[byteCount] = data;
          if (byteCount == DATA_1)
          {
            receivedSerialCmdBye(serialData[DATA_1]);
            byteCount = 0;
            break;
          }
          byteCount++;
          break;
        case CMD_SERVOSPEED: // receive data for command servospeed
          serialData[byteCount] = data;
          if (byteCount == DATA_2)
          {
            receivedSerialCmdServoSpeed(serialData[DATA_1], serialData[DATA_2]);
            byteCount = 0;
            break;
          }
          byteCount++;
          break;
        case CMD_MOVE: // receive data for command move
          serialData[byteCount] = data;
          if (byteCount == DATA_2)
          {
            receivedSerialCmdMove(serialData[DATA_1], serialData[DATA_2]);
            byteCount = 0;
            break;
          }
          byteCount++;
          break;
        case CMD_SETSERVOPOS: // receive data for command setservopos
          serialData[byteCount] = data;
          if (byteCount == DATA_3)
          {
            receivedSerialCmdSetServoPos(serialData[DATA_1], serialData[DATA_2], serialData[DATA_3]);
            byteCount = 0;
            break;
          }
          byteCount++;
          break;
      }
    }
  }
}

void sendSerialCmdHello(byte data)
{
  Serial.write(CMD_HELLO);
  Serial.write(data);
}

void sendSerialCmdBye(byte data)
{
  Serial.write(CMD_BYE);
  Serial.write(data);
}

void sendSerialCmdServoPosition(byte servo, int position)
{
  byte positionp1, positionp2;
  positionp2 = position & B01111111;
  positionp1 = position >> 7;
  
  Serial.write(CMD_SERVOPOSITION);
  Serial.write(servo);
  Serial.write(positionp1);
  Serial.write(positionp2);
}

void sendSerialCmdFsrValue(byte fsr, byte value)
{
  Serial.write(CMD_FSRVALUE);
  Serial.write(fsr);
  Serial.write(value);
}

void receivedSerialCmdHello(byte data)
{
  connected = true;
  Serial.println("controller is ready..");
  setLedMode(LED_ON);
  playConnectMelody();
}

void receivedSerialCmdBye(byte data)
{
  connected = false;
  Serial.println("bye");
  sendSerialCmdBye(HANDSHAKE_ID);
  setLedMode(LED_BLINK);
  playDisconnectMelody();
}

void receivedSerialCmdServoSpeed(byte servo, byte speed)
{
  setServoSpeed(servo, speed);
}

void receivedSerialCmdSetServoPos(byte servo, byte positionp1, byte positionp2)
{
  int pos=(int)( ((positionp1&0xFF)<<7) | (positionp2&0x7F) );
  
  setServoPosition(servo, pos);
  
  /*switch (servo)
  {
    case SHOULDER_YAW_SERVO_NR:
      shoulderServoYawPos = pos;
      break;
    case SHOULDER_PITCH_SERVO_NR:
      shoulderServoPitchPos = pos;
      break;
    case ELBOW_PITCH_SERVO_NR:
      elbowServoPitchPos = pos;
      break;
    case WRIST_ROTATE_SERVO_NR:
      wristServoRotatePos = pos;
      break;
    case GRIPPER_LEFT_SERVO_NR:
      gripperServoLeftPos = pos;
      break;
    case GRIPPER_RIGHT_SERVO_NR:
      gripperServoRightPos = pos;
      break;
  }*/
}

void receivedSerialCmdMove(byte movehi, byte movelo)
{
  movedata1 = movehi;
  movedata2 = movelo;
}

void receivedSerialCmdRecord()
{
  /*if (record)
  {
    move1record[recordstep] = '\0';
    move2record[recordstep] = '\0';
    Serial.println("stopped record");
    record = false;
  }else{
    Serial.println("started record");
    recordstep = 0;
    record = true;
  }*/
}

void receivedSerialCmdReplay()
{
  /*if (replay)
  {
    Serial.println("start replay");
    replay = false;
  }else{
    Serial.println("stop replay");
    replay = true;
  }*/
}

void setLedMode(int mode)
{
  ledMode = mode;
  ledModeChanged = true;
}

void statusLed()
{
  if (ledMode == LED_BLINK)
  {
    unsigned long currentMillis = millis();
   
    if(currentMillis - previousMillis > interval)
    {
      // save the last time you blinked the LED
      previousMillis = currentMillis;  
  
      // if the LED is off turn it on and vice-versa:
      if (ledState == LOW)
        ledState = HIGH;
      else
        ledState = LOW;
  
      // set the LED with the ledState of the variable:
      digitalWrite(LEDPIN, ledState);
    }
  } else if (ledModeChanged && ledMode == LED_ON) {
    digitalWrite(LEDPIN, HIGH);
    ledModeChanged = false;
  } else if (ledModeChanged && ledMode == LED_OFF) {
    digitalWrite(LEDPIN, LOW);
    ledModeChanged = false;
  } 
}

void readAnalogInput()
{
  leftFSR = analogRead(FSRLEFTPIN);
  rightFSR = analogRead(FSRRIGHTPIN);
}

void sendFsrValues()
{
  if (leftFSR != tmpLeftFSR)
  {
    sendSerialCmdFsrValue(0, leftFSR/10);
    tmpLeftFSR = leftFSR;
  }
  if (rightFSR != tmpRightFSR)
  {
    sendSerialCmdFsrValue(1, rightFSR/10);
    tmpRightFSR = rightFSR;
  }
}

void setServoPositions()
{
  if (movedata1 & SHOULDER_YAW_BITMASK)
  {
    if (shoulderServoYawPos > SHOULDER_YAW_MIN) shoulderServoYawPos--;
  }
  if (movedata2 & SHOULDER_YAW_BITMASK)
  {
    if (shoulderServoYawPos < SHOULDER_YAW_MAX) shoulderServoYawPos++;
  }
  
  if (movedata1 & SHOULDER_PITCH_BITMASK)
  {
    if (shoulderServoPitchPos > SHOULDER_PITCH_MIN) shoulderServoPitchPos--;
  }
  if (movedata2 & SHOULDER_PITCH_BITMASK)
  {
    if (shoulderServoPitchPos < SHOULDER_PITCH_MAX) shoulderServoPitchPos++;
  }
  
  if (movedata1 & ELBOW_PITCH_BITMASK)
  {
    if (elbowServoPitchPos > ELBOW_PITCH_MIN) elbowServoPitchPos--;
  }
  if (movedata2 & ELBOW_PITCH_BITMASK)
  {
    if (elbowServoPitchPos < ELBOW_PITCH_MAX) elbowServoPitchPos++;
  }
  
  if (movedata1 & WRIST_ROTATE_BITMASK)
  {
    if (wristServoRotatePos < WRIST_ROTATE_MAX)
    {
      wristServoRotatePos++;
      /*if (millis() - tmpMillis >= 1000)
      {
        tmpMillis = millis();*/
        //sendSerialCmdServoPosition(WRIST_ROTATE_SERVO_NR, wristServoRotatePos);
      //}
    }
  }
  if (movedata2 & WRIST_ROTATE_BITMASK)
  {
    if (wristServoRotatePos > WRIST_ROTATE_MIN)
    {
      wristServoRotatePos--;
      /*if (millis() - tmpMillis >= 1000)
      {
        tmpMillis = millis();*/
        //sendSerialCmdServoPosition(WRIST_ROTATE_SERVO_NR, wristServoRotatePos);
      //}
    }
  }
  
  // Gripper Code
  if (movedata1 & GRIPPER_BITMASK)
  {
    if (gripperServoLeftPos < GRIPPER_LEFT_MAX)
    {
      gripperServoLeftPos++;
      //sendSerialCmdServoPosition(GRIPPER_LEFT_SERVO_NR, gripperServoLeftPos);
    }
    if (gripperServoRightPos > GRIPPER_RIGHT_MIN)
    {
      gripperServoRightPos--;
      //sendSerialCmdServoPosition(GRIPPER_RIGHT_SERVO_NR, gripperServoRightPos);
    }
  }
  if (movedata2 & GRIPPER_BITMASK)
  {
    if (leftFSR < 200 && rightFSR < 200)
    {
      if (gripperServoLeftPos > GRIPPER_LEFT_MIN)
      {
        gripperServoLeftPos--;
        //sendSerialCmdServoPosition(GRIPPER_LEFT_SERVO_NR, gripperServoLeftPos);
      }
      if (gripperServoRightPos < GRIPPER_RIGHT_MAX)
      {
        gripperServoRightPos++;
        //sendSerialCmdServoPosition(GRIPPER_RIGHT_SERVO_NR, gripperServoRightPos);
      }
    }
  }
  //movedata1 = 0x00;
  //movedata2 = 0x00;
}

void updateServos()
{
  if (shoulderServoYawPos != previousShoulderServoYawPos)
  {
    setServoPosition(SHOULDER_YAW_SERVO_NR, shoulderServoYawPos);
    previousShoulderServoYawPos = shoulderServoYawPos;
  }
  if (shoulderServoPitchPos != previousShoulderServoPitchPos)
  {
    setServoPosition(SHOULDER_PITCH_SERVO_NR, shoulderServoPitchPos);
    previousShoulderServoPitchPos = shoulderServoPitchPos;
  }
  if (elbowServoPitchPos != previousElbowServoPitchPos)
  {
    setServoPosition(ELBOW_PITCH_SERVO_NR, elbowServoPitchPos);
    previousElbowServoPitchPos = elbowServoPitchPos;
  }
  if (wristServoRotatePos != previousWristServoRotatePos)
  {
    setServoPosition(WRIST_ROTATE_SERVO_NR, wristServoRotatePos);
    previousWristServoRotatePos = wristServoRotatePos;
  }
  if (gripperServoLeftPos != previousGripperServoLeftPos)
  {
    setServoPosition(GRIPPER_LEFT_SERVO_NR, gripperServoLeftPos);
    previousGripperServoLeftPos = gripperServoLeftPos;
  }
  if (gripperServoRightPos != previousGripperServoRightPos)
  {
    setServoPosition(GRIPPER_RIGHT_SERVO_NR, gripperServoRightPos);
    previousGripperServoRightPos = gripperServoRightPos;
  }
}

void setServoPosition(int servo, int position)
{
  byte data1 = 0;
  byte data2 = 0;
 
  // Check to make sure the servo is right
  if (servo <0 || servo >8)
   return;
 
  // Check to make sure position is within bounds
  if (position < 0 || position > 5000)
    return;
 
  // Controller actually takes values between 500 and 5500,
  // so just add 500
  position+=500;
  // Calculate data bytes from position
  data2 = position & B01111111;
  data1 = position >> 7;
 
  // Start Byte
  pololu.write(0x80);
  // Device ID
  pololu.write(0x01);
  // Command: 0x04 is set absolute position
  pololu.write(0x04);
  // Servo number
  pololu.write((byte)servo);
  // First data byte
  pololu.write(data1);
  // Second data byte
  pololu.write(data2);
}

void setServoSpeed(int servo, byte speedVal)
{
   //this function uses pololu mode command 1 to set speed
   //servo is the servo number (typically 0-7)
   //speedVal is servo speed (1=slowest, 127=fastest, 0=full)
   //set speedVal to zero to turn off speed limiting

  // Check to make sure the servo is right
  if (servo <0 || servo >8)
   return;

   speedVal = speedVal & 0x7f; //take only lower 7 bits of the speed

   //Send a Pololu Protocol command
   pololu.write(0x80);     //start byte
   pololu.write(0x01);     //device id
   pololu.write(0x01);     //command number
   pololu.write((byte)servo);    //servo number
   pololu.write(speedVal); //speed
}

void playConnectMelody()
{
  tone(SPEAKERPIN, 210, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 310, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 420, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 500, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 630, 50);
  delay(100);
  noTone(SPEAKERPIN);
}

void playDisconnectMelody()
{
  tone(SPEAKERPIN, 630, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 500, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 420, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 310, 50);
  delay(100);
  noTone(SPEAKERPIN);
  tone(SPEAKERPIN, 210, 50);
  delay(100);
  noTone(SPEAKERPIN);
}
