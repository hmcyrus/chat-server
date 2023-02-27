
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable. It allows the implementation of the display
     * method in the client.
     */
    ChatIF clientUI;

    //Constructors ****************************************************
    /**
     * Constructs an instance of the chat client.
     *
     * @param host The server to connect to.
     * @param port The port number to connect on.
     * @param clientUI The interface type variable.
     */
    public ChatClient(String host, int port, ChatIF clientUI)
            throws IOException {
        super(host, port); //Call the superclass constructor
        this.clientUI = clientUI;
        openConnection();
    }

    //Instance methods ************************************************
    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        // if the message is a command
        if(msg instanceof Envelope)
        {
            Envelope env = (Envelope) msg;
            handleCommandFromServer(env);
        }
        else
        {
            clientUI.display(msg.toString());
        }   
    }
    
    
    // handles all envelope based commands from server
    public void handleCommandFromServer(Envelope env)
    {
        String id = env.getId();
        
        // return envelope of the client side #who command
        if(id.equals("who"))
        {
            // extract the list of user in room
            ArrayList<String> usersInRoom = (ArrayList<String>) env.getContents();
            
            // display who message header
            clientUI.display("User IDs in room " + env.getArgs());
            clientUI.display("===================================");
            
            // loop through and display all users in room
            for(String userId : usersInRoom)
            {
                clientUI.display(userId);
            }
        }

        if(id.equals("ftplist"))
        {
            // extract the list of file
            ArrayList<String> fileNames = (ArrayList<String>) env.getContents();

            clientUI.sendFileList(fileNames);
        }
    }
        
        

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {

        if (message.charAt(0) == '#') {

            handleClientCommand(message);

        } else {
            try {
                sendToServer(message);
            } catch (IOException e) {
                clientUI.display("Could not send message to server.  Terminating client.......");
                quit();
            }
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException e) {
        }
        System.exit(0);
    }

    public void connectionClosed() {

        System.out.println("Connection closed");

    }

    protected void connectionException(Exception exception) {

        System.out.println("Server has shut down");

    }

    public void handleClientCommand(String message) {

        if (message.equals("#quit")) {
            clientUI.display("Shutting Down Client");
            quit();

        }

        if (message.equals("#logoff")) {
            clientUI.display("Disconnecting from server");
            try {
                closeConnection();
            } catch (IOException e) {
            };

        }

        if (message.indexOf("#setHost") > 0) {

            if (isConnected()) {
                clientUI.display("Cannot change host while connected");
            } else {
                setHost(message.substring(8, message.length()));
            }

        }

        if (message.indexOf("#setPort") > 0) {
            // #setport 5556
            if (isConnected()) {
                clientUI.display("Cannot change port while connected");
            } else {
                setPort( Integer.parseInt(message.substring( 8, message.length() ).trim() ) );
            }

        }

        if (message.indexOf("#login") >= 0) {

            // #login name
            if (isConnected()) {
                clientUI.display("already connected");
            } else {

                try {
                    String userId = message.substring(6, message.length()).trim();
                    Envelope env = new Envelope("login", "", userId);
                    
                    openConnection();
                    
                    sendToServer(env);
                    
                } catch (IOException e) {
                    clientUI.display("failed to connect to server.");
                }
            }
        }
        
        if (message.indexOf("#join") >= 0) {

            // # join room1
            // user cannot join room when they are not connected to server
            if (!isConnected()) {
                clientUI.display("not connected. Could not join room");
            }
            // if they are connected to a server
            else 
            {
                
                try {
                    // try to parse a room name from the client command
                    String roomName = message.substring(5, message.length()).trim();
                    
                    // create an envelope with the id of join and the contents of the room name
                    Envelope env = new Envelope("join", "", roomName);
                    
                    sendToServer(env);
                    
                } catch (IOException e) {
                    clientUI.display("failed to join room.");
                }
            }
        }
        
        if (message.indexOf("#pm") >= 0) {

            // # pm USER this is my message
            
            // user cannot send pm when they are not connected to server
            if (!isConnected()) {
                clientUI.display("not connected. Could not send pm");
            }
            // if they are connected to a server
            else 
            {                
                try {
                    // parse out #pm
                    String targetAndMessage = message.substring(3, message.length()).trim();
                    
                    // get the fiest argument which is the target
                    String pmTarget = targetAndMessage.substring(0, targetAndMessage.indexOf(" ")).trim();
                    
                    // get the second argument which is the pm the client whishes to send
                    String pmContent = targetAndMessage
                            .substring(targetAndMessage.indexOf(" "), targetAndMessage.length())
                            .trim();
                    
                    // create an envelope with the id of join and the contents of the room name
                    Envelope env = new Envelope("pm", pmTarget, pmContent);
                    
                    sendToServer(env);
                    
                } catch (IOException e) {
                    clientUI.display("failed to send pm.");
                }
            }
        }
        
        
        
        if (message.indexOf("#who") >= 0) {

            // # who
            // if user isn't connected they aren't in a room with anyone
            if (!isConnected()) {
                clientUI.display("not connected. No valid room");
            }
            // if they are connected to a server
            else 
            {               
                try {
                   
                    // create an envelope with the id of who
                    Envelope env = new Envelope("who", "", "");
                    
                    sendToServer(env);
                    
                } catch (IOException e) {
                    clientUI.display("failed to find others in room.");
                }
            }
        }
        
        
        if (message.indexOf("#yell") >= 0) {

            // # yell Everyone shuld hear this message
            // user cannot send a message when not connected
            if (!isConnected()) {
                clientUI.display("not connected. No one heard your cries");
            }
            // if they are connected to a server
            else 
            {
                
                try {
                    // try to parse text of message from yell command
                    String yellMessage = message.substring(5, message.length()).trim();
                    
                    // create an envelope with the id of yell contents of the message to send to all clients
                    Envelope env = new Envelope("yell", "", yellMessage);
                    
                    sendToServer(env);
                    
                } catch (IOException e) {
                    clientUI.display("failed to yell.");
                }
            }
        }

        if (message.indexOf("#ftpUpload") >= 0) {
            String selectedFilePath = message.substring(10).trim();
            System.out.println("should send this file to server - " + selectedFilePath);

            Envelope env = null ;
            try {
                Path path = Paths.get(selectedFilePath);
                byte[] bytes = Files.readAllBytes(path);

                env = new Envelope("ftpUpload", path.getFileName().toString(), bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                sendToServer(env);
            }
            catch (IOException e){
                clientUI.display("could not send file to server");
            }
        }

        if (message.indexOf("#ftplist") >= 0) {
            System.out.println("get file list from server");
            Envelope env = new Envelope("ftplist", "", null);

            try{
                sendToServer(env);
            }
            catch (IOException e){
                clientUI.display("could not get file list from server");
            }
        }

    }

}
//End of ChatClient class
