package mta.course.java.stepper.dd.impl;

import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.dd.impl.Number.NumberDataDefinition;
import mta.course.java.stepper.dd.impl.list.ListDataDefenition;
import mta.course.java.stepper.dd.impl.mapping.MappingDataDefenition;
import mta.course.java.stepper.dd.impl.doubledd.DoubleDataDefinition;
import mta.course.java.stepper.dd.impl.relation.RelationDataDefinition;
import mta.course.java.stepper.dd.impl.string.StringDataDefinition;

public enum DataDefinitionRegistry implements DataDefinition{
    STRING(new StringDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    RELATION(new RelationDataDefinition()),
    LIST(new ListDataDefenition()),
    MAP (new MappingDataDefenition()),
    Number(new NumberDataDefinition());

    DataDefinitionRegistry(DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    private final DataDefinition dataDefinition;

    @Override
    public String getName() {
        return dataDefinition.getName();
    }

    @Override
    public boolean isUserFriendly() {
        return dataDefinition.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return dataDefinition.getType();
    }

    public <T> T getValue(String name) {
        return dataDefinition.getValue(name);
    }
}
