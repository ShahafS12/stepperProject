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
        super("PropertiesExporter", true);
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Properties export result", DataDefinitionRegistry.STRING));
    }

    public StepResult invoke(StepExecutionContext context) {
        RelationData sourceTable = context.getDataValue("SOURCE", RelationData.class);

        String beforeLog = "About to process " + sourceTable.getNumRows() + " lines of data";
        context.addLogLine("PropertiesExporter", beforeLog);

        ArrayList<String> columns = sourceTable.getColumns();
        int numRows = sourceTable.getNumRows();
        ArrayList<RelationData.SingleRow> rows = sourceTable.getRows();
        String result = null, tmp;

        for (int i=0; i<numRows; i++){
            ArrayList<String> singleRowList = rows.get(i).getData();
            int counterColumns = 0;
            for (String str:singleRowList){
                tmp = "row-" + (i+1) + "." + columns.get(counterColumns) + "=" + str;
                result = result + "\n" + tmp;
                counterColumns++;
            }
            //result = "\n" + result;
        }

        String afterLog = "Extracted total of " + result;
        context.addLogLine("PropertiesExporter", afterLog);

        context.storeDataValue("RESULT", result);

        if(sourceTable.isEmpty()){
            String summaryLine = "The table was empty!";
            context.addLogLine("PropertiesExporter", summaryLine);
            context.addSummaryLine("PropertiesExporter", summaryLine);
            return StepResult.WARNING;
        }

        String summaryLine = "Extracted table successfully!";
        context.addSummaryLine("PropertiesExporter", summaryLine);
        return StepResult.SUCCESS;
    }
}