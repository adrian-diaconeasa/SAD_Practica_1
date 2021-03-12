package senseMVC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author adrian
 */
public class EditableBufferedReader extends BufferedReader {

    //Valores que devuelve read() cuando detecta secuencias de escape
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int HOME = 2;
    public static final int END = 3;
    public static final int INS = 4;
    public static final int DEL = 5;

    //Backspace no es una secuencia de escape sino de control, 127 en ASCII
    public static final int BKSP = 127;

    public EditableBufferedReader(Reader in) {
        super(in);
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
        int toEnd;
        int key;
        this.setRaw();
        Line line = new Line();

        while ((key = this.read()) != 13) { //CR es 13 en la tabla ASCII
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

        this.setCooked();
        return line.getLine();

    }

}
