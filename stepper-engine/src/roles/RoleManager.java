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
        rolesSet.add(new RoleDefinitionImpl("admin", "admin", null));
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
    public void addRole(String roleName, String roleDescription, List<String> flowsAllowed) {
        RoleDefinition role = new RoleDefinitionImpl(roleName, roleDescription, flowsAllowed);
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
}
