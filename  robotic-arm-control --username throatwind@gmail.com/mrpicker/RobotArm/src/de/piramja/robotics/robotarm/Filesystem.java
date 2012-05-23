package de.piramja.robotics.robotarm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Filesystem
{
	public Filesystem()
	{
		
	}
	
	public static File getSettingsDirectory()
	{
	    String userHome = System.getProperty("user.home");
	    if(userHome == null)
	    {
	        throw new IllegalStateException("user.home==null");
	    }
	    File home = new File(userHome);
	    File settingsDirectory = new File(home, ".mrpicker");
	    if(!settingsDirectory.exists())
	    {
	        if(!settingsDirectory.mkdir())
	        {
	            throw new IllegalStateException(settingsDirectory.toString());
	        }
	    }
	    return settingsDirectory;
	}
	
	public static List<String> getFileList()
	{
		ArrayList<String> savedFiles = new ArrayList<String>();
		File dir = getSettingsDirectory();
		String[] children = dir.list();
		
		if (children != null)
		{
			for (int i=0; i<children.length; i++)
			{
				savedFiles.add(children[i]);
		    }
		}
		
		return savedFiles;
	}
	
	public static Movement loadMovement(String filename) throws IOException
	{
		File file = new File(getSettingsDirectory() + System.getProperty("file.separator") + filename);
		 
		FileInputStream fis = new FileInputStream(file);
		//System.out.println(file.exists() + "!!");
		//InputStream in = resource.openStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		for (int readNum; (readNum = fis.read(buf)) != -1;)
		{
			bos.write(buf, 0, readNum);
		}
		byte[] bytes = bos.toByteArray();
		
		ArrayList<Byte> move1 = new ArrayList<Byte>();
		ArrayList<Byte> move2 = new ArrayList<Byte>();
		
		byte lastByte1 = 0;
		byte lastByte2 = 0;
		for (int i=0; i < bytes.length/2; i++)
		{
			if (bytes[i] == 0 && bytes[i+(bytes.length/2)] == 0 && lastByte1 == 0 && lastByte2 == 0)
			{
				
			}else{
				move1.add(bytes[i]);
				move2.add(bytes[i+(bytes.length/2)]);
			}
			lastByte1 = bytes[i];
			lastByte2 = bytes[i+(bytes.length/2)];
		}
		
		return new Movement(move1, move2);
	}
	
	public static void saveMovement(String filename, Movement movement) throws FileNotFoundException, IOException
	{
		File file = new File(getSettingsDirectory() + System.getProperty("file.separator") + filename);
		FileOutputStream fos = new FileOutputStream(file);
		
		byte[] mov = new byte[movement.getMovement1().size()*2];
		for ( int i=0; i<movement.getMovement1().size(); i++) mov[i]=movement.getMovement1().get(i);
		for ( int i=0; i<movement.getMovement2().size(); i++) mov[i+movement.getMovement1().size()]=movement.getMovement2().get(i);
		
		fos.write(mov);
		fos.flush();
		fos.close();
	}
}
