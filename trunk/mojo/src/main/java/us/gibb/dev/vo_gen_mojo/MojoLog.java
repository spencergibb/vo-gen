package us.gibb.dev.vo_gen_mojo;

import java.text.MessageFormat;

import us.gibb.dev.vo_gen.Log;

public class MojoLog implements Log {

    private org.apache.maven.plugin.logging.Log log;

    public MojoLog(org.apache.maven.plugin.logging.Log log) {
        this.log = log;
    }

    public void debug(CharSequence content, Object... params) {
        log.debug(format(content, params));
    }

    public void debug(CharSequence content, Throwable error, Object... params) {
        log.debug(format(content, params), error);
    }

    public void debug(Throwable error) {
        log.debug(error);
    }

    public void error(CharSequence content, Object... params) {
        log.error(format(content, params));
    }

    public void error(CharSequence content, Throwable error, Object... params) {
        log.error(format(content, params), error);
    }

    public void error(Throwable error) {
        log.error(error);
    }

    public void info(CharSequence content, Object... params) {
        log.info(format(content, params));
    }

    public void info(CharSequence content, Throwable error, Object... params) {
        log.info(format(content, params), error);
    }

    public void info(Throwable error) {
        log.info(error);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public void warn(CharSequence content, Object... params) {
        log.warn(format(content, params));
    }

    public void warn(CharSequence content, Throwable error, Object... params) {
        log.warn(format(content, params), error);
    }

    public void warn(Throwable error) {
        log.warn(error);
    }

    private CharSequence format(CharSequence content, Object[] params) {
        return MessageFormat.format(content.toString(), params);
    }

}
