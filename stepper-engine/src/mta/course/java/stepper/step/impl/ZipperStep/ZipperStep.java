package mta.course.java.stepper.step.impl.ZipperStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.io.IOException;



public class ZipperStep extends AbstractStepDefinition {
    public ZipperStep()
    {
        super("Zipper", false);
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("OPERATION", DataNecessity.MANDATORY, "Operation type", DataDefinitionRegistry.Enumeration)); // need to add enumeratuib

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Zip operation result", DataDefinitionRegistry.STRING));
    }

    public StepResult invoke(StepExecutionContext context){
        String finalStepName = context.getStepAlias(this.name());
        String folderName = context.getDataValue(context.getAlias(finalStepName+"."+"SOURCE",String.class), String.class);
        String operationType = context.getDataValue(context.getAlias(finalStepName+"."+"OPERATION",String.class), String.class);

        String beforeStarting = "About to perform operation " + operationType + " on source" + folderName;
        context.addLogLine(finalStepName, beforeStarting);

        String output;

        if (operationType.equals("UNZIP")) {
            try {
                File sourceFile = new File(folderName);
                String parentPath = sourceFile.getParent();
                unzip(folderName, parentPath);
            }
            catch (Exception e ){
                String failedFolder = "The provided path: "+ folderName + " is not able to be unzip";
                context.addSummaryLine(finalStepName, failedFolder);
                output = failedFolder;
                context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT", String.class), output);
                return StepResult.FAILURE;
            }
        } else if (operationType.equals("ZIP")) {
            try{
                File sourceFile = new File(folderName);
                String zipFilePath = sourceFile + ".zip";
                zip(folderName, zipFilePath);
            }
            catch (Exception e){
                String failedFolder = "The provided path: "+ folderName + " is not able to be zip";
                context.addSummaryLine(finalStepName, failedFolder);
                output = failedFolder;
                context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT", String.class), output);
                return StepResult.FAILURE;
            }
        } else {
            String failedFolder = operationType + " is invalid operation type";
            context.addSummaryLine(finalStepName, failedFolder);
            output = failedFolder;
            context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT", String.class), output);
            return StepResult.FAILURE;
        }

        String successSum = "Finished the " + operationType + " successfully";
        context.addSummaryLine(finalStepName, successSum);

        output = "SUCCESS";
        context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT", String.class), output);
        return StepResult.SUCCESS;
    }

    public static void unzip(String zipFilePath, String destinationFolderPath) throws Exception {
        try {
            File destinationFolder = new File(destinationFolderPath);
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs();
            }

            byte[] buffer = new byte[1024];
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String entryPath = destinationFolderPath + File.separator + zipEntry.getName();

                if (zipEntry.isDirectory()) {
                    new File(entryPath).mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(entryPath);
                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }

                zipInputStream.closeEntry();
            }

            zipInputStream.close();

            System.out.println("Unzip completed successfully.");
        } catch (Exception e) {
            throw new Exception("Failed to unzip the file: " + e.getMessage());
        }
    }

    private static void zip(String sourcePath, String zipFilePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(fos);

        File sourceFile = new File(sourcePath);
        if (sourceFile.isDirectory()) {
            zipDirectory(sourceFile, sourceFile.getName(), zos);
        } else {
            zipFile(sourceFile, zos);
        }

        zos.close();
        fos.close();
    }

    private static void zipDirectory(File directory, String baseName, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[1024];

        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, baseName + File.separator + file.getName(), zos);
            } else {
                FileInputStream fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(baseName + File.separator + file.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
                fis.close();
            }
        }
    }

    private static void zipFile(File file, ZipOutputStream zos) throws IOException {
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(file);

        zos.putNextEntry(new ZipEntry(file.getName()));

        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }
}





