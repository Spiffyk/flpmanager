package cz.spiffyk.flpmanager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Reads an {@link InputStream} and discards everything. This is used as a workaround for hanging when nothing
 * reads the stdout.
 * @author spiffyk
 */
public class StreamEater extends Thread {
    InputStream is;

    public StreamEater(InputStream is) {
        this.is = is;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while ( br.readLine() != null) {}
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }
}
