package simplewhiteboard;

/**
 *
 * @author dha13jyu
 */
public class LogicalClock {

    private long globalTime;
    private long localTime;
    private int noOfPeers;
    private Peer peer;

    //vote variables?
    private int yesVotes;
    private int noVotes;

    /**
     * Essentially a modified 2-phase (3 phase? Need to check) commit. Makes
     * more sense personally as it reduces network traffic comapared to totally
     * ordered multicast. Order of events - System sends request from one node
     * to others & increments local clock - The request is the timestamp you
     * *want* - Any system that responds with "yes" must increment their local
     * clock - Any system that responds with "no" stays the same - Once the
     * sender gets 51%+ "yes" votes, it sends a message with a clock time and a
     * payload - Recipients of this specific message must update their clock
     * accordingly, as it is the result of a poll - If the sender gets 50%-
     * "yes" votes, it increments the value it is asking for and tries again
     */
    public LogicalClock() {
        globalTime = 0;
        noOfPeers = 0;
    }

    public LogicalClock(Peer peer, long globalTime, int noOfPeers) {
        this.globalTime = globalTime;
        this.noOfPeers = noOfPeers;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public void updateClock(long increment) {
        //This method should move the local clock "timestamp" forward to the local clock+1
        localTime += increment;
    }

    public synchronized void requestTimestamp() {
        updateClock(1);
        //send poll message;
        yesVotes = 0;
        noVotes = 0;

    }

    public synchronized void processVote(long pollTime, boolean vote) { //note need mechanism to stop spamming requests for returning packets
        if (pollTime == localTime) { //should ensure vote corresponds to action wanted
            if (vote) {
                yesVotes++;
            } else {
                noVotes++;
            }
        }
        if(yesVotes >= noOfPeers/2){ //if you have 51%+ of vote
            syncClock(localTime);
            //send commit msg via Peer class
        }
        if(noVotes > noOfPeers/2){
            requestTimestamp();
        }
    }

    //Takes the form of a commit request
    public synchronized boolean pollClock(long newTimestamp) {
        if (newTimestamp > localTime) {
            localTime = newTimestamp;
            return true;
        } else {
            return false;
        }
    }

    //Called whenever a commit is performed
    public synchronized void syncClock(long timestamp) {
        globalTime = timestamp;
        localTime = timestamp; //local clock reset to timestamp too
    }

    public long getLocalTime() {
        return localTime;
    }

    public long getGlobalTime() {
        return globalTime;
    }

    //Other notes
    //Store list of other, conneced nodes
    //Some sort of method to gossip new nodes across
    //May want to use vector clock for ordering - introduces overhead
    //Or use consensus system
    //Create payload class with timestamp and various payloads, (x,y) for line, radius for circle, string for text
    //Arguably, use IP and UDP port to identify nodes
    //Regular ping keepalive on all nodes on node list
    //Solve issue of redrawing old nodes and node-polling-drawing-order later
    //Ultimate draw message only sent after majority poll achieved, must be drawn by all nodes regardless
    //Lamport Logical clock
    //1. New process, increment and poll users
    //2. If in use or clock is outside, then return no, otherwise return yes and increment your own thus reserving it
    //when incrementing ID, you need to poll other nodes whether you //can use this node
    //if the reply is "no" you poll again 
    //remember if you have more than 50% of "yes" replies then you've won
    //poll before every operation
    //essentially you need to increment, ask every operation if you can use and check
    //when you poll, the node ought to be updated to the value reserved to be requested
}
