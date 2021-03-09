package org.myworldgis.netlogo;

import org.nlogo.agent.AgentSetBuilder;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.Argument;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.AgentKindJ;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.nvm.AssemblerAssistant;
import org.nlogo.nvm.CustomAssembled;
import org.nlogo.nvm.ExtensionContext;
import scala.Int;
import scala.None;
import scala.Option;
import scala.Some;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.List;

public strictfp class CreateTurtlesFromPoints  {

    public static strictfp class Automatic extends GISExtension.Command implements CustomAssembled {

//        public String getAgentClassString() {
//            return "O";
//        }


        public Syntax getSyntax() {
            Object[] arr = new Object[] {Syntax.WildcardType(), Syntax.StringType(), Syntax.CommandBlockType() | Syntax.OptionalType()};
            List<Object> l = Arrays.asList(arr);
            scala.collection.immutable.List<Object> list = JavaConverters.asScalaBuffer(l).toList();
            Syntax syntax = Syntax.commandSyntax(list, Option.empty(), Option.empty(), "O---", Some.apply("-T--"), false, true);
            return syntax;
        }

        public void performInternal (Argument args[], org.nlogo.api.Context context) 
                throws ExtensionException {
            System.out.println(args);

            ExtensionContext eContext = (ExtensionContext) context;
            org.nlogo.nvm.Context nvmContext = eContext.nvmContext();

            World agentWorld = (org.nlogo.agent.World) context.world();
            Turtle turtle = agentWorld.createTurtle(agentWorld.turtles());
            AgentSetBuilder agentSetBuilder = new AgentSetBuilder(AgentKindJ.Turtle(), 1);
            eContext.workspace().joinForeverButtons(turtle);
            agentSetBuilder.add(turtle);

            nvmContext.runExclusiveJob(agentSetBuilder.build(), nvmContext.ip + 1);
        }

        public void assemble(AssemblerAssistant assemblerAssistant){
            assemblerAssistant.block();
            assemblerAssistant.done();
        }

    }

//    public static strictfp class Manual extends GISExtension.Command {
//
//        public String getAgentClassString() {
//            return "O";
//        }
//
//        public Syntax getSyntax() {
//            return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(),
//                                                    Syntax.StringType(),
//                                                    Syntax.ListType(),
//                                                    Syntax.CommandBlockType() | Syntax.OptionalType()});
//        }
//
//        public void performInternal (Argument args[], org.nlogo.api.Context context)
//                throws ExtensionException {
//                    System.out.println(args);
//        }
//    }
}
