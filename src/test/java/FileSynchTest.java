
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Unit test for FileSynch
 */
public class FileSynchTest {

    @Test
    public void testCopyFiles() throws IOException {
        //given
        String src = "h1";
        String dst = "h2";
        String fileName = "test.txt";
        createFiles(src, fileName);

        //when
        FileSynch fs = new FileSynch(src, dst);
        fs.copyFiles();

        //then
        Path dstPath = FileSystems.getDefault().getPath(dst, fileName);
        assertTrue(Files.exists(dstPath));
        deleteFile(src, fileName);
        deleteFile(dst, fileName);
    }


    @Test
    public void testCopyFilesInSubdirectory() throws IOException {
        //given
        String src = "h1";
        String dst = "h3\\h4";
        String fileName = "test.txt";
        createFiles(src, fileName);

        //when
        FileSynch fs = new FileSynch(src, dst);
        fs.copyFiles();

        //then
        Path dstPath = FileSystems.getDefault().getPath(dst, fileName);
        assertTrue(Files.exists(dstPath));
        deleteFile(src, fileName);
        deleteFile(dst, fileName);
    }

    @Test
    public void testCopyFilesFromSubdirectory() throws IOException {
        //given
        String src = "h5\\h6";
        String dst = "h7\\h8";
        String fileName = "test.txt";
        createFiles(src, fileName);

        //when
        FileSynch fs = new FileSynch(src, dst);
        fs.copyFiles();

        //then
        Path dstPath = FileSystems.getDefault().getPath(dst, fileName);
        assertTrue(Files.exists(dstPath));
        deleteFile(src, fileName);
        deleteFile(dst, fileName);
    }

    @Test(expected = NoSuchFileException.class)
    public void testCopyFilesFromNonexistDir() throws IOException {
        //given
        String src = "h9";
        String dst = "h10";
        String fileName = "test.txt";

        //when
        FileSynch fs = new FileSynch(src, dst);
        fs.copyFiles();

        //then
        Path dstPath = FileSystems.getDefault().getPath(dst, fileName);
        assertTrue(Files.exists(dstPath));
    }

    @Test
    public void testCopyFilesWithDifSize() throws IOException {
        //given
        String src = "h11";
        String dst = "h12";
        String fileName = "test.txt";
        Path srcFile = createFiles(src, fileName);
        Path dstFile = createFiles(dst, fileName);

        BufferedWriter bw = Files.newBufferedWriter(srcFile);
        bw.write("It's a test");
        bw.flush();
        bw.close();

        //when
        FileSynch fs = new FileSynch(src, dst);
        fs.copyFiles();

        //then
        long sizeSrc = Files.size(srcFile);
        long sizeDst = Files.size(dstFile);

        assertTrue(sizeSrc == sizeDst);
        deleteFile(src, fileName);
        deleteFile(dst, fileName);
    }

    @Test(expected = NullPointerException.class)
    public void testCopyFilesWithNull() throws IOException {
        FileSynch fs = new FileSynch(null, null);
        fs.copyFiles();
    }


    private Path createFiles(String dir, String fileName) throws IOException {
        Path srcPath = FileSystems.getDefault().getPath(dir);
        if (!Files.exists(srcPath))
            Files.createDirectories(srcPath);
        Path srcFile = FileSystems.getDefault().getPath(dir, fileName);
        if (!Files.exists(srcFile))
            Files.createFile(srcFile);
        return srcFile;
    }

    @Test
    public void testDeleteFiles() throws IOException {
        //given
        String src = "h15";
        String dst = "h16";
        String fileName = "test1.txt";
        String fileNameToDelete = "test2.txt";
        createFiles(src, fileName);
        Path dstFile = createFiles(dst, fileNameToDelete);

        //when
        FileSynch fs = new FileSynch(src, dst);
        fs.deleteFiles();

        //then
        assertTrue(Files.notExists(dstFile));
        deleteFile(src, fileName);
        deleteFile(dst, fileName);
    }

    private void deleteFile(String dir, String fileName) throws IOException {
        Files.deleteIfExists(FileSystems.getDefault().getPath(dir, fileName));
        Files.deleteIfExists(FileSystems.getDefault().getPath(dir));
        int i = dir.lastIndexOf("\\");
        if (i > 0)
            Files.deleteIfExists(FileSystems.getDefault().getPath(dir.substring(0, i)));
    }
}


