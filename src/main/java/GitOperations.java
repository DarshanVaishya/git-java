import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.commons.codec.digest.DigestUtils;

public class GitOperations {
	public void initGit() {
		final File root = new File(".git");
		new File(root, "objects").mkdirs();
		new File(root, "refs").mkdirs();
		final File head = new File(root, "HEAD");

		try {
			head.createNewFile();
			Files.write(head.toPath(), "ref: refs/heads/master\n".getBytes());
			System.out.println("Initialized git directory");
		} catch (IOException e) {
			System.err.println("An error occured while initializing git directory.");
			throw new RuntimeException(e);
		}
	}

	public void handleCatFile(String hash) {
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
			System.err.println("File with hash " + hash + " doesn't exist.");
			e.printStackTrace();
		}
	}

	public void handleHashObject(String fileName) {
		try {
			// Read file content
			File file = new File(fileName);
			String fileContent = Files.readString(file.toPath());

			// Compute hash of file
			String newContent = "blob " + fileContent.length() + "\0" + fileContent;
			String hash = DigestUtils.sha1Hex(newContent);
			System.out.println(hash);

			// zlib compress the content
			byte[] compressedData = getZlibHash(newContent);

			// Create blob file
			String folderName = hash.substring(0, 2);
			String blobFileName = hash.substring(2);
			File blobFolder = new File("./.git/objects/" + folderName);
			if (!blobFolder.exists()) {
				blobFolder.mkdirs();
			}
			File blobFile = new File("./.git/objects/" + folderName + "/" + blobFileName);
			Files.write(blobFile.toPath(), compressedData);
		} catch (IOException e) {
			System.err.println("Error encountered while making the blob file.");
			e.printStackTrace();
		}

	}

	public byte[] getZlibHash(String content) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			// Deflater uses Zlib to compress data
			DeflaterOutputStream dos = new DeflaterOutputStream(baos);
			dos.write(content.getBytes());
			dos.flush();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return baos.toByteArray();
	}
}