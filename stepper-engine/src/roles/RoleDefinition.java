package roles;

import java.util.List;

public interface RoleDefinition
{
    String getRoleName();
    String getRoleDescription();
    List<String> getFlowsAllowed();
    List<String> getUsersAssigned();
    void setFlowsAllowed(List<String> flowsAllowed);

    void setUsersAssigned(List<String> usersAllowed);

    public void addUserAssigned(String username);
}
