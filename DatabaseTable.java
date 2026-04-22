public class DatabaseTable extends java.awt.List {
    // List to store users
    private java.util.List<User> userList = new java.util.ArrayList<>();

    public void addUser(User user) {
        userList.add(user);
        this.add(user.toString()); // Adds string representation to visual AWT List
    }

    public void removeUser(int index) {
        if (index >= 0 && index < userList.size()) {
            userList.remove(index);
            this.remove(index); // Removes from visual AWT List
        }
    }

    public int getUserCount() {
        return userList.size();
    }

    public java.util.List<User> getAllUsers() {
        return new java.util.ArrayList<>(userList);
    }
}

