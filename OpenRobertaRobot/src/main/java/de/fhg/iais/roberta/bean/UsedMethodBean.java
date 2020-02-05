package de.fhg.iais.roberta.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Container for used methods.
 */
public class UsedMethodBean implements OraBean {
    private final Set<Enum<?>> usedMethods = new HashSet<>(); // All needed helper methods as a Set

    private final Collection<Class<? extends Enum<?>>> additionalEnums = new ArrayList<>();

    public Set<Enum<?>> getUsedMethods() {
        return Collections.unmodifiableSet(this.usedMethods);
    }

    public Collection<Class<? extends Enum<?>>> getAdditionalEnums() {
        return Collections.unmodifiableCollection(this.additionalEnums);
    }

    public static class Builder {
        private final UsedMethodBean usedMethodBean = new UsedMethodBean();

        public Builder addUsedMethod(Enum<?> usedMethod) {
            this.usedMethodBean.usedMethods.add(usedMethod);
            return this;
        }

        public Builder addAdditionalEnums(Collection<Class<? extends Enum<?>>> additionalEnums) {
            this.usedMethodBean.additionalEnums.addAll(additionalEnums);
            return this;
        }

        public UsedMethodBean build() {
            return this.usedMethodBean;
        }
    }
}
