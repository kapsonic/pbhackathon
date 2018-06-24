package com.pb.jobclient.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

@Slf4j
public class Lock {
    private static File lockFile = new File("update.lock");
    private static FileLock lock;

    public static FileLock acquire() throws Exception {
        if (lock != null) {
            throw new Exception("Already locked");
        }
        if (!lockFile.exists()) {
            try {
                lockFile.createNewFile();
            } catch (IOException e) {
                log.error("Unable to create lock file ", e);
                return null;
            }
        }
        try {
            RandomAccessFile file = new RandomAccessFile(lockFile, "rw");
            FileChannel channel = file.getChannel();
            lock = channel.tryLock();
            return lock;
        } catch (FileNotFoundException e) {
            log.error("Cannot find lock file", e);
        } catch (IOException e) {
            log.error("Error locking the lock file", e);
        }

        return null;
    }

    public static boolean release() throws Exception {
        if (lock == null) {
            throw new Exception("File is not locked");
        }
        try {
            lock.release();
            lock = null;
            return true;
        } catch (Exception e) {
            log.error("Error releasing lock", e);
            return false;
        }
    }

}
