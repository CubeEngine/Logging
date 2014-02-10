package de.cubeisland.engine.logging.target.format;

import de.cubeisland.engine.logging.LogEntry;
import de.cubeisland.engine.logging.MacroProcessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DefaultFormat implements Format
{
    private static final MacroProcessor macroProcessor = new MacroProcessor();

    protected final DateFormat dateFormat;
    private final String format;

    public DefaultFormat(String format, DateFormat dateFormat)
    {
        assert format != null : "The format String cannot be null";
        assert dateFormat != null : "The DateFormatter cannot be nul";
        this.format = format;
        this.dateFormat = dateFormat;
    }

    public DefaultFormat(String format)
    {
        this(format, new SimpleDateFormat());
    }

    public DefaultFormat()
    {
        this("{date} [{level}] {msg}");
    }

    public void writeEntry(LogEntry logEntry, StringBuilder builder)
    {
        String message = String.valueOf(logEntry.getMessage());
        Map<String, Object> map = new HashMap<String, Object>();
        if (logEntry.getArgs() != null && logEntry.getArgs().length != 0)
        {
            message = DefaultFormat.parseArgs(message, logEntry.getArgs());
        }
        map.put("msg", message);
        map.put("date", dateFormat.format(logEntry.getDate()));
        map.put("level", logEntry.getLevel().getName());
        message = macroProcessor.process(format, map);
        builder.append(message).append("\n");
        Throwable throwable = logEntry.getThrowable();
        if (throwable != null)
        {
            if (throwable.getLocalizedMessage() != null &&
               !throwable.getLocalizedMessage().equals(logEntry.getMessage()))
            {
                builder.append(logEntry.getMessage()).append("\n");
            }
            this.causedBy(builder, throwable);
        }
    }

    private void causedBy(StringBuilder builder, Throwable throwable)
    {
        builder.append(throwable.getClass().getName());
        if (throwable.getLocalizedMessage() != null)
        {
            builder.append(": ").append(throwable.getLocalizedMessage());
        }
        builder.append("\n");
        for (StackTraceElement element : throwable.getStackTrace())
        {
            builder.append("\tat ").append(element).append("\n");
        }
        if (throwable.getCause() != null)
        {
            builder.append("Caused by: ");
            this.causedBy(builder, throwable.getCause());
        }
    }

    private static final String arg = "\\{\\}";

    public static String parseArgs(String msg, Object... args)
    {
        if (args == null || args.length == 0)
        {
            return msg;
        }
        int i = 0;
        while (msg.contains("{}"))
        {
            try
            {
                msg = msg.replaceFirst(arg, "\\{" + i++ + "\\}");
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                throw new IllegalArgumentException("Not enough arguments!", e);
            }
        }
        for (i = 0; i < args.length; i++)
        {
            if (msg.contains("{"))
            {
                msg = msg.replaceAll("\\{" + i + "\\}", String.valueOf(args[i]).replace("\\","\\\\"));
            }
            else
            {
                break;
            }
        }
        return msg;
    }
}
