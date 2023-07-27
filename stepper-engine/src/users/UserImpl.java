package users;

import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserImpl implements UserDefinition
{
    private final String username;
    private final boolean isAdmin;
    private Set<String> roles;
    private Map<Integer, FlowExecutionStatistics> executionHistory;
    private int executionsCount;
    public UserImpl(String username, boolean isAdmin, Set<String> roles)
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
        this.roles = new HashSet<>();
    }
    public String getUsername()
    {
        return username;
    }
    public boolean isUserAdmin()
    {
        return isAdmin;
    }
    public Set<String> getRoles()
    {
        return roles;
    }
    public void setRoles(Set<String> roles)
    {
        this.roles = roles;
    }
    public void addRole(String role)
    {
        roles.add(role);
    }
    public void removeRole(String role)
    {
        roles.remove(role);
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

    public boolean isManager()
    {
        return roles.contains("Manager");
    }
}
