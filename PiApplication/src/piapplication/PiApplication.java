package piapplication;

import Rfid.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class
 * @author Sacredgamer
 */
public class PiApplication {

    static final Logger log = Logger.getLogger(PiApplication.class.getName());

    /**
     * This is an example class to show the functionality of the RfidListener and RfidWriter
     * 
     */
    @SuppressWarnings({"LoggerStringConcat", "CallToPrintStackTrace"})
    public static void main(String[] args) {

        //change this to your read- and write python files
        Interface.init("/home/pi/MFRC522-python/Read.py", "/home/pi/MFRC522-python/Write.py");

        log.info("Reader test:");
        try {
            RfidListener reader = new RfidListener();
            reader.read();

            log.info("Id: " + reader.getId());
            log.info("Content: " + reader.getContent());
        } catch (IOException ioe) {
            Logger.getLogger(PiApplication.class.getName()).log(Level.SEVERE, null, ioe);
        }

        log.info("Writer test:");
        try {
            new RfidWriter().write("this_is_a_ test");
        } catch (IOException ex) {
            Logger.getLogger(PiApplication.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
