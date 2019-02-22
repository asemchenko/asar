/*
 *  Copyright (c) 2019.
 *  Author: Andrii Semchenko
 *  email: andriysemchenko@gmail.com
 *  Project: asar
 *  License: MIT
 */

package compressor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ArchiveService {
    public static void compress(String srcPath, String dstPath) throws IOException {
        try (FilesOutput filesOutput = new FilesOutput(new GZIPOutputStream(new FileOutputStream(dstPath)))) {
            Path rootPath = Path.of(srcPath);
            FileVisitor<Path> visitor = new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.err.println("Compressing " + file.toString());
                    filesOutput.write(file, rootPath);
                    return FileVisitResult.CONTINUE;
                }
            };
            Files.walkFileTree(rootPath, visitor);
        }
    }

    public static void decompress(String archivePath, String dstDir) throws IOException, FileInputException {
        Path dst = Path.of(dstDir);
        if (!Files.exists(dst)) {
            Files.createDirectory(dst);
        }
        if (!Files.isDirectory(dst)) {
            throw new IOException(String.format("Destination '%s' is not a directory", dstDir));
        }
        try (FilesInput filesInput = new FilesInput(new GZIPInputStream(new FileInputStream(archivePath)))) {
            for (FileEntry entry = filesInput.readNextEntry(); entry != null; entry = filesInput.readNextEntry()) {
                Path pathToCurFile = dst.resolve(entry.relativePath);
                createSubDirsIfNotExist(pathToCurFile);
                try (OutputStream curFile = new FileOutputStream(pathToCurFile.toString())) {
                    filesInput.transferTo(curFile);
                }
            }
        }
    }

    private static void createSubDirsIfNotExist(Path path) throws IOException {
        try {
            Path pathToSubDir = path.getParent();
            Files.createDirectories(pathToSubDir);
        } catch (FileAlreadyExistsException e) {
            // do nothing
        }
    }
}
