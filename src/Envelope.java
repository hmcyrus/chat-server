
import java.io.Serializable;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author SWRB
 */

//POJO - Plain old java object
public class Envelope implements Serializable {

    // what command is this
    private String id;
       
    // are there any specifics that need to be established
    private String args;
    
    // the main data of the command
    private Object contents;

    // Constructor 
    public Envelope() {
    }
    
    public Envelope(String id, String args, Object contents) {
        this.id = id;
        this.args = args;
        this.contents = contents;
    }
    
    // getters and setters

    public String getId() {
        return id;
    }

    public String getArgs() {
        return args;
    }

    public Object getContents() {
        return contents;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public void setContents(Object contents) {
        this.contents = contents;
    }

   

    

}
