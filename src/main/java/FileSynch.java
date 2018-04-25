
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Directories synchronization
 */
public class FileSynch {
    String source;
    String destination;

    FileSynch(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    public boolean emptyArgs() {
        if ((source != null && source.trim().length() > 0) && (destination != null && destination.trim().length() > 0))
            return false;
        else return true;
    }

    public void copyFiles() throws IOException {
        if (emptyArgs()) {
            System.out.println("Set source and destination path");
            throw new NullPointerException();
        }
        Path srcPath = FileSystems.getDefault().getPath(source);
        Path dsPath = FileSystems.getDefault().getPath(destination);
        Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                Path targetdir = dsPath.resolve(srcPath.relativize(dir));
                if (!Files.exists(targetdir)) {
                    Files.createDirectories(targetdir);
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Path resolvedDsPath = dsPath.resolve(srcPath.relativize(file));
                if (Files.exists(resolvedDsPath)) {
                    if (Files.size(resolvedDsPath) != Files.size(file))
                        Files.copy(file, resolvedDsPath, REPLACE_EXISTING);
                } else
                    Files.copy(file, resolvedDsPath);
                System.out.println(String.format("File '%s' was copied to '%s'", file, resolvedDsPath));
                return CONTINUE;
            }
        });

    }

    protected void deleteFiles() throws IOException {
        if (emptyArgs()) {
            System.out.println("Set source and destination path");
            throw new NullPointerException();
        }
        Path srcPath = FileSystems.getDefault().getPath(source);
        Path dsPath = FileSystems.getDefault().getPath(destination);

        Files.walkFileTree(dsPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Path targetdir = srcPath.resolve(dsPath.relativize(dir));
                if (Files.notExists(targetdir)) {
                    Files.deleteIfExists(dir);
                    System.out.println("Directory " + dir + " was deleted");
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Path resolvedSrcPath = srcPath.resolve(dsPath.relativize(file));
                if (Files.notExists(resolvedSrcPath)) {
                    Files.delete(file);
                    System.out.println("File " + file + " was deleted");
                }
                return CONTINUE;
            }
        });
    }

    public static void main(String[] args) {
        if (args.length < 2)
            System.out.println("Set source and destination path ");
        String source = args[0];
        String destination = args[1];
        FileSynch synch = new FileSynch(source, destination);
        try {
            synch.copyFiles();
            synch.deleteFiles();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
