import org.mindrot.jbcrypt.BCrypt;

public class GenerateAdminHash {
    public static void main(String[] args) {

        String hash = BCrypt.hashpw(
                "admin123",
                BCrypt.gensalt()
        );

        System.out.println("HASH START");
        System.out.println(hash);
        System.out.println("HASH LENGTH: " + hash.length());
        System.out.println("HASH END");
    }
}