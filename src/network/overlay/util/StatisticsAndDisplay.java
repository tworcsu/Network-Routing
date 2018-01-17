package network.overlay.util;

import network.overlay.node.Registry;
import network.overlay.transport.TCPSender;
import network.overlay.wireformats.PullTrafficSummary;
import network.overlay.wireformats.TrafficSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by toddw on 2/11/17.
 */
public class StatisticsAndDisplay implements Runnable {
    private int sentTotal;
    private int receivedTotal;
    private long sumSentTotal;
    private long sumReceivedTotal;
    private int relayTotal;
    private ArrayList<TrafficSummary> nodes;
    private Registry node;

    public StatisticsAndDisplay(Registry node) {
        this.node = node;
        nodes = new ArrayList<>();
        sentTotal = 0;
        receivedTotal = 0;
        sumSentTotal = 0;
        sumReceivedTotal = 0;
        relayTotal = 0;
    }

    public void add(TrafficSummary summary) {
        sentTotal += summary.numberSent;
        receivedTotal += summary.numberReceived;
        sumSentTotal += summary.sumSent;
        sumReceivedTotal += summary.sumReceived;
        relayTotal += summary.numberRelayed;
        nodes.add(summary);
    }

    public int size() {
        return this.nodes.size();
    }

    public void printStats() {
        System.out.printf("%-20s%10s%10s%15s%15s%10s\n","Node","Sent","Received","Sum Sent","Sum Received","Relayed");
        System.out.println("--------------------------------------------------------------------------------");
        for (TrafficSummary n : nodes) {
            System.out.printf("%-20s%10d%10d%15d%15d%10d\n",
                    n.hostName + ":" + n.portNumber,n.numberSent,n.numberReceived,n.sumSent,n.sumReceived,n.numberRelayed);
        }
        System.out.printf("%-20s%10d%10d%15d%15d%10d\n",
                "Sum",sentTotal,receivedTotal,sumSentTotal,sumReceivedTotal,relayTotal);
    }

    @Override
    public void run() {
        try {
            //System.out.println("Going to sleep");
            TimeUnit.SECONDS.sleep(15);
            //System.out.println("Requesting traffic summaries");
            PullTrafficSummary pull = new PullTrafficSummary();
            for (TCPSender send : node.nodeRegistry.values()) {
                send.sendData(pull.getBytes());
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //while(this.size() != node.nodeRegistry.size()) {
            //System.out.println(this.size() + ":" + node.nodeRegistry.size());
        //}
        //this.printStats();
        //node.clearCompleted();

    }
}
