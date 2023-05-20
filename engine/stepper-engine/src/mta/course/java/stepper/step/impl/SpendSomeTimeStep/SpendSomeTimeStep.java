package mta.course.java.stepper.step.impl.SpendSomeTimeStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

public class SpendSomeTimeStep extends AbstractStepDefinition {
    public SpendSomeTimeStep() {
        super("Spend Some Time", true);
        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY, "Total sleeping time(sec)", DataDefinitionRegistry.Number));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        String finalStepName = context.getStepAlias(this.name());
        int secondToSpend = (int) context.getDataValue(context.getAlias(finalStepName+"."+"TIME_TO_SPEND",Number.class), Number.class);
        if (secondToSpend <= 0){
            String negativeTimeToSpend = "Cannot sleep non positive number of time (" + secondToSpend + ")";
            context.addLogLine(finalStepName, negativeTimeToSpend);
            context.addSummaryLine(finalStepName, negativeTimeToSpend);
            return StepResult.FAILURE;
        }

        String BeforeSleeping = "About to sleep for " + secondToSpend + " seconds...";
        context.addLogLine(finalStepName, BeforeSleeping);

         Thread myThread =  new Thread(() -> {
            sleepForSomeTime(secondToSpend*1000);
        });
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return StepResult.FAILURE;
        }
        String afterSleeping = "Done sleeping...";
        context.addLogLine(finalStepName, afterSleeping);
        context.addSummaryLine(finalStepName, afterSleeping);
        return StepResult.SUCCESS;
    }

    private static void sleepForSomeTime(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }
    }
}
