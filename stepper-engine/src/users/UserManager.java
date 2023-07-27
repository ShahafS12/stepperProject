package users;

import roles.RoleDefinition;
import roles.RoleDefinitionImpl;

import java.util.*;

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


    public Set<UserDefinition> getUsersSet(){
        return usersSet;
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
    public synchronized List<String> getUsersNames(){
        List<String> names = new ArrayList<>();
        for(UserDefinition user : usersSet){
            String username = user.getUsername();
            names.add(user.getUsername());
        }
        return names;
    }

    public boolean isUserExists(String username) {
        for(UserDefinition user : usersSet) {
            if(user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    public UserDefinition getUser(String username) {
        for(UserDefinition user : usersSet) {
            if(user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public void addRoleToUsers(List<String> usersAllowed, RoleDefinitionImpl role)
    {
        for(UserDefinition user : usersSet) {
            if(usersAllowed.contains(user.getUsername())) {
                user.addRole(role);
            }
        }
    }


    public void updateUser(String userName, List<String> selectedRoles, boolean isManager)
    {
        UserDefinition user = getUser(userName);
        if(user != null) {
            user.setRoles(new HashSet<>(selectedRoles));
            if(isManager) {
                user.addRole("Manager");
            }
            else {
                user.removeRole("Manager");
            }
        }
    }
}