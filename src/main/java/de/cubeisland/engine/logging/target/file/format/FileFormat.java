package de.cubeisland.engine.logging.target.file.format;

import de.cubeisland.engine.logging.target.format.Format;

public interface FileFormat extends Format
{
    void writeHeader(StringBuilder builder);
    void writeTrailer(StringBuilder builder);
}
