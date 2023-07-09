package users;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
Adding and retrieving users is synchronized and in that manner - these actions are thread safe
Note that asking if a user exists (isUserExists) does not participate in the synchronization and it is the responsibility
of the user of this class to handle the synchronization of isUserExists with other methods here on it's own
 */
public class UserManager {

    private final Set<UserDefinition> usersSet;
    private boolean hasAdmin;

    public UserManager() {
        usersSet = new HashSet<>();
    }


    public synchronized void addUser(String username) {
        UserDefinition user = new UserImpl(username, false);
        usersSet.add(user);
    }
    public synchronized void addAdmin() {
        if(!hasAdmin) {
            UserDefinition user = new UserImpl("admin", true);
            usersSet.add(user);
            hasAdmin = true;
        }
        else {
            throw new RuntimeException("Admin already exists");
        }
    }

    public synchronized void removeUser(String username) {
        usersSet.remove(username);
    }

    public synchronized Set<UserDefinition> getUsers() {
        return Collections.unmodifiableSet(usersSet);
    }

    public boolean isUserExists(String username) {
        for(UserDefinition user : usersSet) {
            if(user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
}