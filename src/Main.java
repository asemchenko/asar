/*
 *  Copyright (c) 2019.
 *  Author: Andrii Semchenko
 *  email: andriysemchenko@gmail.com
 *  Project: asar
 *  License: MIT
 */

import compressor.ArchiveService;
import compressor.FileInputException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Error - invalid arguments count");
            showUsage();
            System.exit(1);
        }
        try {
            process(args[0], args[1], args[2]);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void showUsage() {
        System.out.println("Usage:");
        System.out.println("\tCompression\tasar -c <dir> <archive>");
        System.out.println("\tDecompression\tasar -d <archive> <dir>");
    }

    private static void process(String option, String src, String dst) throws IOException, FileInputException {
        if (option.equals("-c")) {
            ArchiveService.compress(src, dst);
        } else if (option.equals("-d")) {
            ArchiveService.decompress(src, dst);
        } else {
            System.out.println("Invalid option");
            showUsage();
        }
    }
}
