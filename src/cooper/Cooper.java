/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cooper;

import Controller.Controller;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import Listener.*;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 *
 * @author Alberto Meana
 */
public class Cooper {

    /**
     * @param args the command line arguments
     *
     * @author Alberto Meana
     */
    public static void main(String[] args) throws Exception {
        
        String nameListener = "Listener2";
        String nameController = "Controller2";
        
        AgentsConnection.connect("isg2.ugr.es", 6000, "Furud", "Canmaior", "Ishiguro", false);
        Listener listener = new Listener( new AgentID( nameListener ),nameListener,nameController );
        Controller controller = new Controller( new AgentID( nameController ),nameListener,nameController );
         
        listener.start();
        controller.start();
        
    }
    
}
