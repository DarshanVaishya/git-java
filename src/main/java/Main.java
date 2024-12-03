public class Main {
	public static void main(String[] args) {
		System.err.println("Logs from your program will appear here!");
		final String command = args[0];
		GitOperations operator = new GitOperations();

		switch (command) {
			case "init" -> {
				operator.initGit();
			}

			case "cat-file" -> {
				String hash = args[2];
				operator.handleCatFile(hash);
			}

			case "hash-object" -> {
				String fileName = args[2];
				operator.handleHashObject(fileName);
			}

			default -> System.out.println("Unknown command: " + command);
		}
	}
}
