
package gabriel.betbot.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author gabriel
 */
public class FileUtil {
        private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(FileUtil.class.getName());

    
    public static void writeStringToFile(final String s, final String fileName) {
        Path path = Paths.get(fileName);
        try {
            Files.write(path, s.getBytes());
        } catch (IOException ex) {
           LOG.error("Error writing to file");
           throw new RuntimeException(ex);
        }
    }

}
