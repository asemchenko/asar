/*
 *  Copyright (c) 2019.
 *  Author: Andrii Semchenko
 *  email: andriysemchenko@gmail.com
 *  Project: asar
 *  License: MIT
 */

package compressor;

import java.io.*;

public class FilesInput extends FilterInputStream {
    private DataInputStream inputStream;
    private long curFileSize;
    private long curFileReadBytes;

    public FilesInput(InputStream inputStream) {
        super(inputStream);
        this.inputStream = new DataInputStream(inputStream);
    }

    public FileEntry readNextEntry() throws IOException, FileInputException {
        closeEntry();
        FileEntry fileEntry = new FileEntry();
        try {
            fileEntry.load(inputStream);
        } catch (EOFException e) {
            return null;
        }
        curFileSize = fileEntry.sizeBytes;
        curFileReadBytes = 0;
        return fileEntry;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException("Buffer can not be null");
        }
        if ((off < 0) || (len < 0) || (len > b.length - off)) {
            throw new IndexOutOfBoundsException("Invalid parameters for buffer");
        }
        long bytesToBeRead = (curFileSize - curFileReadBytes < len) ? curFileSize - curFileReadBytes : len;
        int readBytes = inputStream.read(b, off, (int) bytesToBeRead);
        curFileReadBytes += readBytes;
        return readBytes;
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        long totalTransferred = 0;
        int read = read(buffer);
        while (read != 0) {
            totalTransferred += read;
            out.write(buffer, 0, read);
            read = read(buffer);
        }
        return totalTransferred;
    }

    public void closeEntry() throws IOException, FileInputException {
        if (curFileSize == curFileReadBytes) {
            return;
        }
        long actualSkipped = inputStream.skip(curFileSize - curFileReadBytes);
        if (actualSkipped < curFileSize - curFileReadBytes) {
            throw new FileInputException();
        }
        curFileSize = 0;
        curFileReadBytes = 0;
    }
}
