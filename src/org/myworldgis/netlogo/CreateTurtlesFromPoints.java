package org.myworldgis.netlogo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.nlogo.agent.TreeAgentSet;
import org.nlogo.agent.Turtle;
import org.nlogo.agent.World;
import org.nlogo.api.AgentException;
import org.nlogo.api.Argument;
import org.nlogo.api.Color;
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
import java.util.Collections;

public strictfp class CreateTurtlesFromPoints {

    private static final Map<String, Double> localColorNameMap;
    static {
        Map<String, Double> tmpMap = new HashMap<String, Double>();
        for (int i = 0; i < Color.ColorNames().length; i ++) {
            tmpMap.put(Color.ColorNames()[i], Color.getColorNumberByIndex(i));
        }
        localColorNameMap = Collections.unmodifiableMap(tmpMap);
    }

    // The SyntaxJ convenience class doesn't currently support a command primitive that has a blockAgentClassString
    // Therefore, this function abstracts away the messy work of plugging inputs into the Scala base
    // Syntax.commandSyntax procedure to make the proper syntax for an observer primitive that creates a turtle
    // scope inside its command block. - James Hovet 3/15/21
    private static Syntax makeTurtleCreationCommandSyntax(Object[] syntaxTokens) {
        scala.collection.immutable.List<Object> list = JavaConverters.asScalaBuffer(Arrays.asList(syntaxTokens)).toList();
        return Syntax.commandSyntax(list, Option.empty(), Option.empty(), "O---", Some.apply("-T--"), false, true);
    }

    private static abstract strictfp class TurtlesFromPoints extends GISExtension.Command implements CustomAssembled {

        protected Map<String, Integer> getAutomaticPropertyNameToTurtleVarIndexMappings(List<String> variableNamesList, VectorDataset.Property[] properties) {
            HashMap<String, Integer> propertyNameToTurtleVarIndex = new HashMap<String, Integer>();
            for (VectorDataset.Property prop : properties) {
                String propertyName = prop.getName();
                int index = variableNamesList.indexOf(propertyName.toUpperCase().replace(' ', '-'));
                if (index != -1) {
                    propertyNameToTurtleVarIndex.put(prop.getName().toUpperCase(), index);
                }
            }
            return propertyNameToTurtleVarIndex;
        }

        protected abstract Map<String, Integer> getPropertyNameToTurtleVarIndex(List<String> variableNamesList, VectorDataset.Property[] properties, Argument[] args) throws ExtensionException;

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
                    // Copied from default turtle initialization code, otherwise all turtles would be black on creation - James Hovet 3/31/21
                    Turtle turtle = world.createTurtle(agentSet, nvmContext.job.random.nextInt(14), 0);
                    Geometry thisPoint = geom.getGeometryN(subPointIndex);
                    Coordinate nlogoPosition = state.gisToNetLogo(thisPoint.getCoordinate(), null);

                    // max-pxcor + 0.5 and max-pycor + 0.5 are illegal x and ycor's in NetLogo but are guaranteed to
                    // occur if you use `gis:set-world-envelope gis:envelope-of dataset` to set your world-envelope.
                    // This isn't a problem if world wrapping is on, but if it isn't we need to nudge them back into
                    // the legal area.
                    if (! world.wrappingAllowedInX() && nlogoPosition.x == world.maxPxcorBoxed() + 0.5) {
                        nlogoPosition.x = Math.nextDown(nlogoPosition.x);
                    }
                    if (! world.wrappingAllowedInY() && nlogoPosition.y == world.maxPycorBoxed() + 0.5) {
                        nlogoPosition.y = Math.nextDown(nlogoPosition.y);
                    }

                    turtle.setTurtleOrLinkVariable("XCOR", nlogoPosition.x);
                    turtle.setTurtleOrLinkVariable("YCOR", nlogoPosition.y);

                    for (Map.Entry<String, Integer> entry : propertyNameToTurtleVarIndex.entrySet()) {
                        Integer variableIndex = entry.getValue();
                        String variableName = entry.getKey();
                        Object valueToSetTo = feature.getProperty(variableName);

                        if ((valueToSetTo != null)
                                && variableIndex != world.turtlesOwnIndexOf("BREED")
                                && variableIndex != world.turtlesOwnIndexOf("XCOR")
                                && variableIndex != world.turtlesOwnIndexOf("YCOR")) {

                            if ((variableIndex == world.turtlesOwnIndexOf("COLOR") || variableIndex == world.turtlesOwnIndexOf("LABEL-COLOR")) && valueToSetTo instanceof String) {
                                String colorName = (String) valueToSetTo;
                                if (localColorNameMap.containsKey(colorName)) {
                                    valueToSetTo = localColorNameMap.get(colorName);
                                } else {
                                    throw new ExtensionException(colorName + " is not a supported color name. Only the default hues or netlogo color number representations are supported. see https://ccl.northwestern.edu/netlogo/docs/programming.html#colors for a list of default colors and a table of color number representations.");
                                }
                            }

                            if (variableIndex == world.turtlesOwnIndexOf("HIDDEN?") && valueToSetTo instanceof String) {
                                String stringValue = (String) valueToSetTo;
                                if (stringValue.equalsIgnoreCase("true") || Double.parseDouble(stringValue) == 1) {
                                    valueToSetTo = true;
                                } else if (stringValue.equalsIgnoreCase("false") || Double.parseDouble(stringValue) == 0) {
                                    valueToSetTo = false;
                                } else {
                                    throw new ExtensionException(stringValue + " is not a supported boolean value. Only true/false or 0/1 are accepted");
                                }
                            }

                            if (variableIndex == world.turtlesOwnIndexOf("HIDDEN?") && valueToSetTo instanceof Number) {
                                Double doubleValue = (Double) valueToSetTo;
                                if (doubleValue == 1) {
                                    valueToSetTo = true;
                                } else if (doubleValue == 0) {
                                    valueToSetTo = false;
                                } else {
                                    throw new ExtensionException(doubleValue.toString() + " is not a supported boolean value. Only true/false or 0/1 are accepted");
                                }
                            }

                            turtle.setTurtleVariable(variableIndex, valueToSetTo);
                        }
                    }

                    agentSet.add(turtle);
                }
            }

            nvmContext.runExclusiveJob(agentSet, nvmContext.ip + 1);
        }

        public void assemble(AssemblerAssistant assemblerAssistant) {
            assemblerAssistant.block();
            assemblerAssistant.done();
        }

    }

    public static strictfp class TurtlesFromPointsAutomatic extends TurtlesFromPoints {

        public Syntax getSyntax() {
            return makeTurtleCreationCommandSyntax(new Object[]{Syntax.WildcardType(), Syntax.StringType(), Syntax.CommandBlockType() | Syntax.OptionalType()});
        }

        public Map<String, Integer> getPropertyNameToTurtleVarIndex(List<String> variableNamesList, VectorDataset.Property[] properties, Argument[] args) {
            return getAutomaticPropertyNameToTurtleVarIndexMappings(variableNamesList, properties);
        }
    }

    public static strictfp class TurtlesFromPointsManual extends TurtlesFromPoints {

        static final String improperSyntaxExceptionMessage = "The variable mapping must be of the form: [[\"property-name\" \"turtle-variable-name\"] [\"property-name\" \"turtle-variable-name\"] (etc.)]";

        public Syntax getSyntax() {
            return makeTurtleCreationCommandSyntax(new Object[]{Syntax.WildcardType(), Syntax.StringType(), Syntax.ListType(), Syntax.CommandBlockType() | Syntax.OptionalType()});
        }

        public Map<String, Integer> getPropertyNameToTurtleVarIndex(List<String> variableNamesList, VectorDataset.Property[] properties, Argument[] args) throws ExtensionException {
            Map<String, Integer> propertyNameToTurtleVarIndexMappings = getAutomaticPropertyNameToTurtleVarIndexMappings(variableNamesList, properties);

            LogoList manualList = args[2].getList();
            for (Object pairing : manualList.javaIterable()) {
                if (!(pairing instanceof LogoList)) {
                    throw new ExtensionException(improperSyntaxExceptionMessage);
                }

                LogoList pairingList = ((LogoList) pairing);
                if (pairingList.length() != 2) {
                    throw new ExtensionException(improperSyntaxExceptionMessage);
                }

                Object firstObj = pairingList.first();
                Object secondObj = pairingList.butFirst().first();

                if (!(firstObj instanceof String) || !(secondObj instanceof String)) {
                    throw new ExtensionException(improperSyntaxExceptionMessage);
                }

                String propertyName = ((String) firstObj).toUpperCase();
                String variableName = ((String) secondObj).toUpperCase();

                int index = variableNamesList.indexOf(variableName);
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
