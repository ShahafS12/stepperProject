package roles;

import java.util.List;

public class RoleDefinitionImpl implements RoleDefinition
{
    private String roleName;
    private String roleDescription;
    private List<String> flowsAllowed;
    public RoleDefinitionImpl(String roleName, String roleDescription, List<String> flowsAllowed) {
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
}
