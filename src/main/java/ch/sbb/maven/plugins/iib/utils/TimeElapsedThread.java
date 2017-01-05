/*
 * Copyright (C) Schweizerische Bundesbahnen SBB, 2017.
 */
package ch.sbb.maven.plugins.iib.utils;

import org.apache.maven.plugin.logging.Log;

/**
 *
 * 
 *
 * @author steve (user_vorname user_nachname)
 * @version $Id: $
 * @since pom_version, 2017
 */
public class TimeElapsedThread extends Thread {
    private long startTime = -1;
    private long sleepTime = 20000;
    Log log;

    public TimeElapsedThread(Log log) {
        this.log = log;
    }

    @Override
    public void run() {
        try {
            startTime = System.currentTimeMillis();

            while (true) {
                Thread.sleep(sleepTime);

                long timeElapsed = System.currentTimeMillis() - startTime;
                long minutes = timeElapsed / 60000;
                long seconds = (timeElapsed - (minutes * 60000)) / 1000;
                String message = "";
                if (minutes == 0) {
                    message += seconds + " seconds elapsed...";
                } else if (minutes == 1) {
                    if (seconds < 2) {
                        message += minutes + " minute elapsed...";
                    } else {
                        message += minutes + " minute and " + seconds + " seconds elapsed...";
                    }
                } else {
                    if (seconds < 2) {
                        message += minutes + " minutes elapsed...";
                    } else {
                        message += minutes + " minutes and " + seconds + " seconds elapsed...";
                    }
                }
                log.info(message);
            }
        } catch (Exception ie) {
            log.info("shutting down timer");
        }
    }
}