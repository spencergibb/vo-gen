package us.gibb.dev.vo_gen;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * LogUtils is meant to simplify logging.  It offers three basic advantages over native Java Logging.
 * <ol>
 *      <li>Provides varargs params instead of Object[]</li>
 *      <li>Can deduce the logger</li>
 *      <li>Can simplify logger declaration</li>
 *  </ol>           
 * 
 * LogUtils contains two main classes of methods - methods that take a logger and methods that deduce
 * the logger. <b>NOTE: </b> While using the methods that deduce the logger is convenient there is a 
 * performance drawback to this.
 * 
 * @see java.util.logging.Logger
 */
public class LogUtils {
    
    private static Logger logger = Logger.getLogger(LogUtils.class.getName());
    private static final ContextHelper contextHelper = new ContextHelper();
    
    private LogUtils() {
        //exists to thwart instantiation
    }
    
    /**
     * Attempts to determine a reasonable logger for the current stack of execution
     * and returns it.  If no such logger can be obtained (which should be rare if ever), the 
     * anonymous logger is returned instead.
     * 
     * @return Logger
     */
    public static Logger getLogger() {
        Class<?>[] callingClasses = contextHelper.getClassContext();
        
        //find caller
        for (Class<?> callingClass : callingClasses) {
            String className = callingClass.getName();
            if (!className.contains(LogUtils.class.getName())) {
                fine(logger, "Found logger for {0}", callingClass);
                return Logger.getLogger(callingClass.getName());
            }
        }
        warning(logger, "Failed to automatically find calling class.  Please use Logger.getLogger(...) for this logger instead.  Returning anonymous logger.");
        return Logger.getAnonymousLogger();
    }
    

    /**
     * Log a FINE message with optional Object parameters.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void fine(String msg, Object... params) {
        log(getLogger(), Level.FINE, msg, params);
    }

    /**
     * Log an INFO message with optional Object parameters.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void info(String msg, Object... params) {
        log(getLogger(), Level.INFO, msg, params);
    }
    
    /**
     * Log a WARNING message with optional Object parameters.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void warning(String msg, Object... params) {
        log(getLogger(), Level.WARNING, msg, params);
    }

    /**
     * Log a SEVERE message with optional Object parameters.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void severe(String msg, Object... params) {
        log(getLogger(), Level.SEVERE, msg, params);
    }

    /**
     * Log a SEVERE message, with a throwable, and optional Object parameters.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param throwable Throwable to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void severe(String msg, Throwable throwable, Object... params) {
        log(getLogger(), Level.SEVERE, msg, throwable, params);
    }

    /**
     * Log throwing an exception with optional Object parameters.  This will only be logged if the 
     * logger level is FINER or greater.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param throwable Throwable to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void throwing(String msg, Throwable throwable, Object... params) {
        log(getLogger(), Level.FINE, msg, throwable, params);
    }

    /**
     * Log a method entry with optional Object parameters.  This will only be logged if the logger 
     * level is FINER or greater.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param params Parameters to the method being entered
     */
    public static void entering(Object...params) {
        entering(getLogger(), params);
    }
    
    /**
     * Log a method entry with optional Object parameters.  This will only be logged if the logger 
     * level is FINER or greater.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param params Parameters to the method being entered
     * 
     * @deprecated Please use entering(String msg, Object...params) instead.  The msg parameter
     * is not utilized in the call, and thus the params are meant to represent the paramaters
     * being passed to the method that is being entered.
     */
    public static void entering(String msg, Object...params) {
        entering(getLogger(), params);
    }
    
    /**
     * Log a method exit with optional Object parameters.  This will only be logged if the logger 
     * level is FINER or greater.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param result Object representing the return value of the exiting method
     */
    public static void exiting(Object result) {
        exiting(getLogger(), result);
    }
    
    /**
     * Log a method exit with optional Object parameters.  This will only be logged if the logger 
     * level is FINER or greater.
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in 
     * the message if the message is logged
     * 
     * @deprecated Please use exiting(Object result) instead.  The msg parameter is not used, and
     * params should be a single parameter of type Object representing the return value of the
     * method being exited.
     */
    public static void exiting(String msg, Object... params) {
        exiting(getLogger(), msg, params);
    }
    
    /**
     * Log a message, at the specified level, with optional Object parameters.
     * 
     * <p>
     * If the logger is currently enabled for the given message 
     * level then a corresponding LogRecord is created and forwarded 
     * to all the registered output Handler objects.
     * <p>
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg   The string message to be logged
     * @param params    Vararg of Object parameters to be replaced in the 
     * message if the message is logged
     */
    public static void log(Level level, String msg, Object... params) {
        log(getLogger(), level, msg, params);
    }

    /**
     * Log a message, at the specified level, with a Throwable, and with 
     * optional Object parameters.
     * 
     * <p>
     * If the logger is currently enabled for the given message 
     * level then a corresponding LogRecord is created and forwarded 
     * to all the registered output Handler objects.
     * <p>
     * 
     * <p>NOTE: The logger will be deduced at a performance hit.<p>
     * 
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg   The string message to be logged
     * @param throwable Throwable to be logged
     * @param params    Vararg of Object parameters to be replaced in the 
     * message if the message is logged
     */
    public static void log(Level level, String msg, Throwable throwable, Object... params) {
        log(getLogger(), level, msg, throwable, params);
    }
    
    
    /**
     * Log a FINE message with optional Object parameters, using the specified logger.
     * 
     * @param logger The Logger to use in logging the message
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void fine(Logger logger, String msg, Object... params) {
        log(logger, Level.FINE, msg, params);
    }

    /**
     * Log an INFO message with optional Object parameters, using the specified logger.
     * 
     * @param logger The Logger to use in logging the message
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void info(Logger logger, String msg, Object... params) {
        log(logger, Level.INFO, msg, params);
    }
    
    /**
     * Log a WARNING message with optional Object parameters, using the specified logger.
     * 
     * @param logger The Logger to use in logging the message
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void warning(Logger logger, String msg, Object... params) {
        log(logger, Level.WARNING, msg, params);
    }

    /**
     * Log a SEVERE message with optional Object parameters, using the specified logger.
     * 
     * @param logger The Logger to use in logging the message
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void severe(Logger logger, String msg, Object... params) {
        log(logger, Level.SEVERE, msg, params);
    }

    /**
     * Log a SEVERE message, with a throwable, and with optional Object parameters, 
     * using the specified logger.
     * 
     * @param logger The Logger to use in logging the message
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void severe(Logger logger, String msg, Throwable throwable, Object... params) {
        log(logger, Level.SEVERE, msg, throwable, params);
    }

    /**
     * Log throwing an exception with optional Object parameters, using the specified logger.  
     * This will only be logged if the logger level is FINER or greater.
     * 
     * @param logger The Logger to use in logging the exception
     * @param msg The string message to be logged
     * @param throwable Throwable to be logged
     * @param params Vararg of Object parameters to be replaced in the message if the message is logged
     */
    public static void throwing(Logger logger, String msg, Throwable throwable, Object... params) {
        log(logger, Level.FINE, msg, throwable, params);
    }

    /**
     * Log a method entry with optional Object parameters, using the specified logger.  This will 
     * only be logged if the logger level is FINER or greater.
     * 
     * @param logger The Logger to use in logging the message
     * @param params Parameters to the method being entered
     */
    public static void entering(Logger logger, Object...params) {
        if(!logger.isLoggable(Level.FINER)) {
            return;
        }
        CallerInfo callerInfo = inferCaller();
        logger.entering(callerInfo.getSourceClassName(), callerInfo.getSourceMethodName(), params);
    }
    
    /**
     * Log a method exit with optional Object parameters, using the specified logger.  This will 
     * only be logged if the logger level is FINER or greater.
     * 
     * @param logger The Logger to use in logging the message
     * @param msg The string message to be logged
     * @param params Vararg of Object parameters to be replaced in 
     * the message if the message is logged
     * 
     * @deprecated Please use exiting(Logger logger, Object... params) instead.  The msg parameter
     * is not utilized, and the params should actually be a single parameter representing the return
     * value of the method being exited.
     */
    public static void exiting(Logger logger, String msg, Object... params) {
        if(!logger.isLoggable(Level.FINER)) {
            return;
        }
        CallerInfo callerInfo = inferCaller();
        logger.exiting(callerInfo.getSourceClassName(), callerInfo.getSourceMethodName(), params);
    }
    
    /**
     * Log a method exit with optional Object parameters, using the specified logger.  This will 
     * only be logged if the logger level is FINER or greater.
     * 
     * @param logger The Logger to use in logging the message
     * @param result Object representing the return value of the exiting method
     */
    public static void exiting(Logger logger, Object result) {
        if(!logger.isLoggable(Level.FINER)) {
            return;
        }
        CallerInfo callerInfo = inferCaller();
        logger.exiting(callerInfo.getSourceClassName(), callerInfo.getSourceMethodName(), result);
    }
    
    /**
     * Log a message, at the specified level, with optional Object parameters,
     * using the specified logger.
     * 
     * <p>
     * If the logger is currently enabled for the given message 
     * level then a corresponding LogRecord is created and forwarded 
     * to all the registered output Handler objects.
     * <p>
     * 
     * @param logger The Logger to use in logging the message
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg   The string message to be logged
     * @param params    Vararg of Object parameters to be replaced in the 
     * message if the message is logged
     * 
     * @see java.util.Logger.log(Level level, String msg, Object params[]);
     */
    public static void log(Logger logger, Level level, String msg, Object... params) {
        log(logger, level, msg, null, params);
    }

    /**
     * Log a message, at the specified level, with a Throwable, and with 
     * optional Object parameters, using the specified logger.
     * 
     * <p>
     * If the logger is currently enabled for the given message 
     * level then a corresponding LogRecord is created and forwarded 
     * to all the registered output Handler objects.
     * <p>
     * 
     * @param logger The Logger to use in logging the message
     * @param level One of the message level identifiers, e.g. SEVERE
     * @param msg   The string message to be logged
     * @param throwable Throwable to be logged
     * @param params    Vararg of Object parameters to be replaced in the 
     * message if the message is logged
     * 
     * @see java.util.Logger.log(Level level, String msg, Object params[]);
     */
    public static void log(Logger logger, Level level, String msg, Throwable throwable, Object... params) {
        if(!logger.isLoggable(level)) {
            return;
        }
        LogRecord logRecord = new LogRecord(level, msg);
        logRecord.setParameters(params);
        logRecord.setThrown(throwable);
        CallerInfo callerInfo = inferCaller();
        logRecord.setSourceClassName(callerInfo.getSourceClassName());
        logRecord.setSourceMethodName(callerInfo.getSourceMethodName());
        logger.log(logRecord);
    }
    
    private static CallerInfo inferCaller() {
        // Get the stack trace.
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        // First, search back to a method in the Logger class.
        int index = 0;
        while (index < stack.length) {
            StackTraceElement frame = stack[index];
            String cname = frame.getClassName();
            if (cname.contains(LogUtils.class.getName())) {
                break;
            }
            index++;
        }
        // Now search for the first frame before the "Logger" class.
        while (index < stack.length) {
            StackTraceElement frame = stack[index];
            String cname = frame.getClassName();
            if (!cname.equals(LogUtils.class.getName())) {
                // We've found the relevant frame.
                return new CallerInfo(cname, frame.getMethodName());
            }
            index++;
        }
        // We haven't found a suitable frame, so just punt.  This is
        // OK as we are only committed to making a "best effort" here.
        return new CallerInfo();
    }
    
    private static class CallerInfo {
        private String sourceClassName;
        private String sourceMethodName;
        
        public CallerInfo() {}
        public CallerInfo(String sourceClassName, String sourceMethodName) {
            this.sourceClassName = sourceClassName;
            this.sourceMethodName = sourceMethodName;
        }
        
        public String getSourceClassName() {
            return sourceClassName;
        }
        public String getSourceMethodName() {
            return sourceMethodName;
        }
    }
    
    private static class ContextHelper extends SecurityManager {
        @Override
        public Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }
}
