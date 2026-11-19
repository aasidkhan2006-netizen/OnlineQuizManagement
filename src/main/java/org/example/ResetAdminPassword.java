import org.example.database.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ResetAdminPassword {

    public static void main(String[] args) {

        String username = "admin";
        String newPassword = "admin123";

        // Generate a fresh BCrypt hash
        String hashedPassword = BCrypt.hashpw(
                newPassword,
                BCrypt.gensalt()
        );

        String sql = "UPDATE admins SET password = ? WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, hashedPassword);
            ps.setString(2, username);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Admin password updated successfully!");
                System.out.println("Username: " + username);
                System.out.println("Password: " + newPassword);
                System.out.println("Hash length: " + hashedPassword.length());
            } else {
                System.out.println("Admin account not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}