/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

/**
 *
 * @author Vicente Martínez && Rafael Ruiz
 */
public class HeuristicaBasica {

    boolean init;
    int fuel;
    String direccion;
    
    public HeuristicaBasica()
    {
        this.init = true;
        
        this.direccion = "moveS";
  
    }
    
    public String EjecutarHeuristica(int[][] radar)
    {
        
        if(init)
        {
            fuel = 100;
            
            init = false;
            
            return "REFUEL";
            
            
        }else if(radar[2][2] == 2)
        {
            return "FOUND";
            
        }else if(radar[1][3] == 2)
        {
            
            return "moveNE";
            
        }else if(radar[3][3] == 2)
        {
            
            return "moveSE";
            
        }else if(radar[1][1] == 2)
        {
            
            return "moveNW";
            
        }else if(radar[3][1] == 2)
        {
            
            return "moveSW";
                        
        }else if(radar[1][2] == 1 && radar[1][3] == 1) //
        {
            
            return "moveE";
            
        }else if(direccion == "moveS" && radar[3][2] == 1)
        {
            direccion = "moveN";
            
            return "moveE";
            
        }else if(direccion == "moveN" && radar[1][2] == 1)
        {
            direccion = "moveS";
            
            return "moveE";
        }else
        {
            return direccion;
        }
        
    }
}
