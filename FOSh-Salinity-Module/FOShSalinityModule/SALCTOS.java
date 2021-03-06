import java.io.*;
import java.awt.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import javax.swing.*;

public class SALCTOS
                implements Runnable
{
  Talker  talker;
  PrimaryFrame sf;
  String  msg;// Must be placed here to pass to chat client, other wise must be effectivly final.
  String[] splitString;// see above.
  String id;
//====================================================================================================================
  public SALCTOS(String serverName,int port,String id, PrimaryFrame sf)
  {   try
      {
        this.id = id;
        talker  = new Talker(serverName,port,id);
        this.sf = sf;
        new Thread(this).start();
      }
      catch(IOException ioe)
      {
         System.out.println("Error connecting to OS, OS module might not be running!");
         System.out.println("");
         JOptionPane.showMessageDialog(null, "Error connecting to OS!", "Error connecting to OS please close and try again!", JOptionPane.ERROR_MESSAGE);
         System.exit(1);
         ioe.printStackTrace();
      }
  }//end of constructor
//====================================================================================================================
  public synchronized  void sendMessage(String msgToSend)
  {
    try
      {
        talker.send(msgToSend);
      }
      catch(IOException ioe)
      {
        System.out.println("Error sending message  to this server, connection might have been lost. ");
        ioe.printStackTrace();
      }
  }
//====================================================================================================================
  public void run()
  {
    boolean connected = true;
    try
      {
        while(connected)
        {
           msg = talker.recieve();
           if(msg.startsWith("+WHORU"))
           {
             this.sendMessage("+IAMA SALINITY_MOD");
           }
           else if(msg.startsWith("+CONNECTED"))
           {
            sf.connected();
           }
           else if(msg.startsWith("+MINMAX"))
           {
              splitString = msg.toString().split(" ");
              System.out.println("splitString[1]: "+splitString[1]);
              System.out.println("splitString[1]: "+splitString[2]);
              sf.setMinMax(splitString[1],splitString[2]);
           }
        }
        talker.close();// CLOSES CONNECTION TO TALKER IF  NOT CONNECTED!!
      }
      catch(IOException ioe)
      {
         connected = false;
         System.out.println("Error connecting to OS from the TEMPCTOS of this MOD, the connection was established but might have timed out!");
         JOptionPane.showMessageDialog(null, "Connection timed out", "Connection to the OS module has been lost, the program will now close. Please relaunch to start again!", JOptionPane.ERROR_MESSAGE);
         System.out.println("");
         ioe.printStackTrace();
         System.exit(1);
      }
  }
//====================================================================================================================
}
