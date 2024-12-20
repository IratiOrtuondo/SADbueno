/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MVC;

/**
 *
 * @author ortuu
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class EditableBufferedReader extends BufferedReader {
    // Constantes para teclas especiales
    private static final int ARROW_RIGHT = -102;
    private static final int ARROW_LEFT = -103;
    private static final int HOME = -104;
    private static final int END = -105;
    private static final int BACKSPACE = -106;
    private static final int DELETE = -107;
    private static final int INSERT = -108;
    

    public EditableBufferedReader(Reader in) {
        super(in);
    }

    public void setRaw() {
        try {
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "stty -echo raw </dev/tty"}).waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al establecer modo raw: " + e.getMessage());
        }
    }

    public void unsetRaw() {
        try {
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "stty echo cooked </dev/tty"}).waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error al regresar al modo normal: " + e.getMessage());
        }
    }

    @Override
    public String readLine() throws IOException {
        MultiLine model = new MultiLine();
        Console view = new Console();
        setRaw();
        try {
            
        int key;
        while ((key = read()) != 13) { // Mientras no se presione Enter
            switch (key) {
                case ARROW_RIGHT: // Flecha derecha
                    model.moveCursorRight();
                    break;

                case ARROW_LEFT: // Flecha izquierda
                    model.moveCursorLeft();
                    break;

                case HOME: // Home
                    model.moveCursorHome();
                    break;

                case END: // End
                    model.moveCursorEnd();
                    break;

                case BACKSPACE: // Backspace
                    model.deleteCharBeforeCursor();
                    break;

                case DELETE: // Delete
                    model.deleteCharAtCursor();
                    break;

                case INSERT: // Insert
                    model.toggleInsertMode();
                    break;


                default: // Cualquier otro carácter
                    if (model.isInsertMode()) {
                        model.insertChar((char) key);
                    } else {
                        model.overwriteChar((char) key);
                    }
                    break;
            }

            view.displayText(model.getText(), model.getCursorPos());
        }
        } finally {
            unsetRaw();
        }
        return model.getText(); // Devolver todas las líneas
    }

   
    public int read() throws IOException {
        int c = super.read();
        if (c == 27) { // Si se detecta ESC
            super.read(); // Lee y descarta '['
            int arrowKey = super.read();
            switch (arrowKey) {
                case 'C': return ARROW_RIGHT; // Flecha derecha
                case 'D': return ARROW_LEFT; // Flecha izquierda
                case 'H': return HOME; // Home
                case 'F': return END; // End
                case '2':
                    int tilde = super.read();
                    if (tilde == '~') return INSERT; // Insert
                    break;
                case '3':
                    int tilde2 = super.read();
                    if (tilde2 == '~') return DELETE; // Delete
                    break;
            }
        } else if (c == 127) {
            return BACKSPACE; // Backspace
        }
        return c; // Retorna el carácter normal si no es una tecla especial
    }
}
