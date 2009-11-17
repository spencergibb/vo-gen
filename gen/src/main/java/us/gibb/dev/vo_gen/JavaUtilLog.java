package us.gibb.dev.vo_gen;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaUtilLog implements Log {
    
    private Logger logger;

    public JavaUtilLog(Class<?> clazz) {
        this.logger = Logger.getLogger(clazz.getName());
    }

    public void error(CharSequence content, Object... params) {
        LogUtils.severe(logger, content.toString(), params);
    }

    public void error(CharSequence content, Throwable throwable, Object... params) {
        LogUtils.severe(logger, content.toString(), throwable, params);
    }

    public void error(Throwable throwable) {
        LogUtils.severe(logger, null, throwable);
    }

    public void debug(CharSequence content, Object... params) {
        LogUtils.fine(logger, content.toString(), params);
    }

    public void debug(CharSequence content, Throwable throwable, Object... params) {
        LogUtils.log(logger, Level.FINE, content.toString(), throwable, params);
    }

    public void debug(Throwable throwable) {
        LogUtils.log(logger, Level.FINE, null, throwable);
    }

    public void info(CharSequence content, Object... params) {
        LogUtils.info(content.toString(), params);
    }

    public void info(CharSequence content, Throwable throwable, Object... params) {
        LogUtils.log(logger, Level.INFO, content.toString(), throwable, params);
    }

    public void info(Throwable throwable) {
        LogUtils.log(logger, Level.INFO, null, throwable);
    }

    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    public void warn(CharSequence content, Object... params) {
        LogUtils.warning(content.toString(), params);
    }

    public void warn(CharSequence content, Throwable throwable, Object... params) {
        LogUtils.log(logger, Level.WARNING, content.toString(), throwable, params);
    }

    public void warn(Throwable throwable) {
        LogUtils.log(logger, Level.WARNING, null, throwable);
    }

}
