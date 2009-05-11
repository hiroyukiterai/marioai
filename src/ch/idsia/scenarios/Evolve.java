package ch.idsia.scenarios;

import ch.idsia.ai.Evolvable;
import ch.idsia.ai.agents.IAgent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.ai.agents.ai.SimpleMLPAgent;
import ch.idsia.ai.ea.ES;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.LOGGER;
import wox.serial.Easy;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class Evolve {

    final static int generations = 400; //completely enough. If not succeed within 400 generations, then it drops to suboptimum...
    final static int populationSize = 100;


    public static void main(String[] args) {
        EvaluationOptions options = new CmdLineOptions(args);
        options.setMaxAttempts(1);
        options.setPauseWorld(true);
        List<IAgent> bestAgents = new ArrayList<IAgent>();
        DecimalFormat df = new DecimalFormat("0000");
        for (int difficulty = 0; difficulty < 11; difficulty++)
        {
            System.out.println("New Evolve phase with difficulty = " + difficulty + " started.");
            Evolvable initial = new SimpleMLPAgent();

            options.setLevelDifficulty(difficulty);
            options.setAgent((IAgent)initial);

            options.setMaxFPS(true);
            options.setVisualization(false);

            Task task = new ProgressTask(options);
            ES es = new ES (task, initial, populationSize);

            for (int gen = 0; gen < generations; gen++)
            {
                es.nextGeneration();
                double bestResult = es.getBestFitnesses()[0];
//                LOGGER.println("Generation " + gen + " best " + bestResult, LOGGER.VERBOSE_MODE.INFO);
                System.out.println("Generation " + gen + " best " + bestResult);
                options.setVisualization(gen % 30 == 0 || bestResult > 4000);
                options.setMaxFPS(!(gen % 30 == 0 || bestResult > 4000));
                IAgent a = (IAgent) es.getBests()[0];
                a.setName(((IAgent)initial).getName() + df.format(gen));
                RegisterableAgent.registerAgent(a);
                bestAgents.add(a);
                double result = task.evaluate(a)[0];
//                LOGGER.println("trying: " + result, LOGGER.VERBOSE_MODE.INFO);
                options.setVisualization(false);
                options.setMaxFPS(true);
                if (result > 4000)
                    break; // Go to next difficulty.
            }
        }
        // TODO: log dir / log dump dir option
        // TODO: reduce number of different
        // TODO: -fq 30, -ld 1:15, 8 
        LOGGER.println("Saving bests... ", LOGGER.VERBOSE_MODE.INFO);

        options.setVisualization(true); int i = 0;
        for (IAgent bestAgent : bestAgents) {
            Easy.save(bestAgent, "bestAgent" +  df.format(i++) + ".xml");
        }

        LOGGER.println("Saved! Press return key to continue...", LOGGER.VERBOSE_MODE.INFO);
        try {System.in.read();        } catch (IOException e) {            e.printStackTrace();        }

//        for (IAgent bestAgent : bestAgents) {
//            task.evaluate(bestAgent);
//        }


        LOGGER.save("log.txt");
        System.exit(0);
    }
}