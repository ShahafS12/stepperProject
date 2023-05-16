package mta.course.java.stepper.step.impl.PropertiesExporterStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.util.ArrayList;

public class PropertiesExporterStep extends AbstractStepDefinition {
    public PropertiesExporterStep() {
        super("Properties Exporter", true);
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Properties export result", DataDefinitionRegistry.STRING));
    }

    public StepResult invoke(StepExecutionContext context) {
        String finalStepName = context.getStepAlias(this.name());
        RelationData sourceTable = context.getDataValue(context.getAlias(finalStepName+"."+"SOURCE",RelationData.class), RelationData.class);

        String beforeLog = "About to process " + sourceTable.getNumRows() + " lines of data";
        context.addLogLine(finalStepName, beforeLog);

        if(sourceTable.getNumRows() == 0){
            String summaryLine = "The table was empty!";
            context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT",String.class), "");
            context.addLogLine(finalStepName, summaryLine);
            context.addSummaryLine(finalStepName, summaryLine);
            return StepResult.WARNING;
        }

        ArrayList<String> columns = sourceTable.getColumns();
        int numRows = sourceTable.getNumRows();
        ArrayList<RelationData.SingleRow> rows = sourceTable.getRows();
        String result = null, tmp;

        for (int i=0; i<numRows; i++){
            ArrayList<String> singleRowList = rows.get(i).getData();
            int counterColumns = 0;
            for (String str:singleRowList){
                tmp = "row-" + (i+1) + "." + columns.get(counterColumns) + "=" + str;
                if (result == null)
                    result = tmp;
                else
                    result = result + "\n" + tmp;
                counterColumns++;
            }
            //result = "\n" + result;
        }

        String afterLog = "Extracted total of " + result;
        context.addLogLine(finalStepName, afterLog);

        context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT",String.class), result);

        String summaryLine = "Extracted table successfully!";
        context.addSummaryLine(finalStepName, summaryLine);
        return StepResult.SUCCESS;
    }
}