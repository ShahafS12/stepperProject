package users;

import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import roles.RoleDefinitionImpl;

import java.util.*;

public class UserImpl implements UserDefinition
{
    private final String username;
    private final boolean isAdmin;
    private boolean isManager  = false;
    private Set<RoleDefinitionImpl> roles;
    private Map<Integer, FlowExecutionStatistics> executionHistory;
    private int executionsCount;
    public UserImpl(String username, boolean isAdmin, Set<RoleDefinitionImpl> roles)
    {
        this.username = username;
        this.isAdmin = isAdmin;
        this.roles = roles;
        executionsCount = 0;
    }
    public UserImpl(String username, boolean isAdmin)
    {
        this.username = username;
        this.isAdmin = isAdmin;
        executionsCount = 0;

        roles = new HashSet<RoleDefinitionImpl>();
    }
    public String getUsername()
    {
        return username;
    }
    public boolean isUserAdmin()
    {
        return isAdmin;
    }
    public Set<RoleDefinitionImpl> getRoles()
    {
        return roles;
    }
    public List<String> getRolesNames()
    {
        List<String> rolesNames = new ArrayList<>();
        for (RoleDefinitionImpl role : roles)
        {
            rolesNames.add(role.getRoleName());
        }
        return rolesNames;
    }
    public void setRoles(Set<RoleDefinitionImpl> roles)
    {
        this.roles = roles;
    }
    public void addRole(RoleDefinitionImpl role)
    {
        roles.add(role);
    }
    public void removeRole(String role)
    {
        for (RoleDefinitionImpl roleDefinition : roles)
        {
            if (roleDefinition.getRoleName().equals(role))
            {
                roles.remove(roleDefinition);
                return;
            }
        }

    }
    public Map<Integer, FlowExecutionStatistics> getExecutionHistory()
    {
        return executionHistory;
    }
    public void addExecutionHistory(FlowExecutionStatistics statistics)
    {
        executionHistory.put(executionsCount, statistics);
        executionsCount++;
    }

    @Override
    public void setManager(boolean isManager) {
        this.isManager = isManager;
    }

    @Override
    public RoleDefinitionImpl getRole(String roleName) {
        for (RoleDefinitionImpl role : roles)
        {
            if (role.getRoleName().equals(roleName))
            {
                return role;
            }
        }
        return null;
    }

    public boolean isManager() {
        return isManager;
    }


}
