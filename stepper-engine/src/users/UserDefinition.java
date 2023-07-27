package users;

import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import roles.RoleDefinitionImpl;

import java.util.Map;
import java.util.Set;

public interface UserDefinition {
    String getUsername();

    Set<RoleDefinitionImpl> getRoles();

    Map<Integer, FlowExecutionStatistics> getExecutionHistory();

    boolean isUserAdmin();

    void setRoles(Set<RoleDefinitionImpl> roles);

    void addRole(RoleDefinitionImpl role);

    void removeRole(String role);

    void addExecutionHistory(FlowExecutionStatistics statistics);

    void setManager(boolean isManager);

}