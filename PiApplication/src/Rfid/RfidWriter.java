package Rfid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This RfidWriter implements an interface between the python write file and java.
 * @author Sacredgamer
 */
public class RfidWriter {

    Logger log = Logger.getLogger(RfidWriter.class.getName());

    /**
     * Start RFID writer and wait till tag is in range.
     * 
     * @param newText Text to write to the tag.
     * @throws IOException If the Interface write file was not found or is not python.
     */
    public void write(String newText) throws IOException {
        evalueateText(newText);
        boolean tryAgain = true;
        log.info("Put the tag to the Reader.");
        while (tryAgain) {
            Interface.clear();

            Process p = Runtime.getRuntime().exec("python " + Interface.getWritePath());
            BufferedWriter processInput = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
            new Thread(new writer(processInput, newText)).start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            tryAgain = errorEvaluation(new Scanner(in));

            p.destroy();
        }
        log.info("Written!");

    }

    private void evalueateText(String newText) {
        if (newText.length() > 48) {
            log.warning("Only the first 48 Characters will be written to the Tag.");
        }
    }

    private boolean errorEvaluation(Scanner scanner) {
        while (scanner.hasNext()) {
            if (scanner.hasNext(".*Error.*") || scanner.hasNext(".*AUTH.*")) {
                log.warning("Error while writing try again...");
                return true;
            }
            scanner.next();
        }
        return false;
    }

    private class writer implements Runnable {

        private BufferedWriter processInput;
        private String newText;

        public writer(BufferedWriter processInput, String newText) {
            this.processInput = processInput;
            this.newText = newText;
        }

        @Override
        public void run() {
            try {
                processInput.write(newText + "\n");
                processInput.flush();
            } catch (IOException ex) {
                Logger.getLogger(RfidWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
