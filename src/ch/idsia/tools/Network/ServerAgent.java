package ch.idsia.tools.Network;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.environments.Environment;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 30, 2009
 * Time: 9:43:27 PM
 * Package: ch.idsia.tools.Network
 */

public class ServerAgent extends RegisterableAgent implements Agent
{
    Server server = null;
    private int port;

    public ServerAgent(int port, boolean enable)
    {
        super("ServerAgent");
        this.port = port;
        if (enable)
        {
            createServer(port);
        }
    }

    // A tiny bit of singletone-like concept. Server is created ones for each egent. Basically we are not going
    // To create more than one ServerAgent at a run, but this flexibility allows to add this feature with certain ease.
    private void createServer(int port) {
        this.server = new Server(port, Environment.numberOfObservationElements, Environment.numberOfButtons);
        this.name += server.getClientName();
    }

    public boolean isAvailable()
    {
        return (server != null) && server.isClientConnected();
    }

    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        if (server == null)
            this.createServer(port);
    }

    private void sendLevelSceneObservation(Environment observation) throws IOException
    {
        byte[][] levelScene = observation.getLevelSceneObservation();

        String tmpData = "" +
                observation.mayMarioJump() + " " + observation.isMarioOnGround();
        for (int x = 0; x < levelScene.length; ++x)
        {
            for (int y = 0; y < levelScene.length; ++y)
            {
                tmpData += " " + (levelScene[x][y]);
            }
        }
        server.sendSafe(tmpData);
        // TODO: StateEncoderDecoder.Encode.Decode.  zip, gzip do not send mario position. zero instead for better compression.
    }

    private boolean[] receiveAction() throws IOException, NullPointerException
    {
        String data = server.recvSafe();
        boolean[] ret = new boolean[Environment.numberOfButtons];
        String s = "[";
        for (int i = 0; i < Environment.numberOfButtons; ++i)
        {
            ret[i] = (data.charAt(i) == '1');
            s += data.charAt(i);
        }
        s += "]";

        System.out.println("ServerAgent: action received :" + s);
        return ret;
    }

    public boolean[] getAction(Environment observation)
    {
        try
        {
            System.out.println("ServerAgent: sending observation...");
            sendLevelSceneObservation(observation);
            action = receiveAction();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("I/O Communication Error");
            reset();
        }
        return action;
    }

    public AGENT_TYPE getType()
    {
        return Agent.AGENT_TYPE.TCP_SERVER;
    }
}