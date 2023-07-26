package roles;

import java.util.ArrayList;
import java.util.List;

public class RoleDefinitionImpl implements RoleDefinition
{
    private String roleName;
    private String roleDescription;
    private List<String> flowsAllowed;
    private List<String> usersAssigned;
    public RoleDefinitionImpl(String roleName, String roleDescription, List<String> flowsAllowed) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.flowsAllowed = flowsAllowed;
        usersAssigned = new ArrayList<>();
    }
    public RoleDefinitionImpl(String roleName, String roleDescription, List<String> flowsAllowed, List<String> usersAssigned) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.flowsAllowed = flowsAllowed;
    }
    public String getRoleName() {
        return roleName;
    }
    public String getRoleDescription() {
        return roleDescription;
    }
    public List<String> getFlowsAllowed() {
        return flowsAllowed;
    }
    public List<String> getUsersAssigned() {
        return usersAssigned;
    }
}
