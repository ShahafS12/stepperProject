package roles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleManager
{
    private static Set<RoleDefinition> rolesSet;
    public RoleManager() {
        rolesSet = new HashSet<>();
        rolesSet.add(new RoleDefinitionImpl("All Flows", "All the roles in the system", new ArrayList<>()));
        rolesSet.add(new RoleDefinitionImpl("Read Only", "Read only access", new ArrayList<>()));
    }
    public List<RoleDefinition> getRoles() {
        return (List<RoleDefinition>) rolesSet;
    }
    public List<String> getRolesNames(){
        List<String> names = new ArrayList<>();
        for(RoleDefinition role : rolesSet){
            names.add(role.getRoleName());
        }
        return names;
    }
    public void addRole(RoleDefinitionImpl role) {
        rolesSet.add(role);
    }
    public void removeRole(String roleName) {
        rolesSet.remove(roleName);
    }
    public RoleDefinition getRole(String roleName) {
        for(RoleDefinition role : rolesSet) {
            if(role.getRoleName().equals(roleName)) {
                return role;
            }
        }
        return null;
    }
    public List<String> getRoleFlows(String roleName) {
        for(RoleDefinition role : rolesSet) {
            if(role.getRoleName().equals(roleName)) {
                return role.getFlowsAllowed();
            }
        }
        return null;
    }
    public void addFlowToRole(String roleName, String flowName) {
        for(RoleDefinition role : rolesSet) {
            if(role.getRoleName().equals(roleName)) {
                role.getFlowsAllowed().add(flowName);
            }
        }
    }

    public void updateRole(String roleName, List<String> flowsAllowed, List<String> usersAllowed)
    {
        for(RoleDefinition role : rolesSet) {
            if(role.getRoleName().equals(roleName)) {
                role.setFlowsAllowed(flowsAllowed);
                role.setUsersAssigned(usersAllowed);
            }
        }
    }
}
