package cz.spiffyk.flpmanager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamEater extends Thread {
    InputStream is;

    // reads everything from is until empty. 
    public StreamEater(InputStream is) {
        this.is = is;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line=null;
            while ( (line = br.readLine()) != null) {}
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }
}
