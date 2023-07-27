package roles;

import java.util.ArrayList;
import java.util.List;

public class RoleDefinitionImpl implements RoleDefinition
{
    private String roleName;
    private String roleDescription;
    private List<String> flowsAllowed;
    private List<String> usersAssigned;
    private boolean isManager;
    public RoleDefinitionImpl(String roleName, String roleDescription, List<String> flowsAllowed) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.flowsAllowed = flowsAllowed;
        usersAssigned = new ArrayList<>();
        isManager = false;
    }
    public RoleDefinitionImpl(String roleName, String roleDescription, List<String> flowsAllowed, List<String> usersAssigned) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.flowsAllowed = flowsAllowed;
        this.usersAssigned = usersAssigned;
        isManager = false;
    }
    public String getRoleName() {
        return roleName;
    }
    public String getRoleDescription() {
        return roleDescription;
    }
    public boolean isManager() {
        return isManager;
    }
    public void setManager(boolean manager) {
        isManager = manager;
    }
    public List<String> getFlowsAllowed() {
        return flowsAllowed;
    }
    public List<String> getUsersAssigned() {
        return usersAssigned;
    }
    public void setFlowsAllowed(List<String> flowsAllowed) {
        this.flowsAllowed = flowsAllowed;
    }
    public void setUsersAssigned(List<String> usersAssigned) {
        this.usersAssigned = usersAssigned;
    }
}
