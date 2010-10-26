package ch.idsia.tools;

import ch.idsia.benchmark.mario.engine.GlobalOptions;
import ch.idsia.benchmark.mario.simulation.SimulationOptions;

import java.awt.*;


/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey@idsia.ch
 * Date: Apr 12, 2009
 * Time: 7:49:07 PM
 * Package: .Tools
 */
public class EvaluationOptions extends SimulationOptions
{
final Point viewLocation = new Point(42, 42);

public EvaluationOptions() { super(); }

public void setUpOptions(String[] args)
{
    if (args != null)
        for (int i = 0; i < args.length - 1; i += 2)
            try
            {
                setParameterValue(args[i], args[i + 1]);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                // Basically we can push the red button to explaud the computer, since this case must happen never.
                System.err.println("Error: Wrong number of input parameters");
//                System.err.println("It is a perfect day to kill yourself with the yellow wall");
            }
    GlobalOptions.isVisualization = isVisualization();
    GlobalOptions.FPS = getFPS() /*GlobalOptions.FPS*/;
    GlobalOptions.isPauseWorld = isPauseWorld();
    GlobalOptions.isPowerRestoration = isPowerRestoration();
//        GlobalOptions.isTimer = isTimer();
}

public Boolean isExitProgramWhenFinished()
{
    return b(getParameterValue("-ewf"));
}

public void setExitProgramWhenFinished(boolean exitProgramWhenFinished)
{
    setParameterValue("-ewf", s(exitProgramWhenFinished));
}

public Point getViewLocation()
{
    viewLocation.x = i(getParameterValue("-vlx"));
    viewLocation.y = i(getParameterValue("-vly"));
    return viewLocation;
}

public Boolean isViewAlwaysOnTop()
{
    return b(getParameterValue("-vaot"));
}

public void setFPS(int fps)
{
    setParameterValue("-fps", s(fps));
    GlobalOptions.FPS = getFPS();
}

public Integer getFPS()
{
    return i(getParameterValue("-fps"));
}

public String getAgentFullLoadName()
{
    return getParameterValue("-ag");
}

//    public boolean isTimer()
//    {
//        return b(getParameterValue("-t"));
//    }

}