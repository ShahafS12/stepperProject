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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


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
        String operationType = context.getDataValue(context.getAlias(finalStepName+"."+"FILTER",String.class), String.class);

        String beforeStarting = "About to perform operation " + operationType + " on source" + folderName;
        context.addLogLine(finalStepName, beforeStarting);

        if (operationType.equals("unzip")) {
            try {
                File sourceFile = new File(folderName);
                String parentPath = sourceFile.getParent();
                unzip(folderName, parentPath);
            }
            catch (Exception e ){
                String failedFolder = "The provided path: "+ folderName + " is not able to be unzip";
                return StepResult.FAILURE;
            }
        } else if (operationType.equals("zip")) {
            try{
                File sourceFile = new File(folderName);
                String parentPath = sourceFile.getParent();
                zip(folderName, parentPath);
            }
            catch (Exception e){
                String failedFolder = "The provided path: "+ folderName + " is not able to be zip";
                return StepResult.FAILURE;
            }
        } else {
            String failedFolder = operationType + " is invalid operation type";
            return StepResult.FAILURE;
        }

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


    public static void zip(String sourcePath, String zipFilePath) throws Exception {
        try {
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fos);
            File sourceFile = new File(sourcePath);

            if (sourceFile.isDirectory()) {
                zipDirectory(sourceFile, sourceFile.getName(), zipOutputStream);
            } else {
                zipFile(sourceFile, sourceFile.getName(), zipOutputStream);
            }

            zipOutputStream.close();
            fos.close();

            System.out.println("Zip completed successfully.");
        } catch (Exception e) {
            throw new Exception("Failed to create the zip file: " + e.getMessage());
        }
    }

    private static void zipDirectory(File directory, String parentPath, ZipOutputStream zipOutputStream) throws Exception {
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                zipDirectory(file, parentPath + File.separator + file.getName(), zipOutputStream);
            } else {
                zipFile(file, parentPath, zipOutputStream);
            }
        }
    }

    private static void zipFile(File file, String parentPath, ZipOutputStream zipOutputStream) throws Exception {
        byte[] buffer = new byte[1024];
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(parentPath + File.separator + file.getName());
        zipOutputStream.putNextEntry(zipEntry);

        int length;
        while ((length = fis.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, length);
        }

        fis.close();
        zipOutputStream.closeEntry();
    }




}