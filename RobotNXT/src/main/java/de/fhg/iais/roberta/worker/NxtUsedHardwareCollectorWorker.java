package de.fhg.iais.roberta.worker;

import java.util.ArrayList;
import java.util.List;

import de.fhg.iais.roberta.bean.UsedHardwareBean;
import de.fhg.iais.roberta.bean.UsedHardwareBean.Builder;
import de.fhg.iais.roberta.bean.UsedMethodBean;
import de.fhg.iais.roberta.components.Project;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.visitor.collect.NxtUsedHardwareCollectorVisitor;
import de.fhg.iais.roberta.visitor.validate.AbstractCollectorVisitor;

public final class NxtUsedHardwareCollectorWorker extends AbstractUsedHardwareCollectorWorker {
    @Override
    protected AbstractCollectorVisitor getVisitor(UsedHardwareBean.Builder builder, Project project) {
        return null; // as execute is overridden this is not needed
    }

    @Override
    public void execute(Project project) {
        UsedHardwareBean.Builder usedHardwareBeanBuilder = new UsedHardwareBean.Builder();
        UsedMethodBean.Builder usedMethodBeanBuilder = new UsedMethodBean.Builder();
        NxtUsedHardwareCollectorVisitor visitor = new NxtUsedHardwareCollectorVisitor(usedHardwareBeanBuilder, usedMethodBeanBuilder, project.getConfigurationAst());
        Iterable<ArrayList<Phrase<Void>>> tree = project.getProgramAst().getTree();
        collectGlobalVariables(tree, visitor);
        for ( List<Phrase<Void>> phrases : tree ) {
            for ( Phrase<Void> phrase : phrases ) {
                if ( phrase.getKind().getName().equals("MAIN_TASK") ) {
                    usedHardwareBeanBuilder.setProgramEmpty(phrases.size() == 2);
                } else {
                    phrase.accept(visitor);
                }
            }
        }
        UsedHardwareBean usedHardwareBean = usedHardwareBeanBuilder.build();
        project.addWorkerResult("CollectedHardware", usedHardwareBean);
        UsedMethodBean usedMethodBean = usedMethodBeanBuilder.build();
        project.addWorkerResult("UsedMethodsInHardware", usedMethodBean);
    }
}