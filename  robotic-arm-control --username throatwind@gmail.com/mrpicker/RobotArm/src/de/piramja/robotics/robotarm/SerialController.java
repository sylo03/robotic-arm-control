package de.piramja.robotics.robotarm;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

public class SerialController implements SerialPortEventListener
{
	final static int TIMEOUT = 2000;
	
	//some ascii values
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;
	
	// serial protocol commands PC <-> Controller
    public final static byte CMD_HELLO = (byte)0x01; // controller or pc says hello
    public final static byte CMD_BYE = (byte)0x02; // controller or pc says bye
	
	// serial protocol commands PC -> Controller
    public final static byte CMD_SERVOSPEED = (byte)0x03; // pc sets servo speed
    public final static byte CMD_MOVE = (byte)0x04; // pc moves servos
    public final static byte CMD_SETSERVOPOS = (byte)0x05; // pc sets servo position
    public final static byte CMD_RECORD = (byte)0x06;
    public final static byte CMD_REPLAY = (byte)0x07;
	
	// serial protocol commands Controller -> PC
	final static byte CMD_SERVOPOSITION = (byte)0x08; // controller sends servo position
	final static byte CMD_FSRVALUE = (byte)0x09; // controller sends fsr (force sensitive resistor) value
	
	// id for handshake
	final static byte HANDSHAKE_ID = (byte)0xfe;
	
	final static int DATA_CMD = 0; // first byte for command
	final static int DATA_1 = 1; // second byte for data1
	final static int DATA_2 = 2; // third byte for data2
	final static int DATA_3 = 3; // fourth byte for data3
	
	private byte[] serialData = new byte[4]; // byte array to hold 4 bytes of data for each command (4 serial cycles a command)
	
	int byteCount = 0;
	
	private boolean connected = false;
	
	private SerialPort serialPort;
	private InputStream inputStream;
    private OutputStream outputStream;
    
    private HashMap<String, CommPortIdentifier> serialPorts;
	
	private RobotArmController controller; // application controller
	
	
	public SerialController(RobotArmController controller)
	{
		this.controller = controller;
		
		searchAvailablePorts();
	}
	
	public void searchAvailablePorts()
	{
		serialPorts = new HashMap<String, CommPortIdentifier>();
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		
		while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();
            
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                serialPorts.put(curPort.getName(), curPort);
            }
        }
	}
	
	public HashMap<String, CommPortIdentifier> getAvailableSerialPorts()
	{
		return serialPorts;
	}
	
	public void connect(CommPortIdentifier port)
	{
		if (isConnected())
			return;
		
		controller.info("connecting to controller..");
		
		try {
			CommPort commPort = port.open("RobotArmControl", TIMEOUT);
            serialPort = (SerialPort)commPort;
            
            serialPort.setSerialPortParams(
            	    200000,
            	    SerialPort.DATABITS_8,
            	    SerialPort.STOPBITS_1,
            	    SerialPort.PARITY_NONE);
            
            try {
                inputStream = serialPort.getInputStream();
                outputStream = serialPort.getOutputStream();
            } catch (IOException e) {
                controller.error("could not open I/O Streams:  " + e.toString());
                return;
            }
            
            try {
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            }
            catch (TooManyListenersException e)
            {
                controller.error("too many listeners on serial port. (" + e.toString() + ")");
            }
            
            setConnected(true);

            controller.info(port.getName() + " opened successfully..");
        } catch (PortInUseException e) {
            controller.error(port.getName() + " is already in use. (" + e.toString() + ")");
        } catch (Exception e) {
            controller.error("failed to open " + port.getName() + ". (" + e.toString() + ")");
        }
	}
	
	public void disconnect()
	{
		if (!isConnected())
			return;
		
		controller.info("disconnecting from controller..");
		
		try {
            serialPort.removeEventListener();
            serialPort.close();
            inputStream.close();
            outputStream.close();
            setConnected(false);

            controller.info("disconnected..");
        }
        catch (Exception e)
        {
            controller.error("failed to close " + serialPort.getName() + ". (" + e.toString() + ")");
        }
	}
	
	public void write(byte data)
	{
		if (!isConnected())
			return;
		
        try
        {
        	outputStream.write(data);
            outputStream.flush();
        }
        catch (Exception e)
        {
            controller.error("failed to write serial data. (" + e.toString() + ")");
        }
	}
	
	public void serialEvent(SerialPortEvent evt)
	{
		if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte data = (byte)inputStream.read();
                
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
                		case CMD_SERVOPOSITION: // received command servo position
                			serialData[DATA_CMD] = data;
                			byteCount++;
                			break;
                		case CMD_FSRVALUE: // received command fsr value
                			serialData[DATA_CMD] = data;
                			byteCount++;
                			break;
                		default:
                			byteCount = 0;
                			//controller.error("unknown serial command: " + (int)data);
                			if (data != NEW_LINE_ASCII)
                            {
                				controller.controllerInfo(new String(new byte[] {data}), false);
                            }else{
                                controller.controllerInfo("", true);
                            }
                			break;
                	}
                }else{
                	switch(serialData[DATA_CMD])
                	{
                		case CMD_HELLO: // receive data for command hello
                			serialData[byteCount] = data;
                			if (byteCount == DATA_1)
                			{
                				controller.receivedSerialCmdHello(serialData[DATA_1]);
                				byteCount = 0;
                				break;
                			}
                			byteCount++;
                			break;
                		case CMD_BYE: // receive data for command bye
                			serialData[byteCount] = data;
                			if (byteCount == DATA_1)
                			{
                				controller.receivedSerialCmdBye(serialData[DATA_1]);
                				byteCount = 0;
                				break;
                			}
                			byteCount++;
                			break;
                		case CMD_SERVOPOSITION: // receive data for command servo position
                			serialData[byteCount] = data;
                			if (byteCount == DATA_3)
                			{
                				controller.receivedSerialCmdServoPosition(serialData[DATA_1], serialData[DATA_2], serialData[DATA_3]);
                				byteCount = 0;
                				break;
                			}
                			byteCount++;
                			break;
                		case CMD_FSRVALUE: // receive data for command fsr value
                			serialData[byteCount] = data;
                			if (byteCount == DATA_2)
                			{
                				controller.receivedSerialCmdFsrValue(serialData[DATA_1], serialData[DATA_2]);
                				byteCount = 0;
                				break;
                			}
                			byteCount++;
                			break;
                	}
                }
                
                controller.view_populateForms();

                /*if (singleData != NEW_LINE_ASCII)
                {
                    window.controllerInfo(new String(new byte[] {singleData}), false);
                }
                else
                {
                    window.controllerInfo("");
                }*/
            }
            catch (Exception e)
            {
                controller.error("failed to read serial data. (" + e.toString() + ")");
            }
        }
	}

	public boolean isConnected()
	{
		return connected;
	}

	public void setConnected(boolean connected)
	{
		this.connected = connected;
	}
}
