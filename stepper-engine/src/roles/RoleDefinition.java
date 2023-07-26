package roles;

import java.util.List;

public interface RoleDefinition
{
    String getRoleName();
    String getRoleDescription();
    List<String> getFlowsAllowed();
    List<String> getUsersAssigned();

}
