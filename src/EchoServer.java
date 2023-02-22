
import java.io.IOException;
import java.util.ArrayList;

public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    //Constructors ****************************************************
    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {

        super(port);

        try {
            this.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }

    }

    //Instance methods ************************************************
    /**
     * This method handles any messages received from the client.
     *
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received: " + msg + " from " + client);
        if(msg instanceof Envelope)
        {
            // handle command from client
            handleCommandFromClient((Envelope)msg, client);
        }
        else
        {
            // default handling of normal messages
            // this.sendToAllClients(msg);
            
            // send messages only to clients in the same room as the sender
            this.sendToAllClientsInRoom(msg, client);
        }       
    }
    
    // copy of sendToAllClients that only sends to clients in the same room
    public void sendToAllClientsInRoom(Object msg, ConnectionToClient sender) {
        Thread[] clientThreadList = getClientConnections();
        String userId = sender.getInfo("userId").toString();
        String room = sender.getInfo("room").toString();
        
        // loop through all clients
        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient currentClient = ((ConnectionToClient) clientThreadList[i]);
            String currentClientRoom = currentClient.getInfo("room").toString();
            String senderRoom = sender.getInfo("room").toString();
            
            // if client[i] has the same room as sender then send the message
            if( currentClientRoom.equals(senderRoom) )
            {    
                try {
                    currentClient.sendToClient(room + " >> " + userId + ": " + msg);
                } catch (Exception ex) {
                }
            }
        }
    }
    
    
    // copy of sendToAllClients that only sends to clients in the same room
    public void sendToAClient(Object msg, ConnectionToClient sender, String pmTarget) {
        Thread[] clientThreadList = getClientConnections();
        String userId = sender.getInfo("userId").toString();
        
        // loop through all clients
        for (int i = 0; i < clientThreadList.length; i++) {
            ConnectionToClient currentClient = ((ConnectionToClient) clientThreadList[i]);
            String currentClientUserId = currentClient.getInfo("userId").toString();
           
            // if client[i] has a username that matches the PMs target
            if( currentClientUserId.equals(pmTarget) )
            {    
                try {
                    currentClient.sendToClient("PM >> " + userId + ": " + msg);
                } catch (Exception ex) {
                }
            }
        }
    }
    
    
    public void handleCommandFromClient(Envelope env, ConnectionToClient client)
    {
        String id = env.getId();
        
        // login command contains userId contents
        if(id.equals("login"))
        {
            client.setInfo("userId", env.getContents().toString());
        }
        // join command contains room contents
        if(id.equals("join"))
        {
            client.setInfo("room", env.getContents().toString());
        }
        // send message only to target whose userID == env.args
        if(id.equals("pm"))
        {
            String message = env.getContents().toString();
            String target = env.getArgs();
            sendToAClient(message, client, target);
        }
        
        
        // send back an envelope with an ArrayList<String> containing all members 
        // of the room the sender is in 
        if (id.equals("who"))
        {
            // find the senders room
            String sendersRoom = client.getInfo("room").toString();
            
            // find all users in that room
            // put all those users into a ArrayList<String>
            ArrayList<String> usersInRoom = getUsersInRoom(sendersRoom);
            
            // build return envelope with id of who and contents of usersInRoom
            Envelope returnEnv = new Envelope("who", sendersRoom, usersInRoom);
            
            // send envelope with that ArrayList<String> back
            try
            {
                client.sendToClient(returnEnv);
            }
            catch(IOException ioe)
            {
            }
        }    
            // yell message to all users in all rooms
            if(id.equals("yell"))
            {
            String message = env.getContents().toString();
            sendToAllClients("YELL: >> " + message);
            }
        
    }
    
    /**
     * Finds all clients in a certain room and returns them in an ArrayList<String>
     * 
     */
    public ArrayList<String> getUsersInRoom(String nameOfRoom)
    {
        ArrayList<String> results = new ArrayList<String>();
        Thread[] clientThreadList = getClientConnections();        
        
        // loop through all client connections
        for (int i = 0; i < clientThreadList.length; i++) {
            
            // get current client's room
            ConnectionToClient currentClient = ((ConnectionToClient) clientThreadList[i]);
            String currentClientRoom = currentClient.getInfo("room").toString();
            
            // if that client is in nameOfRoom, add its userId to the results
            if(currentClientRoom.equals(nameOfRoom))
            {
                results.add(currentClient.getInfo("userId").toString());
            }
        }
        
        return results;   
    }

    
    
    /**
     * This method overrides the one in the superclass. Called when the server
     * starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass. Called when the server
     * stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    //Class methods ***************************************************
    /**
     * This method is responsible for the creation of the server instance (there
     * is no UI in this phase).
     *
     * @param args[0] The port number to listen on. Defaults to 5555 if no
     * argument is entered.
     */
    public static void main(String[] args) {
        int port = 0; //Port to listen on

        port = DEFAULT_PORT; //Set port to 5555

        // get the port number from the command line arguments
        try {
            port = Integer.parseInt(args[0]);
        } catch (IndexOutOfBoundsException ioobe) {
            port = DEFAULT_PORT;
        }

        try {
            port = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            port = DEFAULT_PORT;
        }

        EchoServer sv = new EchoServer(port);

        try {
            sv.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }

    }

    protected void clientConnected(ConnectionToClient client) {

        System.out.println("<Client Connected:" + client + ">");

    }

    // override clientException so that it only calls client disconnected on
    // and IOException
    synchronized protected void clientException(
            ConnectionToClient client, Throwable exception) {

        // connection between client and server interrupted
        if (exception instanceof IOException) {
            clientDisconnected(client);
        }

    }

    // Overrides hook to display disconnect message to console
    synchronized protected void clientDisconnected(
            ConnectionToClient client) {
        System.out.println("<Client Disconnected:" + client + ">");

    }

}
//End of EchoServer class