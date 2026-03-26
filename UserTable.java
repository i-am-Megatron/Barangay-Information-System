import java.util.ArrayList;
import java.util.List;

public class UserTable {
    private List<User> users;

    public UserTable() {
        this.users = new ArrayList<>();
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void displayTable() {
        System.out.println("+----------------------+-----------------+");
        System.out.println("| Email                | Username        |");
        System.out.println("+----------------------+-----------------+");
        for (User user : users) {
            System.out.println(user.toString());
        }
        System.out.println("+----------------------+-----------------+");
    }
    
}

public static void main(String[] args) {
    UserTable userTable = new UserTable();
    userTable.addUser(new User("john.doe@example.com"));
    userTable.addUser(new User("jane.smith@example.com"));
    userTable.displayTable();
}
