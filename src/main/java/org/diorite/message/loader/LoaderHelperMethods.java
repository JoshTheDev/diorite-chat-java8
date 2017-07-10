package org.diorite.message.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

final class LoaderHelperMethods
{
    private LoaderHelperMethods() {}

    static InputStreamReader createInputStreamReader(MessagesLoader loader, File file, Charset charset)
    {
        try
        {
            if (! file.exists())
            {
                File absoluteFile = file.getAbsoluteFile();
                absoluteFile.getParentFile().mkdirs();
                absoluteFile.createNewFile();
            }
        }
        catch (IOException e)
        {
            throw new MessagesLoadException(loader, file, "can't create a file.", e);
        }
        try
        {
            FileInputStream fileInputStream = new FileInputStream(file);
            return new InputStreamReader(fileInputStream, charset.newDecoder());
        }
        catch (IOException e)
        {
            throw new MessagesLoadException(loader, file, e.getMessage(), e);
        }
    }
}
