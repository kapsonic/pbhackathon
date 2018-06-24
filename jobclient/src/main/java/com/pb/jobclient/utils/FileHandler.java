package com.pb.jobclient.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Slf4j
@Component
public class FileHandler {

	private BufferedOutputStream st = null;

	public FileHandler() {

	}

	public void write(Path path, String content) {

		write(path, content.getBytes());
	}

	public void write(Path path, byte[] content) {

		try {
			flushContentStream(path, content, StandardOpenOption.CREATE);
		} catch (Exception e) {
			// do nothing
		}
	}

	public void moveFile(File src, File dest) {
		try {
			FileUtils.moveFile(src, dest);
		} catch (IOException e) {
			log.error("Error moving file from {} to {}", src.getPath(), dest.getPath(), e);
		}
	}
	
    public void deleteFile(File file) {
    	FileUtils.deleteQuietly(file);
    }

	public boolean writeErrorFile(Path path) {
		try {
			Files.createFile(path);
			return true;
		} catch (IOException e) {
			log.error("Error creating error file '{}'", path, e);
			return false;
		}
	}

	public void flushContentStream(Path path, String content,
			StandardOpenOption options) throws Exception {
		flushContentStream(path, content.getBytes(), options);
	}

	public void flushContentStream(Path path, byte[] content,
			StandardOpenOption options) throws Exception {

		try {
			st = new BufferedOutputStream(Files.newOutputStream(path, options));
			st.write(content);
			st.flush();

		} catch (Exception e) {
			log.error("Error writing file content to path = " + path, e);
			throw e;
		} finally {
			if (st != null)
				try {
					st.close();
				} catch (IOException e) {
					log.error(
							"Error closing the output stream (writing downloaded content to local hot download folder).",
							e);
				}
		}

	}

	public boolean createParent(Path path) {
		final Path parent = path.getParent();

		if (parent != null && parent.toFile().exists()) {
			return true;
		}

		try {
			Files.createDirectories(parent);
			return true;
		} catch (IOException e) {
			log.error("Error creating parent directory '{}'",
					parent.toAbsolutePath(), e);
			return false;
		}
	}

	public boolean fileExists(Path path) {
		return path.toFile().exists();
	}

	public static void main(String[] args) throws Exception {

		FileHandler h = new FileHandler();

		String home = System.getProperty("user.home");
		;
		File f = new File(home, "/.relay/_clientlock");

		Path path = Paths.get(f.toURI());

		String lastTime = null;
		if (Files.exists(path)) {
			List<String> lines = Files.readAllLines(path);
			if (lines != null && lines.size() > 0)
				lastTime = lines.get(0).split("=")[1];

			System.out.println(lastTime);

			// /fetch from timestamp

			// get new last time stamp

			// write
			java.util.Date d = new java.util.Date();
			long time = d.getTime();
			System.out.println("Writing new time stamp." + time);
			h.flushContentStream(path, "_lst=" + time,
					StandardOpenOption.CREATE);

		} else {
			h.flushContentStream(path, "_lst=1222\nagaga\n;kaknjma;lnka",
					StandardOpenOption.CREATE);

		}
	}

}
