public class User {
    private String email;
    private String username;


    public User(String email) {
        this.email = email;
        this.username = username;
    }

    private String generateUsername(String email) {
        if (email != null && email.contains("@")) {
            return email.split("@")[0];
        }
        return "UnknownUser";
    }
    

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return toString() {
            return String.format("| %-20s | %-15s |", email, username);
        }
}

