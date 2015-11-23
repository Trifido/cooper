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

    private int battery;
    private double minValue;
    private Pair<Integer, Integer> gps;
    private int[][] radar;
    private double[][] scanner;
    private Integer[][] world;
    private double []minObstacle;
    private double []finalPoint;
    private String actionAnterior;
    private double minValueFind;
    private boolean upMVF;
    private boolean initHeu2;
    
    /**
     * Constructor de la clase
     * @author Vicente Martínez
     */
    public HeuristicaBasica(){
        minObstacle= new double[2];
        finalPoint= new double[2];
        this.gps = new Pair(0,0);
        this.radar = new int[5][5];
        this.scanner = new double[5][5];
        this.world = new Integer[500][500];
        this.minValueFind= Double.POSITIVE_INFINITY;
        this.upMVF= true;
        this.initHeu2= true;
        actionAnterior="";
        
        for(int i=0; i<500; i++)
            for(int j=0; j<500; j++)
                this.world[i][j]= 1;
    }
    
    /**
     * @param bat Entero que representa el nivel de batería.
     * @param gps Objeto Pair que representa las coordenadas del bot.
     * @param radar Matriz de enteros con la información del radar.
     * @param scanner Matriz de double con la informacion del scanner.
     * @author Vicente Martínez
     */
    public void actualizarSensores(int bat, Pair<Integer, Integer> gps, int[][] radar, double[][] scanner){
        this.battery= bat;
        this.gps= gps;
        this.radar= radar;
        this.scanner= scanner;
    }
    
    
    
        /**
     * Función encargada de obtener el beneficio de cada casilla.
     * 
     * @param i Coordenada x de la casilla a la que se va a mover.
     * @param j Coordenada y de la casilla a la que se va a mover.
     * @return double Devuelve el producto de: nºvisitas x scanner[x][y]
     * @author Vicente Martínez
     */
    public double getBenefit(int i, int j){
        if (i == 1 && j == 1)
            return this.scanner[i][j] * this.world[gps.first - 1][gps.second - 1];
        else if (i == 1 && j == 2)
            return this.scanner[i][j] * this.world[gps.first][gps.second - 1];
        else if (i == 1 && j == 3) 
            return this.scanner[i][j] * this.world[gps.first + 1][gps.second - 1];
        else if (i == 2 && j == 1)
            return this.scanner[i][j] * this.world[gps.first - 1][gps.second];
        else if (i == 2 && j == 3)
            return this.scanner[i][j] * this.world[gps.first + 1][gps.second];
        else if (i == 3 && j == 1)
            return this.scanner[i][j] * this.world[gps.first - 1][gps.second + 1];
        else if (i == 3 && j == 2)
            return this.scanner[i][j] * this.world[gps.first][gps.second + 1];
        else
            return this.scanner[i][j] * this.world[gps.first + 1][gps.second + 1];
    }
    
    /**
     * Función encargada de establecer la acción elegida y guardar las visitas
     * realizadas en el mapa "world".
     * 
     * @param npos Es un pair con con las coordenadas de la casilla elegida.
     * @return String Devuelve la accion elegida.
     * @author Vicente Martínez
     */
    public String nextAction(Pair<Integer, Integer> npos){
        
        String act;
        
        if (npos.first == 1 && npos.second == 1){
            act = "moveNW";
            this.world[this.gps.first - 1][this.gps.second - 1]++;
        }
        else if (npos.first == 1 && npos.second == 2){
            act = "moveN";
            this.world[this.gps.first][this.gps.second - 1]++;
        }
        else if (npos.first == 1 && npos.second == 3){
            act = "moveNE";
            this.world[this.gps.first + 1][this.gps.second - 1]++;
        }
        else if (npos.first == 2 && npos.second == 1){
            act = "moveW";
            this.world[this.gps.first - 1][this.gps.second]++;
        }
        else if (npos.first == 2 && npos.second == 3){
            act = "moveE";
            this.world[this.gps.first + 1][this.gps.second]++;
        }
        else if (npos.first == 3 && npos.second == 1){
            act = "moveSW";
            this.world[this.gps.first - 1][this.gps.second + 1]++;
        }
        else if (npos.first == 3 && npos.second == 2){
            act = "moveS";
            this.world[this.gps.first][this.gps.second + 1]++;
        }
        else{
            act = "moveSE";
            this.world[this.gps.first + 1][this.gps.second + 1]++;
        }
        
        return act;
    }
    
    /**
     * Funcion encargada guardar las visitas realizadas en el mapa "world" para
     * la heuristica 2.
     * @param act Accion elegida.
     * @author Vicente Martínez
     */
    public void changeWorld(String act){            
        if(act == "moveNW")
            this.world[this.gps.first - 1][this.gps.second - 1]++;
        else if (act == "moveN")
            this.world[this.gps.first][this.gps.second - 1]++;
        else if (act == "moveNE")
            this.world[this.gps.first + 1][this.gps.second - 1]++;
        else if ( act == "moveW")
            this.world[this.gps.first - 1][this.gps.second]++;
        else if (act == "moveE")
            this.world[this.gps.first + 1][this.gps.second]++;
        else if (act == "moveSW")
            this.world[this.gps.first - 1][this.gps.second + 1]++;
        else if ( act == "moveS")
            this.world[this.gps.first][this.gps.second + 1]++;
        else
            this.world[this.gps.first + 1][this.gps.second + 1]++;
    }
    
    /**
     * Funcion encargada de determinar si el bot se encuentra completamente rodeado.
     * @author Vicente Martínez
     * @return boolean indica si el bot se encuentra completamente rodeado por
     * obstaculos
     */
    public boolean ahogado(){   
        for(int i=1; i<4; i++)
            for(int j=1; j<4; j++)
                if(radar[i][j] != 1){
                    return false;
                }
        return true;
    }
    
    
    /**
     * Funciones encargadas de comprobar si es posible moverse a una dirección. 
     * @author Vicente Martínez
     * @return Presenta o no obstaculo.
     */
    public boolean isMovS(){
        return radar[3][2]!=1;
    }
    public boolean isMovSW(){
        return radar[3][1]!=1;
    }
    public boolean isMovSE(){
        return radar[3][3]!=1;
    }
    public boolean isMovE(){
        return radar[2][3]!=1;
    }
    public boolean isMovNE(){
        return radar[1][3]!=1;
    }
    public boolean isMovN(){
        return radar[1][2]!=1;
    }
    public boolean isMovNW(){
        return radar[1][1]!=1; 
    }
    public boolean isMovW(){
        return radar[2][1]!=1;
    }
    
    /**
     * Funcion encargada de comprobar que el mapa no tiene solución, para ello
     * se comprueba si se ha rodeado completamente al objetivo y se ha llegado a finalPoint
     * @param act Accion elegida para moverse.
     * @return boolean true en el caso de no existir solución, false en caso contrario.
     * @author Vicente Martínez
     */
    public boolean sinSolucion(String act){
                
        if(act == "moveNW")
            return ((this.gps.first - 1)==finalPoint[0] && (this.gps.second - 1)==finalPoint[1]);
        else if (act == "moveN")
            return ((this.gps.first)==finalPoint[0] && (this.gps.second - 1)==finalPoint[1]);
        else if (act == "moveNE")
            return ((this.gps.first + 1)==finalPoint[0] && (this.gps.second - 1)==finalPoint[1]);
        else if ( act == "moveW")
            return ((this.gps.first - 1)==finalPoint[0] && (this.gps.second)==finalPoint[1]);
        else if (act == "moveE")
            return ((this.gps.first + 1)==finalPoint[0] && (this.gps.second)==finalPoint[1]);
        else if (act == "moveSW")
            return ((this.gps.first - 1)==finalPoint[0] && (this.gps.second + 1)==finalPoint[1]);
        else if ( act == "moveS")
            return ((this.gps.first)==finalPoint[0] && (this.gps.second + 1)==finalPoint[1]);
        else
            return ((this.gps.first + 1)==finalPoint[0] && (this.gps.second + 1)==finalPoint[1]);
    }
        
    /**
     * Funcion encargada de comprobar si se inicia heuristica 2 o 1, usará heuristica1
     * si la distancia minima es mayor a una casilla vacía adyacente, usará heuristica2
     * si la distancia minima es menor al resto de casillas vacias adyacentes.
     * 
     * @return boolean true Heuristica2, false Heuristica1 
     * @author Vicente Martínez
     */
    public boolean initHeuristic2(){
        double minObst= Double.POSITIVE_INFINITY;
        double minVoid= Double.POSITIVE_INFINITY;
        
        for(int i=1; i<4; i++){
            for(int j=1; j<4; j++){
                if((i!=2 || j!=2) && (radar[i][j] == 1)){
                    if(scanner[i][j]<minObst)
                        minObst= scanner[i][j];
                }
                else if((i!=2 || j!=2) && (radar[i][j] != 1)){
                    if(scanner[i][j]<minVoid)
                        minVoid= scanner[i][j];
                }
            }
        }
        
        if(minObst<minVoid && minVoid != Double.POSITIVE_INFINITY){
            if(this.minValueFind>scanner[2][2]){
                this.minValueFind= scanner[2][2];
            }
        }
        
        if(minObst==Double.POSITIVE_INFINITY || this.minValueFind>minVoid)
            return false;
        else if(this.minValueFind<minVoid)
            return true;
        else
            return true;

    }
    
    /**
     * Funcion heuristica2 algortimo basado en el algoritmo de la mano derecha, es usada
     * cuando initHeuristic2() es true, dependiendo de las casillas adyacentes 
     * el bot se moverá a una u otra direccion.
     * 
     * @return String Con la acción elegida.
     * @author Vicente Martínez
     */
    public String heuristic2(){
        String act= new String();
        boolean accionElegida=false;
        
        if(!isMovE() && !isMovW() && !accionElegida){
            if(actionAnterior=="moveSE" || actionAnterior=="moveS" || actionAnterior=="moveSW"){
               if(isMovSW()){
                  act= "moveSW";
                  accionElegida=true;
               }
               else if(isMovS()){
                  act= "moveS";
                  accionElegida=true;
               }
            }
            else if(actionAnterior=="moveNE" || actionAnterior=="moveN" || actionAnterior=="moveNW"){
                if(isMovNE()){
                  act= "moveNE";
                  accionElegida=true;
               }
               else if(isMovN()){
                  act= "moveN";
                  accionElegida=true;
               }
            }
        }
        
        if(!isMovN() && !isMovS() && !accionElegida){ 
            if(actionAnterior=="moveSE" || actionAnterior=="moveE" || actionAnterior=="moveNE"){
               if(isMovSE()){
                  act= "moveSE";
                  accionElegida=true;
               }
               else if(isMovE()){
                  act= "moveE";
                  accionElegida=true;
               }
            }
            else if(actionAnterior=="moveSW" || actionAnterior=="moveW" || actionAnterior=="moveNW"){
                if(isMovSW()){
                  act= "moveSW";
                  accionElegida=true;
               }
               else if(isMovW()){
                  act= "moveW";
                  accionElegida=true;
               }
            }
        }
        
        if(!isMovE() && !accionElegida){
            if(isMovNE()){
                act= "moveNE";
                accionElegida=true;
            }
            else if(isMovN()){
                act= "moveN";
                accionElegida=true;
            }
        }
        if(!isMovN() && !accionElegida){
            if(isMovNW()){
                act= "moveNW";
                accionElegida=true;
            }
            else if(isMovW()){
                act= "moveW";
                accionElegida=true;
            }
        }
        if(!isMovW() && !accionElegida){
            if(isMovSW()){
                act= "moveSW";
                accionElegida=true;
            }
            else if(isMovS()){
                act= "moveS";
                accionElegida=true;
            }
        }
        if(!isMovS() && !accionElegida){
            if(isMovSE()){
                act= "moveSE";
                accionElegida=true;
            }
            else if(isMovE()){
                act= "moveE";
                accionElegida=true;
            }
        }
        if(!isMovSE() && !accionElegida){
            if(isMovE()){
                act= "moveE";
                accionElegida=true;
            }
            else if(isMovNE()){
                act= "moveNE";
                accionElegida=true;
            }
        }
        if(!isMovSW() && !accionElegida){
            if(isMovS()){
                act= "moveS";
                accionElegida=true;
            }
            else if(isMovSE()){
                act= "moveSE";
                accionElegida=true;
            }
        }
        if(!isMovNW() && !accionElegida){
            if(isMovW()){
                act= "moveW";
                accionElegida=true;
            }
            else if(isMovSW()){
                act= "moveSW";
                accionElegida=true;
            }
        }
        if(!accionElegida){
            if(isMovN())
                act= "moveN";
            else if(isMovNW())
                act= "moveNW";
        }
        
        if(sinSolucion(act)){
            System.out.println("-------------------------- No tiene solución. --------------------------");
            return "found";
        }
        
        changeWorld(act);
        return act;
    }
    
    /**
     * Funcion encargada de realizar la heuristica 1, es un algoritmo Greedy que 
     * analiza el nºvisitas y la distancia gps para decidir la accion.
     * 
     * @return String con la accion elegida.
     * @author Vicente Martínez
     */
    public String heuristic1(){
        Pair<Integer, Integer> newpos = new Pair(2,2);
        this.minValue= Double.POSITIVE_INFINITY;
        double benefit;
        finalPoint[0]= 0;
        finalPoint[1]= 0;

        for(int i=1; i<4; i++){
            for(int j=1; j<4; j++){
                if((i!=2 || j!=2) && this.radar[i][j] != 1){ 
                    benefit= getBenefit(i, j);
                    if(this.minValue > benefit){
                        this.minValue= benefit;
                        newpos.first= i;
                        newpos.second= j;
                    }
                }
                else if (i!=2 || j!=2){
                    this.world[i][j]= -1;
                }
            }
        }
        actionAnterior= nextAction(newpos);
        return actionAnterior;
    }
    
    /**
     * Funcion llamada por el agente Controller que le permitirá determinar
     * la accion a realizar.
     * 
     * @return String con la accion elegida.
     * @author Vicente Martínez
     */
    public String heuristic(){
        this.minValue= Double.POSITIVE_INFINITY;
        
        if(this.battery < 2){
            System.out.println("CONTROLLER: REFUEL");
            return "refuel";
        }
        //Si el bot está sobre la casilla 2 (objetivo), fin
        if(this.radar[2][2] == 2){
            System.out.println("CONTROLLER: FOUND");
            return "found";
        }
        else if(ahogado()){
            System.out.println("AHOGADO");
            return "found";
        }
        else if(initHeuristic2()){
            System.out.println("HEURISTICA 2");
            
            if(initHeu2){
                finalPoint[0]= gps.first;
                finalPoint[1]= gps.second;
                initHeu2= false;
            }
            actionAnterior= heuristic2();
            return actionAnterior;
        }
        else{
            initHeu2=true;
            return heuristic1();
        }
    }
}
