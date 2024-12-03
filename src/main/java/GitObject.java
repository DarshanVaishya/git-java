import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class GitObject {

	public String generateHash(String content) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(content.getBytes());
			byte[] bytes = md.digest();

			String hash = new BigInteger(1, bytes).toString(16);
			System.out.println(hash);
			return hash;

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
}
