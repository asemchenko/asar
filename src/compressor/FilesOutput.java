/*
 *  Copyright (c) 2019.
 *  Author: Andrii Semchenko
 *  email: andriysemchenko@gmail.com
 *  Project: asar
 *  License: MIT
 */

package compressor;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FilesOutput extends FilterOutputStream {

    private DataOutputStream outputStream;

    public FilesOutput(OutputStream outputStream) {
        super(outputStream);
        this.outputStream = new DataOutputStream(outputStream);
    }

    public void write(String path, String rootPath) throws IOException {
        write(Path.of(path), Path.of(rootPath));
    }

    public void write(Path path, Path rootPath) throws IOException {
        File file = path.toFile();
        if (!file.exists()) {
            throw new IOException(String.format("File '%s' does not exists", path.toString()));
        }
        FileEntry entry = getEntry(path, rootPath);
        entry.save(outputStream);
        new FileInputStream(file).transferTo(outputStream);
    }

    private FileEntry getEntry(Path path, Path rootPath) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
        FileEntry fileEntry = new FileEntry();
        fileEntry.relativePath = rootPath.relativize(path).toString();
        fileEntry.lastModified = attributes.lastModifiedTime().toMillis();
        fileEntry.createdAt = attributes.creationTime().toMillis();
        fileEntry.sizeBytes = attributes.size();
        return fileEntry;
    }

    private void writeEntry(@NotNull FileEntry entry) throws IOException {
        entry.save(outputStream);
    }

}
