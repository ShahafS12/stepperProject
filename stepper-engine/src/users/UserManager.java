package users;

import roles.RoleDefinition;
import roles.RoleDefinitionImpl;
import roles.RoleManager;

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


    public void updateUser(String userName, List<String> selectedRoles, boolean isManager, RoleManager roleManager)
    {
        UserDefinition user = getUser(userName);
        Set<RoleDefinition> roles = roleManager.getRoles();
        if(user != null) {
            for (String role : selectedRoles) {
                RoleDefinitionImpl roleInUser = user.getRole(role);
                if (roleInUser == null){
                    RoleDefinitionImpl newRole = (RoleDefinitionImpl) roleManager.getRole(role);
                    newRole.addUserAssigned(user.getUsername());
                    user.addRole(newRole);
                }
            }
            for (RoleDefinitionImpl role : user.getRoles()){
                if (!selectedRoles.contains(role.getRoleName())){
                    role.removeUserAssigned(user.getUsername());
                    user.removeRole(role.getRoleName());
                }
            }
            if(isManager) {
                user.setManager(true);
                RoleDefinition roleAllFlows = roleManager.getRole("All Flows");
                roleManager.getRole("All Flows").addUserAssigned(user.getUsername());
                user.addRole((RoleDefinitionImpl) roleAllFlows);
            }
            else {
                user.setManager(false);
            }
        }
    }
}