import java.io.*;

public class EditableBufferedReader extends BufferedReader {

    InputStreamReader in;

    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int HOME = 2;
    public static final int END = 3;
    public static final int INS = 4;
    public static final int DEL = 5;
    public static final int BSKP = 127;

    public EditableBufferedReader(Reader reader) {
        super(reader);
    }

    public void setRaw() {
        try {
            String[] cmd = {"/bin/sh", "-c", "stty -echo raw </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();

        } catch (Exception e) {

        }
    }

    public void unsetRaw() {
        try {
            String[] cmd = {"/bin/sh", "-c", "stty echo cooked </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();

        } catch (Exception e) {

        }
    }
    /*
    	Cursor right: \033[C
        Cursor left:  \033[D
        Home:         \033[H
        END:          \033[F
        INS:          \033[2~
        Delete:       \033[3~
     */
    @Override
    public int read() throws IOException {
        int in;

        switch (in = super.read()) {
            case '\033':
                super.read();
                switch (in = super.read()) {
                    case 'C':
                        return RIGHT;
                    case 'D':
                        return LEFT;
                    case 'H':
                        return HOME;
                    case 'F':
                        return END;
                    case '2':
                        super.read();
                        return INS;
                    case '3':
                        super.read();
                        return DEL;
                    default:
                        return in;
                }
            default:
                return in;
        }
    }
