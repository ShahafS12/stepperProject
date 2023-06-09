package mta.course.java.stepper.step.impl.CSVExporterStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.util.ArrayList;

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
        super("CSV Exporter", true);
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        String finalStepName = context.getStepAlias(this.name());
        RelationData sourceTable = context.getDataValue(context.getAlias(finalStepName+"."+"SOURCE",RelationData.class), RelationData.class);

        String beforeLog = "About to process " + sourceTable.getNumRows() + " line of data";
        context.addLogLine(finalStepName, beforeLog);

        ArrayList<String> columnsList =sourceTable.getColumns();
        String columns = String.join(",",columnsList);
        String result = String.join("\n", columns);

        int numRows = sourceTable.getNumRows();
        ArrayList<RelationData.SingleRow> rows = sourceTable.getRows();

        for (int i=0; i<numRows; i++){
            ArrayList<String> singleRowList = rows.get(i).getData();
            String singleRowDataString = String.join(",", singleRowList);
            result = result + "\n" + singleRowDataString;
        }

        context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT",String.class), result);

        if(numRows == 0){
            String emptyTable = "The table is EMPTY!";
            context.addLogLine(finalStepName, emptyTable);
            context.addSummaryLine(finalStepName, emptyTable);
            return StepResult.WARNING;
        }

        String summaryLine = "Successfully created the table!";
        context.addSummaryLine(finalStepName, summaryLine);
        return StepResult.SUCCESS;
    }
}