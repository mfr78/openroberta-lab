package de.fhg.iais.roberta.worker;

import de.fhg.iais.roberta.bean.CodeGeneratorSetupBean;
import de.fhg.iais.roberta.bean.OraBean;
import de.fhg.iais.roberta.bean.UsedMethodBean;
import de.fhg.iais.roberta.components.Project;
import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.visitor.lang.codegen.AbstractLanguageVisitor;

/**
 * Uses the {@link AbstractLanguageVisitor} to visit the current AST and generate the robot's specific source code.
 */
public abstract class AbstractLanguageGeneratorWorker implements IWorker {

    /**
     * Returns the appropriate visitor for this worker. Used by subclasses to keep the execute method generic.
     * Could be removed in the future, when visitors are specified in the properties as well, or inferred from the worker name.
     *
     * @param project the project
     * @return the appropriate visitor for the current robot
     */
    protected abstract AbstractLanguageVisitor getVisitor(Project project, OraBean... beans);

    @Override
    public void execute(Project project) {
        OraBean usedHardwareBean = project.getWorkerResult("CollectedHardware");
        OraBean usedMethodBean = project.getWorkerResult("UsedMethods");
        OraBean usedMethodInHardwareBean = project.getWorkerResult("UsedMethodsInHardware");

        // Prepare bean for the code generation visitor
        CodeGeneratorSetupBean.Builder codeGenSetupBeanBuilder = new CodeGeneratorSetupBean.Builder();
        codeGenSetupBeanBuilder.setFileExtension(project.getSourceCodeFileExtension());
        codeGenSetupBeanBuilder.setHelperMethodFile(project.getRobotFactory().getPluginProperties().getStringProperty("robot.helperMethods"));
        if ( usedMethodBean != null ) { // Some robots may need additional helper methods, which are collected in this bean
            codeGenSetupBeanBuilder.addAdditionalEnums(((UsedMethodBean) usedMethodBean).getAdditionalEnums());
            codeGenSetupBeanBuilder.addUsedMethods(((UsedMethodBean) usedMethodBean).getUsedMethods());
        }
        if ( usedMethodInHardwareBean != null ) { // Some robots potentially need helper methods for their hardware
            codeGenSetupBeanBuilder.addAdditionalEnums(((UsedMethodBean) usedMethodInHardwareBean).getAdditionalEnums());
            codeGenSetupBeanBuilder.addUsedMethods(((UsedMethodBean) usedMethodInHardwareBean).getUsedMethods());
        }

        AbstractLanguageVisitor visitor = this.getVisitor(project, usedHardwareBean, codeGenSetupBeanBuilder.build());
        visitor.setStringBuilders(project.getSourceCode(), project.getIndentation());
        visitor.generateCode(project.isWithWrapping());
        project.setResult(Key.COMPILERWORKFLOW_PROGRAM_GENERATION_SUCCESS);
    }
}
