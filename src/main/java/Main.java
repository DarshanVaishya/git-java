public class Main {
	public static void main(String[] args) {
		System.err.println("Logs from your program will appear here!");
		final String command = args[0];
		GitOperations handler = new GitOperations();

		switch (command) {
			case "init" -> {
				handler.initGit();
			}

			case "cat-file" -> {
				String blobSHA = args[2];
				handler.readBlob(blobSHA);
			}

			case "hash-object" -> {
				String fileName = args[2];
				handler.writeBlob(fileName);
			}

			case "ls-tree" -> {
				String treeSHA = args[2];
				handler.readTree(treeSHA);
			}

			default -> System.out.println("Unknown command: " + command);
		}
	}
}
