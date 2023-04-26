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
        super("CSVExporter", true);
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        RelationData sourceTable = context.getDataValue("SOURCE", RelationData.class);

        String beforeLog = "About to process " + sourceTable.getNumRows() + " line of data";
        context.addLogLine("CSVExporter", beforeLog);

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

        context.storeDataValue("RESULT", result);

        if(sourceTable.isEmpty()){ // TODO: the content will have 1 line of columns?! check it! its written in the explanation word
            String emptyTable = "The table is EMPTY!";
            context.addLogLine("CSVExporter", emptyTable);
            context.addSummaryLine("CSVExporter", emptyTable);
            return StepResult.WARNING;
        }

        String summaryLine = "Successfully created the table!";
        context.addSummaryLine("CSVExporter", summaryLine);
        return StepResult.SUCCESS;
    }
}