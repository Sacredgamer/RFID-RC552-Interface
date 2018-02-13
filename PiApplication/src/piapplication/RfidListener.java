package piapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * This RfidListener implements an interface between the python write and java.
 * @author Sacredgamer
 */
public class RfidListener {

    private long id;
    private final StringBuilder content;

    private Scanner scanner;
    private boolean tryAgain;
    private static final Logger log = Logger.getLogger(RfidListener.class.getName());

    public RfidListener() {
        content = new StringBuilder();
        id = 0;
        tryAgain = true;
    }
    /**
     * Start RFID reader and wait till tag is in range.
     * 
     * @throws IOException 
     * If the Interface read file was not found or is not python.
     */
    public synchronized void read() throws IOException {
        log.info("Reading...");
        while (tryAgain) {
            Interface.clear();
            tryAgain = false;

            Process p = Runtime.getRuntime().exec("python " + Interface.getReadPath());
            scanner = new Scanner(new BufferedReader(new InputStreamReader(p.getInputStream())));

            handleScanner();
            
            scanner.close();
            p.destroy();
        }
        log.info("Read!");
    }
    
    public long getId(){
        return id;
    }
    
    public String getContent(){
        return content.toString();
    }

    private void handleScanner() {
        while (scanner.hasNext() && !tryAgain) {
            if (errorEvaluation()) {
                abort();
            } else {
                readContent();
            }
        }
    }

    private boolean errorEvaluation() {
        if (scanner.hasNext(".*Error.*") || scanner.hasNext(".*AUTH.*")) {
            log.warning("Error while reading try again...");
            return true;
        }
        return false;
    }

    private void abort() {
        content.setLength(0);
        id = 0;
        tryAgain = true;
    }

    private void readContent() {
        if (id == 0) {
            id = Long.parseLong(scanner.next());
        } else {
            content.append(scanner.next());
        }
    }

}
