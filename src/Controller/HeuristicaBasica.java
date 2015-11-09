package Controller;

/**
 * 
 * Clase Heurística Básica
 * 
 * Encargada de establecer la acción elegida
 * 
 * @author Vicente Martínez and Rafael Ruiz
 * 
 */
public class HeuristicaBasica {

    private boolean init;
    private int fuel;
    private String direccion;
    
     /**
     * 
     * Constructor de la Clase HeuristicaBasica
     * 
     * @author Rafael Ruiz
     * 
     */
    
    public HeuristicaBasica()
    {
        this.init = true;
        
        this.direccion = "moveS";
  
    }
    
    
    /**
     * Función encargada de elegir la mejor acción básica posible.
     * 
     * @param radar Mapa del mundo a estudiar
     * @return 
     * @author Vicente Martínez
     * 
     */
    
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
