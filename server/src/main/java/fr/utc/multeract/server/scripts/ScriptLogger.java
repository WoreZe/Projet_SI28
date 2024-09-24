package fr.utc.multeract.server.scripts;

import org.slf4j.Logger;

public class ScriptLogger {
    private final Logger logger;

    public ScriptLogger(Logger logger) {
        this.logger = logger;
    }

    public void log(Object message) {
        logger.info(message.toString());
    }

    public void error(Object message) {
        logger.error(message.toString());
    }

    public void warn(Object message) {
        logger.warn(message.toString());
    }

    public void debug(Object message) {
        logger.debug(message.toString());
    }

    public void trace(Object message) {
        logger.trace(message.toString());
    }

    public void info(Object message) {
        logger.info(message.toString());
    }
}

