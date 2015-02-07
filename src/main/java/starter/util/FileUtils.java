package starter.util;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private final static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    
    public static long getLastModified(String resource) {
        URL url = FileUtils.class.getResource(resource);
        
        try {
            if (url.getProtocol().equals("file")) {
                Path path = Paths.get(url.toURI());
                
                return findLastModified(path);
            }
            
            final Map<String, String> env = new HashMap<>();
            final String[] array = url.toURI().toString().split("!");
            
            try (FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env)) {
                final Path path = fs.getPath(array[1]);
                
                return findLastModified(path);
            }            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long findLastModified(Path path) throws IOException {
        long[] lastModified = new long[1];
        String[] resourceName = new String[1];
        
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile() && attrs.lastModifiedTime().toMillis() > lastModified[0]) {
                    lastModified[0] = attrs.lastModifiedTime().toMillis();
                    resourceName[0] = file.getFileName().toString();
                }
   
                return FileVisitResult.CONTINUE;
            }
        });
        
        logger.info("Last modified resource: " + resourceName[0] + " at " + lastModified[0]);
        
        return lastModified[0];
    }
}
