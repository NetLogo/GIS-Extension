package org.myworldgis.netlogo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.nlogo.agent.TreeAgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;
import org.nlogo.api.Argument;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.nvm.AssemblerAssistant;
import org.nlogo.nvm.CustomAssembled;
import org.nlogo.nvm.ExtensionContext;
import scala.Option;
import scala.Some;
import scala.collection.JavaConverters;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public strictfp class CreateTurtlesFromPoints {

    private static Syntax turtleCreationCommandSyntaxHelper(Object[] syntaxTokens) {
        scala.collection.immutable.List<Object> list = JavaConverters.asScalaBuffer(Arrays.asList(syntaxTokens)).toList();
        return Syntax.commandSyntax(list, Option.empty(), Option.empty(), "O---", Some.apply("-T--"), false, true);
    }

    private static abstract strictfp class TurtlesFromPoints extends GISExtension.Command implements CustomAssembled {

        public abstract Map<String, Integer> getPropertyNameToTurtleVarIndex(List<String> variableNamesList, VectorDataset.Property[] properties, Argument[] args) throws ExtensionException;

        public void performInternal(Argument args[], org.nlogo.api.Context context)
                throws ExtensionException, AgentException {
            ExtensionContext eContext = (ExtensionContext) context;
            org.nlogo.nvm.Context nvmContext = eContext.nvmContext();
            World world = (org.nlogo.agent.World) context.world();

            if (!(args[0].get() instanceof VectorDataset)) {
                throw new ExtensionException("Not a VectorDataset");
            }

            VectorDataset dataset = (VectorDataset) args[0].get();
            if (dataset.getShapeType() != VectorDataset.ShapeType.POINT) {
                throw new ExtensionException("Not a point dataset");
            }

            String breedName = args[1].getString().toUpperCase();
            Map<String, TreeAgentSet> breeds = world.breeds();
            TreeAgentSet agentSet;
            if (breedName.equalsIgnoreCase("turtles")) {
                agentSet = world.turtles();
            } else if (breeds.containsKey(breedName)) {
                agentSet = breeds.get(breedName);
            } else {
                throw new ExtensionException(breedName + " is not a defined breed");
            }

            int allTurtlesVarCount = world.getVariablesArraySize((org.nlogo.api.Turtle) null, world.turtles());
            int breedVarCount = world.getVariablesArraySize((org.nlogo.api.Turtle) null, agentSet);
            String[] variableNames = new String[breedVarCount];
            for (int i = 0; i < allTurtlesVarCount; i += 1) {
                variableNames[i] = world.turtlesOwnNameAt(i).toUpperCase();
            }
            for (int i = allTurtlesVarCount; i < breedVarCount; i += 1) {
                variableNames[i] = world.breedsOwnNameAt(agentSet, i).toUpperCase();
            }

            VectorDataset.Property[] properties = dataset.getProperties();
            List<String> variableNamesList = Arrays.asList(variableNames);

            Map<String, Integer> propertyNameToTurtleVarIndex = getPropertyNameToTurtleVarIndex(variableNamesList, properties, args);

            GISExtensionState state = GISExtension.getState();
            Collection<VectorFeature> features = dataset.getFeatures();
            for (VectorFeature feature : features) {
                Geometry geom = feature.getGeometry();
                for (int subPointIndex = 0; subPointIndex < geom.getNumGeometries(); subPointIndex++) {
                    Turtle turtle = world.createTurtle(agentSet);

                    for (Map.Entry<String, Integer> entry : propertyNameToTurtleVarIndex.entrySet()) {
                        turtle.setTurtleVariable(entry.getValue(), feature.getProperty(entry.getKey()));
                    }

                    Geometry thisPoint = geom.getGeometryN(subPointIndex);
                    Coordinate nlogoPosition = state.gisToNetLogo(thisPoint.getCoordinate(), null);
                    turtle.setTurtleOrLinkVariable("XCOR", nlogoPosition.x);
                    turtle.setTurtleOrLinkVariable("YCOR", nlogoPosition.y);

                    agentSet.add(turtle);
                }
            }

            nvmContext.runExclusiveJob(agentSet, nvmContext.ip + 1);
        }

        public void assemble(AssemblerAssistant assemblerAssistant) {
            assemblerAssistant.block();
            assemblerAssistant.done();
        }

        protected Map<String, Integer> getAutomaticPropertyNameToTurtleVarIndexMappings(List<String> variableNamesList, VectorDataset.Property[] properties) {
            HashMap<String, Integer> propertyNameToTurtleVarIndex = new HashMap<String, Integer>();
            for (VectorDataset.Property prop : properties) {
                String propertyName = prop.getName();
                if (propertyName.equalsIgnoreCase(("breed")) || propertyName.equalsIgnoreCase("who")) {
                    continue;
                }
                int index = variableNamesList.indexOf(propertyName.toUpperCase().replace(' ', '-'));
                if (index != -1) {
                    propertyNameToTurtleVarIndex.put(prop.getName(), index);
                }
            }
            return propertyNameToTurtleVarIndex;
        }
    }

    public static strictfp class TurtlesFromPointsAutomatic extends TurtlesFromPoints {

        public Syntax getSyntax() {
            return turtleCreationCommandSyntaxHelper(new Object[]{Syntax.WildcardType(), Syntax.StringType(), Syntax.CommandBlockType() | Syntax.OptionalType()});
        }

        public Map<String, Integer> getPropertyNameToTurtleVarIndex(List<String> variableNamesList, VectorDataset.Property[] properties, Argument[] args) {
            return getAutomaticPropertyNameToTurtleVarIndexMappings(variableNamesList, properties);
        }
    }

    public static strictfp class TurtlesFromPointsManual extends TurtlesFromPoints {

        final ExtensionException improperSyntaxException = new ExtensionException("The variable mapping must be of the form: [[\"property-name\" \"turtle-variable-name\"] [\"property-name\" \"turtle-variable-name\"] (etc.)]");

        public Syntax getSyntax() {
            return turtleCreationCommandSyntaxHelper(new Object[]{Syntax.WildcardType(), Syntax.StringType(), Syntax.ListType(), Syntax.CommandBlockType() | Syntax.OptionalType()});
        }

        public Map<String, Integer> getPropertyNameToTurtleVarIndex(List<String> variableNamesList, VectorDataset.Property[] properties, Argument[] args) throws ExtensionException {
            Map<String, Integer> propertyNameToTurtleVarIndexMappings = getAutomaticPropertyNameToTurtleVarIndexMappings(variableNamesList, properties);

            LogoList manualList = args[2].getList();
            for (Object pairing : manualList.javaIterable()){
                if (!(pairing instanceof LogoList)){
                    throw improperSyntaxException;
                }

                LogoList pairingList = ((LogoList) pairing);
                Object firstObj = pairingList.first();
                Object secondObj = pairingList.butFirst().first();

                if (!(firstObj instanceof String) || !(secondObj instanceof String)) {
                    throw improperSyntaxException;
                }

                String propertyName = (String) firstObj;
                String variableName = (String) secondObj;

                int index = variableNamesList.indexOf(variableName.toUpperCase());
                if (index != -1) {
                    propertyNameToTurtleVarIndexMappings.put(propertyName, index);
                } else {
                    throw new ExtensionException("There is no variable " + variableName + " defined. use turtles-own or <breeds>-own to define one.");
                }
            }
            return propertyNameToTurtleVarIndexMappings;
        }
    }
}
