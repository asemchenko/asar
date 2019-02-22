/*
 *  Copyright (c) 2019.
 *  Author: Andrii Semchenko
 *  email: andriysemchenko@gmail.com
 *  Project: asar
 *  License: MIT
 */

package compressor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FileEntry {
    public String relativePath;
    long lastModified;
    long createdAt;
    long sizeBytes;

    public void save(DataOutputStream stream) throws IOException {
        stream.writeUTF(relativePath);
        stream.writeLong(lastModified);
        stream.writeLong(createdAt);
        stream.writeLong(sizeBytes);
    }

    public void load(DataInputStream stream) throws IOException {
        relativePath = stream.readUTF();
        lastModified = stream.readLong();
        createdAt = stream.readLong();
        sizeBytes = stream.readLong();
    }
}
