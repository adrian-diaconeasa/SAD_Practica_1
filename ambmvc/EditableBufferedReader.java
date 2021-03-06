/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ambMVC;
import java.io.*;
/**
 *
 * @author oussama
 */
public class EditableBufferedReader extends BufferedReader {
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int HOME = 2;
    public static final int END = 3;
    public static final int INS = 4;
    public static final int DEL = 5;
    //Backspace no es una secuencia de escape sino de control, 127 en ASCII
    public static final int BKSP = 127;
  
    private Line line;
    private Console console;
    private InputStreamReader in;

    public EditableBufferedReader(Reader in) {
        super(in);
        line = new Line();
        console = new Console(line);
    }

    public void setRaw() throws IOException {
        String[] cmd = {"/bin/bash", "-c", "stty -echo raw </dev/tty"};
        Runtime.getRuntime().exec(cmd);
    }

    public void setCooked() throws IOException { //Con "stty cooked no funciona correctamente el terminal
        String[] cmd = {"/bin/bash", "-c", "stty sane </dev/tty"};
        Runtime.getRuntime().exec(cmd);
    }
    /*
    El método read() tiene que tratar de forma especial las siguientes 
    secuencias de escape:
    
    RightArrow --> CSI C
    LeftArrow --> CSI D
    Home --> SS3 H
    End --> SS3 F
    Ins --> CSI 2 ~
    Delete --> CSI 3 ~ 
    (BlankSpace --> 
    
     */
    @Override
    public int read() throws IOException {
        int key;

        switch (key = super.read()) { //El primer switch detecta las secuencias de escape
            case '\033': //Inicio de seq. de escape en octal
                super.read(); //"Ignoramos" el [

                switch (super.read()) {
                    case 'C':
                        return RIGHT;
                    case 'D':
                        return LEFT;
                    case 'H':
                        return HOME;
                    case 'F':
                        return END;
                    case '2':
                        super.read(); //"Ignoramos el ~
                        return INS;
                    case '3':
                        super.read();
                        return DEL;
                }

            default: //Para cualquier otro caso devolvemos directamente el valor leído
                return key;

        }
    }

    /*
    Ya que no estamos programando un editor multilínea podemos finalizar el
    bucle de lectura con el retorno de carro, CR.
     */
    @Override
    public String readLine() throws IOException {
        this.setRaw();

        int key;
        while ((key = this.read()) != 13) {
            console.output(key);
        }
        
        this.setCooked();
        return line.getLine();
    }

    


}
