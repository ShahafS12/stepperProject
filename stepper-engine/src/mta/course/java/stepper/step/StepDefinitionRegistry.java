package mta.course.java.stepper.step;

import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.impl.CSVExporterStep.CSVExporterStep;
import mta.course.java.stepper.step.impl.CommandLineStep.CommandLineStep;
import mta.course.java.stepper.step.impl.FileDumperStep.FileDumperStep;
import mta.course.java.stepper.step.impl.FilesContentExtractorStep.FilesContentExtractorStep;
import mta.course.java.stepper.step.impl.FilesDeleterStep.FilesDeleterStep;
import mta.course.java.stepper.step.impl.FilesRenamerStep.FilesRenamerStep;
import mta.course.java.stepper.step.impl.HelloWorldStep;
import mta.course.java.stepper.step.impl.HttpCallStep.HttpCallStep;
import mta.course.java.stepper.step.impl.JsonDataExtractorStep.JsonDataExtractorStep;
import mta.course.java.stepper.step.impl.PersonDetailsStep;
import mta.course.java.stepper.step.impl.PropertiesExporterStep.PropertiesExporterStep;
import mta.course.java.stepper.step.impl.SpendSomeTimeStep.SpendSomeTimeStep;
import mta.course.java.stepper.step.impl.CollectFilesInFolderStep.CollectFilesInFolderStep;
import mta.course.java.stepper.step.impl.ToJsonStep.ToJsonStep;
import mta.course.java.stepper.step.impl.ZipperStep.ZipperStep;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    CSV_EXPORTER(new CSVExporterStep()),
    FILE_DUMPER(new FileDumperStep()),
    FILES_CONTENT_EXTRACTOR(new FilesContentExtractorStep()),
    FILES_DELETER(new FilesDeleterStep()),
    FILES_RENAMER(new FilesRenamerStep()),
    PROPERTIES_EXPORTER(new PropertiesExporterStep()),
    ZIPPER(new ZipperStep()),
    COMMAND_LINE(new CommandLineStep()),
    TO_JSON(new ToJsonStep()),
    HTTP_CALL(new HttpCallStep()),
    JSON_DATA_EXTRACTOR(new JsonDataExtractorStep()),
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    public static StepDefinitionRegistry fromString(String name) {
        name = name.replaceAll(" ", "_");
        for (StepDefinitionRegistry registry : values()) {
            if (registry.name().equalsIgnoreCase(name)) {
                return registry;
            }
        }
        return null;
    }
}
