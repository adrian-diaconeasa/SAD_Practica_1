/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ambMVC;

import static ambMVC.EditableBufferedReader.*;

/**
 *
 * @author oussama
 */
public class Console {
    private Line line;
    
    public Console(Line line){
        this.line = line;
    }
    
    public void output(int key){
        int toEnd;
        switch (key) {
                case RIGHT:
                    if (line.right()) {
                        System.out.print("\033[C");
                    }
                    break;
                case LEFT:
                    if (line.left()) {
                        System.out.print("\033[D");
                    }
                    break;
                case HOME:
                    System.out.print("\033[" + line.home() + "D"); //Movemos tantas veces a izda. como el resultado de line.home()
                    break;
                case END:
                    toEnd = line.end();
                    if(toEnd > 0){
                    System.out.print("\033[" + toEnd + "C");
                    }
                    break;
                case INS:
                    //Aqui no usamos secuencia de escape porque la insercción/sobreescritura se hace en la función setLine()
                    line.insert();
                    break;
                case DEL:
                    if (line.del()) {
                        System.out.print("\033[P");
                    }
                    break;
                case BKSP:
                    if (line.bksp()) {
                        System.out.print("\033[D"); //Movemos cursor a dcha.
                        System.out.print("\033[P"); //Insertamos espacio en blanco
                    }
                    break;
                default:
                    if (line.setLine((char) key)) { //Casteamos el int para convertirlo a char
                        System.out.print("\033[@");
                        System.out.print((char) key);
                    } else {
                        System.out.print((char) key);
                    }

            } 
    }
}
