/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.IOException;

/**
 *
 * @author apran
 */
public class GUIConsole extends JFrame implements ChatIF {
    //class properties
   ChatClient client;
    //buttons
    private JButton closeB = new JButton("Close");
    private JButton openB = new JButton("Open");
    private JButton sendB = new JButton("Send");
    private JButton quitB = new JButton("Quit");
    //textfields
    private JTextField portTxF = new JTextField("5555");
    private JTextField hostTxF = new JTextField("127.0.0.1");
    private JTextField messageTxF = new JTextField("");
    //labels
    private JLabel portLB = new JLabel("Port: ", JLabel.RIGHT);
    private JLabel hostLB = new JLabel("Host: ", JLabel.RIGHT);
    private JLabel messageLB = new JLabel("Message: ", JLabel.RIGHT);
    
    //main chat area
    private JTextArea messageList = new JTextArea();
    
    
    //constructor
    public GUIConsole(String host, int port) 
    {
      
        super("Simple Chat GUI");
        setSize(300, 400);
        
        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        add("Center", messageList);
        add("South", bottom);
        
        //layout of the bottom jframe
        bottom.setLayout(new GridLayout(5, 2, 5, 5));
        bottom.add(hostLB); bottom.add(hostTxF);
        bottom.add(portLB); bottom.add(portTxF);
        bottom.add(messageLB); bottom.add(messageTxF);
        bottom.add(openB); bottom.add(sendB);
        bottom.add(closeB); bottom.add(quitB);
        
        // Action listener for sending
        sendB.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                send(messageTxF.getText()); //no need to re display
               // display(messageTxF.getText() + "\n");
            }
        });
        
          try {
            client = new ChatClient(host, port, this);
        }
          catch (IOException exception) 
          {
            System.out.println("Error: Can't setup connection!!!!"
                    + " Terminating client.");
            System.exit(1);
        }
        //make our window visible
        setVisible(true);
    }
    /**
     * This method overrides the method in the ChatIF interface. It displays a
     * message onto the screen.
     *
     * @param message The string to be displayed.
     */
     public void display(String message) {
        messageList.insert(message, 0);
    }
    
    public static void main(String[] args)
    {
        GUIConsole chat = new GUIConsole("localhost", 5555);
    }
    
    //gathers text from the messageTxF abd sends it to the 
    // server via client.handleMessageFromCLient
    public void send(String message)
    {
        //String message = messageTxF.getText()+"\n";
        client.handleMessageFromClientUI(message);
    }
}
