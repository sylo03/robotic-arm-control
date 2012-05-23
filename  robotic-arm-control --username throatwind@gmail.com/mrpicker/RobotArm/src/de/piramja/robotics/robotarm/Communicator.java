package de.piramja.robotics.robotarm;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

import de.piramja.robotics.robotarm.gui.RobotArmGui;

public class Communicator implements SerialPortEventListener
{
    //passed from main GUI
    RobotArmGui window = null;

    //for containing the ports that will be found
    private Enumeration ports = null;
    //map the port names to CommPortIdentifiers
    private HashMap portMap = new HashMap();

    //this is the object that contains the opened port
    private CommPortIdentifier selectedPortIdentifier = null;
    private SerialPort serialPort = null;

    //input and output streams for sending and receiving data
    private InputStream input = null;
    private OutputStream output = null;

    //just a boolean flag that i use for enabling
    //and disabling buttons depending on whether the program
    //is connected to a serial port or not
    private boolean bConnected = false;

    //the timeout value for connecting with the port
    final static int TIMEOUT = 2000;

    //some ascii values for for certain things
    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    //a string for recording what goes on in the program
    //this string is written to the GUI
    String logText = "";

    public Communicator(RobotArmGui window)
    {
        this.window = window;
    }

    //search for all the serial ports
    //pre: none
    //post: adds all the found ports to a combo box on the GUI
    public void searchForPorts()
    {
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements())
        {
            CommPortIdentifier curPort = (CommPortIdentifier)ports.nextElement();

            //get only serial ports
            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                window.getComboBoxPort().addItem(curPort.getName());
                window.getComboBoxPort().setSelectedItem(curPort.getName());
                portMap.put(curPort.getName(), curPort);
            }
        }
    }

    //connect to the selected port in the combo box
    //pre: ports are already found by using the searchForPorts method
    //post: the connected comm port is stored in commPort, otherwise,
    //an exception is generated
    public void connect()
    {
        String selectedPort = (String)window.getComboBoxPort().getSelectedItem();
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);

        CommPort commPort = null;

        try
        {
            //the method below returns an object of type CommPort
            commPort = selectedPortIdentifier.open("TigerControlPanel", TIMEOUT);
            //the CommPort object can be casted to a SerialPort object
            serialPort = (SerialPort)commPort;
            
            if (initIOStream())
            {
            	initListener();
            }
            //for controlling GUI elements
            setConnected(true);

            //logging
            window.info(selectedPort + " opened successfully..");

            //CODE ON SETTING BAUD RATE ETC OMITTED
            //XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

            //enables the controls on the GUI if a successful connection is made
            window.toggleConnectedButtons();
        }
        catch (PortInUseException e)
        {
            window.error(selectedPort + " is in use. (" + e.toString() + ")");
        }
        catch (Exception e)
        {
            window.error("failed to open " + selectedPort + "(" + e.toString() + ")");
        }
    }

    //open the input and output streams
    //pre: an open port
    //post: initialized intput and output streams for use to communicate data
    public boolean initIOStream()
    {
        //return value for whather opening the streams is successful or not
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
            
            successful = true;
            return successful;
        }
        catch (IOException e) {
            window.error("I/O streams failed to open. (" + e.toString() + ")");
            return successful;
        }
    }

    //starts the event listener that knows whenever data is available to be read
    //pre: an open serial port
    //post: an event listener for the serial port that knows when data is recieved
    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            window.error("too many listeners. (" + e.toString() + ")");
        }
    }

    //disconnect the serial port
    //pre: an open serial port
    //post: clsoed serial port
    public void disconnect()
    {
        //close the serial port
        try
        {
            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();
            setConnected(false);
            window.toggleConnectedButtons();

            window.info("disconnected..");
        }
        catch (Exception e)
        {
            window.error("failed to close " + serialPort.getName() + "(" + e.toString() + ")");
        }
    }
    
    public void sendHello()
    {
    	writeData((byte)255, (byte)255); // send hello
    }
    
    public void sendBye()
    {
    	writeData((byte)0, (byte)255);
    }

    final public boolean isConnected()
    {
        return bConnected;
    }

    public void setConnected(boolean bConnected)
    {
        this.bConnected = bConnected;
    }

    //what happens when data is received
    //pre: serial event is triggered
    //post: processing on the data it reads
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte singleData = (byte)input.read();

                if (singleData != NEW_LINE_ASCII)
                {
                    window.controllerInfo(new String(new byte[] {singleData}), false);
                }
                else
                {
                    window.controllerInfo("");
                }
            }
            catch (Exception e)
            {
                window.error("failed to read data. (" + e.toString() + ")");
            }
        }
    }

    //method that can be called to send data
    //pre: open serial port
    //post: data sent to the other device
    public void writeData(byte data1, byte data2)
    {
    	if (!isConnected()) return;
        try
        {
        	output.write(data1);
            output.flush();
            output.write(data2);
            output.flush();
        }
        catch (Exception e)
        {
        	System.out.println(e.toString());
            window.error("failed to write data. (" + e.toString() + ")");
        }
    }
}