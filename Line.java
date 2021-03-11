/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package senseMVC;

import java.util.ArrayList;

/**
 *
 * @author adrian
 */
public class Line {

    private int pos; //Posición del cursor
    private boolean insert; //Modo insercción/sobreescritura
    private ArrayList<Character> line;

    /*
    Hemos elegido un ArrayList porque necesitamos una lista ordenada que permita
    duplicados y que pueda cambiar dinamicamente
     */
    public Line() {
        this.pos = 0;
        this.insert = true; //Empezamos en modo insercción (tecla insert no pulsada)
        this.line = new ArrayList<>();
    }

    public boolean setLine(char c) {

        if (insert) {  //En modo insercción
            line.add(pos, c);
            pos++;
            return true; //Para "controlar" la función readLine() de EditableBufferedReader
        } /*Si estamos al final de la línea procedemos como antes, para cualquier 
        otro caso usamos set() para sobreescribir el carácter*/ else { //En modo sobreescritura 

            if (pos >= line.size() - 1) {
                line.add(pos, c);
            } else {
                line.set(pos, c);
            }
            pos++;
            return false;
        }
    }

    public String getLine() { //Convertimos el ArrayList a String para mostrarlo
        String str = "";

        for (int i = 0; i < line.size(); i++) {
            str += line.get(i);
        }

        return str;
    }

    public boolean right() {
        if (pos < line.size()) {
            pos++;
            return true;
        } else {
            return false;
        }
    }

    public boolean left() {
        if (pos > 0) {
            pos--;
            return true;
        } else {
            return false;
        }
    }

    public int home() {
        int posActual = pos;
        this.pos = 0;
        return posActual; //Número de índices que nos tendremos que mover a la izda.
    }

    public int end() {
        int posActual = pos;
        this.pos = line.size();
        return (line.size() - posActual); //Mismo que home() pero a dcha.
    }

    public void insert() {
        this.insert = !insert;
    }

    public boolean del() {
        if (pos < line.size()) { //Comprobamos que el cursor no está al final de la línea
            line.remove(pos);
            return true;
        } else {
            return false;
        }
    }

    public boolean bksp() {
        if (pos > 0){
            line.remove(pos - 1); //Se borra el carácter a la izda.
            pos--; //Además en este caso el cursor también se mueve
            return true;
        } else {
            return false;
        }
    }
    
    
}
