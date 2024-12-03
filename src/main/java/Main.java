import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.digest.DigestUtils;

public class Main {
	public static void main(String[] args) throws IOException {
		// You can use print statements as follows for debugging, they'll be visible
		// when running tests.
		System.err.println("Logs from your program will appear here!");

		final String command = args[0];

		switch (command) {
			case "init" -> {
				final File root = new File(".git");
				new File(root, "objects").mkdirs();
				new File(root, "refs").mkdirs();
				final File head = new File(root, "HEAD");

				try {
					head.createNewFile();
					Files.write(head.toPath(), "ref: refs/heads/master\n".getBytes());
					System.out.println("Initialized git directory");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			case "cat-file" -> {
				String hash = args[2];
				String folderName = hash.substring(0, 2);
				String fileName = hash.substring(2);
				File blobFile = new File("./.git/objects/" + folderName + "/" + fileName);

				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(new InflaterInputStream(new FileInputStream(blobFile))));
					String blob = reader.readLine();
					String content = blob.substring(blob.indexOf("\0") + 1);
					System.out.print(content);

					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			case "hash-object" -> {
				try {
					String name = args[2];

					// Read file content
					File file = new File(name);
					String fileContent = Files.readString(file.toPath());

					// Compute hash of file
					String newContent = "blob " + fileContent.length() + "\0" + fileContent;
					String hash = DigestUtils.sha1Hex(newContent);
					System.out.println(hash);

					// zlib compress the content
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DeflaterOutputStream dos = new DeflaterOutputStream(baos);
					dos.write(newContent.getBytes());
					dos.flush();
					dos.close();

					// Create blob file
					String folderName = hash.substring(0, 2);
					String fileName = hash.substring(2);

					File blobFolder = new File("./.git/objects/" + folderName);

					if (!blobFolder.exists()) {
						blobFolder.mkdirs();
					}
					File blobFile = new File("./.git/objects/" + folderName + "/" + fileName);

					Files.write(blobFile.toPath(), baos.toByteArray());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			default -> System.out.println("Unknown command: " + command);
		}
	}
}
