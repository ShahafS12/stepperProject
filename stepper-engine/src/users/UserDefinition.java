package users;

import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;

import java.util.Map;
import java.util.Set;

public interface UserDefinition
{
    String getUsername();
    Set<String> getRoles();
    Map<Integer, FlowExecutionStatistics> getExecutionHistory();
    boolean isUserAdmin();
}
