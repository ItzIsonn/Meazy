package me.itzisonn_.meazy;

import me.itzisonn_.meazy.lang.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory2;

import java.lang.reflect.Field;

public class MeazyLogger extends Logger {
    private MeazyLogger(Logger logger) {
        super(getContext(logger), logger.getName(), getMessageFactory(logger));
    }

    public MeazyLogger(String id) {
        this((Logger) LogManager.getLogger(id));
    }

    public MeazyLogger() {
        this("meazy");
    }



    public void log(Level level, Text text) {
        super.log(level, text.toString());
    }



    @Override
    @Deprecated(forRemoval = true)
    public void log(Level level, String message) {
        super.log(level, message);
    }

    @Override
    @Deprecated(forRemoval = true)
    public void log(Level level, String message, Object p1) {
        super.log(level, message, p1);
    }

    @Override
    @Deprecated(forRemoval = true)
    public void log(Level level, String message, Object p1, Object p2) {
        super.log(level, message, p1, p2);
    }

    @Override
    @Deprecated(forRemoval = true)
    public void log(Level level, String message, Object p1, Object p2, Object p3) {
        super.log(level, message, p1, p2, p3);
    }

    @Override
    @Deprecated(forRemoval = true)
    public void log(Level level, String message, Object p1, Object p2, Object p3, Object p4) {
        super.log(level, message, p1, p2, p3, p4);
    }

    @Override
    @Deprecated(forRemoval = true)
    public void log(Level level, String message, Object p1, Object p2, Object p3, Object p4, Object p5) {
        super.log(level, message, p1, p2, p3, p4, p5);
    }

    @Override
    @Deprecated(forRemoval = true)
    public void log(Level level, String message, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        super.log(level, message, p1, p2, p3, p4, p5, p6);
    }



    private static LoggerContext getContext(Logger logger) {
        try {
            Field contextField = logger.getClass().getDeclaredField("context");
            contextField.setAccessible(true);
            return (LoggerContext) contextField.get(logger);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MessageFactory2 getMessageFactory(Logger logger) {
        try {
            Field messageFactoryField = logger.getClass().getSuperclass().getDeclaredField("messageFactory");
            messageFactoryField.setAccessible(true);
            return (MessageFactory2) messageFactoryField.get(logger);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
