package org.jgodeye.trace.core;

import java.lang.instrument.Instrumentation;

import org.jgodeye.common.Constants;
import org.jgodeye.common.Context;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher.Junction;
import net.bytebuddy.matcher.ElementMatchers;

public class Bootstrap {
    
    public static void bind(String jgodeyeHome, Instrumentation inst) {
        init(jgodeyeHome);
        build(inst);
    }
    
    private static void init(String jgodeyeHome) {
        Context.initContext(jgodeyeHome);
        Logger.initLogger();
    }
    
    private static void build(Instrumentation inst) {

        new AgentBuilder.Default().type(initJunction())
            .transform(
                ((builder, typeDescription, classLoader, module) ->
                 builder.visit(net.bytebuddy.asm.Advice.to(Advice.class).on(ElementMatchers.any())))
            )
            .installOn(inst);
    }

    private static Junction<NamedElement> initJunction() {
        if (Constants.JGODEYE_TRACE_STARTWITH_ALL.equals(Context.getJgodeyeTraceStartwith())) {
            return ElementMatchers.any();
        }
        return ElementMatchers.nameStartsWith(Context.getJgodeyeTraceStartwith());
    }
}
